# Progetto Sistemi Distribuiti 2023-2024 - API REST

**Attenzione**: l'unica rappresentazione ammessa è in formato JSON. Pertanto vengono assunti gli header `Content-Type: application/json` e `Accept: application/json`.

## `/utente`

### GET
**attenzione** DA RIMUOVERE

**Descrizione**: Restituisce l'elenco degli utenti registrati.

**Parametri**: non sono previsti parametri.

**Header**: nessuno oltre quelli di default del client.

**Body richiesta**: non previsto body.

**Risposta**: viene restituita una lista di utenti registrati, con campi `id` (intero), `nome` (stringa), `cognome` (stringa), `email` (stringa).

**Codici di stato restituiti**: viene restituito sempre il codice `200 ok`.

### POST

**Descrizione**: registra un utente al sistema.

**Parametri**: nessuno.

**Header**: nessuno.

**Body richiesta**: singolo utente con i campi `nome`, `cognome`, `email`, eventuali id inseriti verranno ignorati e ne verrà assegnato uno server-side.

**Risposta**: body vuoto e la risorsa creata è la risorsa è identificata dall'URI http://localhost:8080/utente/{id}, dove {id} è l'identificativo univoco dell'utente.

**Codici di stato restituiti**:

* 201 Created: successo.
* 409 Conflict: un utente con la stessa email è già registrato 
* 400 Bad Request: c'è un errore del client (JSON, campo mancante o altro).
## `/utente/login/{id}`

### GET

**Descrizione**: effettua il login.

**Parametri**: un parametro del percorso `id` che rappresenta l'utenza con cui fare il login.

**Header**: nessuno.

**Body richiesta**: nessuno.

**Risposta**: viene restituita una rappresentazione dell'utente loggato, con campi `id` (intero), `nome` (stringa), `cognome` (stringa), `email` (stringa).

**Codici di stato restituiti**:

* 200 Ok: successo.
* 404 Not found: non esiste un utente con l'id passato come parametro
## `/dominio`

### GET

**Descrizione**: restituisce una lista di domini registrati dall'utente correntemente loggato.

**Parametri**: nessuno.

**Header**: nessuno.

**Body richiesta**: nessuno.

**Risposta**: viene restituita una lista di domini, con campi `id` (intero), `proprietario` (intero), `dominio`(stringa), `dataRegistrazione` (data), `dataScadenza` (data).

**Codici di stato restituiti**:

* 200 Ok: successo.
* 401 Unauthorized: il login non è stato effettuato, non è possibile vedere alcun dominio