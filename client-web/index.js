const API_URI = "http://localhost:8080";

window.onload = init();

async function init() 
{
    let reg = document.getElementById("registrati");
    reg.addEventListener("click", () => {
        window.location.href = "registrazione.html";
    });
}

