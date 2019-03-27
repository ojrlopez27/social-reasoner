package social.reasoner.control.emulators;

/**
 * Created by oscarr on 11/29/16.
 */
public class SystemIntentStep extends EmulationStep {
    private String intent;
    private String phase;


    public SystemIntentStep(String intent, String phase, double rapportScore, String userCS, boolean isSmiling,
                                boolean isGazeAtPartner) {
        super(rapportScore, userCS, isSmiling, isGazeAtPartner);
        this.intent = intent;
        this.phase = phase;
    }

    public SystemIntentStep(String intent, String phase, double rapportScore, String userCS, boolean isSmiling,
                                boolean isGazeAtPartner, boolean availableSE) {
        super(rapportScore, userCS, isSmiling, isGazeAtPartner, availableSE);
        this.intent = intent;
        this.phase = phase;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    @Override
    public String toString(){
        return "{[intent: " + intent + "], " +
                "[phase: " + phase + "], " +
                "[rapport score: " + rapportScore + "], " +
                "[user conv. strategy: " + userCS + "], " +
                "[is user smiling?: " + isSmiling+ "], " +
                "[is user looking at partner?: " + isGazeAtPartner+ "], " +
                "[are there available shared experiences?: " + availableSharedExperiences + "]}";


    }
}
