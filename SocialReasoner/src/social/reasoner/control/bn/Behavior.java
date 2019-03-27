package social.reasoner.control.bn;

import java.util.List;
import java.util.Vector;
import java.util.Collection;

/**
 * A competence module i can be described by a tuple (ci ai di zi). Where: 
 * ci is a list of preconditions which have to be full-filled before the 
 * competence module can become active. ai and di represent the expected 
 * effects of the competence module's action in terms of an add-list and a 
 * delete-list. In addition, each competence module has a level of activation zi
 *
 * @author oromero
 *
 */
public class Behavior implements BehaviorInterface{
	private String name;
	private List <String> preconditions = new Vector <String>();
	private List <String> addList = new Vector <String>();
	private List <String> deleteList = new Vector <String>();
	private double activation = 0, priorActivation = 0;
	private int id;
	private boolean executable = false, activated = false, activatedPrior;
	private String description;
    private List<String> addGoals = new Vector<>();

	public Behavior() {}

	public Behavior(String name, String[] preconds, String[] addlist, String[] deletelist){
		this.name = name;
		for(int i = 0; preconds != null && i < preconds.length; i++)
			preconditions.add(preconds[i]);
		for(int i = 0; addlist != null && i < addlist.length; i++)
			addList.add(addlist[i]);
		for(int i = 0; deletelist != null && i < deletelist.length; i++)
			deleteList.add(deletelist[i]);
	}

    public Behavior(String name, String description, String[] preconds, String[] addlist, String[] deletelist){
        this(name, preconds, addlist, deletelist);
        this.description = description;
    }

    public Behavior(String name, String description, String[] preconds, String[] addlist, String[] deletelist, String[] addGoals){
        this(name, description, preconds, addlist, deletelist);
        this.description = description;
        for(int i = 0; addGoals != null && i < addGoals.length; i++)
            this.addGoals.add(addGoals[i]);

    }

    public String getDescription() {
        return description;
    }

	public List<String> getAddGoals() {
		return addGoals;
	}
	public List <String> getAddList() {
		return addList;
	}
	public void setAddList(List <String> addList) {
		this.addList = addList;
	}
	public List <String> getDeleteList() {
		return deleteList;
	}
	public void setDeleteList(List <String> deleteList) {
		this.deleteList = deleteList;
	}
	public double getActivation() {
		return activation;
	}
	public void setActivation(double activation) {
		this.activation = activation;
	}
	public double getPriorActivation() {
		return priorActivation;
	}
	public void setPriorActivation(double activation) {
		this.priorActivation = activation;
	}
	public boolean getActivatedPrior() {
		return activatedPrior;
	}
	public void setActivationPrior(boolean activated) {
		this.activatedPrior = activated;
	}
	public Collection<String> getPreconditions() {
		if(preconditions != null && preconditions.size() > 0)
			return preconditions;
		return new Vector<String>();
	}
	public void setPreconditions(List <String> preconditions) {
		this.preconditions = preconditions;
	}
	public int getId(){
		return this.id;
	}
	public boolean getExecutable(){
		return executable;
	}
	public String getName(){
		return name;
	}
	public boolean getActivated(){
		return activated;
	}
	public void setActivated(boolean a){
		activatedPrior = activated;
		activated = a;
	}

	public void setPrecondition(String proposition){
		preconditions.add(proposition);
	}

	/**
	 * Determines if is into the add-list
	 * @param proposition
	 * @return
	 */
	public boolean isSuccesor(String proposition){
		if(addList.contains(proposition) == true)
			return true;
		return false;
	}

	/**
	 * Determines if is into the delete-list
	 * @param proposition
	 * @return
	 */
	public boolean isInhibition(String proposition){
		if(deleteList.contains(proposition) == true)
			return true;
		return false;
	}
	public void setAddList(String proposition){
		addList.add(proposition);
	}

	/**
	 * Determines if is into the preconditions set
	 * @param proposition
	 * @return
	 */
	public boolean isPrecondition(String proposition){
		if(preconditions.contains(proposition))
			return true;
		return false;
	}

	/**
	 * the input of activation to module x from the state at time t is
	 * @param states
	 * @param matchedStates
	 * @param phi
	 * @return
	 */
	public double calculateInputFromState(List<String> states, int[] matchedStates, double phi){
		double activation = 0;
		for(int i = 0; i < preconditions.size(); i++){
			int index = states.indexOf(preconditions.get(i));
			if(index != -1){
				double temp = phi * (1.0d / (double) matchedStates[index]) * (1.0d / (double) preconditions.size());
				activation += temp;
				//System.out.println("state gives "+ this.name +" an extra activation of "+temp);
			}
		}
		return activation;
	}

	/**
	 * The input of activation to competence module x from the goals at time t is
	 * @param goals
	 * @param achievedPropositions
	 * @param gamma
	 * @return
	 */
	public double calculateInputFromGoals(List<String> goals, int[] achievedPropositions, double gamma){
		double activation = 0;
		for(int i = 0; i < addList.size(); i++){
			int index = goals.indexOf(addList.get(i));
			if(index != -1){
				double temp = gamma * (1.0d / (double) achievedPropositions[index]) * (1.0d / (double) addList.size());
				activation += temp;
				//System.out.println("goals give "+ this.name +" an extra activation of "+temp);
			}
		}
		return activation;
	}

	/**
	 * The removal of activation from competence module x by the goals that are protected
	 * at time t is.
	 * @param goalsR
	 * @param undoPropositions
	 * @param delta
	 * @return
	 */
	public double calculateTakeAwayByProtectedGoals(List<String> goalsR, int[] undoPropositions, double delta){
		double activation = 0;
		for(int i = 0; i < deleteList.size(); i++){ //ojrl addlist
			int index = goalsR.indexOf(deleteList.get(i)); //ojrl addList
			if(index != -1){
				double temp = delta * (1.0d / (double) undoPropositions[index]) * (1.0d / (double) deleteList.size());
				activation += temp;
				//System.out.println("goalsR give "+ this.name +" an extra activation of "+temp);
			}
		}
		return activation;
	}

	/**
	 * A function executable(i t), which returns 1 (true) if competence module i is executable
	 * at time t (i.e., if all of the preconditions of competence module i are members
	 * of S (t)), and 0 (false) otherwise.
	 */
	public boolean isExecutable (List <String> states){
		List<String> preconds = new Vector<String> (this.getPreconditions());
		for(int i = 0; i < preconds.size(); i++){
			if(states.contains(preconds.get(i)) == false){
				executable = false;
				return executable;
			}
		}
		executable = true;
		return executable;
	}

	public void resetActivation(boolean reset){
		if(reset){
			priorActivation = activation;
			activation = 0;
		}
		executable = false;
		activatedPrior = activated;
		activated = false;
	}

	public void updateActivation(double act){
		priorActivation = activation;
		activation += act;
		if(activation < 0)
			activation = 1;
	}

	public void decay(double factor){
		activation *= factor;
	}

	public void setId(int id) {
		this.id = id;
	}
}
