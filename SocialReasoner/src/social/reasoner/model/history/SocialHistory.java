package social.reasoner.model.history;


import social.reasoner.model.Constants;
import social.reasoner.model.blackboard.Blackboard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oscarr on 4/29/16.
 */
public class SocialHistory {
    protected List<CSItem> historyList;
    protected Blackboard blackboard;
    private static SocialHistory history;
    private final String THIS = "SocialHistory";

    protected SocialHistory(){
        historyList = new ArrayList<>();
        blackboard = Blackboard.getInstance();
    }

    public static SocialHistory getInstance(){
        if( history == null ){
            history = new SocialHistory();
        }
        return history;
    }

    public void add(long timestamp, String cs, String rapporLevel, double rapportScore){
        historyList.add(new CSItem(timestamp, getCS(cs), rapporLevel, rapportScore));
    }

    public void addStates(){
        boolean isACK = false, isPR = false, isVSN = false, isSD = false, isQESD = false, isRSE = false, isASN = false;
        if( !historyList.isEmpty() ){
            blackboard.removeMessagesContain("_HISTORY_SYSTEM");
            String previous = historyList.get( historyList.size() - 1).getCs();
            if( previous.equals(Constants.SD_SYSTEM_CS) ){
                blackboard.setStatesString( Constants.SD_HISTORY_SYSTEM, THIS );
                isSD = true;
            }else if( previous.equals( Constants.QESD_SYSTEM_CS)) {
                blackboard.setStatesString( Constants.QESD_HISTORY_SYSTEM, THIS );
                isQESD = true;
            }else if( previous.equals( Constants.ACK_SYSTEM_CS)) {
                blackboard.setStatesString( Constants.ACK_HISTORY_SYSTEM, THIS );
                isACK = true;
            }else if( previous.equals(Constants.RSE_SYSTEM_CS)) {
                blackboard.setStatesString( Constants.RSE_HISTORY_SYSTEM, THIS );
                isRSE = true;
            }else if( previous.equals(Constants.ASN_SYSTEM_CS)) {
                blackboard.setStatesString( Constants.ASN_HISTORY_SYSTEM, THIS );
                isASN = true;
            }else if( previous.equals(Constants.VSN_SYSTEM_CS)) {
                if( checkMultipleCSInARow( Constants.VSN_SYSTEM_CS ) ){
                    blackboard.setStatesString( Constants.SEVERAL_ASN_HISTORY_SYSTEM, THIS );
                }else{
                    blackboard.setStatesString( Constants.VSN_HISTORY_SYSTEM, THIS );
                }
                isVSN = true;
            }else if( previous.equals(Constants.PR_SYSTEM_CS)) {
                blackboard.setStatesString( Constants.PR_HISTORY_SYSTEM, THIS );
                isPR = true;
            }
            //we need at least n turns to analyze patterns in history
            if( historyList.size() > 0 ) {
                if (!isACK) {
                    blackboard.setStatesString(Constants.NOT_ACK_HISTORY_SYSTEM, THIS);
                }
                if (!isPR) {
                    blackboard.setStatesString(Constants.NOT_PR_HISTORY_SYSTEM, THIS);
                }
                if (!isVSN) {
                    blackboard.setStatesString(Constants.NOT_VSN_HISTORY_SYSTEM, THIS);
                }
                if (!isASN) {
                    blackboard.setStatesString(Constants.NOT_ASN_HISTORY_SYSTEM, THIS);
                }
                if (!isRSE) {
                    blackboard.setStatesString(Constants.NOT_RSE_HISTORY_SYSTEM, THIS);
                }
                if (!isQESD) {
                    blackboard.setStatesString(Constants.NOT_QESD_HISTORY_SYSTEM, THIS);
                }
                if (!isSD) {
                    blackboard.setStatesString(Constants.NOT_SD_HISTORY_SYSTEM, THIS);
                }
            }
        }
    }

    protected boolean checkMultipleCSInARow(String cs){
        int count = 0;
        for( int i = historyList.size() -1; i >= 0; i-- ){
            if( historyList.get(i).getCs().equals(cs) ) {
                count++;
            }else{
                break;
            }
        }
        return count > 1;
    }

    protected String getCS(String cs){
        if( "ASN".equals(cs) ){
            return Constants.ASN_SYSTEM_CS;
        }
        if( "VSN".equals(cs) ){
            return Constants.VSN_SYSTEM_CS;
        }
        if( "SD".equals(cs) ){
            return Constants.SD_SYSTEM_CS;
        }
        if( "QESD".equals(cs) ){
            return Constants.QESD_SYSTEM_CS;
        }
        if( "PR".equals(cs) ){
            return Constants.PR_SYSTEM_CS;
        }
        if( "RSE".equals(cs) ){
            return Constants.RSE_SYSTEM_CS;
        }
        if( "ACK".equals(cs) ){
            return Constants.ACK_SYSTEM_CS;
        }
        return cs;
    }

    public static void reset() {
        if( history != null ) {
            history.historyList.clear();
        }
    }
}

// Conversational Strategy
class CSItem {
    private long timestamp;
    private String cs;
    private String rapporLevel;
    private double rapportScore;

    public CSItem(long timestamp, String cs, String rapporLevel, double rapportScore) {
        this.timestamp = timestamp;
        this.cs = cs;
        this.rapporLevel = rapporLevel;
        this.rapportScore = rapportScore;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCs() {
        return cs;
    }

    public void setCs(String cs) {
        this.cs = cs;
    }

    public String getRapporLevel() {
        return rapporLevel;
    }

    public void setRapporLevel(String rapporLevel) {
        this.rapporLevel = rapporLevel;
    }

    public double getRapportScore() {
        return rapportScore;
    }

    public void setRapportScore(int rapportScore) {
        this.rapportScore = rapportScore;
    }

    @Override
    public String toString(){
        return cs + " : " + rapportScore;
    }
}