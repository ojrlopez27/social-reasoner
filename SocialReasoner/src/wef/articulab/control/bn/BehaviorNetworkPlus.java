package wef.articulab.control.bn;

import wef.articulab.control.MainController;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

/**
 * @author oromero
 */
public class BehaviorNetworkPlus{ //} implements BehaviorNetworkInterface{
	private List<BehaviorPlus> modules = new Vector<>();
//	private List<String> states = new Vector <>();
    private CopyOnWriteArrayList<String> states = new CopyOnWriteArrayList<>();
	private List<String> goals = new Vector <>();
    private boolean removePrecond = false;
	private transient List<String> goalsResolved = new Vector <>();

	private double pi = 15; //20 the mean level of activation,
	private double theta = 15; //45 the threshold of activation, where is lowered 10% every time no module could be selected, and is reset to its initial value whenever a module becomes active.
	private double initialTheta = 15;//45
	private double phi = 70; //20 // 90 the amount of activation energy injected by the state per true proposition,
	private double gamma = 20; //70 the amount of activation energy injected by the goals per goal,
	private double delta = 50; // 90 not defined the amount of activation energy taken away by the protected goals per protected goal.

	private transient double[][] activationSuccesors;
	private transient double[][] activationPredeccesors;
	private transient double[][] activationConflicters;
	private transient double[] activationInputs;
	private transient double[] activationLinks;
	private transient boolean execution = false;
	private transient int indexBehActivated = -1;
	private transient int cont = 1;
	private transient Double[] activations;
    private transient boolean verbose = false;
    private transient List<String> previousStates;
    private transient List<BehaviorPlus> modulesCopy = new Vector<>();
    private transient String nameBehaviorActivated;
	private String output;
	private String matchesOutput;
	private String statesOutput;

	public BehaviorNetworkPlus(){}

	public List<BehaviorPlus> getModules() {
		return modules;
	}

	public synchronized double[] getActivations() {
		int size = activations.length;
		double[] actvs = new double[ size ];
		for( int i = 0; i < size; i++){
			actvs[i] = activations[i];
		}
		return actvs;
	}

	public int getIdxBehActivated() {
		return indexBehActivated;
	}

    public boolean isRemovePrecond() {
        return removePrecond;
    }

    public BehaviorPlus getBehaviorActivated(){
		if( indexBehActivated >= 0 ) {
			return modules.get(indexBehActivated);
		}
		return null;
	}

	public String getOutput() {
		return output;
	}

	public String getMatchesOutput() {
		return matchesOutput;
	}

	public String getStatesOutput() {
		return statesOutput;
	}

	public String getNameBehaviorActivated() {
        return nameBehaviorActivated;
	}

	public void setModules(List<BehaviorPlus> modules) {
		this.modules = modules;
		activationSuccesors = new double[modules.size()][modules.size()];
		activationPredeccesors = new double[modules.size()][modules.size()];
		activationConflicters = new double[modules.size()][modules.size()];
		activationInputs = new double[modules.size()];
		activationLinks = new double[modules.size()];
	}

	public void setModules(List<BehaviorPlus> modules, int size) {
		this.modules = modules;
		for( int i = 0; i < modules.size(); i++ ){
			modules.get(i).setIdx(i);
		}

		activationSuccesors = new double[modules.size()][modules.size()];
		activationPredeccesors = new double[modules.size()][modules.size()];
		activationConflicters = new double[modules.size()][modules.size()];
		activationInputs = new double[modules.size()];
		activationLinks = new double[modules.size()];
		if( size < modules.size() ) {
			activations = new Double[modules.size()];
		}else{
			activations = new Double[size];
		}
	}

	public double getPi() {
		return pi;
	}

	public void setPi(double pi) {
		this.pi = pi;
	}

	public double getTheta() {
		return theta;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

	public double getInitialTheta() {
		return initialTheta;
	}

	public void setInitialTheta(double itheta) {
		this.initialTheta = itheta;
	}

	public double getPhi() {
		return phi;
	}

	public void setPhi(double phi) {
		this.phi = phi;
	}

	public double getGamma() {
		return gamma;
	}

	public void setGamma(double gamma) {
		this.gamma = gamma;
	}

	public double getDelta() {
		return delta;
	}

	public void setDelta(double delta) {
		this.delta = delta;
	}

	/**
	 * a function S (t) returning the propositions that are observed to be true at time t
	 * (the state of the environment as perceived by the agent) S being implemented
	 * by an independent process (or the real world),
	 */
	public CopyOnWriteArrayList<String> getState (){
		if(states != null)
			return states;
		return new CopyOnWriteArrayList<>();
	}

	public String getStateString(){
		String result = "";
		if(states != null) {
			for( String state : states ){
				result += state + ", ";
			}
			result = result.substring(0, result.length()-2);
		}
		return result;
	}

    public String getPreviousStateString(){
        String result = "";
        if(previousStates != null) {
            for( String state : previousStates ){
                result += state + ", ";
            }
            result = result.substring(0, result.length()-2);
        }
        return result;
    }

	public synchronized void setState(CopyOnWriteArrayList<String> states){
        try {
            this.states = states; //new CopyOnWriteArrayList<>(states);
        }catch (Exception e){
            e.printStackTrace();
        }
	}

//	public void addState(List<String> states){
//		if(states == null)
//			this.states = null;
//		else
//			for(int i = 0; i < states.size(); i++) {
//				if( !this.states.contains(states.get(i)) ) {
//					this.states.add(states.get(i));
//				}
//			}
//	}

	/**
	 * a function G(t) returning the propositions that are a goal of the agent at time
	 * t G being implemented by an independent process,
	 */
	public List<String> getGoals (){
		if(goals != null)
			return goals;
		return new Vector<>();
	}

	public void setGoals(List<String> goals){
		if(goals == null)
			this.goals = null;
		else
			for(int i = 0; i < goals.size(); i++)
				this.goals.add(goals.get(i));
	}

	/**
	 * a function R(t) returning the propositions that are a goal of the agent that
	 * has already been achieved at time t R being implemented by an independent
	 * process (e.g. some internal or external goal creator),
	 */
	public Collection<String> getGoalsR (){
		if(goalsResolved != null)
			return goalsResolved;
		return new Vector<>();
	}

	public void setGoalsR(List<String> goalsR){
		if(goalsR == null)
			this.goalsResolved = null;
		else
			for(int i = 0; i < goalsR.size(); i++)
				this.goalsResolved.add(goalsR.get(i));
	}

	/**
	 * A function executable(i t), which returns 1 if competence module i is executable
	 * at time t (i.e., if all of the preconditions of competence module i are members
	 * of S (t)), and 0 otherwise.
	 */
	public boolean executable (int i){
		try {
			return modules.get(i).isExecutable(states);
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

    public boolean executable (int idx, int maximum){
        try {
            return modules.get(idx).isExecutable(maximum);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

	public boolean executable (int idx, double maximum){
		try {
			return modules.get(idx).isExecutable(maximum);
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * a function M (j), which returns the set of modules that match proposition j ,
	 * i.e., the modules x for which j E cx,
	 */
	public List<BehaviorPlus> matchProposition(String proposition){
		Vector<BehaviorPlus> behaviors = new Vector<>();
		for(BehaviorPlus behavior : modules){
			if(behavior.isPrecondition(proposition))
				behaviors.add(behavior);
		}
		return behaviors;
	}

	/**
	 * a function A(j ), which returns the set of modules that achieve proposition j ,
	 * i.e., the modules x for which j E ax,
	 */
	public Vector<BehaviorPlus> achieveProposition(String proposition){
		Vector<BehaviorPlus> behaviors = new Vector<>();
		for(BehaviorPlus beh : modules){
			if( beh.isSuccesor(proposition) )
				behaviors.add( beh );
		}
		return behaviors;
	}

	/**
	 * a function U (j ), which returns the set of modules that undo proposition j , i.e.,
	 * the modules x for which j E dx,
	 */
	public Vector<BehaviorPlus> undoProposition(String proposition){
		Vector<BehaviorPlus> behaviors = new Vector<>();
		for(BehaviorPlus beh : modules){
			if( beh.isInhibition(proposition) )
				behaviors.add( beh );
		}
		return behaviors;
	}

	/**
	 * a function U (j ), which returns the set of modules that undo proposition j , i.e.,
	 * the modules x for which j E dx, and j E S(t)
	 */
	public Vector<BehaviorPlus> undoPropositionState(String proposition, int indexBehx){
		Vector<BehaviorPlus> behaviors = new Vector<>();
		for(BehaviorPlus behx : modules){
			if(behx.getIdx() != indexBehx){
				if( behx.isInhibition(proposition) && states.contains(proposition) )
					behaviors.add(behx);
			}
		}
		return behaviors;
	}

	/**
	 * The impact of the state, goals and protected goals on the activation level of a
	 / module is computed.
	 */
	public void computeActivation (){
		Vector<String> statesList = new Vector<>(getState());
		Vector<String> goalsList = new Vector<>(getGoals());
		Vector<String> goalsRList = new Vector<>(getGoalsR());
		int[] matchesByState = new int[statesList.size()];
		int[] matchesByGoal = new int[goals.size()];
		int[] matchesByGoalsResolved = new int[states.size()];//new int[goals.size()];

		//#M(j)
		for(int i = 0; i < statesList.size(); i++){
			matchesByState[i] = matchProposition(statesList.get(i)).size();
		}
		//#A(j)
		for(int i = 0; i < goalsList.size(); i++){
			matchesByGoal[i] = achieveProposition(goalsList.get(i)).size();
		}
		//#U(j)
		//ojrl for(int i = 0; i < statesList.size(); i++){
		for(int i = 0; i < goalsRList.size(); i++){
			//ojrl matchesByGoalsResolved[i] = undoProposition(statesList.get(i)).size();
			matchesByGoalsResolved[i] = undoProposition(goalsRList.get(i)).size();
		}

		for(int i = 0; i < modules.size(); i++){
			double actState = modules.get(i).calculateInputFromState(statesList, matchesByState, phi);
			double actGoal = modules.get(i).calculateInputFromGoals(goalsList, matchesByGoal, gamma);
			double actGoalR = modules.get(i).calculateTakeAwayByProtectedGoals(goalsRList, matchesByGoalsResolved, delta); //ojrl states
			activationInputs[i] += actState + actGoal - actGoalR;
		}
	}

	/**
	 * The way the competence module activates and inhibits related modules through
	 * its successor links, predecessor links and conflicter links is computed.
	 */
	public void computeLinks (){
        //int highest = highestNumPreconditions();
		double highest = highestUtility();
		for(int i = 0; i < modules.size(); i++){
			//boolean isExecutable = executable(i);
            boolean isExecutable = executable(i, highest);
			activationSuccesors[i] = spreadsForward(modules.get(i).getIdx(), isExecutable);
			activationPredeccesors[i] = spreadsBackward(modules.get(i).getIdx(), isExecutable);
			activationConflicters[i] = takesAway(modules.get(i).getIdx());
		}
	}

	private double highestUtility() {
		double maximum = 0;
		for( int i = 0; i < modules.size(); i++ ){
			modules.get( i ).calculateMatchPreconditions( states );
			double utility = modules.get( i ).computeUtility();
			if( utility> maximum ){
				maximum = utility;
			}
		}
		return maximum;
	}

	/**
     * This method returns the behavior with the highest number of preconditions present at the current state.
     * @return
     */
    private int highestNumPreconditions() {
        int maximum = 0;
        for( int i = 0; i < modules.size(); i++ ){
            int numMatched = modules.get( i ).calculateMatchPreconditions( states );
            if( numMatched > maximum ){
                maximum = numMatched;
            }
        }
        return maximum;
    }

    /**
	 * An executable competence module x spreads activation forward. It increases
	 * (by a fraction of its own activation level) the activation level of those
	 * successors y for which the shared proposition p E ax n cy is not true.
	 * Intuitively, we want these successor modules to become more activated because
	 * they are 'almost executable', since more of their preconditions will be fulfilled
	 * after the competence module has become active.
	 * Formally, given that competence module x = (cx ax dx zx) is executable,
	 * it spreads forward through those successor links for which the proposition
	 * that defines them p E ax is false.
	 */
	public double[] spreadsForward(int indexBehavior, boolean executable){
		double[] activation = new double[modules.size()];
		if(executable){
			Vector<String> addList = new Vector<> ( modules.get(indexBehavior).getAddList() );
			for(String addPremise : addList ){
				if( !states.contains(addPremise) ){ //p E ax is false
                    List<BehaviorPlus> sparseBehaviors = matchProposition(addPremise); //j E ax n cy
					for(BehaviorPlus beh : sparseBehaviors ){
						int cardinalityMj = sparseBehaviors.size();
						int cardinalityCy = beh.getPreconditions().size();
						double temp = beh.getActivation() * (phi/gamma) * (1d / cardinalityMj) * (1d / cardinalityCy);
                        activation[beh.getIdx()] += temp;
                        if( verbose ) {
                            System.out.println(modules.get(indexBehavior).getName() + " spreads " + temp + " forward to " +
                                    beh.getName() + " for " + addPremise);
                        }
					}
				}
			}
		}
		return activation;
	}

	/**
	 * A competence module x that is NOT executable spreads activation backward.
	 * It increases (by a fraction of its own activation level) the activation level of
	 * those predecessors y for which the shared proposition p E cx n ay is not true.
	 * Intuitively, a non-executable competence module spreads to the modules that
	 * `promise' to fulfill its preconditions that are not yet true, so that the competence
	 * module may become executable afterwards. Formally, given that competence
	 * module x = (cx ax dx zx) is not executable, it spreads backward through those
	 * predecessor links for which the proposition that defined them p E cx is false.
	 * @param indexBehavior
	 * @param executable
	 * @return
	 */
	public double[] spreadsBackward(int indexBehavior, boolean executable){
		double[] activation = new double[modules.size()];
		if(!executable){
			BehaviorPlus beh = modules.get(indexBehavior);
			Collection<List<String>> condList = new Vector<> (beh.getPreconditions());

			for(List<String> condRow : condList ){
                for(String cond : condRow ){
                    if( !states.contains( cond ) ) { //p E cx is false
                        Vector<BehaviorPlus> sparseBehaviors = achieveProposition(cond); //j E cx n ay
                        for (int j = 0; j < sparseBehaviors.size(); j++) {
							int cardinalityAj = sparseBehaviors.size();
                            int cardinalityAy = sparseBehaviors.get(j).getAddList().size();
                            double temp = beh.getActivation() * (1d / cardinalityAj) * (1d / cardinalityAy);
                            activation[sparseBehaviors.get(j).getIdx()] += temp;
                            if( verbose ) {
                                System.out.println(beh.getName() + " spreads " + temp + " backward to " +
                                        sparseBehaviors.get(j).getName() + " for " + cond);
                            }
                        }

                    }
                }
			}
		}
		return activation;
	}

	/**
	 * Inhibition of Conflicters
	 * Every competence module x (executable or not) decreases (by a fraction of its
	 * own activation level) the activation level of those conflicters y for which the
	 * shared proposition p E cx n dy is true. Intuitively, a module tries to prevent a
	 * module that undoes its true preconditions from becoming active. Notice that
	 * we do not allow a module to inhibit itself (while it may activate itself). In
	 * case of mutual conflict of modules, only the one with the highest activation
	 * level inhibits the other. This prevents the phenomenon that the most relevant
	 * modules eliminate each other. Formally, competence module x = (cx ax dx zx)
	 * takes away activation energy through all of its conflicter links for which the
	 * proposition that defines them p E cx is true, except those links for which there
	 * exists an inverse conflicter link that is stronger.
	 */
	public double[] takesAway(int indexBehavior){
		double[] activation = new double[modules.size()];
		BehaviorPlus behx = modules.get(indexBehavior);
        Collection<List<String>> condListX = new Vector<>(behx.getPreconditions());

		for( List<String> condRow : condListX ){
            for( String cond : condRow ) {
				Vector<BehaviorPlus> sparseBehaviors = undoPropositionState(cond, indexBehavior); //j E cx n dy
                for ( BehaviorPlus behy : sparseBehaviors) {
                    double temp = 0;
                    if ((behx.getActivation() <= behy.getActivation()) && inverseTakesAway(indexBehavior, behy.getIdx())) {
                        activation[behy.getIdx()] = 0;
                    } else {
                        int cardinalityUj = sparseBehaviors.size();
                        int cardinalityDy = behy.getDeleteList().size();
                        temp = behx.getActivation() * (delta / gamma) * (1d / cardinalityUj) * (1d / cardinalityDy);
                        activation[behy.getIdx()] += temp;//Math.max(activationTemp, sparseBehaviors.get(j).getActivationPrior());
                    }
                    if( verbose ) {
                        System.out.println(behx.getName() + " decreases (inhibits)" + behy.getName() + " with " + temp +
                                " for " + cond);
                    }
                }
            }
		}
		return activation;
	}

	/**
	 * Finds the intersection set S(t) n cy n dx
	 * @param indexBehx
	 * @param indexBehy
	 * @return
	 */
	private boolean inverseTakesAway(int indexBehx, int indexBehy){
        Collection<List<String>> condsListy = new Vector<> (modules.get(indexBehy).getPreconditions());
		BehaviorPlus behx = modules.get(indexBehx);

		for(List<String> condRow : condsListy ){
            for( String cond : condRow ) {
                if (behx.getDeleteList().contains(cond) ) //cy n dx
                    if (states.contains( cond )) //cy n S(t)
                        return true;
            }
		}
		return false;
	}

	/**
	 * A decay function ensures that the overall activation level remains constant.
	 */
	public void applyDecayFx (){
		double sum = 0, factor, mayor = 0;
		int indexMayor = 0;

		for(int i = 0; i < modules.size(); i++){
			sum += modules.get(i).getActivation();
			if(modules.get(i).getActivation() > mayor){
				mayor = modules.get(i).getActivation();
				indexMayor = i;
			}
		}
		if(sum > pi * modules.size()){
			factor = pi * modules.size() / sum;
			for(int i = 0; i < modules.size(); i++){
				modules.get(i).decay(factor);
			}
		}
		//ojrl decay bahavior which increments its activation continously and is not activated
		if(modules.get(indexMayor).getActivation() > (theta * 1.5) && !modules.get(indexMayor).getActivated()){
			modules.get(indexMayor).decay(0.5);
			for (BehaviorPlus module : modules) {
				if (module.getExecutable() && module.getIdx() != modules.get(indexMayor).getIdx()
						&& !module.getActivated() && indexBehActivated != -1) {
					if (indexBehActivated != module.getIdx())
						module.decay(1.5);
					else
						module.decay(0.9);
				}
			}
		}

		for(int i = 0; i < modules.size(); i++){
            if( verbose ) {
//                System.out.println("activation-level " + modules.get(i).getName() + ": " + modules.get(i).getActivation());
            }
		}
	}

	/**
	 * The competence module that fulfills the following three conditions becomes
	 * active: (i) It has to be executable, (ii) Its level of activation has to surpass a
	 * certain threshold and (iii) It must have a higher activation level than all other
	 * competence modules that fulfill conditions (i) and (ii). When two competence
	 * modules fulfilll these conditions (i.e., they are equally strong), one of them is
	 * chosen randomly. The activation level of the module that has become active is
	 * reinitialized to 0 2. If none of the modules fulfills conditions (i) and (ii), the
	 * threshold is lowered by 10%.
	 */
	public int activateBehavior (){
		double act = 0;
		indexBehActivated = -1;
		for( BehaviorPlus beh : modules ){
            if( beh.getActivation() >= theta && beh.getExecutable() && beh.getActivation() > act){
                indexBehActivated = modules.indexOf(beh);
                act = beh.getActivation();
                nameBehaviorActivated = beh.getId(); //beh.getName();
            }
        }
        execution = false;
		String values = "";
		matchesOutput = "";
		for( BehaviorPlus beh : modules ){
			String matches = "$ " + beh.getName() + ": " + beh.getStateMatches();
			int number = beh.getNumMatches();
			if( MainController.verbose ) {
				System.err.println(matches.replace("$ ", "(" + number + "-" + beh.getActivation() + ") "));
			}
			matchesOutput += matches.replace("$ ", "(" + number + ") ") + ", ";
			values += "["+beh.getName() + ": " + beh.getActivation() + "], ";
		}
		if( MainController.verbose ) {
			System.out.println(values + ", theta: " + theta);
		}
        if( indexBehActivated >= 0 ){
			statesOutput = Arrays.toString(states.toArray());
			if(MainController.verbose ) {
				System.err.println("Executing Behavior: " + modules.get(indexBehActivated).getName());
				System.err.println("States: " + statesOutput );
			}
            execution = true;
            modules.get(indexBehActivated).setActivated(true);
            protectGoals(modules.get(indexBehActivated));
        }
		return indexBehActivated;
	}

	/**
	 *
	 * @param behIndex
	 */
	public synchronized void triggerPostconditions(int behIndex){
		BehaviorPlus beh = modules.get(behIndex);
		Vector<String> addList = new Vector<>(beh.getAddList());
		Vector<String> deleteList = new Vector<>(beh.getDeleteList());

        synchronized ( states ) {
            try {
                previousStates = new Vector<>(states);
                //we do not need to add preconditions to state since it is done by DMMain
//                for (String anAddList : addList) {
//                    if (!anAddList.contains("_history")) {
//                        states.remove(anAddList);
//                        states.add(anAddList);
//                    }
//                }
                if (removePrecond) {
                    for (List<String> preconds : beh.getPreconditions()) {
                        for (String precond : preconds) {
                            states.remove(precond);
                        }
                    }
                } else {
                    List<String> toRemove = new ArrayList<>();
                    for (String premise : deleteList) {
                        for( String st : states ){
                            if( premise.contains("*") && Pattern.compile(premise.replace("*", "[a-zA-Z0-9]*"))
                                    .matcher(st).matches()) {
                                toRemove.add( st );
                            }
                        }
                        for( String st : toRemove ){
                            states.remove( st );
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
		for (String goal : beh.getAddGoals()) {
			goals.remove( goal );
			goals.add( goal );
		}
	}

	/**
	 * Protect the goals achieved
	 * @param beh
	 */
	public void protectGoals(BehaviorPlus beh){
		for (String goalTemp : beh.getAddList()) {
			if (goals.contains(goalTemp)) {
				goalsResolved.add(goalTemp);
				goals.remove(goalTemp);
			}
		}
	}

	/**
	 * updates the activation of each behavior
	 */
	public void updateActivation(){
		//activation level of a competence module y at time t
		for(int i = 0; i < modules.size(); i++){
			for(int j = 0; j < modules.size(); j++){
				activationLinks[i] += activationSuccesors[j][i] + activationPredeccesors[j][i] - activationConflicters[j][i];
			}
			modules.get(i).updateActivation(activationInputs[i] + activationLinks[i]);
			activationInputs[i] = 0;
			activationLinks[i] = 0;
		}
	}

	/**
	 * Reset the activation of the behaviors
	 */
	public void reset(){
		//reseting
		if(execution)
			theta = initialTheta;
		for (BehaviorPlus module : modules) {
			module.resetActivation(module.getActivated());
		}
	}

    public int selectBehavior() {
        if (activations == null) {
            setModules(modules, modules.size() + 1);
        }
        activations[modules.size()] = theta;
        if (verbose) {
            System.out.println("\nStep " + (cont + 1) + " goals achieved: " + goalsResolved.size() + " goals: " + goals.size());
        }
        computeActivation();

        computeLinks();

        updateActivation();

        activateBehavior();

        recordActivations();

        applyDecayFx();

        cont++;
        return indexBehActivated;
    }

    private void recordActivations(){
        for (int i = 0; i < modules.size(); i++) {
            BehaviorPlus b = modules.get(i);
            if (verbose) {
                System.out.println("Behavior: " + b.getName() + "  activation: " + b.getActivation());
            }
            activations[i] = modules.get(i).getActivation();
        }
        if (indexBehActivated != -1) {
            activations[modules.size()] = modules.get(indexBehActivated).getActivation();
        }
    }

    public void execute(int cycle){
        if( cycle > 0) {
            triggerPostconditions(indexBehActivated);
        }
        if( !execution ){
            theta *= 0.9;
            if( verbose ) {
                System.err.println("None of the executable modules has accumulated enough activation to become active");
                System.out.println("Theta: " + theta);
            }
        }

        if( cycle > 0 ) {
            reset();
        }
    }

    public int getHighestActivation() {
        int idx = 5; //ASN
        double max = 0;
        for(int i = 0; i< modules.size(); i++){
            BehaviorPlus beh = modules.get(i);
            if( beh.getActivation() > theta && beh.getActivation() > max ){
                idx = i;
            }
        }
        modules.get(idx).setActivation(max * 1.15);
        nameBehaviorActivated = modules.get(idx).getId(); //.getName();
        return indexBehActivated = idx;
    }

	public int getHighestActivationUsingNone(){
		int idx = 7, maxPrecTrue = 0;
		double max = 0;
		for(int i = 0; i< modules.size(); i++){
			BehaviorPlus beh = modules.get(i);
			if( beh.getName().equals("NONE")){
				idx = i;
			}
			if( beh.getActivation() > theta && beh.getActivation() > max ){
				max = beh.getActivation();
			}
			if( beh.getNumMatches() > maxPrecTrue ){
				maxPrecTrue = beh.getNumMatches();
			}
		}
        if( max == 0){
            max = theta;
        }
        indexBehActivated = idx;
        nameBehaviorActivated = modules.get(idx).getId(); //getName();
        modules.get(idx).setActivation(max * 1.30);
		modules.get(idx).setNumMatches(maxPrecTrue);
        modules.get(idx).setActivated(true);
        execution = true;
        recordActivations();
		return idx;
	}

    public String[] getModuleNamesByHighestActivation() {
        getModulesCopy();
        Collections.sort( modulesCopy );
        Collections.reverse( modulesCopy );
        String[] results = new String[modulesCopy.size()];
		DecimalFormat formatter = new DecimalFormat("#0.00");
		output = "";
        for( int i = 0; i < modulesCopy.size(); i++ ){
			BehaviorPlus bp = modulesCopy.get(i);
            results[i] = bp.getId();
			output += "["+bp.getName()+": ("+formatter.format( bp.getActivation())+") - ("+bp.getNumMatches()+")], ";
        }
		output += "theta: " + theta;
        return results;
    }

    public void getModulesCopy() {
        modulesCopy.clear();
        for( BehaviorPlus beh : modules ){
            modulesCopy.add( beh.clone() );
        }
    }

	public String[] getModuleNames() {
		String[] names = new String[ modules.size() ];
		for(int i = 0; i < names.length; i++){
			names[i] = modules.get(i).getName();
		}
		return names;
	}

	public double[] getOnlyActivations() {
		int size = modules.size();
		double[] actvs = new double[ size ];
		for( int i = 0; i < size; i++){
			actvs[i] = activations[i];
		}
		return actvs;
	}

	public void resetAll() {
		for( BehaviorPlus beh : modules ){
			beh.reset();
		}
		theta = initialTheta;
	}
}
