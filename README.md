# Progetto Sistemi Distribuiti 2023-2024
Gruppo SuperDistributori

Il progetto consiste nella realizzazione di una applicazione web per l'acquisto e la gestione di domini web, è composto da tre parti:
- **Client web** : un'interfaccia per l'acquisto e il rinnovo di domini web. La comunicazione con il server avviene tramite API REST
- **Server Web** : implementa la logica dell'applicazione, comunica con il client esponendo delle API REST, comunica con il database attraverso un protocollo testuale custom tramite socket TCP.
- **Database**: Un database documentale con storage in memoria principale (ram) che gestice utenti registrati, domini e acquisti. Il database è generico e può essere riutilizzato per altri progetti senza modifiche. La comunicazione avviene tramite protocollo testuale custom su socket TCP. La configurazione iniziale avviene tramite lettura automatica, all'avvio, di file json posti nell'apposita cartella.

## Componenti del gruppo

* Daniele Besozzi (894998) <d.besozzi@campus.unimib.it>
* Le Yang Shi (894536) <l.shi1@campus.unimib.it> 
* Hicham Benbouzid (894680) <h.benbouzid@campus.unimib.it>

## Compilazione ed esecuzione

Sia il server Web sia il database sono applicazioni Java gestire con Maven. All'interno delle rispettive cartelle si può trovare il file `pom.xml` in cui è presente la configurazione di Maven per il progetto. Si presuppone l'utilizzo della macchina virtuale di laboratorio, per cui nel `pom.xml` è specificato l'uso di Java 21.

Il server Web e il database sono dei progetti Java che utilizano Maven per gestire le dipendenze, la compilazione e l'esecuzione.

### Client Web

Per avviare il client Web è necessario utilizzare l'estensione "Live Preview" su Visual Studio Code, come mostrato durante il laboratorio. Tale estensione espone un server locale con i file contenuti nella cartella `client-web`.

**Attenzione**: è necessario configurare CORS in Google Chrome come mostrato nel laboratorio.

### Server Web

Il server Web utilizza Jetty e Jersey. Si può avviare eseguendo `mvn jetty:run` all'interno della cartella `server-web`. Espone le API REST all'indirizzo `localhost` alla porta `8080`.

### Database

Il database è una semplice applicazione Java. Si possono utilizzare i seguenti comandi Maven:

* `mvn clean`: per ripulire la cartella dai file temporanei,
* `mvn compile`: per compilare l'applicazione,
* `mvn exec:java`: per avviare l'applicazione (presuppone che la classe principale sia `Main.java`). Si pone in ascolto all'indirizzo `localhost` alla porta `3030`.
