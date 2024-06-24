const API_URI = "http://localhost:8080";

window.onload = init();

async function accessoUtente(event)
{
    // Prevenire il comportamento di invio del modulo predefinito
    event.preventDefault();

    const id = document.getElementById('id').value;

    try {
        const response = await fetch(API_URI + '/utente/login/' + id, {
            method: 'GET'
        });

        if(response.status === 200)
        {
            console.log('Utente loggato con successo');
            //document.cookie = "userId=" + id + "; path=/";
            window.location.href = "domini.html?id=" + id;
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
    document.getElementById("login").addEventListener("submit", accessoUtente);

    let reg = document.getElementById("registrati");
    reg.addEventListener("click", () => {
        window.location.href = "registrazione.html";
    });
}

