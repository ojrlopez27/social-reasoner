package social.reasoner.model.blackboard;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by oscarr on 4/29/16.
 */
public interface BlackboardListener {
    void updateModel(CopyOnWriteArrayList<String> states);
}
