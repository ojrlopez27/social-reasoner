package wef.articulab.control.bn;

import java.util.Collection;
import java.util.List;

/**
 * Created by oscarr on 5/13/16.
 */
public interface BehaviorInterface {
    String getDescription();

    List<String> getAddGoals();
    List <String> getAddList();
    void setAddList(List <String> addList);
    List <String> getDeleteList();
    void setDeleteList(List <String> deleteList);
    double getActivation();
    void setActivation(double activation);
    Collection getPreconditions();
    int getId();
    boolean getExecutable();
    String getName();
    boolean getActivated();
    void setActivated(boolean a);
    boolean isSuccesor(String proposition);
    boolean isInhibition(String proposition);
    void setAddList(String proposition);
    boolean isPrecondition(String proposition);
    double calculateInputFromState(List<String> states, int[] matchedStates, double phi);
    double calculateInputFromGoals(List<String> goals, int[] achievedPropositions, double gamma);
    double calculateTakeAwayByProtectedGoals(List<String> goalsR, int[] undoPropositions, double delta);
    boolean isExecutable (List <String> states);
    void resetActivation(boolean reset);
    void updateActivation(double act);
    void decay(double factor);
    void setId(int id);
}
