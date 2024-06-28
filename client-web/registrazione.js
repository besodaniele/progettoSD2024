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

    try {
        // Effettuare una richiesta POST all'API per registrare un nuovo utente
        const response = await fetch(API_URI + '/utente/', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });
    
        // Gestire la risposta dell'API
        if (response.status == 201) 
        {
            console.log('Utente registrato con successo');
            // Ottenere Location dalla risposta
            const location = response.headers.get("Location");
            // Stampare l'ID dell'utente nella console
            console.log('ID Utente:', location.split("/").pop());
            // Reindirizzare l'utente alla pagina dell'ID
            window.location.href = "id.html?id=" + location.split("/").pop();
        }
        else if (response.status == 409) 
        {
            console.error('Utente già esistente');
            document.getElementById('err').innerText = 'Utente già esistente';
        }
        else if(response.status == 400)
        {
            console.error('Errore nel client');
            document.getElementById('err').innerText = 'Errore nei parametri';
        }
        else 
        {
            console.error('Errore nella registrazione dell\'utente');
        }
    } catch (error) {
        console.error('Errore:', error);
    }
};

async function init()
{
    // Aggiungere un ascoltatore per la sottomissione del modulo
    document.getElementById("registrazione").addEventListener("submit", inviaDati);

    // Aggiungere un ascoltatore per il link di accesso
    document.getElementById("esci").addEventListener("click", () => {
        window.location.href = "index.html";
    });
}