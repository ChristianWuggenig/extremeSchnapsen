package cardfactory.com.extremeschnapsen;

public class RoundPoints {

    private long roundpointsID;
    private int currentroundpoints;
    private int pointsplayer1;
    private int pointsplayer2;
    private int rundenphase; // 0 Normal, 1 Schlussphase, 2 Zugedreht
    private int trumpExchanged; // 0 für nein, 1 für ja;
    // bei den Jokern gilt -> 0 wurde noch nicht gespielt; 1 wurde gespielt
    private int sightJokerPlayer1; //0 -> noch keinen gezogen
    private int sightJokerPlayer2; //1 -> bereits gespielt
    private int parrySightJokerPlayer1;
    private int parrySightJokerPlayer2;
    private int cardExchangeJokerPlayer1;
    private int cardExchangeJokerPlayer2;



    public RoundPoints (long roundpointsID, int currentroundpoints, int pointsplayer1, int pointsplayer2){
        this.roundpointsID = roundpointsID;
        this.currentroundpoints = currentroundpoints;
        this.pointsplayer1 = pointsplayer1;
        this.pointsplayer2 = pointsplayer2;
        this.rundenphase = 0;
        this.trumpExchanged = 0;
        this.sightJokerPlayer1 = 0;
        this.sightJokerPlayer2 = 0;
        this.parrySightJokerPlayer1 = 0;
        this.parrySightJokerPlayer2 = 0;
        this.cardExchangeJokerPlayer1 = 0;
        this.cardExchangeJokerPlayer2 = 0;
    }

    public RoundPoints(long roundpointsID, int currentroundpoints, int pointsplayer1, int pointsplayer2,
                       int rundenphase, int trumpExchanged, int sightJokerPlayer1,
                       int sightJokerPlayer2, int parrySightJokerPlayer1, int parrySightJokerPlayer2,
                       int cardExchangeJokerPlayer1, int cardExchangeJokerPlayer2) {

        this.roundpointsID = roundpointsID;
        this.currentroundpoints = currentroundpoints;
        this.pointsplayer1 = pointsplayer1;
        this.pointsplayer2 = pointsplayer2;
        this.rundenphase = rundenphase;
        this.trumpExchanged = trumpExchanged;
        this.sightJokerPlayer1 = sightJokerPlayer1;
        this.sightJokerPlayer2 = sightJokerPlayer2;
        this.parrySightJokerPlayer1 = parrySightJokerPlayer1;
        this.parrySightJokerPlayer2 = parrySightJokerPlayer2;
        this.cardExchangeJokerPlayer1 = cardExchangeJokerPlayer1;
        this.cardExchangeJokerPlayer2 = cardExchangeJokerPlayer2;
    }

    public long getRoundpointsID() {
        return roundpointsID;
    }

    public void setRoundpointsID(long roundpointsID) {
        this.roundpointsID = roundpointsID;
    }

    public int getCurrentroundpoints() {
        return currentroundpoints;
    }

    public void setCurrentroundpoints(int currentroundpoints) {
        this.currentroundpoints = currentroundpoints;
    }

    public int getPointsplayer1() {
        return pointsplayer1;
    }

    public void setPointsplayer1(int pointsplayer1) {
        this.pointsplayer1 = pointsplayer1;
    }

    public int getPointsplayer2() {
        return pointsplayer2;
    }

    public void setPointsplayer2(int pointsplayer2) {
        this.pointsplayer2 = pointsplayer2;
    }

    public void updatePlayer1Points(int pointsToAdd){
        this.pointsplayer1 += pointsToAdd;
    }

    public void updatePlayer2Points(int pointsToAdd){
        this.pointsplayer2 += pointsToAdd;
    }

    public int getRundenphase() {
        return rundenphase;
    }

    public void setRundenphase(int rundenphase) {
        this.rundenphase = rundenphase;
    }

    public int getTrumpExchanged() {
        return trumpExchanged;
    }

    public void setTrumpExchanged(int trumpExchanged) {
        this.trumpExchanged = trumpExchanged;
    }

    public int getSightJokerPlayer1() {
        return sightJokerPlayer1;
    }

    public void setSightJokerPlayer1(int sightJokerPlayer1) {
        this.sightJokerPlayer1 = sightJokerPlayer1;
    }

    public int getSightJokerPlayer2() {
        return sightJokerPlayer2;
    }

    public void setSightJokerPlayer2(int sightJokerPlayer2) {
        this.sightJokerPlayer2 = sightJokerPlayer2;
    }

    public int getParrySightJokerPlayer1() {
        return parrySightJokerPlayer1;
    }

    public void setParrySightJokerPlayer1(int parrySightJokerPlayer1) {
        this.parrySightJokerPlayer1 = parrySightJokerPlayer1;
    }

    public int getParrySightJokerPlayer2() {
        return parrySightJokerPlayer2;
    }

    public void setParrySightJokerPlayer2(int parrySightJokerPlayer2) {
        this.parrySightJokerPlayer2 = parrySightJokerPlayer2;
    }

    public int getCardExchangeJokerPlayer1() {
        return cardExchangeJokerPlayer1;
    }

    public void setCardExchangeJokerPlayer1(int cardExchangeJokerPlayer1) {
        this.cardExchangeJokerPlayer1 = cardExchangeJokerPlayer1;
    }

    public int getCardExchangeJokerPlayer2() {
        return cardExchangeJokerPlayer2;
    }

    public void setCardExchangeJokerPlayer2(int cardExchangeJokerPlayer2) {
        this.cardExchangeJokerPlayer2 = cardExchangeJokerPlayer2;
    }

    public String toString(){
        String output = roundpointsID + " " + currentroundpoints + " " + pointsplayer1 + " " + pointsplayer2 + " " +
                rundenphase + " " + trumpExchanged + " " + sightJokerPlayer1 + " " + sightJokerPlayer2 + " " +
                parrySightJokerPlayer1 + " " + parrySightJokerPlayer2 + " " + cardExchangeJokerPlayer1 + " "
                + cardExchangeJokerPlayer2;
        return output;
    }

}


