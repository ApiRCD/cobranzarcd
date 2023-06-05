const regexText = new RegExp(/^([A-Za-z\s])+$/g) 
const headConexion = {
    'Accept': 'application/json',
    'Content-Type': 'application/json',
    'X-CSRF-TOKEN': document.getElementById('_token').value //the token is create in head html
}

$('#btnGuardar').click(function(){
    CifradoDatos()    
})

// function GenerarLigaPago(){

//     var objOrdenPago = {
//         ordenId: $('#ordenId').val(),
//         nombreCliente: $('#nameClient').val(),
//         telefonoCliente: $('#phone').val(),
//         subtotal: $('#subtotal').val(),
//         impuestos: $('#impuestos').val(),
//         total: $('#total').val(),
//         concepto: $('#concepto').val(),
//         emailCliente: $('#email').val(),
//         documentoCliente: "0",
//         tipoDocumentoCliente: "0",
//         urlReturn: window.location.origin,
//         items: [{
//             "cantidad": 1,
//             "producto": " - MASCOTAS - PAGO ÚNICO -SILICE",
//             "precio": 21000,
//             "moneda": "USD"
//         }]
//     }

//     $.ajax({
//         url: '/generarlinkpago',
//         type: 'POST',
//         data: {
//             objson: JSON.stringify(objOrdenPago)
//         },        
//         success: function(data)
//         {
//             console.log(data)
//         }
//     });
    
// }


function CifradoDatos(){    
    var nameholder = $('#nameHolder').val()
    var numtarjeta = $('#numberTarjet').val()
    var expmonth = $('#expiryMonth').val()
    var expyear = $('#expiryYear').val()
    var cvc = $('#cvc').val()
    if($('#nameHolder').val() == "")
    {
        notificacion('error', 'El campo nombre es requerido')
        return 
    }
    if(numtarjeta.length < 19)
    {
        notificacion('error', 'Tarjeta incorrecta')
        return
    }
    if(expmonth == '')
    {
        notificacion('error', 'El mes de expiración es requerido')
        return 
    }
    if(expyear == "")
    {
        notificacion('error', 'El año de expiración es requerido')
        return
    }
    if(cvc.length < 3)
    {
        notificacion('error', 'El campo CVC es incorrecto')
        return
    }         
    
    //  DATOS DE TARJETA DEL CLIENTE
    var datosTarjeta = {
        number: numtarjeta.replaceAll(' ',''),
        holder_name: nameholder,
        expiry_month: parseInt(expmonth),
        expiry_year: parseInt(expyear),
        cvc: cvc,
        saved: true,
        method: "tarjeta"
    }


    //  DATOS PARA GENERAR LA ORDEN DE PAGO 
    var objOrdenPago = {
        ordenId: $('#ordenId').val(),
        nombreCliente: $('#nameClient').val(),
        telefonoCliente: $('#phone').val(),
        subtotal: $('#subtotal').val(),
        impuestos: $('#impuestos').val(),
        total: $('#total').val(),
        concepto: $('#concepto').val(),
        emailCliente: $('#email').val(),
        documentoCliente: "0",
        tipoDocumentoCliente: "0",
        urlReturn: window.location.origin,
        sistema: $('#sistema').val()
        // items: [{
        //     "cantidad": 1,
        //     "producto": " - MASCOTAS - PAGO ÚNICO -SILICE",
        //     "precio": 21000,
        //     "moneda": "USD"
        // }]
    }

    activeLoader('Procesando..', 'No recargue o actualize la pagina mienstras se procesa su pago.')   
console.log(objOrdenPago)
    var reciboId = "";
    var tarjetaTokenizada = "";    

    $.ajax({
        url: "/clavecifrado",
        type: "POST",
        success: function(data){
            
            if(data.error == false)
            {
                var tarjetaEncryp = codifTarjeta(JSON.stringify(datosTarjeta), data.data)
                
                if(tarjetaEncryp == -1)
                {
                    closeAlert()
                    setTimeout(function(){
                        notificacion('error', 'Error al procesar')
                    },10)
                }
                else
                {                               
                    var DatosTarjeta = {
                        data0: tarjetaEncryp,
                        client: {
                            email: $('#email').val(),
                            name: $('#nameHolder').val(),
                            phone: $('#phone').val()
                        }
                    }

                    $.ajax({
                        url: '/cobrosilice',
                        type: 'POST',
                        data: {
                            DatosTarjeta: JSON.stringify(DatosTarjeta),
                            OrdenPago: JSON.stringify(objOrdenPago)
                        },
                        success: function(data)
                        {
                            console.log(data)
                            closeAlert()
                            if(data.error == false)
                            {
                                var resp = JSON.parse(data.data)
                                if(resp.status == true)
                                {
                                    successAlert("Hecho", "Cobro realizado correctamente")
                                    setTimeout(function(){
                                        window.location.href = '/gracias'
                                    },2000)
                                }
                                else
                                {
                                    errorAlert("error", resp.menssage)
                                }
                            }
                            else
                            {
                                setTimeout(function(){
                                    errorAlert("Error",data.data)
                                },10)
                            }
                        }
                    })
                    
                }
            }
        }
    })
return 

    $.ajax({
        url: '/tokenizartarjeta',
        type: 'POST',
        data: {                  
            DatosTarjeta: JSON.stringify(DatosTarjeta)   
        },
        success: function(data)
        {
            console.log('tokenizado tarjeta......')
            console.log(data)
            console.log(JSON.parse(data.mensaje))                                           
            if(data.error != false)
            {
                closeAlert()
                setTimeout(function(){
                    errorAlert("Error",  "No se pudo tokenizar la tarjeta")
                },10)
            }
            else
            {
                var respTar = JSON.parse(data.data)
                tarjetaTokenizada = resptar.token
            }
            
        }
    })


    $.ajax({
        url: '/generarlinkpago',
        type: 'POST',
        data: {
            objson: JSON.stringify(objOrdenPago)
        },        
        success: function(data)
        {
            console.log(data)
            if(data.error != false)
            {
                closeAlert()
                setTimeout(function(){
                    errorAlert('Error', "No se pudo generar la orden de pago.")
                },10)
            }
            else
            {
                var resp = JSON.parse(data.data)
                reciboId = resp.reciboId

                
            }
        }
    });
    

    
}

//   CODIFICAR DE TARJETAS PROPORCIONADO POR SILICE
function codifTarjeta(datosTarjeta, clave) {     
    try { 
        var salt16 = CryptoJS.lib.WordArray.random(16); 
        var keyIV = CryptoJS.PBKDF2(clave, salt16, { 
                keySize: (32 + 16) / 4, // 12 words a 4 bytes = 48 bytes 
                iterations:1000, // Choose a sufficiently high iteration count 
                hasher:CryptoJS.algo.SHA256// Default digest is SHA-1 
            }); 
        var key32 = CryptoJS.lib.WordArray.create(keyIV.words.slice(0, 32 / 4)); 
        var iv16 = CryptoJS.lib.WordArray.create(keyIV.words.slice(32 / 4, (32 + 16) / 4)); 
        var cipherParams = CryptoJS.AES.encrypt(datosTarjeta, key32, { iv:iv16 }); 
        var ciphertext = cipherParams.ciphertext; 
        var saltCiphertext = salt16.clone().concat(ciphertext); 
        var saltCiphertextB64 = saltCiphertext.toString(CryptoJS.enc.Base64); 

        return saltCiphertextB64; 
    } catch (error) { 
        console.log(error)
        return -1 
    } 
}


//  CANDADOS DE FORMATO PARA LOS INPUTS DE NUMERO DE TARJETA Y CVC

var tarjetaInput = document.getElementById("numberTarjet");
var cvc = document.getElementById('cvc')
        
tarjetaInput.addEventListener("input", function(event) {
    var input = event.target;
    var inputValue = input.value;
         
    // Elimina cualquier caracter que no sea un dígito
    inputValue = inputValue.replace(/\D/g, "");     
         
    // Aplica el formato de tarjeta (grupos de 4 dígitos separados por espacios)
    var formattedValue = "";
    for (var i = 0; i < inputValue.length; i++) {
        if (i > 0 && i % 4 === 0) {
            formattedValue += " ";
        }
        formattedValue += inputValue[i];
    }            
    if(formattedValue.length >= 19)
    {
        input.value = formattedValue.substr(0, 19)
        return
    }         
    // Actualiza el valor del input con el formato de tarjeta
    input.value = formattedValue;
    
});

cvc.addEventListener("input", function(event) {
    var input = event.target;
    var inputValue = input.value;
         
    // Elimina cualquier caracter que no sea un dígito
    inputValue = inputValue.replace(/\D/g, "");     
         
    // Aplica el formato de tarjeta (grupos de 4 dígitos separados por espacios)
    var formattedValue = "";
    for (var i = 0; i < inputValue.length; i++) {
        if (i > 0 && i % 4 === 0) {
            formattedValue += " ";
        }
        formattedValue += inputValue[i];
    }            
    if(inputValue.length >= 3)
    {
        input.value = formattedValue.substr(0,3)
        return
    }         
    // Actualiza el valor del input con el formato de tarjeta
    input.value = formattedValue;
    
});
