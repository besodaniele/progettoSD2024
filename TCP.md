# Progetto Sistemi Distribuiti 2023-2024 - TCP
Il protocollo ha una struttura richiesta e risposta. Le richieste e le risposte sono formattate come stringhe semplici. 

# Comandi: 

# GET

Il comando GET viene utilizzato per ottenere un valore JSON dal database. Il server, per inviare una richiesta di GET al database, deve inviare una stringa strutturato nel seguente modo: 

```
"get" "tabella.key.param" "where" "param=value"
```

La `"key"` e `"param"` possono essere `"*"` per indicare di ottenere ALL, la condizione `"where"` può essere optionale. 

L'operazione ritorna una stringa JSON, se non trova nessuna corrispodenza ritorna `"{}"`. 

`400`: BAD REQUEST, `404`: NOT FOUND.

# INSERT 

Il comando INSERT permette al server di memorizzare una stringa JSON con la sua chiave nel database. Il server, per inviare una richiesta di INSERT al database, deve inviare una stringa strutturato nel seguente modo: 

```
"insert" "tableName" "key" "nameKey" "user" "JSON" 
```

Dove `"tableName"` è la tabella in cui inserire il JSON, `"key"` è la chiave associata al JSON, `"nameKey"` è la chiave inserito nell'operazione LOCK, `"user"` è l'utente che sta cercando di l'operazione (serve per eseguire il check del LOCK), `"JSON"` è il JSON da inserire. 

`200`: OK, `400`: BAD REQUEST, `409`: CONFLICT. 

# UPDATE 

Il comando UPDATE permette al server di aggiornare un JSON con la chiave associata nel database. Il server, per inviare una richiesta di UPDATE al database, deve inviare una stringa strutturato nel seguente modo: 

```
"update" "tableName" "key" "nameKey" "user" "JSON"
```

Dove `"tableName"` è la tabella in cui aggiornare il JSON, `"key"` è la chiave associata al JSON da aggiornare, `"nameKey"` è la chiave inserito nell'operazione LOCK, `"user"` è l'utente che sta cercando di l'operazione (serve per eseguire il check del LOCK), `"JSON"` è il JSON aggiornato. 

`200`: OK, `400`: BAD REQUEST, `404`: NOT FOUND, `409`: CONFLICT

# DELETE 

Il comando DELETE permette al server di eliminare un JSON con la sua chiave associato nel database. Il server, per inviare una richiesta di DELETE al database, deve inviare una stringa strutturato nel seguente modo: 

```
"delete" "tableName" "key"
```

Dove `"tableName"` è la tabella da cui eliminare il JSON, `"key"` è la chiave associato al JSON da eliminare. 

`200`: OK, `400`: BAD REQUEST, `404`: NOT FOUND.

# GETLASTINDEX 

Il comando GETLASTINDEX permette al server di ottenere il più grande identificativo di una tabella. Il server, per inviare una richiesta di GETLASTINDEX al database, deve inviare una stringa strutturato nel seguente modo: 

```
"getLastIndex" "tableName"
```

Dove `"tableName"` è la tabella in considerazione. 

Se la tabella è vuota ritorna `"0"`. 

`200`:  OK, `400`: BAD REQUEST, `404`: NOT FOUND.

# LOCK

Il comando LOCK permette al server di fare il lock di una risorsa del database con il cliente. Il server, per inviare una richiesta di LOCK al database, deve inviare una stringa strutturato nel seguente modo: 

```
"lock" "tableName" "nameKey" "user"
```

Dove `"tableName"` è la tabella della risorsa da legare, `"nameKey"` è il nome della risorsa, `"user"` è l'utente che cerca di fare il lock. 

`200`: OK, `400`: BAD REQUEST, `409`: CONFLICT. 

# UNLOCK

Il comando UNLOCK premettere al server di togliere il lock di una risorsa del database dal cliente. Il server, per inviare una richiesta di UNLOCK al database, deve inviare una stringa strutturato nel seguente modo: 

```
"unlock" "tableName" "nameKey" "user"
```

Dove `"tableName"` è la tabella della risorsa con il lock, `"nameKey"` è il nome della risorsa, `"user"` è l'utente che aveva il lock. 

`200`: OK, `400`: BAD REQUEST, `404`: NOT FOUND. 