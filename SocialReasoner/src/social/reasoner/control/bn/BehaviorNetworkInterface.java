package social.reasoner.control.bn;

import java.util.Collection;
import java.util.List;

/**
 * Created by oscarr on 5/13/16.
 */
public interface BehaviorNetworkInterface {
    List<BehaviorInterface> getModules();
    Double[] getActivations();
    int getIdxBehActivated();
    BehaviorInterface getBehaviorActivated();
    String getNameBehaviorActivated();
    void setModules(List<BehaviorInterface> modules);
    void setModules(List<BehaviorInterface> modules, int size);
    double getPi();
    void setPi(double pi);
    double getTheta();
    void setTheta(double theta);
    double getInitialTheta();
    void setInitialTheta(double itheta);
    double getPhi();
    void setPhi(double phi);
    double getGamma();
    void setGamma(double gamma);
    double getDelta();
    void setDelta(double delta);
    Collection<String> getState ();
    void setState(List<String> states);
    void addState(List<String> states);
    Collection<String> getGoals ();
    void setGoals(List<String> goals);
    Collection<String> getGoalsR ();
    void setGoalsR(List<String> goalsR);
    boolean executable (int i);
    List<BehaviorInterface> matchProposition (String proposition);
    List<BehaviorInterface> achieveProposition (String proposition);
    List<BehaviorInterface> undoProposition (String proposition);
    List<BehaviorInterface> undoPropositionState (String proposition, int indexBehx);
    void computeActivation ();
    void computeLinks ();
    double[] spreadsForward(int indexBehavior, boolean executable);
    double[] spreadsBackward(int indexBehavior, boolean executable);
    double[] takesAway(int indexBehavior);
    void applyDecayFx ();
    int activateBehavior ();
    void execute(int behIndex);
    void protectGoals(BehaviorInterface beh);
    void updateActivation();
    void reset();
    int selectBehavior();
}
