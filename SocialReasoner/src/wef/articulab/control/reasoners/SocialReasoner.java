package wef.articulab.control.reasoners;

import wef.articulab.control.MainController;
import wef.articulab.control.bn.BehaviorNetworkPlus;
import wef.articulab.control.bn.BehaviorPlus;
import wef.articulab.control.controllers.BehaviorNetworkController;
import wef.articulab.control.util.Utils;
import wef.articulab.control.vht.VHTConnector;
import wef.articulab.model.SocialReasonerOutput;
import wef.articulab.model.blackboard.Blackboard;
import wef.articulab.model.history.SocialHistory;
import wef.articulab.model.intent.SystemIntent;
import wef.articulab.model.messages.SROutputMessage;
import wef.articulab.view.ui.Visualizer;

import java.util.Arrays;
import java.util.List;

/**
 * Created by oscarr on 6/3/16.
 */
public class SocialReasoner {
    private BehaviorNetworkPlus network;
    private String name;
    private Blackboard blackboard;
    private int cycles = 0;
    private final int maxNumCycles = 8;
    //private Model model;
    //private State current;
    private VHTConnector vhtConnector;
    private SocialHistory socialHistory;
    private Visualizer visualizer;
    //private TaskReasoner taskReasoner;
    private static SocialReasoner instance;
    private int stepCount;
    private boolean flagSentSROutput = false;

    private SocialReasoner(BehaviorNetworkController bnt, String name){
        this.network = bnt.getNetwork();
        this.name = name;
        this.blackboard = Blackboard.getInstance();
        this.vhtConnector = VHTConnector.getInstance();
        this.socialHistory = SocialHistory.getInstance();
        this.visualizer = Visualizer.getInstance();
        initialize();
        blackboard.setStatesString( bnt.getStates(), bnt.getName() );
    }

    public static SocialReasoner getInstance(BehaviorNetworkController bnt, String name){
        if( instance == null ){
            instance = new SocialReasoner( bnt, name);
        }
        return instance;
    }

    public static SocialReasoner getInstance(){
        return instance;
    }

    public String getOutput(){
        return network.getOutput();
    }

    public String getMatches(){
        return network.getMatchesOutput();
    }

    public String getStates(){
        return network.getStatesOutput();
    }

    public void execute(SystemIntent intent){
        try {
            network.setState(blackboard.getModel());
            if(MainController.verbose) System.out.println("*** States: " + Arrays.toString( blackboard.getModel().toArray() ) );
            boolean isDecsionMade = false;
            while( !isDecsionMade ) {
                if(MainController.verbose) {
                    System.out.println("cycle: " + cycles);
                }
                int idx = network.selectBehavior();
                if ((idx >= 0 && cycles > 0) || cycles >= maxNumCycles) {
                    if (idx < 0 && cycles >= maxNumCycles) {
                        network.getHighestActivationUsingNone(); // NONE // previously: network.getHighestActivation();
                    }
                    String behaviorName = network.getNameBehaviorActivated();
                    socialHistory.add(System.currentTimeMillis(), behaviorName, MainController.rapportLevel, MainController.rapportScore);
                    MainController.conversationalStrategies = network.getModuleNamesByHighestActivation();
                    Utils.exchange(MainController.conversationalStrategies, behaviorName);

                    // 2 turns: one the user one the system
                    MainController.currentTurn = MainController.currentTurn + 2;

                    // send results to NLG and print them out on the screen
                    if (vhtConnector != null) {
                        sendBNActivations();
                        sendToNLG( intent, MainController.conversationalStrategies );
                        sendToClassifier(MainController.conversationalStrategies);
                        flagSentSROutput = true;
                    }
                    isDecsionMade = true;
                    if (MainController.useSRPlot) {
                        //visualizer.printFSMOutput(output);
                        //visualizer.printStates(network.getStateString(), current.phase, current.name);
                        //Utils.sleep( 3000 );
                    }

                    //update state
                    network.execute(cycles);
                    cycles = 0;
                } else {
                    cycles++;
                }
                if (MainController.useSRPlot) {
                    visualizer.plot(network.getActivations(), network.getModules().size(), name, network.getTheta(),
                            network.getNameBehaviorActivated());
                }
                if (vhtConnector != null && !flagSentSROutput && stepCount % 20 == 0) {
                    sendBNActivations();
                }
                stepCount++;
                flagSentSROutput = false;
                System.gc();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if( MainController.flagResetTR || MainController.flagReset ){
                if( MainController.flagResetTR ) {
                    MainController.flagResetTR = false;
                }
            }
        }
    }

    private void sendBNActivations() {
        if( !VHTConnector.simulateVHT ) {
            SocialReasonerOutput output = new SocialReasonerOutput();
            output.setActivations(network.getOnlyActivations());
            output.setNames(network.getModuleNames());
            output.setThreshold(network.getTheta());
            vhtConnector.sendActivations(output);
        }
    }

    private void sendToNLG(SystemIntent intent, String[] convStrategies){
        if( convStrategies != null ){
            for( int i = 0; i< convStrategies.length; i++ ){
                convStrategies[i] = convStrategies[i].substring( 0, convStrategies[i].indexOf("_") );
            }
        }
        if( !VHTConnector.simulateVHT ) {
            SROutputMessage srOutputMessage = new SROutputMessage();
            srOutputMessage.addIntent(intent, convStrategies);
            srOutputMessage.setPhase(intent.getPhase());
            srOutputMessage.setRapport(MainController.rapportScore);

            //json = json.replace("}],\"rapport\"", ",\"conversational_strategies\":" + jsonConvStrat + "}],\"rapport\"");
            vhtConnector.sendToNLG(Utils.toJson(srOutputMessage));
        }
    }



    private void sendToClassifier(String[] convStrategies){
        vhtConnector.sendToClassifier( convStrategies[0] );
    }

    public void initialize() {
        List<BehaviorPlus> modules = network.getModules();
        int size = modules.size();
        String[] names = new String[size + 1];
        for(int i = 0; i < names.length-1; i++) {
            names[i] = modules.get(i).getName();
        }
        names[ size ] = "Activation Threshold";
    }

//    public void setModel(Model model) {
//        this.model = model;
//        current = model.current;
//        taskReasoner = TaskReasoner.getInstance();
//    }

    public static void reset() {
        instance.network.resetAll();
        instance = null;
    }
}
