package wef.articulab.control.emulators;

import wef.articulab.model.Constants;

/**
 * Created by oscarr on 9/12/16.
 */
public class EmulationStep {
    protected double rapportScore;
    protected String userCS;
    protected boolean isSmiling;
    protected boolean isGazeAtPartner;
    protected boolean availableSharedExperiences;

    public EmulationStep(double rapportScore, String userCS, boolean isSmiling, boolean isGazeAtPartner) {
        this.rapportScore = rapportScore;
        this.userCS = convertCS( userCS );
        this.isSmiling = isSmiling;
        this.isGazeAtPartner = isGazeAtPartner;
    }

    private String convertCS(String userCS) {
        if( userCS.equals("SD") )
            return Constants.SD_USER_CS;
        else if( userCS.equals("RSE") )
            return Constants.RSE_USER_CS;
        else if( userCS.equals("PR") )
            return Constants.PR_USER_CS;
        else if( userCS.equals("VSN") )
            return Constants.VSN_USER_CS;
        else if( userCS.equals("ASN") )
            return Constants.ASN_USER_CS;
        else if( userCS.equals("ACK") )
            return Constants.ACK_USER_CS;
        else if( userCS.equals("QESD") )
            return Constants.QESD_USER_CS;
        else if( userCS.equals("ACK") )
            return Constants.ACK_USER_CS;
        System.err.println("\n\n************** USER CONVERSATIONAL STRATEGY WASNT RECOGNIZED ******");
        return userCS;
    }

    public EmulationStep(double rapportScore, String userCS, boolean isSmiling, boolean isGazeAtPartner, boolean availableSE) {
        this(rapportScore, userCS, isSmiling, isGazeAtPartner);
        this.availableSharedExperiences = availableSE;
    }

    public boolean isAvailableSharedExperiences() {
        return availableSharedExperiences;
    }

    public void setAvailableSharedExperiences(boolean availableSharedExperiences) {
        this.availableSharedExperiences = availableSharedExperiences;
    }

    public double getRapportScore() {
        return rapportScore;
    }

    public void setRapportScore(int rapportScore) {
        this.rapportScore = rapportScore;
    }

    public String getUserCS() {
        return userCS;
    }

    public void setUserCS(String userCS) {
        this.userCS = userCS;
    }

    public boolean isSmiling() {
        return isSmiling;
    }

    public void setIsSmiling(boolean isSmiling) {
        this.isSmiling = isSmiling;
    }

    public boolean isGazeAtPartner() {
        return isGazeAtPartner;
    }

    public void setIsGazeAtPartner(boolean isGazeAtPartner) {
        this.isGazeAtPartner = isGazeAtPartner;
    }
}
