package wef.articulab.view.emulators;

import wef.articulab.control.MainController;
import wef.articulab.control.util.Utils;
import wef.articulab.model.blackboard.Blackboard;
import wef.articulab.model.Constants;

import java.util.HashMap;

/**
 * Created by oscarr on 5/12/16.
 */
public class InputController {
    public HashMap<String, InputMappings> mappings;
    private HashMap<String, NonVerbal> mappingsNV;
    private boolean useLCS = true;
    private Blackboard blackboard;
    private InputEmulator inputEmulator;
    public String phase;
    public String intention;
    public boolean shouldSplit;


    public InputController(InputEmulator emulator) {
        inputEmulator = emulator;
        blackboard = Blackboard.getInstance();
        createMappings();
    }

    public InputEmulator getInputEmulator() {
        return inputEmulator;
    }

    public void createMappings(){
        mappings = new HashMap<>();
        mappingsNV = new HashMap<>();

        InputMappings inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.SELF_NAMING );
        mappings.put("hi i'm", inputMappings);
        mappings.put("hi, i'm", inputMappings);
        mappings.put("who are you?", inputMappings);
        mappings.put("what can you do?", inputMappings);
        mappings.put("my names is", inputMappings);
        mappings.put("just call me", inputMappings);
        mappings.put("i'm", inputMappings);
        mappings.put("i' am", inputMappings);
        mappings.put("you can call me", inputMappings);

//        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.USER_GREETING_WORD );
//        mappings.put("hi", inputMappings);
//        mappings.put("hi there", inputMappings);
//        mappings.put("hello", inputMappings);
//        mappings.put("hey", inputMappings);
//        mappings.put("hey there", inputMappings);
//        mappings.put("good morning", inputMappings);
//        mappings.put("good afternoon", inputMappings);

//        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.USER_SELF_INTRODUCTION );
//        mappings.put("i'm a", inputMappings);
//        mappings.put("i' am a", inputMappings);
//        mappings.put("i work", inputMappings);
//        mappings.put("i'm here", inputMappings);
//        mappings.put("i'm the", inputMappings);


        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.POSITIVE_CONFIRMATION );
        mappings.put("perfect", inputMappings);
        mappings.put("great", inputMappings);
        mappings.put("yes", inputMappings);
        mappings.put("yah", inputMappings);
        mappings.put("ok", inputMappings);
        mappings.put("cool", inputMappings);
        mappings.put("yes please", inputMappings);
        mappings.put("please", inputMappings);
        mappings.put("yes, i would like this", inputMappings);
        mappings.put("awesome", inputMappings);
        mappings.put("that's good", inputMappings);
        mappings.put("that would be great", inputMappings);
        mappings.put("yes this is my first time attending", inputMappings);
        mappings.put("yes, this is my first time attending", inputMappings);
        mappings.put("this is my first time attending", inputMappings);
        mappings.put("yes, sounds great", inputMappings);
        mappings.put("nice, really nice", inputMappings);
        mappings.put("yes, that sounds interesting", inputMappings);
        mappings.put("sure", inputMappings);
        mappings.put("go ahead", inputMappings);
        mappings.put("okay", inputMappings);

        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.NEGATIVE_CONFIRMATION );
        mappings.put("no", inputMappings);
        mappings.put("no thank you", inputMappings);
        mappings.put("nah", inputMappings);
        mappings.put("wrong", inputMappings);
        mappings.put("bad", inputMappings);
        mappings.put("i don't want that", inputMappings);
        mappings.put("no thanks", inputMappings);
        mappings.put("no, i don't think so", inputMappings);
        mappings.put("no, this is really great", inputMappings);
        mappings.put("i don't think so", inputMappings);


        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.FIND_PERSON );
//        mappings.put("i'd like to meet", inputMappings);
//        mappings.put("i'd love to meet", inputMappings);
//        mappings.put("available for a meeting?", inputMappings);
//        mappings.put("can you set up a meeting with", inputMappings);
//        mappings.put("help me meet", inputMappings);
//        mappings.put("i want to meet", inputMappings);
//        mappings.put("i would like to meet", inputMappings);
//        mappings.put("help me get in touch with", inputMappings);
//        mappings.put("i'd like to connect with", inputMappings);
//        mappings.put("i'd love to meet", inputMappings);


        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.REQUEST_PERSON_RECOMMENDATION );
        mappings.put("i'd like to meet some interesting people", inputMappings);
        mappings.put("i'd like to meet some people", inputMappings);
        mappings.put("i'd like to meet people in the field of", inputMappings);
        mappings.put("i'd like to meet people working at", inputMappings);
        mappings.put("tell me which are the most popular people", inputMappings);


        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.USER_TELLS_ABOUT_WORK );
        mappings.put("i am a", inputMappings);
        mappings.put("i am the", inputMappings);
        mappings.put("i am working on", inputMappings);
        mappings.put("we are working on", inputMappings);
        mappings.put("i'm a", inputMappings);
        mappings.put("i'm the", inputMappings);


        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.REQUEST_SESSION_RECOMMENDATION );
        mappings.put("attend some interesting sessions", inputMappings);
//        mappings.put("attend some sessions", inputMappings);
//        mappings.put("i'd like to attend some sessions", inputMappings);
//        mappings.put("i'd like to attend some interesting sessions", inputMappings);
//        mappings.put("sessions sounds like a good idea", inputMappings);
//        mappings.put("i'm interested in sessions about", inputMappings);
//        mappings.put("i'm interested in sessions starting at", inputMappings);
//        mappings.put("tell me which are the most popular sessions", inputMappings);
//        mappings.put("i'd like to attend session", inputMappings);
//        mappings.put("what time does session start?", inputMappings);
//        mappings.put("what time does session end?", inputMappings);


        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.GRATITUDE );
        mappings.put("thank you", inputMappings);
        mappings.put("thank you very much", inputMappings);
//        mappings.put("you are awesome", inputMappings);
//        mappings.put("you are great", inputMappings);
//        mappings.put("you're great", inputMappings);
        mappings.put("thanks", inputMappings);
        mappings.put("thanks a lot", inputMappings);
        mappings.put("thanks a lot", inputMappings);
        mappings.put("thank you so much", inputMappings);
        mappings.put("thanks, this is really helpful", inputMappings);


        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.WORK_INTEREST );
//        mappings.put("i'm interested in sessions about", inputMappings);
//        mappings.put("i'd like to meet people in the field of", inputMappings);
//        mappings.put("i'd like to meet people working at", inputMappings);
//        mappings.put("i'm interested in topics such as", inputMappings);
        mappings.put("i like", inputMappings);


        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.DISLIKE );
//        mappings.put("i'm not interested in sessions about", inputMappings);
//        mappings.put("i am not interested in meeting people in the field of", inputMappings);
//        mappings.put("i am not interested in meeting people working at", inputMappings);
//        mappings.put("i am not interested in topics such as", inputMappings);
        mappings.put("i don't like", inputMappings);
        mappings.put("i dislike", inputMappings);


        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.REQUEST_FOOD_RECOMMENDATION );
        mappings.put("where can i find food?", inputMappings);
        mappings.put("are there any cafe's here ?", inputMappings);
        mappings.put("i'm hungry", inputMappings);
        mappings.put("i'm looking for a place to get some food", inputMappings);



        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.RECOMMEND_PARTY );
        mappings.put("do you have any information about", inputMappings);
        mappings.put("are there any party in", inputMappings);
        mappings.put("are there any party in", inputMappings);

        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.MULTIPLE_GOALS );
        mappings.put("people and session", inputMappings);
        mappings.put("people and sessions", inputMappings);
        mappings.put("sessions and people", inputMappings);
        mappings.put("session and people", inputMappings);
        mappings.put("i'd like to meet some people and attend some sessions", inputMappings);
        mappings.put("i'd like to attend some sessions and meet some people", inputMappings);



//        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.GREETING_WORD );
//        mappings.put("nice to meet you too", inputMappings);
//        mappings.put("finally I meet you", inputMappings);
//        mappings.put("so glad to meet you", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_INTRODUCTION, Constants.USER_ASKS_AFFILIATION );
//        mappings.put("who are you affiliated with?", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_GREETING, Constants.START_CONVERSATION );
//        mappings.put("initialize", inputMappings);
//        mappings.put("initialise", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_TASK, Constants.ASK_RECOMMEND_SESSION_TOPIC );
//        mappings.put("can you recommend sessions", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_TASK, Constants.ASK_RECOMMEND_SESSION );
//        mappings.put("is there a session I should attend?", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_TASK, Constants.RECOMMEND_SESSION_POPULAR );
//        mappings.put("tell me which are the most popular sessions", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_TASK, Constants.USER_RESPONDS_FIRST_TIME_ATTENDING );
//        mappings.put("this is my first time attending", inputMappings);
//        mappings.put("this is not my first time attending", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_GOAL_ELICITATION, Constants.USER_RESPONDS_ELICIT_GOALS_PEOPLE);
//        mappings.put("i'd like to meet people", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_GOAL_ELICITATION, Constants.USER_RESPONDS_ELICIT_GOALS_PEOPLE);
//        mappings.put("i'd like to attend sessions", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_TASK, Constants.USER_RESPONDS_QUESTION_WORK);
//        mappings.put("engineering company manufacturing", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_TASK, Constants.USER_REPONDS_LET_ME_LOOK);
//        mappings.put("thank you", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_TASK, Constants.USER_REPONDS_SOMEBODY_TO_MEET);
//        mappings.put("yes sounds great", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_TASK, Constants.USER_ASK_SEND_MEETING_REQ);
//        mappings.put("please send a message", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_TASK, Constants.USER_RESPONDS_SEND_MEETING_REQ);
//        mappings.put("thanks this is very helpful", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_TASK, Constants.USER_RESPONDS_CONTINUE_RECOMMENDATIONS);
//        mappings.put("sessions sounds like a good idea", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_TASK, Constants.USER_RESPONDS_FASHION_INDUSTRY);
//        mappings.put("i don't think so", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_TASK, Constants.USER_RESPONDS_WAY_DRESSED);
//        mappings.put("nice really nice", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_TASK, Constants.USER_RESPONDS_COLLABORATIVE_SESSION);
//        mappings.put("yes that sounds interesting", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_TASK, Constants.USER_RESPONDS_SESSION_GESTURE);
//        mappings.put("ok", inputMappings);
//        mappings.put("okay", inputMappings);
//
//        inputMappings = new InputMappings(Constants.PHASE_FAREWELL, Constants.USER_RESPONDS_PRE_FAREWELL);
        mappings.put("no this is really great thanks", inputMappings);

        inputMappings = new InputMappings(Constants.PHASE_GOAL_ELICITATION, Constants.RESET);
        mappings.put(Constants.RESET, inputMappings);

        /** ******************************************************************************************************** **/

        mappingsNV.put("GW", new NonVerbal(0, "ASN") );
        mappingsNV.put("AFTA", new NonVerbal(5, "SD") );
        mappingsNV.put("EG", new NonVerbal(0, "NONE") );
        mappingsNV.put("QAB", new NonVerbal(6, "SD") );
        mappingsNV.put("LL", new NonVerbal(3, "ASN") );
        mappingsNV.put("ASM", new NonVerbal(5, "NONE") );
        mappingsNV.put("SMA", new NonVerbal(5, "ASN") );
        mappingsNV.put("SMR", new NonVerbal(5, "NONE") );
        mappingsNV.put("MTD", new NonVerbal(6, "NONE") );
        mappingsNV.put("SFI", new NonVerbal(6, "NONE") );
        mappingsNV.put("WD", new NonVerbal(5, "NONE") );
        mappingsNV.put("ECS", new NonVerbal(5, "NONE") );
        mappingsNV.put("DSG", new NonVerbal(3, "NONE") );
        mappingsNV.put("ALH", new NonVerbal(5, "ASN") );
        mappingsNV.put("FFCB", new NonVerbal(4, "NONE") );
    }

    public synchronized String extractPremises( String utterance ){
        String originalUtterance = utterance = utterance.toLowerCase().trim();
        int idx = utterance.indexOf('[');
        if( idx != -1){
            utterance = utterance.substring(0, idx);
        }
        utterance = utterance.replace(",", "");
        if( shouldSplit ){
            utterance = utterance.split(" ")[1];
            shouldSplit = false;
        }
        InputMappings mapping;
        if( useLCS ){
            String winnerTemplate = "";
            double max = 0.0, length;
            for( String template : mappings.keySet() ){
                length = Utils.calculateLCSWords(template.split(" "), utterance.split(" "));
                if( length > 0){
//                    double templateLength = template.split(" ").length;
//                    if( templateLength != length) {
//                        length = length / templateLength;
//                    }
                }
                if( length > max ){
                    max = length;
                    winnerTemplate = template;
                }
            }
            utterance = winnerTemplate;
        }
        mapping = mappings.get( utterance );
        if( MainController.useFSM ){
            if (mapping != null) {
                //TaskReasoner.inputFromUser = mapping.intention;
            }else{
                //TaskReasoner.inputFromUser = originalUtterance;
            }
            MainController.noInputFlag = false;
        }else {
            if (mapping != null) {
                phase = mapping.phase;
                String extracted = mapping.phase + ":" + mapping.intention;
                blackboard.setStatesString(extracted, "InputEmulator");
                return extracted;
            }
        }
        return "";
    }

    public String[] extractNonVerbal(String step){
        NonVerbal nv = mappingsNV.get( step );
        String[] result = new String[2];
        if( nv != null ) {
            result[0] = nv.rapportScore + " : " + (nv.rapportScore == 0 ? "NONE" : nv.rapportScore < 4 ? "LOW-RAPPORT" :
                    nv.rapportScore > 4 ? "HIGH-RAPPORT" : "MEDIUM-RAPPORT");
            result[1] = nv.userCS;
        }
        return result;
    }

    public String encodeIntention(String behaviorName, boolean isCS) {
        if( !isCS ) {
            return intention = behaviorName;
        }
        return intention;
    }

    public void extractInputa(String message) {
        String input = inputEmulator.userInput.getText() + "\n\n" + "Incoming message: " + message + "\n" +
                "Extracted premises: " + extractPremises( message ) + "\n\nBehavior Activated: " + MainController.behavior;
        inputEmulator.userInput.setText(input);
    }

    class NonVerbal{
        int rapportScore;
        String userCS;
        String emotion;
        boolean smile;
        boolean eyeGaze;
        boolean headNod;

        public NonVerbal(int rapportScore, String userCS) {
            this.rapportScore = rapportScore;
            this.userCS = userCS;
        }
    }

    class Verbal{
        String phase;
        String userIntention;
        String utterance;
        int jitter;
        int shimmer;
    }

    class InputMappings{
        String phase;
        String intention;

        public InputMappings(String phase, String intention) {
            this.phase = phase;
            this.intention = intention;
        }
    }
}
