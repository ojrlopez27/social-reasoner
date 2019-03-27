package social.reasoner.model.nlu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oscarr on 6/5/16.
 */
public class Parameter{
    private String name;
    private boolean required;
    private List<Value> value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<Value> getValue() {
        return value;
    }

    public void setValue(List<Value> value) {
        this.value = value;
    }

    public String getValueString() {
        if( value != null && !value.isEmpty() ){
            return value.get(0).getEntity();
        }
        return "";
    }

    public void setValueString(String valueString) {
        Value val = new Value();
        val.setEntity( valueString );
        if( value == null ){
            value = new ArrayList<>();
        }
        value.add( val );
    }
}

