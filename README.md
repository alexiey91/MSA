# MSA
Publish/Subscribe Content Based System

Description:

1.1 La logica

Premessa

Il paradigma di interazione (connettore) publish-subscribe presenta caratteristiche interessanti per sistemi caratterizzati da una

elevata dinamicità, grazie all'elevato livello di disaccoppiamento che è in grado di garantire tra componenti.

In un sistema basato su tale paradigma, i componenti coinvolti possono ricoprire due ruoli (anche in modo non esclusivo):

produttore o consumatore di eventi. I componenti di tipo produttore generano eventi; quelli di tipo consumatore effettuano una

sottoscrizione per determinati insiemi di eventi, e ricevono tutti gli eventi generati appartenenti a quell'insieme.

La sottoscrizione può essere effettuata con varie modalità:

- topic-based;

- content-based;

- type-based.

Le modalità topic-based e type-based presuppongono che l'insieme totale di eventi generabili sia suddiviso in classi

(eventualmente organizzate in modo parzialmente gerarchico), e che la sottoscrizione di un consumatore specifichi la/e classe/i a

cui è interessato. In questo caso, può generalmente succedere che non tutti gli eventi generati in tale/i classe/i siano di effettivo

interesse per il consumatore. Una indicazione più fine si ottiene nella modalità content-based, in cui la sottoscrizione può

specificare un filtro (p.es., un predicato logico) rispetto al contenuto degli eventi generati.

Tale filtro riduce la quantità di eventi ricevuti da un sottoscrittore, ma comporta evidentemente un carico di lavoro aggiuntivo.

Servizio

Il servizio da sviluppare ha lo scopo di aggiungere una funzionalità di filtraggio content-based a un sistema che implementa un

connettore publish-subscribe in modalità topic-based, usando a tale scopo un event-service basato su architettura centralizzata.

Lo scenario d'uso in cui questo servizio opera è quindi costituito da un insieme di componenti che interagiscono usando il modello

publish-subscribe in modalità topic-based. Ogni componente può delegare al servizio-filtro il compito di selezionare in modalità

content-based gli eventi per cui ha effettuato una sottoscrizione.

Il servizio-filtro associato a un componente sottoscrittore riceve da esso la specificazione delle classi di eventi a cui è interessato e

del filtro da applicare a tali classi; raccoglie poi tutti gli eventi generati appartenenti a tali classi, e fa arrivare al sottoscrittore solo

quelli che soddisfano il filtro.

1.2 L’ambiente d’uso

L’ambiente in cui si immagina che operi questo servizio è costituito, in generale, da una piattaforma consistente di una

molteplicità di nodi mobili con vari livelli di disponibilità di risorse interne (memoria, cpu, ecc.), connessi tra loro da infrastrutture

di comunicazione di varia qualità. Su tali nodi sono in esecuzione componenti che svolgono il ruolo di

produttori/consumatori/event service/servizio-filtro.

2 LAVORO PROGETTUALE

Si richiede di progettare e realizzare, utilizzando una piattaforma adeguata, un prototipo del sistema indicato nella sezione

precedente. In particolare, il sistema dovrà includere i seguenti elementi:

- un adeguato insieme di categorie di eventi generabili dai produttori;

- un event-service che raccoglie gli eventi generati dai produttori e li inoltra ai sottoscrittori;

- componenti produttori;

- componenti consumatori;

- servizio filtro (una o più istanze, a seconda delle scelte progettuali).

Si richiede inoltre di definire una politica adattativa di gestione dell'istanziazione e localizzazione dei componenti del sistema e

delle loro modalità di interazione, che sia adeguata ad un ambiente mobile, caratterizzato da possibile scarsità di risorse per i

componenti in esecuzione su determinati nodi, e/o variazioni rilevanti della qualità delle risorse disponibili. La politica adattativa

dovrà essere progettata tenendo conto dei principi architetturali discussi nell'ambito del corso.

Inoltre, è richiesta una valutazione dell'adeguatezza della politica adattativa, confrontandola con una soluzione non adattativa, in

funzione dell'impatto risultante su misure di prestazione quali:

• traffico generato su rete;

• consumo di energia da parte di nodi mobili;

• carico computazionale/di memorizzazione per nodi mobili;

tenendo anche conto del fatto che il contesto (disponibilità di risorse) in cui opera il servizio può variare nel tempo, per esempio

per effetto della mobilità di alcuni nodi, e/o delle attività in essere sui vari nodi.

Tale adeguatezza dovrà essere argomentata nella relazione di accompagnamento, tramite (non necessariamente tutte):

- considerazioni empiriche;

- modelli matematici;

- misurazioni/simulazioni.

L'argomentazione dovrà essere basata anche su appropriate considerazioni riguardanti: quantità dei dati memorizzati/scambiati, carico computazionale per gestire le operazioni, ecc.