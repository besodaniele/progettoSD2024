const API_URI = "http://localhost:8080";

window.onload = init();

async function inviaDati(event)
{
    // Prevenire il comportamento di invio del modulo predefinito
    event.preventDefault();
  
    // Creare un oggetto con i dati del modulo
    let data = {
      nome: document.getElementById('nome').value,
      cognome: document.getElementById('cognome').value,
      email: document.getElementById('email').value
    };
  
    // Invia una richiesta POST al server
    try {
        const response = fetch(API_URI + '/registrazione', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
        });

        if(!response.ok)
        {
            throw new Error("Errore! status: " + response.status);
        }

        const responseData = await response.json();
        //console.log(responseData);
        //return responseData;
        window.location.href = "id.html?id=" + responseData.id;
    } catch (error) {
        console.error('Error:', error);
    }
};

async function init() 
{
    document.getElementById("registrazione").addEventListener("submit", inviaDati);
}