package wef.articulab.control.controllers;

import wef.articulab.control.util.Utils;
import wef.articulab.control.bn.*;
import wef.articulab.model.blackboard.BlackboardListener;
import wef.articulab.model.Constants;

import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by oscarr on 4/28/16.
 */
public class ConversationalStrategyBN extends BehaviorNetworkController implements BlackboardListener {
    private boolean load = true;

    public ConversationalStrategyBN(){
        network = new BehaviorNetworkPlus();
        createBN();
    }

    @Override
    public BehaviorNetworkPlus createBN() {
        name = "Conversational Strategy BN";

        if( load ){
            network = Utils.fromJson("json/SocialReasonerBN_02.17.2017.json", BehaviorNetworkPlus.class);
            modules = network.getModules();
            NUM_BEHAVIORS = modules.size();
            NUM_VARIABLES = NUM_BEHAVIORS + 1; // +2 includes the threshold line and behavior activated
            states = network.getState();
            goals = network.getGoals();
        }else {
            BehaviorPlus behavior;

            // Self Disclosure
            behavior = new BehaviorPlus("SD");
            behavior.setDescription("Self Disclosure");
            behavior.addPreconditions(new String[][]{new String[]{Constants.PLEASURE_COMING_TOGETHER, Constants.USER_TELLS_ABOUT_WORK,
                    Constants.FEEDBACK_LIKE_WORK, Constants.FEEDBACK_DISLIKE_WORK,}});
            behavior.addPreconditions(new String[][]{new String[]{Constants.ASN_HISTORY_SYSTEM, Constants.PR_HISTORY_SYSTEM, Constants.VSN_HISTORY_SYSTEM}});
            behavior.addAddList(Arrays.asList(Constants.SD_SYSTEM_CS));
            modules.add(behavior);

            // Violation Social Norm
            behavior = new BehaviorPlus("VSN");
            behavior.setDescription(Constants.VIOLATION_SOCIAL_NORM);
            behavior.addPreconditions(new String[][]{new String[]{Constants.USER_RESPONDS_FASHION_INDUSTRY}});
            behavior.addAddList(Arrays.asList(Constants.VSN_SYSTEM_CS));
            modules.add(behavior);

            // Question to Elicit Self Disclosure
            behavior = new BehaviorPlus("QESD");
            behavior.setDescription("Question to Elicit Self Disclosure");
            behavior.addPreconditions(new String[][]{new String[]{Constants.WHO_ONE_IS, Constants.USER_RESPONDS_FIRST_TIME_ATTENDING,
                    Constants.START_GOAL_ELICITATION, Constants.START_PERSON_PRE_RECOMMEND_NOTICE, Constants.RECOMMEND_PERSON,
                    Constants.USER_REPONDS_SOMEBODY_TO_MEET, Constants.YOUR_WELCOME, Constants.USER_RESPONDS_SESSION_GESTURE}});
            behavior.addPreconditions(new String[][]{new String[]{Constants.SD_HISTORY_SYSTEM, Constants.QESD_HISTORY_SYSTEM, Constants.PR_HISTORY_SYSTEM}});
            behavior.addAddList(Arrays.asList(Constants.QESD_SYSTEM_CS));
            modules.add(behavior);

            // Referring to Shared Experiences
            behavior = new BehaviorPlus("RSE");
            behavior.setDescription(Constants.SHARED_EXPERIENCES);
            behavior.addPreconditions(new String[][]{new String[]{Constants.AVAILABLE_SHARED_EXPERIENCES}});
            behavior.addAddList(Arrays.asList(Constants.RSE_SYSTEM_CS));
            modules.add(behavior);

            // Praise
            behavior = new BehaviorPlus("PR");
            behavior.setDescription(Constants.PRAISE);
            behavior.addPreconditions(new String[][]{new String[]{Constants.USER_RESPONDS_ELICIT_GOALS_PEOPLE,
                    Constants.USER_RESPONDS_QUESTION_WORK, Constants.START_RECOMMEND_INDUSTRY}});
            behavior.addPreconditions(new String[][]{new String[]{Constants.SD_HISTORY_SYSTEM, Constants.QESD_HISTORY_SYSTEM}});
            behavior.addAddList(Arrays.asList(Constants.PR_SYSTEM_CS));
            modules.add(behavior);

            // Adhere Social Norm
            behavior = new BehaviorPlus("ASN");
            behavior.setDescription(Constants.ADHERE_SOCIAL_NORM);
            behavior.addPreconditions(new String[][]{new String[]{Constants.GREETING_WORD,
                    Constants.USER_RESPONDS_PRE_FAREWELL}});
            behavior.addAddList(Arrays.asList(Constants.ASN_SYSTEM_CS));
            modules.add(behavior);


            // Back-Channel
            behavior = new BehaviorPlus("ACK");
            behavior.setDescription(Constants.BACK_CHANNEL);
            behavior.addPreconditions(new String[][]{new String[]{}});
            //        behavior.addPreconditions(new String[][]{ new String[]{Constants.NOT_ACK_HISTORY_SYSTEM}});
            behavior.addAddList(Arrays.asList(Constants.ACK_SYSTEM_CS));
            modules.add(behavior);

            NUM_BEHAVIORS = modules.size();
            NUM_VARIABLES = NUM_BEHAVIORS + 1; // +2 includes the threshold line and behavior activated

            states = new CopyOnWriteArrayList<>(Arrays.asList(new String[]{Constants.GREETING_WORD}));
            goals.add(Constants.RAPPORT_INCREASED);

            network.setGoals(goals);
            network.setPi(20);
            network.setTheta(15);
            network.setInitialTheta(15);
            network.setPhi(20);
            network.setGamma(70);
            network.setDelta(50);

            network.setModules(modules, NUM_VARIABLES);
            network.setState(states);
            network.setGoalsR(new Vector<>());

            Utils.toJson(network, "SocialReasonerBN");
        }

        title = "Conversational Strategy BN";
        int size = modules.size();
        series = new String[size+1];
        for( int i = 0; i < size; i++ ){
            series[i] = modules.get(i).getName();
        }
        series[size] = "Activation";
        return network;
    }

    @Override
    public String extractState(String state) {
        String extracted = "";
        if( state.equals( Constants.SELF_INTRODUCTION ) ){
            extracted += (extracted.isEmpty()? "" : ":") + Constants.USER_KNOWS_CHIP;
        }else if( state.equals(Constants.RESET) ){
            extracted += (extracted.isEmpty()? "" : ":") + Constants.USER_RESPONDS_FIRST_TIME_ATTENDING;
        }
        return extracted;
    }

    @Override
    public String removeState(String state) {
        String remove = null;
        if( state.equals(Constants.SD_SYSTEM_CS) ){
            remove = Constants.ASN_SYSTEM_CS + ":" + Constants.VSN_SYSTEM_CS + ":" + Constants.QESD_SYSTEM_CS + ":" +
                    Constants.RSE_SYSTEM_CS + ":" + Constants.ACK_SYSTEM_CS + ":" + Constants.PR_SYSTEM_CS;
        }else if( state.equals(Constants.ASN_SYSTEM_CS) ){
            remove = Constants.SD_SYSTEM_CS + ":" + Constants.VSN_SYSTEM_CS + ":" + Constants.QESD_SYSTEM_CS + ":" +
                    Constants.RSE_SYSTEM_CS + ":" + Constants.ACK_SYSTEM_CS + ":" + Constants.PR_SYSTEM_CS;
        }else if( state.equals(Constants.VSN_SYSTEM_CS) ){
            remove = Constants.ASN_SYSTEM_CS + ":" + Constants.SD_SYSTEM_CS + ":" + Constants.QESD_SYSTEM_CS + ":" +
                    Constants.RSE_SYSTEM_CS + ":" + Constants.ACK_SYSTEM_CS + ":" + Constants.PR_SYSTEM_CS;
        }else if( state.equals(Constants.QESD_SYSTEM_CS) ){
            remove = Constants.ASN_SYSTEM_CS + ":" + Constants.VSN_SYSTEM_CS + ":" + Constants.SD_SYSTEM_CS + ":" +
                    Constants.RSE_SYSTEM_CS + ":" + Constants.ACK_SYSTEM_CS + ":" + Constants.PR_SYSTEM_CS;
        }else if( state.equals(Constants.RSE_SYSTEM_CS) ){
            remove = Constants.ASN_SYSTEM_CS + ":" + Constants.VSN_SYSTEM_CS + ":" + Constants.QESD_SYSTEM_CS + ":" +
                    Constants.SD_SYSTEM_CS + ":" + Constants.ACK_SYSTEM_CS + ":" + Constants.PR_SYSTEM_CS;
        }else if( state.equals(Constants.ACK_SYSTEM_CS) ){
            remove = Constants.ASN_SYSTEM_CS + ":" + Constants.VSN_SYSTEM_CS + ":" + Constants.QESD_SYSTEM_CS + ":" +
                    Constants.RSE_SYSTEM_CS + ":" + Constants.SD_SYSTEM_CS + ":" + Constants.PR_SYSTEM_CS;
        }else if( state.equals(Constants.PR_SYSTEM_CS) ) {
            remove = Constants.ASN_SYSTEM_CS + ":" + Constants.VSN_SYSTEM_CS + ":" + Constants.QESD_SYSTEM_CS + ":" +
                    Constants.RSE_SYSTEM_CS + ":" + Constants.ACK_SYSTEM_CS + ":" + Constants.SD_SYSTEM_CS;
        }else if( state.startsWith( "phase_" ) ){
            remove = getPhasesToRemove( state );
        }else if( state.equals(Constants.RESET) ){
            remove = Constants.REMOVE_ALL;
        }
        return remove;
    }

    private String getPhasesToRemove(String exceptPhase){
        String result = "";
        for(String state : blackboard.getModel() ){
            if( state.startsWith("phase_") && !state.equals(exceptPhase) ){
                result += result.isEmpty()? state : ":" + state;
            }
        }
        return result;
    }

    @Override
    public void updateModel(CopyOnWriteArrayList<String> states) {
        network.setState( states );
    }

}
