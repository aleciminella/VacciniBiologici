# VacciniBiologici

Applicazione Android sviluppata in Kotlin per supportare, in modo didattico, la valutazione delle raccomandazioni vaccinali in pazienti candidati a terapia biologica o già in trattamento.

Il progetto non sostituisce il giudizio clinico: rappresenta le indicazioni come regole logiche semplificate, utili per ragionare su scenari clinici simulati e discutere il processo decisionale durante la presentazione.

## Obiettivi del progetto

- Inserire dati clinici essenziali del paziente.
- Validare età e coerenza delle condizioni selezionate.
- Calcolare raccomandazioni vaccinali in base a terapia, età, storia vaccinale.
- Separare i vaccini per stato: raccomandato, possibile, da rimandare/valutare, controindicato.
- Organizzare il risultato come piano temporale rispetto alla terapia biologica.
- Salvare scenari clinici anonimi/simulati in locale tramite Room.

## Flusso principale

1. L'utente seleziona la terapia biologica.
2. Inserisce l'età del paziente.
3. Indica se la documentazione vaccinale è completa, incompleta o non disponibile.
4. Seleziona eventuali condizioni cliniche o patologie concomitanti.
5. Avvia il calcolo delle raccomandazioni.
6. L'app mostra una sintesi e un dettaglio ordinato per momento clinico.
7. Gli scenari possono essere salvati localmente per confronto e discussione.

## Significato della documentazione vaccinale

Il campo non indica se vaccinare automaticamente il paziente. Serve a capire quanto sono affidabili le informazioni sui vaccini già eseguiti.

- Completa e documentata: le raccomandazioni si concentrano su richiami, età e condizioni di rischio.
- Incompleta: l'app suggerisce di verificare o completare cicli e richiami se necessario.
- Non disponibile/non documentata: l'app usa un approccio prudente e segnala la necessità di controllare documentazione, anamnesi o sierologia prima di decidere.

## Architettura

Il progetto è diviso in moduli logici:

- `model`: strutture dati cliniche, vaccini, raccomandazioni e scenari.
- `data`: catalogo vaccini e repository degli scenari clinici.
- `data/local`: database Room, entity e DAO.
- `rules`: validazione input e motore delle raccomandazioni.
- `ui`: schermata Compose per inserimento dati, risultati e piano vaccinale.

Questa separazione rende il progetto più modulare e facilita l'aggiunta di nuove regole, nuovi vaccini o nuove schermate.

## Regole cliniche implementate

Le regole considerano:

- terapia biologica o immunosoppressione;
- terapia pianificata ma non ancora iniziata;
- età del paziente;
- documentazione vaccinale;
- gravidanza;
- immunodeficienza severa;
- comorbilità cardio-polmonari, metaboliche, renali, epatiche;
- asplenia o deficit funzionale splenico;
- vaccini vivi attenuati.

I vaccini vivi attenuati sono trattati con particolare prudenza: durante immunosoppressione o gravidanza vengono indicati come controindicati o da evitare, mentre prima dell'inizio della terapia possono richiedere verifica specialistica.

## Fonti considerate

Il progetto rappresenta in forma semplificata indicazioni derivate da:

- linee guida EULAR;
- CDC Adult Immunization Schedule;
- linee guida nazionali;
- raccomandazioni di società scientifiche.

Le fonti sono riportate anche nelle singole raccomandazioni vaccinali mostrate dall'app.

## Test

Sono presenti test unitari sulla parte logica:

- validazione dell'età;
- incoerenze tra condizioni cliniche e dati inseriti;
- raccomandazioni principali per pazienti immunosoppressi;
- gestione dei vaccini vivi attenuati;
- gestione della storia vaccinale non documentata;
- priorità in presenza di condizioni di rischio.

Comando utile:

```bash
./gradlew :app:testDebugUnitTest
```

## Limiti

L'app è un progetto universitario e non un dispositivo medico. Le raccomandazioni generate sono pensate per scenari didattici, non per decisioni cliniche reali.

Per un utilizzo clinico reale servirebbero aggiornamento continuo delle fonti, validazione specialistica, gestione dettagliata dei calendari vaccinali e integrazione con dati sanitari certificati.
