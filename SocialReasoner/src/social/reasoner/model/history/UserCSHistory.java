package social.reasoner.model.history;

import social.reasoner.model.Constants;
import social.reasoner.model.blackboard.Blackboard;
import social.reasoner.control.MainController;

import java.util.ArrayList;

/**
 * Created by oscarr on 9/8/16.
 */
public class UserCSHistory extends SocialHistory {

    private static UserCSHistory history;
    private final String THIS = "UserCSHistory";

    private UserCSHistory(){
        historyList = new ArrayList<>();
        blackboard = Blackboard.getInstance();
    }

    public static UserCSHistory getInstance(){
        if( history == null ){
            history = new UserCSHistory();
        }
        return history;
    }

    public void add(){
        if( !MainController.isBeginningConversation ) {
            add(System.currentTimeMillis(), MainController.userConvStrategy, MainController.rapportLevel, MainController.rapportScore);
        }
    }

    @Override
    public void addStates(){
        boolean isACK = false, isPR = false, isVSN = false, isSD = false, isQESD = false, isRSE = false, isASN = false;
        if( !historyList.isEmpty() ){
            blackboard.removeMessagesContain("_HISTORY_USER");
            String previous = historyList.get( historyList.size() - 1).getCs();
            if( previous.equals(Constants.SD_USER_CS) ){
                blackboard.setStatesString( Constants.SD_HISTORY_USER, THIS );
                isSD = true;
            }else if( previous.equals( Constants.QESD_USER_CS)) {
                blackboard.setStatesString( Constants.QESD_HISTORY_USER, THIS );
                isQESD = true;
            }else if( previous.equals( Constants.ACK_USER_CS)) {
                blackboard.setStatesString( Constants.ACK_HISTORY_USER, THIS );
                isACK = true;
            }else if( previous.equals(Constants.RSE_USER_CS)) {
                blackboard.setStatesString( Constants.RSE_HISTORY_USER, THIS );
                isRSE = true;
            }else if( previous.equals(Constants.ASN_USER_CS)) {
                blackboard.setStatesString( Constants.ASN_HISTORY_USER, THIS );
                isASN = true;
            }else if( previous.equals(Constants.VSN_USER_CS)) {
                if( checkMultipleCSInARow( Constants.VSN_USER_CS ) ){
                    blackboard.setStatesString( Constants.SEVERAL_ASN_HISTORY_USER, THIS );
                }else{
                    blackboard.setStatesString( Constants.VSN_HISTORY_USER, THIS );
                }
                isVSN = true;
            }else if( previous.equals(Constants.PR_USER_CS)) {
                blackboard.setStatesString( Constants.PR_HISTORY_USER, THIS );
                isPR = true;
            }
            for( CSItem cs : historyList ){
                if( cs.getCs().equals( Constants.VSN_USER_CS) ){
                    blackboard.setStatesString( Constants.ONCE_VSN_HISTORY_USER, THIS );
                    break;
                }
            }
            //we need at least n turns to analyze patterns in history
            if( historyList.size() > 0 ) {
                if (!isACK) {
                    blackboard.setStatesString(Constants.NOT_ACK_HISTORY_USER, THIS);
                }
                if (!isPR) {
                    blackboard.setStatesString(Constants.NOT_PR_HISTORY_USER, THIS);
                }
                if (!isSD) {
                    blackboard.setStatesString(Constants.NOT_SD_HISTORY_USER, THIS);
                }
                if (!isQESD) {
                    blackboard.setStatesString(Constants.NOT_QESD_HISTORY_USER, THIS);
                }
                if (!isRSE) {
                    blackboard.setStatesString(Constants.NOT_RSE_HISTORY_USER, THIS);
                }
                if (!isASN) {
                    blackboard.setStatesString(Constants.NOT_ASN_HISTORY_USER, THIS);
                }
                if (!isVSN) {
                    blackboard.setStatesString(Constants.NOT_VSN_HISTORY_USER, THIS);
                }
            }
        }
    }

    @Override
    protected String getCS(String cs){
        if( "ASN".equals(cs) ){
            return Constants.ASN_USER_CS;
        }
        if( "VSN".equals(cs) ){
            return Constants.VSN_USER_CS;
        }
        if( "SD".equals(cs) ){
            return Constants.SD_USER_CS;
        }
        if( "QESD".equals(cs) ){
            return Constants.QESD_USER_CS;
        }
        if( "PR".equals(cs) ){
            return Constants.PR_USER_CS;
        }
        if( "RSE".equals(cs) ){
            return Constants.RSE_USER_CS;
        }
        if( "ACK".equals(cs) ){
            return Constants.ACK_USER_CS;
        }
        return cs;
    }

    public static void reset() {
        if( history != null ) {
            history.historyList.clear();
        }
    }

}
