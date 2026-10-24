// Pong — Programmazione Orientata agli Oggetti e Laboratori, Udine, a.a. 2026-27
// Consegna 1: il Punteggio. Scadenza: domenica 25 ottobre 2026.
package pong;

import java.util.ArrayList;
import java.util.List;

/**
 * I conti di una partita: chi ha segnato, quanti punti ha ciascun lato, chi ha vinto.
 * La Partita gli dice chi ha fatto punto; lo Schermo chiede i punti alla Partita, che li chiede a lui.
 *
 * La regola per vincere: un lato vince quando ha almeno {@code puntiPerVincere} punti e almeno
 * {@code scartoMinimo} punti più dell'altro. Esempi, con 11 punti e scarto 2: 11 a 9 vince;
 * 11 a 10 si continua; 12 a 10 vince. Con scarto 1 vince chi arriva per primo a 11.
 *
 * IL PEZZO MANCANTE: i corpi dei metodi qui sotto (e i campi che vi servono).
 * Ogni metodo ha il suo contratto, nel commento sopra: cosa pretende, cosa promette.
 * I test ufficiali (cartella test) controllano i contratti.
 */
public class Punteggio {

    // scrivete qui i campi che vi servono
    private final int puntiPerVincere;
    private final int scartoMinimo;
    private int puntiSinistra;
    private int puntiDestra;
    private boolean finita;
    private Lato vincitore;
    private final List<Lato> storia; 

    
    /**
     * Una partita nuova, 0 a 0, con la sua regola per vincere.
     * Pretende: puntiPerVincere è almeno 1; scartoMinimo è almeno 1.
     * Promette: una partita 0 a 0, non finita, senza vincitore e con la storia vuota.
     */
    public Punteggio(int puntiPerVincere, int scartoMinimo) {

        // controllo delle precondizioni
        if ( puntiPerVincere < 1 || scartoMinimo < 1 ) throw new IllegalArgumentException("puntiPerVincere deve essere almeno 1");
        
        // se le precondizioni vengono rispettate
        // i campi vengono inizializzati come da contratto
        this.puntiPerVincere = puntiPerVincere;
        this.scartoMinimo = scartoMinimo;
        this.puntiSinistra = 0;
        this.puntiDestra = 0;
        this.finita = false;
        this.vincitore = null;
        this.storia = new ArrayList<>();

    }


    /**
     * Il lato chi ha fatto un punto.
     * Pretende: chi non è null.
     * Promette: se la partita non era finita, il lato chi ha un punto in più, l'altro lato non
     *           cambia e il punto si aggiunge in fondo alla storia; se con questo punto il lato chi
     *           vince (regola in cima alla classe), la partita è finita e il vincitore è chi.
     *           Se la partita era già finita, non cambia niente.
     */
    public void segnaPunto(Lato chi) {

        if( chi == null ) throw new IllegalArgumentException("chi non può essere null");
        if( finita ) return;

        if( chi == Lato.DESTRA ) puntiDestra++;
        else puntiSinistra++;

        storia.add(chi);
        
        // fa un controllo su chi ha vinto col metodo ausiliario privato haVinto()
        // registra che la partita è finita e che il vincitore è "chi"
        if( haVinto(chi) ){
            finita = true;
            vincitore = chi;
        }

    }




    /**
     * Pretende: chi non è null.
     * Promette: i punti del lato chi. Non cambia niente.
     */
    public int getPunti(Lato chi) {
        if( chi == null ) throw new IllegalArgumentException("chi non può essere null");
        return chi == Lato.SINISTRA ? puntiSinistra : puntiDestra;
    }

    /**
     * Promette: vero se la partita è finita, cioè se un lato ha vinto. Non cambia niente.
     */
    public boolean isFinita() {
        return finita; 
    }

    /**
     * Promette: il lato che ha vinto; null se la partita non è finita. Non cambia niente.
     */
    public Lato getVincitore() {
        return vincitore; 
    }

    /**
     * L'arbitro ci ripensa: toglie l'ultimo punto della storia (l'ultimo segnato e non già annullato).
     * Pretende: niente.
     * Promette: se nella storia c'è almeno un punto, l'ultimo punto viene tolto, dai conti e dalla
     *           storia, come se non fosse mai stato segnato. Se era il punto che aveva chiuso la
     *           partita, la partita torna in corso, senza vincitore. Se la storia è vuota, non cambia
     *           niente.
     */
    public void annullaUltimoPunto() {

        if( storia.isEmpty() ) return;
        Lato chi = storia.remove(storia.size() - 1); // togli l'ultimo elemento(punto segnato) dalla storia
        if (chi == Lato.SINISTRA) puntiSinistra--;
        else puntiDestra--;

        // ritorno allo status precedente l'annullamento del punto
        finita = false;
        vincitore = null;

        if (haVinto(Lato.SINISTRA)) {
            finita = true;
            vincitore = Lato.SINISTRA;
        } else if (haVinto(Lato.DESTRA)) {
            finita = true;
            vincitore = Lato.DESTRA;
        }

    }

    /**
     * La storia dei punti, dal primo all'ultimo: per ogni punto segnato (e non annullato), il lato
     * che l'ha segnato.
     * Pretende: niente.
     * Promette: una lista con i punti in ordine. Chi riceve la lista non può cambiare il Punteggio
     *           attraverso di lei: aggiungere, togliere o cambiare elementi della lista ricevuta
     *           (se la lista lo permette) non cambia i punti, il vincitore né la storia del Punteggio.
     *           Non promette altro: la lista ricevuta può restare com'era o mostrare anche i punti
     *           segnati dopo; chi la usa non deve contare né sull'una né sull'altra cosa.
     */
    public List<Lato> getStoria() {
        // List.copyOf crea una copia che non si può cambiare
        // chi la riceve non può toccare la storia vera del Punteggio.
        return List.copyOf(storia);
    }

    /**
     * Una partita nuova con la stessa regola.
     * Pretende: niente.
     * Promette: 0 a 0, non finita, senza vincitore, storia vuota; la regola per vincere non cambia.
     */
    public void azzera() {
        puntiSinistra = 0;
        puntiDestra = 0;
        finita = false;
        vincitore = null;
        storia.clear();
    }


    // METODO PRIVATO AUSILIARIO
    private boolean haVinto(Lato chi) {

        int miei;   // i punti del lato che stiamo controllando
        int altri;  // i punti dell'altro lato

        // controlla il lato SINISTRA
        //  se chi è a sinistra, allora "miei" sono i punti di sinistra
        //  e "altri" sono i punti di destra (l'altro lato)
        // (vice versa per la DESTRA)
        if ( chi == Lato.SINISTRA ){
            miei = puntiSinistra;
            altri = puntiDestra;
        }else{
            miei = puntiDestra;
            altri = puntiSinistra;
        }

        // Regole per vincere (tutte e 2 devono essere vere)
        // returna true solo se il lato chi ha sia raggiunto i puntiPerVincere sia
        // un vantaggio almeno pari a scartoMinimo
        return miei >= puntiPerVincere && miei - altri >= scartoMinimo;
    }


}
