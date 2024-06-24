const API_URI = "http://localhost:8080";

window.onload = init();

async function acquistaDominio(event)
{
    event.preventDefault();

    let data = {
      numAnni: document.getElementById('durata').value,
      numeroCarta: document.getElementById('numero').value,
      dataScadenza: document.getElementById('scadenza').value,
      cvv: document.getElementById('cvv').value,
      nomeIntestatario: document.getElementById('nome-intest').value,
      cognomeIntestatario: document.getElementById('cognome-intest').value
    };

    try {
        const response = await fetch(API_URI + '/dominio/' + dominio + '?id=' + id, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        if(response.status === 201)
        {
            console.log('Dominio acquistato con successo');
            window.location.href = "domini.html?id=" + id;
        }
        else
        {
            console.error('Errore nell\'acquisto del dominio');
            document.getElementById('err').innerText = 'Errore nell\'acquisto del dominio';
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function init() 
{
    const urlParams = new URLSearchParams(window.location.search);
    const dominio = urlParams.get('dominio');
    const id = urlParams.get('id');
    console.log("Dominio: " + dominio);
    console.log("ID Utente: " + id);
    document.getElementById("dominio").innerText = dominio;

    let acc = document.getElementById("indietro");
    acc.addEventListener("click", () => {
        window.location.href = "domini.html?=id" + id;
    });

    let acquista = document.getElementById("acquista");
    acquista.addEventListener("click", acquistaDominio(dominio, id));
}