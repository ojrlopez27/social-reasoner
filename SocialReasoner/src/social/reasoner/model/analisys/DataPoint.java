package social.reasoner.model.analisys;

/**
 * Created by oscarr on 2/15/17.
 */
public class DataPoint implements Comparable<DataPoint>{
    private String id;
    private String systemIntent;
    private String rapportScore;
    private String rapportLevel;
    private String rapportVariation;
    private String userCS;
    private String userSmiles;
    private String userGaze;
    private String sharedExp;
    private String numTurns;
    private String asnSystem;
    private String vsnSystem;
    private String sdSystem;
    private String qesdSystem;
    private String prSystem;
    private String ackSystem;
    private String rseSystem;
    private String asnUser;
    private String vsnUser;
    private String sdUser;
    private String qesdUser;
    private String prUser;
    private String ackUser;
    private String rseUser;
    private String wozerOutput;
    private String srOutput;
    private String sortedCS;
    private String sortedWithActivation;
    private String currentState;
    private String matchedStates;
    private String rawData;
    private String userUtterance = "";
    private String systemUtterance;

    public DataPoint(String id, String systemIntent, String rapportScore, String rapportLevel, String rapportVariation,
                     String userCS, String userSmiles, String userGaze, String sharedExp, String numTurns, String asnSystem,
                     String vsnSystem, String sdSystem, String qesdSystem, String prSystem, String ackSystem, String rseSystem,
                     String asnUser, String vsnUser, String sdUser, String qesdUser, String prUser, String ackUser,
                     String rseUser, String wozerOutput, String srOutput, String sortedCS, String sortedWithActivation,
                     String currentState, String matchedStates, String userUtterance, String systemUtterance, String raw) {
        this.id = id;
        this.systemIntent = systemIntent;
        this.rapportScore = rapportScore;
        this.rapportLevel = rapportLevel;
        this.rapportVariation = rapportVariation;
        this.userCS = userCS;
        this.userSmiles = userSmiles;
        this.userGaze = userGaze;
        this.sharedExp = sharedExp;
        this.numTurns = numTurns;
        this.asnSystem = asnSystem;
        this.vsnSystem = vsnSystem;
        this.sdSystem = sdSystem;
        this.qesdSystem = qesdSystem;
        this.prSystem = prSystem;
        this.ackSystem = ackSystem;
        this.rseSystem = rseSystem;
        this.asnUser = asnUser;
        this.vsnUser = vsnUser;
        this.sdUser = sdUser;
        this.qesdUser = qesdUser;
        this.prUser = prUser;
        this.ackUser = ackUser;
        this.rseUser = rseUser;
        this.wozerOutput = wozerOutput;
        this.srOutput = srOutput;
        this.sortedCS = sortedCS;
        this.sortedWithActivation = sortedWithActivation;
        this.currentState = currentState;
        this.matchedStates = matchedStates;
        this.userUtterance = userUtterance;
        this.systemUtterance = systemUtterance;
        this.rawData = raw;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSystemIntent() {
        return systemIntent;
    }

    public void setSystemIntent(String systemIntent) {
        this.systemIntent = systemIntent;
    }

    public String getRapportScore() {
        return rapportScore;
    }

    public void setRapportScore(String rapportScore) {
        this.rapportScore = rapportScore;
    }

    public String getRapportLevel() {
        return rapportLevel;
    }

    public void setRapportLevel(String rapportLevel) {
        this.rapportLevel = rapportLevel;
    }

    public String getRapportVariation() {
        return rapportVariation;
    }

    public void setRapportVariation(String rapportVariation) {
        this.rapportVariation = rapportVariation;
    }

    public String getUserCS() {
        return userCS;
    }

    public void setUserCS(String userCS) {
        this.userCS = userCS;
    }

    public String getUserSmiles() {
        return userSmiles;
    }

    public void setUserSmiles(String userSmiles) {
        this.userSmiles = userSmiles;
    }

    public String getUserGaze() {
        return userGaze;
    }

    public void setUserGaze(String userGaze) {
        this.userGaze = userGaze;
    }

    public String getSharedExp() {
        return sharedExp;
    }

    public void setSharedExp(String sharedExp) {
        this.sharedExp = sharedExp;
    }

    public String getNumTurns() {
        return numTurns;
    }

    public void setNumTurns(String numTurns) {
        this.numTurns = numTurns;
    }

    public String getAsnSystem() {
        return asnSystem;
    }

    public void setAsnSystem(String asnSystem) {
        this.asnSystem = asnSystem;
    }

    public String getVsnSystem() {
        return vsnSystem;
    }

    public void setVsnSystem(String vsnSystem) {
        this.vsnSystem = vsnSystem;
    }

    public String getSdSystem() {
        return sdSystem;
    }

    public void setSdSystem(String sdSystem) {
        this.sdSystem = sdSystem;
    }

    public String getQesdSystem() {
        return qesdSystem;
    }

    public void setQesdSystem(String qesdSystem) {
        this.qesdSystem = qesdSystem;
    }

    public String getPrSystem() {
        return prSystem;
    }

    public void setPrSystem(String prSystem) {
        this.prSystem = prSystem;
    }

    public String getAckSystem() {
        return ackSystem;
    }

    public void setAckSystem(String ackSystem) {
        this.ackSystem = ackSystem;
    }

    public String getRseSystem() {
        return rseSystem;
    }

    public void setRseSystem(String rseSystem) {
        this.rseSystem = rseSystem;
    }

    public String getAsnUser() {
        return asnUser;
    }

    public void setAsnUser(String asnUser) {
        this.asnUser = asnUser;
    }

    public String getVsnUser() {
        return vsnUser;
    }

    public void setVsnUser(String vsnUser) {
        this.vsnUser = vsnUser;
    }

    public String getSdUser() {
        return sdUser;
    }

    public void setSdUser(String sdUser) {
        this.sdUser = sdUser;
    }

    public String getQesdUser() {
        return qesdUser;
    }

    public void setQesdUser(String qesdUser) {
        this.qesdUser = qesdUser;
    }

    public String getPrUser() {
        return prUser;
    }

    public void setPrUser(String prUser) {
        this.prUser = prUser;
    }

    public String getAckUser() {
        return ackUser;
    }

    public void setAckUser(String ackUser) {
        this.ackUser = ackUser;
    }

    public String getRseUser() {
        return rseUser;
    }

    public void setRseUser(String rseUser) {
        this.rseUser = rseUser;
    }

    public String getWozerOutput() {
        return wozerOutput;
    }

    public void setWozerOutput(String wozerOutput) {
        this.wozerOutput = wozerOutput;
    }

    public String getSrOutput() {
        return srOutput;
    }

    public void setSrOutput(String srOutput) {
        this.srOutput = srOutput;
    }

    public String getSortedCS() {
        return sortedCS;
    }

    public void setSortedCS(String sortedCS) {
        this.sortedCS = sortedCS;
    }

    public String getSortedWithActivation() {
        return sortedWithActivation;
    }

    public void setSortedWithActivation(String sortedWithActivation) {
        this.sortedWithActivation = sortedWithActivation;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getMatchedStates() {
        return matchedStates;
    }

    public void setMatchedStates(String matchedStates) {
        this.matchedStates = matchedStates;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getUserUtterance() {
        return userUtterance;
    }

    public void setUserUtterance(String userUtterance) {
        this.userUtterance = userUtterance;
    }

    public String getSystemUtterance() {
        return systemUtterance;
    }

    public void setSystemUtterance(String systemUtterance) {
        this.systemUtterance = systemUtterance;
    }

    @Override
    public int compareTo(DataPoint other) {
        return this.wozerOutput.compareTo(other.getWozerOutput());
    }
}
