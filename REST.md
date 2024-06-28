# Progetto Sistemi Distribuiti 2023-2024 - API REST

**Attenzione**: l'unica rappresentazione ammessa è in formato JSON. Pertanto vengono assunti gli header `Content-Type: application/json` e `Accept: application/json`.

## `/utente`

### POST

**Descrizione**: registra un utente al sistema.

**Parametri**: nessuno.

**Header**: nessuno.

**Body richiesta**: singolo utente con i campi `nome`, `cognome`, `email`, eventuali id inseriti verranno ignorati e ne verrà assegnato uno server-side.

**Risposta**: body vuoto e la risorsa creata è la risorsa è identificata dall'URI http://localhost:8080/utente/{id}, dove {id} è l'identificativo univoco dell'utente.

**Codici di stato restituiti**:

- 201 Created: successo.
- 409 Conflict: un utente con la stessa email è già registrato
- 400 Bad Request: c'è un errore del client (JSON, campo mancante o altro).

## `/utente/login/{id}`

### GET

**Descrizione**: effettua il login.

**Parametri**: un `pathparam` `id` che rappresenta l'utenza con cui fare il login.

**Header**: nessuno.

**Body richiesta**: nessuno.

**Risposta**: viene restituita una rappresentazione dell'utente loggato, con campi `id` (stringa), `nome` (stringa), `cognome` (stringa), `email` (stringa).

**Codici di stato restituiti**:

- 200 Ok: successo.
- 404 Not found: non esiste un utente con l'id passato come parametro
- 400 Bad request: errore nel codice

## `/dominio`

### GET

**Descrizione**: restituisce una lista di domini registrati dall'utente.

**Parametri**: nessuno.

**Header**: `query param` `id` di tipo int, l'utente di cui si vuole sapere i domini.

**Body richiesta**: nessuno.

**Risposta**: viene restituita una lista di domini, con campi `id` (intero), `proprietario` (stringa), `dominio`(stringa), `dataRegistrazione` (data in formato yyyy-mm-dd), `dataScadenza` (data in formato yyyy-mm-dd).

**Codici di stato restituiti**:

- 200 Ok: successo.
- 404 bad request: errore nel parametro

## `/dominio/{dominio}`

### GET

**Descrizione**: restituisce i dati del dominio (se è ancora attivo) e il suo proprietario

**Parametri** un `pathparam` `dominio` che è il nome del dominio da cercare:

**Header**: nessuno.

**Body richiesta**: nessuno.

**Risposta**: viene restituita un dominio, con campi `id` (intero), `proprietario` (intero), `dominio`(stringa), `dataRegistrazione` (data in formato yyyy-mm-dd), `dataScadenza` (data in formato yyyy-mm-dd) e il suo proprietario con campi `nome` (stringa), `cognome` (stringa), `id` (stringa), `email` (stringa).

**Codici di stato restituiti**:

- 200 Ok: successo.
- 400 bad request: errore nel parametro
- 404 not found: il dominio è disponibile

### POST

**Descrizione**: effettua l'acquisto di un dominio.

**Parametri** un `pathparam` `dominio` che è il nome del dominio da comprare, un `query param` `id` di tipo int che è l'id dell'utente che sta effettuando l'acquisto.

**Header**: nessuno.

**Body richiesta**: i parametri dell'acquisto in formato json, con campi `cvv` (stringa), `cognomeIntestatario` (stringa), `nomeIntestatario` (stringa), `numeroCarta` (stringa), `dataScadenza` (data in formato yyyy-mm-dd), `quota` (float), `numAnni` (intero).

**Risposta**: body vuoto e la risorsa creata è la risorsa è identificata dall'URI http://localhost:8080/dominio/{dominio}, dove {dominio} è il nome del dominio (univoco per quello attivo, gli altri nella get del dominio singolo vengono ignorati).

**Codici di stato restituiti**:

- 201 created: successo.
- 403 forbidden: si è superato il limite di anni per cui si può acquistare un dominio (10 anni) o il limite minimo (1 anno).
- 400 bad request: errore nel formato dei parametri o nel body.
- 409 conflict: il dominio esiste già e non è scaduto, oppure qualcun'altro lo sta registrando in questo momento
-

### PUT

**Descrizione**: effettua il rinnovo di un dominio non scaduto

**Parametri** un `pathparam` `dominio` che è il nome del dominio da rinnovare, un `query param` `id` di tipo int che è l'id dell'utente che sta effettuando il rinnovo.

**Header**: nessuno.

**Body richiesta**: i parametri dell'acquisto in formato json, con campi `cvv` (stringa), `cognomeIntestatario` (stringa), `nomeIntestatario` (stringa), `numeroCarta` (stringa), `dataScadenza` (data in formato yyyy-mm-dd), `quota` (float), `numAnni` (intero).

**Risposta**: nessuna.

**Codici di stato restituiti**:

- 201 created: successo.
- 403 forbidden: si è cercato di rinnovare per meno di un anno o si è superato i 10 anni complessivi (anni rimanenti + anni rinnovo)
- 400 bad request: errore nel formato dei parametri
- 404 not found: il dominio che si cerca di rinnovare non esiste o è scaduto
- 409 conflict: dominio già registrato (non dovrebbe succedere a meno di errori interni)

## `/dominio/lock/{dominio}`

### GET

**Descrizione**: blocca un dominio per assicurarsi che non possano acquistare nello stesso momento lo stesso dominio due utenti diversi.

**Parametri** un `path param` `dominio` che è il nome del dominio da bloccare, un `query param` `id` di tipo int che è l'id dell'utente che sta effettuando il lock.

**Header**: nessuno.

**Body richiesta**: nessuno.

**Risposta**: nessuna.

**Codici di stato restituiti**:

- 200 ok: successo
- 409 conflict: lock già effettuato da qualcun altro
- 400 bad request: errore nei parametri

## `/dominio/unlock/{dominio}`
### GET

**Descrizione**: sblocca un dominio per permettere azioni agli altri utenti.

**Parametri** un `path param` `dominio` che è il nome del dominio da sbloccare, un `query param` `id` di tipo int che è l'id dell'utente che sta effettuando l'unlock.

**Header**: nessuno.

**Body richiesta**: nessuno.

**Risposta**: nessuna.

**Codici di stato restituiti**:

- 200 ok: successo
- 404 not found: il dominio non era bloccato
- 400 bad request: errore nei parametri

## `/acquisto`
### GET

**Descrizione**: restituisce una lista di acquisti effettuati dall'utente.

**Parametri**: nessuno.

**Header**: `query param` `id` di tipo int, l'utente di cui si vuole conoscere gli acquisti.

**Body richiesta**: nessuno.

**Risposta**: viene restituita una lista di acquisti, con campi `id` (intero), `cliente` (stringa), `dominio`(stringa), `cvv` (stringa), `cognomeIntestatario` (stringa), `nomeIntestatario` (stringa), `numeroCarta` (stringa), `dataScadenza` (data in formato yyyy-mm-dd), `dataOrdine` (data in formato yyyy-mm-dd), `quota` (float), `numAnni` (intero), `tipo` (stringa, rinnovo o acquisto).

**Codici di stato restituiti**:

- 200 Ok: successo.
- 404 bad request: errore nel parametro