# Progetto Sistemi Distribuiti 2023-2024 - TCP

Documentare qui il protocollo su socket TCP che espone il database.

Come scritto anche nel documento di consegna del progetto, si ha completa libertà su come implementare il protoccolo e i comandi del database. Alcuni suggerimenti sono:

1. Progettare un protocollo testuale (tipo HTTP), è più semplice da implementare anche se meno efficiente.
2. Dare un'occhiata al protocollo di [Redis](https://redis.io/docs/reference/protocol-spec/). Si può prendere ispirazione anche solo in alcuni punti.

Di solito il protoccolo e i comandi del database sono due cose diverse. Tuttavia per il progetto, per evitare troppa complessità, si può documentare insieme il protocollo e i comandi implementati nel database.

La documentazione può variare molto in base al tipo di protocollo che si vuole costruire:

* Se è un protocollo testuale simile a quello di Redis, è necessario indicare il formato delle richieste e delle risposte, sia dei comandi sia dei dati.

* Se è un protocollo binario, è necessario specificare bene il formato di ogni pacchetto per le richieste e per le risposte, come vengono codificati i comandi e i dati.