const API_URI = "http://localhost:8080";

window.onload = init();

async function init() 
{
    // Ottenere l'ID dell'utente dalla query string
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get('id');
    // Stampare l'ID dell'utente nella console
    console.log("ID Utente: " + id);
    // Stampare l'ID sulla pagina
    document.getElementById("id-utente").innerText = id;

    // Aggiungere un ascoltatore per il link di accesso
    let acc = document.getElementById("accedi");
    acc.addEventListener("click", () => {
        window.location.href = "index.html";
    });
}
