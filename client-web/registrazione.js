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
    const response = fetch(API_URI + '/registrazione', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    });
    //.then(response => response.json())
    //.then(data => console.log(data))
    //.catch((error) => {
    //  console.error('Error:', error);
    //});

    if(!response.ok)
    {
        throw new Error("Errore! status: " + response.status);
    }
};

async function init() 
{
    document.getElementById("registrazione").addEventListener("submit", inviaDati);
}