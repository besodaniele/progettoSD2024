const API_URI = "http://localhost:8080";

window.onload = init();

async function lockDominio(dominio, userId, tipologia)
{
    try {
        const response = await fetch(API_URI + '/dominio/lock/' + dominio + '?id=' + userId, {
            method: 'GET'
        });

        if(response.status === 200)
        {
            console.log('Dominio bloccato con successo');
            window.location.href = "acquisto.html?dominio=" + dominio + "&id=" + userId + "&tipo=" + tipologia;
        }
        else
        {
            console.log('Dominio già bloccato');
            document.getElementById('err').innerText = 'Il dominio è già stato bloccato';
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function cercaDominio(userId)
{
    const dominio = document.getElementById('dom').value;
    await ricercaDominio(dominio, userId);
}

async function ricercaDominio(dominio, userId)
{
    try {
        const response = await fetch(API_URI + '/dominio/' + dominio, {
            method: 'GET'
        });

        const ris = document.getElementById("acq");

        const existingButton = ris.querySelector("button");
        if (existingButton) {
            ris.removeChild(existingButton);
        }

        const existingText = ris.querySelector("p");
        if (existingText) {
            ris.removeChild(existingText);
        }

        if(response.status === 404)
        {   
            console.log('Dominio libero');
            document.getElementById('risultato').innerText = 'Il dominio è libero';

            let acquista = document.createElement("button");
            acquista.type = "button";
            acquista.innerText = "Acquista";
            acquista.addEventListener("click", () => {
                lockDominio(dominio, userId, "acquisto");
            });
            ris.appendChild(acquista);
        }
        if(response.status === 200)
        {
            console.log('Dominio occupato');
            document.getElementById('risultato').innerText = 'Il dominio è occupato da:';

            let data = await response.json();
            let par = document.createElement("p");
            par.innerText = data[1].nome + " " + data[1].cognome + " (" + data[1].email + ") e scade il " + data[0].dataScadenza;
            ris.appendChild(par);
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

async function getDomini(userId)
{
    try {
        const response = await fetch(API_URI + "/dominio?id=" + userId, {
            method: 'GET'
        });

        if(response.status == 200)
        {
            return await response.json();
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
    let tab = document.getElementById("domini-utente");
    let riga = tab.insertRow();

    riga.insertCell().innerText = dominio.dominio;
    riga.insertCell().innerText = dominio.dataRegistrazione;
    riga.insertCell().innerText = dominio.dataScadenza;

    let oggi = new Date();
    let scadenza = new Date(dominio.dataScadenza);
    if(oggi <= scadenza)
    {
        let bottone = document.createElement("button");
        bottone.type = "button";
        bottone.innerText = "Rinnova";
        bottone.addEventListener("click", () => {
            lockDominio(dominio.dominio, userId, "rinnovo");
        });
        riga.insertCell().appendChild(bottone);
    }
}

async function getOrdini(userId)
{
    try {
        const response = await fetch(API_URI + "/acquisto?id=" + userId, {
            method: 'GET'
        });

        if(response.status === 200)
        {
            return await response.json();
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
    let tab = document.getElementById("ordini-utente");
    let riga = tab.insertRow();

    riga.insertCell().innerText = ordine.dominio;
    riga.insertCell().innerText = ordine.dataOrdine;
    riga.insertCell().innerText = ordine.tipo;
    riga.insertCell().innerText = ordine.quota;
}

async function init() 
{
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('id');

    let domini = await getDomini(userId);
    
    for(let d in domini)
    {
        addDomini(domini[d], userId);
    } 

    let ordini = await getOrdini(userId);

    for(let o in ordini)
    {
        addOrdini(ordini[o]);
    }

    document.getElementById("cerca-domini").addEventListener("submit", (event) => {
        event.preventDefault();
        cercaDominio(userId);
    });

    document.getElementById("esci").addEventListener("click", () => {
        window.location.href = "index.html";
    });
}