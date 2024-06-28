const API_URI = "http://localhost:8080";

window.onload = init();

async function unlockDominio(dominio, id)
{
    try {
        const response = await fetch(API_URI + '/dominio/unlock/' + dominio + '?id=' + id, {
            method: 'GET'
        });

        if(response.status === 200)
        {
            console.log('Dominio liberato con successo');
        }
        else
        {
            console.error('Errore nel rilascio del dominio');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function acquistaDominio(dominio, id)
{
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

async function rinnovaDominio(dominio, id)
{
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
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        if(response.ok)
        {
            console.log('Dominio rinnovato con successo');
            window.location.href = "domini.html?id=" + id;
        }
        else
        {
            console.error('Errore nel rinnovo del dominio');
            document.getElementById('err').innerText = 'Errore nel rinnovo del dominio';
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
    const tipo = urlParams.get('tipo');
    console.log("Dominio: " + dominio);
    console.log("ID Utente: " + id);
    console.log("Tipo: " + tipo);
    if(tipo == "acquisto")
        document.getElementById("dominio").innerText = "Dominio da acquistare: " + dominio;
    if(tipo == "rinnovo")
        document.getElementById("dominio").innerText = "Dominio da rinnovare: " + dominio;

    //let acquista = document.getElementById("acquista");
    //acquista.addEventListener("click", acquistaDominio(dominio, id));

    document.getElementById("acquisto").addEventListener("submit", (event) => {
        event.preventDefault();
        if(tipo == "acquisto")
            acquistaDominio(dominio, id);
        if(tipo == "rinnovo")
            rinnovaDominio(dominio, id);
    });

    document.getElementById("indietro").addEventListener("click", () => {
        unlockDominio(dominio, id);
        window.location.href = "domini.html?id=" + id;
    });

    window.addEventListener('beforeunload', (event) => {
        // event.preventDefault();
        // event.returnValue = '';
        unlockDominio(dominio, id);
    });
}