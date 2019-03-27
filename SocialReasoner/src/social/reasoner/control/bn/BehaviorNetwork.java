package social.reasoner.control.bn;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * @author oromero
 */
public class BehaviorNetwork implements BehaviorNetworkInterface{
	private List<BehaviorInterface> modules = new Vector<>();
	private List <String> states = new Vector <>();
	private List <String> goals = new Vector <>();
	private List <String> goalsResolved = new Vector <>();

	private double pi = 15; //20 the mean level of activation,
	private double theta = 15; //45 the threshold of activation, where is lowered 10% every time no module could be selected, and is reset to its initial value whenever a module becomes active.
	private double initialTheta = 15;//45
	private double phi = 70; //20 // 90 the amount of activation energy injected by the state per true proposition,
	private double gamma = 20; //70 the amount of activation energy injected by the goals per goal,
	private double delta = 50; // 90 not defined the amount of activation energy taken away by the protected goals per protected goal.
	private double[][] activationSuccesors;
	private double[][] activationPredeccesors;
	private double[][] activationConflicters;
	private double[] activationInputs;
	private double[] activationLinks;
	private boolean execution = false;
	private int indexBehActivated = -1;
	private int cont = 1;
	Double[] activations;

	public List<BehaviorInterface> getModules() {
		return modules;
	}

	public Double[] getActivations() {
		return activations;
	}

	public int getIdxBehActivated() {
		return indexBehActivated;
	}

	public BehaviorInterface getBehaviorActivated(){
		if( indexBehActivated >= 0 ) {
			return modules.get(indexBehActivated);
		}
		return null;
	}

	public String getNameBehaviorActivated() {
		if( indexBehActivated != -1 ){
			return modules.get( indexBehActivated ).getName();
		}
		return "";
	}

	public void setModules(List<BehaviorInterface> modules) {
		this.modules = modules;
		activationSuccesors = new double[modules.size()][modules.size()];
		activationPredeccesors = new double[modules.size()][modules.size()];
		activationConflicters = new double[modules.size()][modules.size()];
		activationInputs = new double[modules.size()];
		activationLinks = new double[modules.size()];
	}

	public void setModules(List<BehaviorInterface> modules, int size) {
		this.modules = modules;
		for( int i = 0; i < modules.size(); i++ ){
			modules.get(i).setId( i );
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
	public Collection<String> getState (){
		if(states != null)
			return states;
		return new Vector<>();
	}

	public void setState(List<String> states){
		this.states = new Vector<>(states);
	}

	public void addState(List<String> states){
		if(states == null)
			this.states = null;
		else
			for(int i = 0; i < states.size(); i++) {
				if( !this.states.contains(states.get(i)) ) {
					this.states.add(states.get(i));
				}
			}
	}

	/**
	 * a function G(t) returning the propositions that are a goal of the agent at time
	 * t G being implemented by an independent process,
	 */
	public Collection<String> getGoals (){
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
		return modules.get(i).isExecutable(states);
	}

	/**
	 * a function M (j), which returns the set of modules that match proposition j ,
	 * i.e., the modules x for which j E cx,
	 */
	public List<BehaviorInterface> matchProposition (String proposition){
		List<BehaviorInterface> behaviors = new Vector<>();
		for(BehaviorInterface behavior : modules){
			if(behavior.isPrecondition(proposition))
				behaviors.add(behavior);
		}
		return behaviors;
	}

	/**
	 * a function A(j ), which returns the set of modules that achieve proposition j ,
	 * i.e., the modules x for which j E ax,
	 */
	public List<BehaviorInterface> achieveProposition (String proposition){
		List<BehaviorInterface> behaviors = new Vector<>();
		for( BehaviorInterface beh : modules){
			if(beh.isSuccesor(proposition))
				behaviors.add(beh);
		}
		return behaviors;
	}

	/**
	 * a function U (j ), which returns the set of modules that undo proposition j , i.e.,
	 * the modules x for which j E dx,
	 */
	public List<BehaviorInterface> undoProposition (String proposition){
		List<BehaviorInterface> behaviors = new Vector<>();
		for(BehaviorInterface beh : modules){
			if(beh.isInhibition(proposition))
				behaviors.add(beh);
		}
		return behaviors;
	}

	/**
	 * a function U (j ), which returns the set of modules that undo proposition j , i.e.,
	 * the modules x for which j E dx, and j E S(t)
	 */
	public List<BehaviorInterface> undoPropositionState (String proposition, int indexBehx){
		List<BehaviorInterface> behaviors = new Vector<>();
		for(BehaviorInterface beh : modules){
			if(beh.getId() != indexBehx){
				BehaviorInterface behy = beh;
				if(behy.isInhibition(proposition) && states.contains(proposition))
					behaviors.add(behy);
			}
		}
		return behaviors;
	}

	/**
	 * The impact of the state, goals and protected goals on the activation level of a
	 / module is computed.
	 */
	public void computeActivation (){
		List<String> statesList = new Vector<>(getState());
		List<String> goalsList = new Vector<>(getGoals());
		List<String> goalsRList = new Vector<>(getGoalsR());
		int[] matchesByState = new int[states.size()];
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
			//System.out.println("activationInputs: "+activationInputs[i]);
		}
	}

	/**
	 * The way the competence module activates and inhibits related modules through
	 * its successor links, predecessor links and conflicter links is computed.
	 */
	public void computeLinks (){
		for(int i = 0; i < modules.size(); i++){
			boolean isExecutable = executable(i);
			activationSuccesors[i] = spreadsForward(modules.get(i).getId(), isExecutable);
			activationPredeccesors[i] = spreadsBackward(modules.get(i).getId(), isExecutable);
			activationConflicters[i] = takesAway(modules.get(i).getId());
		}
	}

	/**
	 * An executable competence module x spreads activation forward. It increases
	 * (by a fraction of its own activation level) the activation level of those
	 * successors y for which the shared proposition p E ax n cy is not true.
	 * Intuitively, we want these successor modules to become more activated because
	 * they are `almost executable', since more of their preconditions will be fulfilled
	 * after the competence module has become active.
	 * Formally, given that competence module x = (cx ax dx zx) is executable,
	 * it spreads forward through those successor links for which the proposition
	 * that defines them p E ax is false.
	 */
	public double[] spreadsForward(int indexBehavior, boolean executable){
		double[] activation = new double[modules.size()];
		if(executable){
			BehaviorInterface beh = modules.get(indexBehavior);
			List<String> addList = new Vector<> (beh.getAddList());

			for(String addPremise : addList){
				if( !states.contains(addPremise)){ //p E ax is false
					List<BehaviorInterface> sparseBehaviors = matchProposition(addPremise); //j E ax n cy
					for(int j = 0; j < sparseBehaviors.size(); j++){
						int cardinalityMj = sparseBehaviors.size();
						int cardinalityCy = sparseBehaviors.get(j).getPreconditions().size();
						double temp = beh.getActivation() * (phi/gamma) * (1d / cardinalityMj) * (1d / cardinalityCy);
						activation[sparseBehaviors.get(j).getId()] += temp;
						//System.out.println(beh.getName()+" spreads "+temp+" forward to "+sparseBehaviors.get(j).getName()+" for "+addList.get(i));
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
			BehaviorInterface beh = modules.get(indexBehavior);
			List<String> condList = new Vector<> (beh.getPreconditions());

			for(String cond : condList){
				if(!states.contains(cond)){ //p E cx is false
					List<BehaviorInterface> sparseBehaviors = achieveProposition(cond); //j E cx n ay
					for(int j = 0; j < sparseBehaviors.size(); j++){
						int cardinalityAj = sparseBehaviors.size();
						int cardinalityAy = sparseBehaviors.get(j).getAddList().size();
						double temp = beh.getActivation() * (1d / cardinalityAj) * (1d / cardinalityAy);
						activation[sparseBehaviors.get(j).getId()] += temp;
						//System.out.println(beh.getName()+" spreads "+temp+" backward to "+sparseBehaviors.get(j).getName()+" for "+condList.get(i));
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
		BehaviorInterface behx = modules.get(indexBehavior);
		List<String> condListX = new Vector<>(behx.getPreconditions());

		for(String cond : condListX){
			List<BehaviorInterface> sparseBehaviors = undoPropositionState(cond, indexBehavior); //j E cx n dy
			for(int j = 0; j < sparseBehaviors.size(); j++){
				BehaviorInterface behy = sparseBehaviors.get(j);
				double temp;
				if((behx.getActivation() <= behy.getActivation())  && inverseTakesAway(indexBehavior, behy.getId())){
					activation[behy.getId()] = 0;
				}else{
					int cardinalityUj = sparseBehaviors.size();
					int cardinalityDy = sparseBehaviors.get(j).getDeleteList().size();
					temp = behx.getActivation() * (delta/gamma) * (1d / cardinalityUj) * (1d / cardinalityDy);
					activation[sparseBehaviors.get(j).getId()] += temp;//Math.max(activationTemp, sparseBehaviors.get(j).getActivationPrior());
				}
				//System.out.println(behx.getName()+" decreases (inhibits)" +behy.getName()+" with "+temp+" for "+condListX.get(i));
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
		List<String> condsListy = new Vector<> (modules.get(indexBehy).getPreconditions());
		BehaviorInterface behx = modules.get(indexBehx);

		for(String cond : condsListy){
			if( behx.getDeleteList().contains(cond) ) //cy n dx
				if( states.contains(cond) ) //cy n S(t)
					return true;
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
			for(BehaviorInterface beh : modules){
				beh.decay(factor);
			}
		}
		//ojrl decay bahavior which increments its activation continously and is not activated
		if(modules.get(indexMayor).getActivation() > (theta * 1.5) && !modules.get(indexMayor).getActivated()){
			modules.get(indexMayor).decay(0.5);
			for (BehaviorInterface module : modules) {
				if (module.getExecutable() && module.getId() != modules.get(indexMayor).getId()
						&& !module.getActivated() && indexBehActivated != -1) {
					if (indexBehActivated != module.getId())
						module.decay(1.5);
					else
						module.decay(0.9);
				}
			}
		}

		for(int i = 0; i < modules.size(); i++){
			//System.out.println("activation-level "+modules.get(i).getName()+": "+modules.get(i).getActivation());
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
		boolean[] active = new boolean[modules.size()];
		double act = 0;
		int index = 0;
		indexBehActivated = -1;
		for(int j = 0; j < modules.size(); j++){
			if(modules.get(j).getActivation() > act){
				index = j;
				act = modules.get(j).getActivation();
			}
		}

		for(int i = 0; i < modules.size(); i++){
			BehaviorInterface beh = modules.get(i);
			if(beh.getActivation() >= theta && beh.getExecutable()){
				active[i] = i == index;
			}else
				active[i] = false;
		}
		execution = false;
		for(int i = 0; i < modules.size(); i++){
			if( active[i] ){
				System.err.println("Executing Behavior "+modules.get(i).getName());
				execution = true;
				indexBehActivated = i; //modules.get(i).getIdx();
				modules.get(i).setActivated(true);
				protectGoals(modules.get(i));
				execute(indexBehActivated);
				break; //ojrl: determine if there are several behaviors activated in the same step, then apply stochastic method
			}
		}
		if( !execution ){
			System.err.println("None of the executable modules has accumulated enough activation to become active");
			theta *= 0.9;
			System.out.println("Theta: "+theta);
		}
		return indexBehActivated;
	}

	/**
	 * Execute the behavior
	 * @param behIndex
	 */
	public void execute(int behIndex){
		BehaviorInterface beh = modules.get(behIndex);
		List<String> addList = new Vector<>(beh.getAddList());
		List<String> deleteList = new Vector<>(beh.getDeleteList());

		for (String anAddList : addList) {
			states.remove( anAddList );
			states.add(anAddList);
		}
		for (String aDeleteList : deleteList) {
			states.remove(aDeleteList);
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
	public void protectGoals(BehaviorInterface beh){
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
		for (BehaviorInterface module : modules) {
			module.resetActivation(module.getActivated());
		}
	}

	public int selectBehavior() {
		activations[ modules.size() ] = theta;
		System.out.println("\nStep "+(cont+1)+" goals achieved: " + goalsResolved.size() + " goals: " + goals.size());
		List<String> st = new Vector<>( states);
		String state = "";
		for (String aSt : st) {
			state += aSt + "  ";
		}
		System.out.println("State: "+state);

		// 1
		computeActivation();

		// 2
		computeLinks();

		// 4
		updateActivation();

		// 3
		activateBehavior();

		System.out.println("\n");
		for( int i = 0; i < modules.size(); i++ ){
			BehaviorInterface b = modules.get(i);
			System.out.println("Behavior: " + b.getName() + "  activation: "+b.getActivation());
			activations[i] = modules.get(i).getActivation();
		}
		if( indexBehActivated != -1 ) {
			activations[modules.size()] = modules.get(indexBehActivated).getActivation();
		}

		// 5
		applyDecayFx();

		//6
		reset();
		cont++;
		return indexBehActivated;
	}
}
