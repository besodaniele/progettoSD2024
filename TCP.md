# Progetto Sistemi Distribuiti 2023-2024 - TCP
Il protocollo ha una struttura di tipo richiesta e risposta. Le richieste e le risposte sono formattate come semplici stringhe. 

# Comandi: 

# GET

Il comando GET viene utilizzato per ottenere un valore JSON dal database. Il server, per inviare una richiesta di GET al database, deve inviare una stringa strutturata nel seguente modo: 

```
"get" "tabella.key.param" "where" "param=value"
```

La `"key"` e `"param"` possono essere `"*"` per indicare di ottenere tutte le chiavi e/o tutti i parametri delle chiavi \
le possibili combinazioni sono: 
- key.param 
- key.*
- *.param 
-  \* . * 
<!-- -->
la condizione `"where"` è opzionale. \
L'operazione ritorna una stringa JSON; se non trova nessuna corrispondenza, ritorna `"{}"`, cioè un JSON vuoto. 

`400`: **BAD REQUEST**, se la richiesta è strutturata male quindi il parser non riesce ad elaborarla.

# INSERT 

Il comando INSERT permette al server di memorizzare una stringa JSON con la sua chiave nel database. Il server, per inviare una richiesta di INSERT al database, deve inviare una stringa strutturata nel seguente modo: 

```
"insert" "tableName" "key" "nameKey" "user" "JSON" 
```

Dove `"tableName"` è la tabella in cui inserire il JSON, `"key"` è la chiave associata al JSON, `"nameKey"` è la chiave utilizzata nell'operazione LOCK, `"user"` è l'utente che sta eseguendo l'operazione (serve per verificare il LOCK), `"JSON"` è il JSON da inserire. 

`200`: **OK**, se il JSON viene inserito con successo.

`400`: **BAD REQUEST**: 
- se la richiesta è strutturata male;
- se non esiste il lock sulla risorsa `"tableName.nameKey"`;
- se `"JSON"` è null.

`409`: **CONFLICT**: 
- se la chiave `"key"` esiste già come chiave in `"tableName"`;
- se `"user"` non possiede il lock.

# UPDATE 

Il comando UPDATE permette al server di aggiornare un JSON con la chiave associata nel database. Il server, per inviare una richiesta di UPDATE al database, deve inviare una stringa strutturata nel seguente modo: 

```
"update" "tableName" "key" "nameKey" "user" "JSON"
```

Dove `"tableName"` è la tabella in cui aggiornare il JSON, `"key"` è la chiave associata al JSON da aggiornare, `"nameKey"` è la chiave utilizzata nell'operazione LOCK, `"user"` è l'utente che sta eseguendo l'operazione (serve per verificare il LOCK), `"JSON"` è il JSON aggiornato. 

`200`: **OK**, se l'aggiornamento termina con successo. 

`400`: **BAD REQUEST**: 
- se la richiesta è strutturata male;
- se non esiste il lock sulla risorsa `"tableName.nameKey"`;
- se `"key"` è null. 

`404`: **NOT FOUND**: 
- se la chiave `"key"` non esiste come chiave in `"tableName"`. 

`409`: **CONFLICT**: 
- se `"user"` non possiede il lock.

# DELETE 

Il comando DELETE permette al server di eliminare un JSON con la sua chiave associata nel database. Il server, per inviare una richiesta di DELETE al database, deve inviare una stringa strutturata nel seguente modo: 

```
"delete" "tableName" "key"
```

Dove `"tableName"` è la tabella da cui eliminare il JSON, `"key"` è la chiave associata al JSON da eliminare. 

`200`: **OK**, se il JSON con la chiave vengono eliminato con successo dal database. 

`400`: **BAD REQUEST**, se la richiesta è strutturata male.

`404`: **NOT FOUND**, se la chiave `"key"` non esiste come chiave in `"tableName"`.

# GETLASTINDEX 

Il comando GETLASTINDEX permette al server di ottenere l'identificativo più grande di una tabella. Il server, per inviare una richiesta di GETLASTINDEX al database, deve inviare una stringa strutturata nel seguente modo: 

```
"getLastIndex" "tableName"
```

Dove `"tableName"` è la tabella in considerazione. 

Se la tabella è vuota ritorna `"0"`. 

`200`: **OK**, se la richiesta viene effettuato con successo. 

`400`: **BAD REQUEST**, se la richiesta è strutturata male.

`404`: **NOT FOUND**, se `"tableName"` non esiste nel database. 

# LOCK

Il comando LOCK permette al server di bloccare una risorsa del database per un cliente. Il server, per inviare una richiesta di LOCK al database, deve inviare una stringa strutturata nel seguente modo: 

```
"lock" "tableName" "nameKey" "user"
```

Dove `"tableName"` è la tabella della risorsa da bloccare, `"nameKey"` è il nome della risorsa, `"user"` è l'utente che cerca di effettuare il lock. 

`200`: **OK**, se il blocco della risorsa è stato effettuato con successo per `"user"`. 

`400`: **BAD REQUEST**, se la richiesta è strutturata male. 

`409`: **CONFLICT**, se la risorsa è già stata bloccata da un altro utente. 

# UNLOCK

Il comando UNLOCK premettere al server di rimuovere il blocco di una risorsa del database da un cliente. Il server, per inviare una richiesta di UNLOCK al database, deve inviare una stringa strutturata nel seguente modo: 

```
"unlock" "tableName" "nameKey" "user"
```

Dove `"tableName"` è la tabella della risorsa precedentemente bloccata, `"nameKey"` è il nome della risorsa, `"user"` è l'utente che aveva il lock. 

`200`: **OK**, se il blocco della risorsa viene rimossa con successo. 

`400`: **BAD REQUEST**, se la richiesta è strutturata male. 

`404`: **NOT FOUND**, se la risorsa non è bloccata, quindi non esiste un lock su questa risorsa. 