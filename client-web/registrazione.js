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
    //try {
    //    const response = await fetch(API_URI + '/utente/', {
    //    method: 'POST',
    //    headers: {
    //        'Content-Type': 'application/json'
    //    },
    //    body: JSON.stringify(data)
    //    });

    //    if(!response.ok)
    //    {
    //        throw new Error("Errore! status: " + response.status);
    //    }

    //    const responseData = await response.json();
        //console.log(responseData);
        //return responseData;
    //    window.location.href = "id.html?id=" + responseData.id;
    //} catch (error) {
    //    console.error('Error:', error);
    //}

        try {
            const response = await fetch(API_URI + '/utente/', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            });
    
            if (response.status === 201) {
                console.log('Utente registrato con successo');
                const location = response.headers.get("Location");
                console.log('ID Utente:', location.split("/").pop());
                window.location.href = "id.html?id=" + location.split("/").pop();
                // Gestisci qui la navigazione o l'aggiornamento dell'interfaccia utente
            } else if (response.status === 409) {
                console.error('Utente già esistente');
                // Gestisci qui la logica per utente già esistente
            } else {
                throw new Error('Errore nella registrazione dell\'utente');
            }
        } catch (error) {
            console.error('Errore:', error);
        }
};

async function init()
{
    document.getElementById("registrazione").addEventListener("submit", inviaDati);

    document.getElementById("esci").addEventListener("click", () => {
        window.location.href = "index.html";
    });
}