const API_URI = "http://localhost:8080";

window.onload = init();

async function accessoUtente(event)
{
    // Prevenire il comportamento di invio del modulo predefinito
    event.preventDefault();

    // Ottenere l'ID dell'utente
    const id = document.getElementById('id').value;

    try {
        // Effettuare una richiesta GET all'API per verificare l'esistenza dell'utente
        const response = await fetch(API_URI + '/utente/login/' + id, {
            method: 'GET'
        });

        // Gestire la risposta dell'API
        if(response.status == 200)
        {
            console.log('Utente loggato con successo');
            window.location.href = "domini.html?id=" + id;
        }
        else if(response.status == 404)
        {
            console.error('Utente non trovato');
            document.getElementById('err').innerText = 'Utente non trovato';
        }
        else if(response.status == 400)
        {
            console.error('Errore nella richiesta');
            document.getElementById('err').innerText = 'Errore nella richiesta';
        }
        else
        {
            console.error('Errore nel login');
            document.getElementById('err').innerText = 'Errore nel login';
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function init() 
{
    // Aggiungere un ascoltatore per la sottomissione del modulo
    document.getElementById("login").addEventListener("submit", accessoUtente);

    // Aggiungere un ascoltatore per il link di registrazione
    let reg = document.getElementById("registrati");
    reg.addEventListener("click", () => {
        window.location.href = "registrazione.html";
    });
}

