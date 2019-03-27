package social.reasoner.model.nlu;

import java.util.List;

/**
 * Created by oscarr on 6/5/16.
 */
public class Intent{
    private String intent;
    private double score;
    private List<Action> actions;

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
