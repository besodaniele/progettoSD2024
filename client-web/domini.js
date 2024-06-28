const API_URI = "http://localhost:8080";

window.onload = init();

async function lockDominio(dominio, userId, tipologia)
{
    try {
        // Effettuare una richiesta GET all'API per bloccare un dominio
        const response = await fetch(API_URI + '/dominio/lock/' + dominio + '?id=' + userId, {
            method: 'GET'
        });

        // Gestire la risposta dell'API
        if(response.status == 200)
        {
            console.log('Dominio bloccato con successo');
            // Reindirizzare l'utente alla pagina di acquisto
            window.location.href = "acquisto.html?dominio=" + dominio + "&id=" + userId + "&tipo=" + tipologia;
        }
        else if(response.status == 409)
        {
            console.error('Dominio già bloccato');
            document.getElementById('err').innerText = 'Il dominio è già stato bloccato';
        }
        else if(response.status == 400)
        {
            console.error('Errore nei parametri');
            document.getElementById('err').innerText = 'Errore nei parametri';
        }
        else
        {
            console.log('Errore nel blocco del dominio');
            document.getElementById('err').innerText = 'Errore nel blocco del dominio';
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function cercaDominio(userId)
{
    // Ottenere il dominio dalla casella di testo
    const dominio = document.getElementById('dom').value;
    // Effettuare una richiesta GET all'API per cercare un dominio
    await ricercaDominio(dominio, userId);
}

async function ricercaDominio(dominio, userId)
{
    try {
        // Effettuare una richiesta GET all'API per cercare un dominio
        const response = await fetch(API_URI + '/dominio/' + dominio, {
            method: 'GET'
        });

        // Pulisco il risultato precedente
        document.getElementById('err').innerText = '';

        // Ottengo il div per il risultato
        const ris = document.getElementById("acq");

        // Rimuovo il bottone e il testo precedente se esistono
        const existingButton = ris.querySelector("button");
        if (existingButton) {
            ris.removeChild(existingButton);
        }

        // Rimuovo il testo precedente se esiste
        const existingText = ris.querySelector("p");
        if (existingText) {
            ris.removeChild(existingText);
        }

        // Gestire la risposta dell'API
        if(response.status == 404)
        {   
            console.log('Dominio libero');
            document.getElementById('risultato').innerText = 'Il dominio è libero';

            // Creo un bottone per l'acquisto del dominio
            let acquista = document.createElement("button");
            acquista.type = "button";
            acquista.innerText = "Acquista";
            // Aggiungo un ascoltatore per il bottone
            acquista.addEventListener("click", () => {
                lockDominio(dominio, userId, "acquisto");
            });
            // Inserisco il bottone nel div
            ris.appendChild(acquista);
        }
        else if(response.status == 200)
        {
            console.log('Dominio occupato');
            document.getElementById('risultato').innerText = 'Il dominio è occupato da:';

            // Ottengo i dati del proprietario del dominio
            let data = await response.json();
            // Creo un paragrafo con i dati del proprietario
            let par = document.createElement("p");
            par.innerText = data[1].nome + " " + data[1].cognome + " (" + data[1].email + ") e scade il " + data[0].dataScadenza;
            // Inserisco il paragrafo nel div
            ris.appendChild(par);
        }
        else if(response.status == 400)
        {
            console.error('Errore nei parametri');
            document.getElementById('err').innerText = 'Errore nei parametri';
        }
        else
        {
            console.error('Errore nella ricerca del dominio');
            document.getElementById('err').innerText = 'Errore nella ricerca del dominio';
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function getDomini(userId)
{
    try {
        // Effettuare una richiesta GET all'API per ottenere i domini di un utente
        const response = await fetch(API_URI + "/dominio?id=" + userId, {
            method: 'GET'
        });

        // Gestire la risposta dell'API
        if(response.status == 200)
        {
            // Restituire i domini dell'utente
            return await response.json();
        }
        else if(response.status == 404)
        {
            console.error('Errore nei parametri');
        }
        else
        {
            console.error('Errore nel recupero dei domini');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function addDomini(dominio, userId)
{
    // Aggiungere una riga alla tabella dei domini
    let tab = document.getElementById("domini-utente");
    let riga = tab.insertRow();

    // Inserire i dati del dominio nella riga
    riga.insertCell().innerText = dominio.dominio;
    riga.insertCell().innerText = dominio.dataRegistrazione;
    riga.insertCell().innerText = dominio.dataScadenza;

    // Aggiungere un bottone per rinnovare il dominio
    let oggi = new Date();
    let scadenza = new Date(dominio.dataScadenza);
    if(oggi <= scadenza)
    {
        // Se il dominio non è scaduto, aggiungere un bottone per rinnovarlo
        let bottone = document.createElement("button");
        bottone.type = "button";
        bottone.innerText = "Rinnova";
        // Aggiungere un ascoltatore per il bottone
        bottone.addEventListener("click", () => {
            lockDominio(dominio.dominio, userId, "rinnovo");
        });
        // Inserire il bottone nella riga
        riga.insertCell().appendChild(bottone);
    }
}

async function getOrdini(userId)
{
    try {
        // Effettuare una richiesta GET all'API per ottenere gli ordini di un utente
        const response = await fetch(API_URI + "/acquisto?id=" + userId, {
            method: 'GET'
        });

        // Gestire la risposta dell'API
        if(response.status == 200)
        {
            // Restituire gli ordini dell'utente
            return await response.json();
        }
        else if(response.status == 404)
        {
            console.error('Errore nei parametri');
        }
        else
        {
            console.error('Errore nel recupero degli ordini');
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function addOrdini(ordine)
{
    // Aggiungere una riga alla tabella degli ordini
    let tab = document.getElementById("ordini-utente");
    let riga = tab.insertRow();

    // Inserire i dati dell'ordine nella riga
    riga.insertCell().innerText = ordine.dominio;
    riga.insertCell().innerText = ordine.dataOrdine;
    riga.insertCell().innerText = ordine.tipo;
    riga.insertCell().innerText = ordine.quota;
}

async function init() 
{
    // Ottenere l'ID dell'utente dalla query string
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('id');

    // Ottenere i domini dell'utente
    let domini = await getDomini(userId);
    
    // Aggiungere i domini alla tabella
    for(let d in domini)
    {
        addDomini(domini[d], userId);
    } 

    // Ottenere gli ordini dell'utente
    let ordini = await getOrdini(userId);

    // Aggiungere gli ordini alla tabella
    for(let o in ordini)
    {
        addOrdini(ordini[o]);
    }

    // Aggiungere un ascoltatore per la ricerca di un dominio
    document.getElementById("cerca-domini").addEventListener("submit", (event) => {
        event.preventDefault();
        cercaDominio(userId);
    });

    // Aggiungere un ascoltatore per il link di uscita
    document.getElementById("esci").addEventListener("click", () => {
        window.location.href = "index.html";
    });
}