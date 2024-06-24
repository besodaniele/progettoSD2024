const API_URI = "http://localhost:8080";

window.onload = init();

async function getDomini()
{
    try {
        const response = await fetch(API_URI + '/dominio/', {
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

    let bottone = document.createElement("button");
    bottone.innerText = "Rinnova";
    riga.insertCell().appendChild(bottone);
    /*bottone.addEventListener("click", async () => {
        try {
            const response = await fetch(API_URI + '/dominio/' + dominio.id, {
                method: 'PUT'
            });

            if(response.status === 200)
            {
                console.log('Dominio rinnovato con successo');
                location.reload();
            }
            else
            {
                console.error('Errore nel rinnovo del dominio');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    });*/
}

async function init() 
{
    let domini = await getDomini();
    
    for(let d in domini)
    {
        addDomini(domini[d]);
    }

    //ordini = await getOrdini();

    let esci = document.getElementById("esci");
    esci.addEventListener("click", () => {
        window.location.href = "index.html";
    });
}