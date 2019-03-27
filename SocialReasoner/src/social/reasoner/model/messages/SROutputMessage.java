package social.reasoner.model.messages;

import social.reasoner.model.intent.SystemIntent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oscarr on 6/3/16.
 */
public class SROutputMessage {
    private String phase;
    private List<Intent> intents;
    private double rapport;
    private Fields fields;

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public List<Intent> getIntents() {
        return intents;
    }

    public void setIntents(List<Intent> intents) {
        this.intents = intents;
    }

    public double getRapport() {
        return rapport;
    }

    public void setRapport(double rapport) {
        this.rapport = rapport;
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

    //FIXME
    public void addIntent(SystemIntent st, String[] conversationalStrategy) {
        if( intents == null ){
            intents = new ArrayList<>();
        }
        intents.add( new Intent( st.getIntent(), conversationalStrategy) );
    }

    public void addFields(String recommendationType, List results) {
        fields = new Fields( recommendationType, results );
    }

    //FIXME
//    public void addIntentNone(State st) {           //Social Reasoner off, all CS "NONE"
//        if( intents == null ){
//            intents = new ArrayList<>();
//        }
//        intents.add( new Intent( st.name, noneString ) );
//    }
}

class Intent{
    private String intent;
    private String[] conversational_strategies;

    public String getIntent() {
        return intent;
    }

    public Intent(String intent, String[] conversational_strategies) {
        this.intent = intent;
        this.conversational_strategies = conversational_strategies;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String[] getConversational_strategies() {
        return conversational_strategies;
    }

    public void setConversational_strategies(String[] conversational_strategies) {
        this.conversational_strategies = conversational_strategies;
    }
}

class Fields{
    private String category;
    private Object results;

    public Fields(String category, Object results) {
        this.category = category;
        this.results = results;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Object getResults() {
        return results;
    }

    public void setResults(Object results) {
        this.results = results;
    }
}