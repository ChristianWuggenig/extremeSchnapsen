package cardfactory.com.extremeschnapsen;

public class RoundPoints {

    private long roundpointsID;
    private int currentroundpoints;
    private int pointsplayer1;
    private int pointsplayer2;

    public RoundPoints (long roundpointsID, int currentroundpoints, int pointsplayer1, int pointsplayer2){
        this.roundpointsID = roundpointsID;
        this.currentroundpoints = currentroundpoints;
        this.pointsplayer1 = pointsplayer1;
        this.pointsplayer2 = pointsplayer2;
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

    public String toString(){
        String output = roundpointsID + " " + currentroundpoints + " " + pointsplayer1 + " " + pointsplayer2;
        return output;
    }

}


