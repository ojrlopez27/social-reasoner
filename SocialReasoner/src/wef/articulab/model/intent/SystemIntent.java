package wef.articulab.model.intent;

/**
 * Created by oscarr on 11/15/16.
 */
public class SystemIntent {
    private String intent;
    private String phase;

    public SystemIntent(String intent, String phase) {
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
}
