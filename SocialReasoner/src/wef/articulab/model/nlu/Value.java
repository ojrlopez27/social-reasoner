package wef.articulab.model.nlu;

/**
 * Created by oscarr on 6/13/16.
 */
public class Value {
    private String entity;
    private String type;
    private double score;

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
