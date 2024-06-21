const API_URI = "http://localhost:8080";

window.onload = init();

async function init() 
{
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get('id');
    console.log("ID Utente: " + id);
    // Stampare l'ID sulla pagina
    document.getElementById("id-utente").innerText = id;

    let acc = document.getElementById("accedi");
    acc.addEventListener("click", () => {
        window.location.href = "index.html";
    });
}
