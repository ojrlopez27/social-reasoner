package social.reasoner.model.nlu;

import java.util.List;

/**
 * Created by oscarr on 6/5/16.
 */
public class NLUOutput {
    private String query;               //用戶說的話
    private List<Intent> intents;       //用戶的意圖
    private List<Entity> entities;      //包含的關鍵字

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<Intent> getIntents() {
        return intents;
    }

    public void setIntents(List<Intent> intents) {
        this.intents = intents;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }
}
