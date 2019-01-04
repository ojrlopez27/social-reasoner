package wef.articulab.model.nlu;

import java.util.List;

/**
 * Created by oscarr on 6/5/16.
 */
public class Action{
    private String name;
    private boolean triggered;
    private List<Parameter> parameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
}