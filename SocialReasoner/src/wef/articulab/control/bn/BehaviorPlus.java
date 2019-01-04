package wef.articulab.control.bn;

import wef.articulab.control.util.Utils;
import wef.articulab.model.intent.SystemIntent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class BehaviorPlus implements Comparable<BehaviorPlus>{ // implements BehaviorInterface{
	private String name;
    private String id;
	private List<List<String>> preconditions = new Vector <>();
	private List<String> addList = new Vector <>();
	private String description;
	private List<String> addGoals = new Vector<>();
	private List<String> deleteList = new Vector <>();

	private transient double activation = 0;
	private transient int idx;
	private transient boolean executable = false, activated = false;
	private transient boolean verbose = false;
    private transient int numMatches;
	private transient String stateMatches;
	private transient int numPreconditions = -1;
    private transient double utility;

    public BehaviorPlus(String name){
		this.name = name;
	}

	public BehaviorPlus(String name, String[][] preconds, String[] addlist, String[] deletelist){
		this.name = name;
		addPreconditions(preconds);
		addList.addAll(Arrays.asList(addlist));
		deleteList.addAll(Arrays.asList(deletelist));
	}

    public BehaviorPlus(String name, String description, String[][] preconds, String[] addlist, String[] deletelist){
        this(name, preconds, addlist, deletelist);
        this.description = description;
    }

    public BehaviorPlus(String name, String description, String[][] preconds, String[] addlist, String[] deletelist, String[] addGoals){
        this(name, description, preconds, addlist, deletelist);
        this.description = description;
        this.addGoals.addAll(Arrays.asList(addGoals));

    }

    public void addPreconditions(String[][] preconds){
        for(int i = 0; preconds != null && i < preconds.length; i++) {
            List<String> precondList = new Vector<>();
            for(int j = 0; j < preconds[i].length; j++){
                precondList.add( preconds[i][j] );
            }
            preconditions.add( precondList );
        }
    }

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getDescription() {
        return description;
    }
	public void setDescription(String description) {
		this.description = description;
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
	public void addAddList(List <String> addList) {
		if( this.addList == null ){
			addList = new Vector<>();
		}
		this.addList.addAll(addList);
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
	public Collection<List<String>> getPreconditions() {
		if(preconditions != null && preconditions.size() > 0)
			return preconditions;
		return new Vector<>();
	}
	public int getIdx(){
		return this.idx;
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
		activated = a;
	}


	/**
	 * Determines if is into the add-list
	 * @param proposition
	 * @return
	 */
	public boolean isSuccesor(String proposition){
        if( addList.contains(proposition) ) {
            return true;
        }else{
            for( String premise : addList ){
				if( ((premise.startsWith("do_") || premise.startsWith("start_") || premise.startsWith("feedback_")
						|| premise.startsWith("end_") || premise.startsWith("feedback_") || premise.startsWith("ask_")
						|| premise.startsWith("outcome_"))) &&
						(proposition.startsWith("do_") || proposition.startsWith("start_") || proposition.startsWith("feedback_")
								|| proposition.startsWith("end_") || proposition.startsWith("feedback_") || proposition.startsWith("ask_")
								|| proposition.startsWith("outcome_"))){
					System.out.println("");
				}
				if( premise.contains("*") && Pattern.compile(premise.replace("*", "[a-zA-Z0-9_]*"))
                        .matcher(proposition).matches()) {
                    return true;
                }
            }
        }
        return false;
	}

	/**
	 * Determines if is into the delete-list
	 * @param proposition
	 * @return
	 */
	public boolean isInhibition(String proposition){
		if( deleteList.contains(proposition) ) {
            return true;
        }else{
            for( String premise : deleteList ){
				if( premise.contains("*") && Pattern.compile(premise.replace("*", "[a-zA-Z0-9_]*"))
                        .matcher(proposition).matches()) {
                        return true;
                }
            }
        }
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
       for(List<String> precondList : preconditions ){
            for( String precond : precondList ){
				if( precond.contains("*")){
                    if ( Pattern.compile( precond.replace("*", "[a-zA-Z0-9_]*") ).matcher( proposition ).matches() ){
                        return true;
                    }
                }else if( precond.equals(proposition) ){
                    return true;
                }

            }
        }
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
		for(List<String> condList : preconditions ){
            for(String cond : condList ) {
                int index = states.indexOf( cond );
                if (index != -1) {
                    double temp = phi * (1.0d / (double) matchedStates[index]) * (1.0d / (double) preconditions.size());
                    activation += temp;
                    if(verbose) {
						System.out.println("state gives " + this.name + " an extra activation of " + temp + " for " + cond);
					}
                }
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
				if(verbose) {
					System.out.println("goals give " + this.name + " an extra activation of " + temp);
				}
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
				if(verbose) {
					System.out.println("goalsR give " + this.name + " an extra activation of " + temp);
				}
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
		Vector<List<String>> preconds = new Vector<> (this.getPreconditions());
		external : for(List<String> precondRow : preconds ){
			for(String precond : precondRow ){
				if(states.contains(precond)){
//					continue external;
					//TODO: remove this. This is just for WEF Demo
					return executable = true;
				}
			}
			return executable = false;
		}
		return executable = true;
	}

    public boolean isExecutable (int maximum){
		return executable = numMatches >= maximum;
    }

	public boolean isExecutable (double maximum){
		return executable = utility >= (maximum * .8);
	}

	public double computeUtility() {
		return utility = this.getActivation() + (this.getNumMatches() * 5);
	}

	public void resetActivation(boolean reset){
		if(reset){
//			activation = 0;
			activation = activation/2;
		}
		executable = false;
		activated = false;
	}

	public void updateActivation(double act){
		activation += act;
		if(activation < 0)
			activation = 1;
	}

	public void decay(double factor){
		activation *= factor;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

    @Override
    public int compareTo(BehaviorPlus other) {
		double thisUtility = this.computeUtility();
		double otherUtility = other.computeUtility();
        return Double.compare( thisUtility, otherUtility);
    }

    @Override
    public BehaviorPlus clone(){
		return Utils.clone(this);
    }

	public void reset() {
		activation = 0;
		executable = false;
		activated = false;
		numMatches = 0;
	}

    public int calculateMatchPreconditions(CopyOnWriteArrayList<String> states) {
        numMatches = 0;
		stateMatches = "";
        for( List<String> precondList : preconditions ){
            for( String precond : precondList ){
                if( states.contains(precond) ){
					stateMatches += "[" + precond + "] ";
                    numMatches++;
                }
            }
        }
		return numMatches;
    }

    public int getNumMatches() {
        return numMatches;
    }

	public void setNumMatches(int numMatches) {
		this.numMatches = numMatches;
	}

	public String getStateMatches() {
		return stateMatches;
	}
}
