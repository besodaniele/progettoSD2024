const API_URI = "http://localhost:8080";

window.onload = init();

async function cercaDominio(event)
{
    event.preventDefault();

    const dominio = document.getElementById('dom').value;

    ricercaDominio(dominio);
}

async function ricercaDominio(dominio)
{
    try {
        const response = await fetch(API_URI + '/dominio/' + dominio, {
            method: 'GET'
        });

        if(response.status === 404)
        {
            console.log('Dominio libero');
            document.getElementById('err').innerText = 'Il dominio è libero';

            let acquista = document.createElement("button");
            acquista.type = "button";
            bottone.innerText = "Acquista";
            esci.addEventListener("click", () => {
                window.location.href = "acquista.html?dominio=" + dominio + "&id=" + userId;
            });
        }
        if(response.status === 200)
        {
            console.log('Dominio occupato');
            document.getElementById('err').innerText = 'Il dominio è occupato';

            data = await response.json();
            let par = document.createElement("p");
            par.innerText = data;
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

async function addDomini(dominio)
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
        bottone.addEventListener("click", ricercaDominio(dominio.dominio));
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
        addDomini(domini[d]);
    } 

    let ordini = await getOrdini(userId);

    for(let o in ordini)
    {
        addOrdini(ordini[o]);
    }

    document.getElementById("cerca-domini").addEventListener("submit", cercaDominio);

    let esci = document.getElementById("esci");
    esci.addEventListener("click", () => {
        window.location.href = "index.html";
    });
}