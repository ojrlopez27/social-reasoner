package wef.articulab.control.vht;

import edu.usc.ict.vhmsg.MessageEvent;
import edu.usc.ict.vhmsg.MessageListener;
import edu.usc.ict.vhmsg.VHMsg;
import wef.articulab.control.MainController;
import wef.articulab.control.util.Utils;
import wef.articulab.model.SocialReasonerOutput;
import wef.articulab.model.nlu.NLUOutput;
import wef.articulab.view.emulators.InputController;


public class VHTConnector implements MessageListener{
    private VHMsg sender;
    private VHMsg receiver;
    private MainController mainController;
    private InputController inputEmulator;
    private boolean verbose = false;

    private static String sendMsgActivations;
    public static boolean sendMessagesToNLG;
    private static String sendMsgToNLG;
    private static String sendMsgToClassifier;
    private static String receiveRapEst;
    private static String receiveNonVerbals;
    private static String receiveNonVerbalsBEAT;
    private static String receiveConvStrat;
    private static String receiveASR;
    private static String receiveSpoke;
    private static String receiveNLG;
    private static String receiveTaskReasoner;
    private static String receiveRecomResults;
    private static String receiveResultFromOscarWEFConn;
    private static String receiveNLU;
    private static String receiveExpress;
    private static String receiveSocialReasoner;
    private static String SRSwitch = "on";                         //SocialReasoner Switch
    public static String flagKeyword;
    public static boolean simulateVHT = true;

    private static VHTConnector instance;
    public static String serverIP;

    private VHTConnector(MainController mainController){
        loadProperties();
        this.mainController = mainController;
        if( !simulateVHT ) {
            sender = new VHMsg();
            receiver = new VHMsg();
            boolean ret = sender.openConnection(serverIP);
            if (!ret) {
                System.out.println("Connection error!");
                return;
            }
            receiver.openConnection(serverIP);
            receiver.enableImmediateMethod();
            receiver.addMessageListener(this);
            subscribe(receiveRapEst);
            subscribe(receiveNonVerbals);
            //        subscribe(receiveNonVerbalsBEAT);
            subscribe(receiveConvStrat);
            subscribe(receiveTaskReasoner);
            subscribe(receiveSocialReasoner);
            subscribe(receiveNLG);
            subscribe(receiveASR);
            subscribe(receiveSpoke);
            subscribe(receiveExpress);
            //        subscribe(receiveRecomResults);
            //        subscribe(receiveNLU);
            //        subscribe(receiveResultFromOscarWEFConn);
        }
    }

    public static VHTConnector getInstance(MainController mainController){
        if( instance == null && wef.articulab.control.MainController.useVHTConnnector){
            instance = new VHTConnector(mainController);
        }
        return instance;
    }

    public static VHTConnector getInstance(){
        return instance;
    }

    public static void setInputController(InputController inputController) {
        instance.inputEmulator = inputController;
    }

    private void loadProperties() {
        sendMsgActivations = MainController.properties.getProperty("sendMsgActivations");
        sendMessagesToNLG = Boolean.valueOf(MainController.properties.getProperty("sendMessagesToNLG"));
        receiveRapEst = MainController.properties.getProperty("receiveRapEst");
        receiveNonVerbals = MainController.properties.getProperty("receiveNonVerbals");
        receiveNonVerbalsBEAT = MainController.properties.getProperty("receiveNonVerbalsBEAT");
        receiveConvStrat = MainController.properties.getProperty("receiveConvStrat");
        receiveASR = MainController.properties.getProperty("receiveASR");
        receiveSpoke = MainController.properties.getProperty("receiveSpoke");
        receiveTaskReasoner = MainController.properties.getProperty("receiveTaskReasoner");
        receiveNLG = MainController.properties.getProperty("receiveNLG");
        receiveRecomResults = MainController.properties.getProperty("receiveRecomResults");
        receiveNLU = MainController.properties.getProperty("receiveNLU");
        receiveResultFromOscarWEFConn = MainController.properties.getProperty("receiveResultFromOscarWEFConn");
        serverIP = MainController.properties.getProperty("vhtIPAddress");
        receiveSocialReasoner = MainController.properties.getProperty("receiveSocialReasoner");
        sendMsgToNLG = MainController.properties.getProperty("sendMsgToNLG");
        sendMsgToClassifier = MainController.properties.getProperty("sendMsgToClassifier");
        receiveExpress = MainController.properties.getProperty("receiveExpress");
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void subscribe(String typeMessage){
        receiver.subscribeMessage( typeMessage );
    }

    public void sendActivations(SocialReasonerOutput output){
        if( !simulateVHT ) {
            String json = Utils.toJson(output);
            if (SRSwitch == "off") {
                return;
            }
            sender.sendMessage(sendMsgActivations + " 0 " + json);
        }
    }

    public void sendActivationsZero() {
        if( !simulateVHT ) {
            String jsonZero = "{\"names\":[\"SD\",\"VSN\",\"QESD\",\"RSE\",\"PR\",\"ASN\",\"ACK\",\"NONE\"],\"activations\":[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0],\"threshold\":15.0}";
            sender.sendMessage(sendMsgActivations + " 0 " + jsonZero);
        }
    }

   public void messageAction( MessageEvent event ){
       String message = event.toString();
       if(verbose) System.out.println("Receiving message: " + message);
       flagKeyword = event.toString();
       processMessage( message );
   }

    private void processMessage(String message) {
        if( !MainController.flagReset && MainController.flagStart ) {
            if (message.startsWith(receiveRapEst)) {
                extractRapportEstimator(message.substring(receiveRapEst.length() + 1));
            } else if( message.startsWith(receiveNonVerbals)) {
                extractNonVerbalsMultisense(message.substring(receiveNonVerbals.length() + 1));
            }else if( message.startsWith(receiveNonVerbalsBEAT)){
                extractNonVerbalsMultisense(message.substring(receiveNonVerbalsBEAT.length() + 1));
            } else if (message.startsWith(receiveASR + " ")) {
                extractASRoutput(message.substring(receiveASR.length() + 1));
            } else if (message.startsWith(receiveSpoke)) {
                extractSpoke(message.substring(receiveSpoke.length() + 1));
            }else if (message.startsWith(receiveConvStrat)) {
                extractConversationalStrategy(message.substring(receiveConvStrat.length() + 1));
            } else if (message.startsWith(receiveTaskReasoner)) {
                extractTaskReasonerIntent(message.substring(receiveTaskReasoner.length() + 1));
            }
            else if (message.startsWith(receiveNLG)) {
                extractNLG(message.substring(receiveNLG.length() + 1));
            }
            //SocialReasoner Switch
            else if (message.startsWith(receiveSocialReasoner + " start")) {
                SRSwitch = "on";
            } else if (message.startsWith(receiveSocialReasoner + " stop")) {
                SRSwitch = "off";
                sendActivationsZero();
            }
            else if (message.startsWith(receiveRecomResults)) {
                //extractRecommendationResults(message.substring(receiveRecomResults.length() + 1));
                //System.err.println(message);
            } else if (message.startsWith(receiveNLU)) {
                //extractNLUOutput(message.substring(receiveNLU.length() + 1));
            } else if (message.startsWith(receiveNLU)) {
                extractNLUOutput(message.substring(receiveNLU.length() + 1));
            } else if (message.startsWith(receiveResultFromOscarWEFConn)) {
                //extractRecommResultsOscarWEFConn(message.substring(receiveResultFromOscarWEFConn.length() + 1));
                //System.err.println(message);
            } else if(message.startsWith(receiveExpress)){
                extractExpress( message.substring(receiveExpress.length() + 1) );
            }
        }
    }

    private void extractSpoke(String message) {
        String[] messages = message.split(" ");
        if(messages != null && messages.length > 0 && !message.contains("stroke")) {
            String utterance = "";
            for (int i = 3; i < messages.length; i++) {
                utterance += messages[i] + " ";
            }
            MainController.systemUtterance = utterance;
        }
    }

    private void extractExpress(String message) {
        int begin = message.indexOf("strategy=") + 10;
        int end =  message.indexOf("rapport=") - 2;
        if( begin >= 0 && end >= 0 ) {
            MainController.wozerOutput = message.substring(begin, end);
        }
    }

    private void extractNLG(String message) {
        MainController.setNonVerbalWindow( true );
    }

    private void extractNonVerbalsMultisense(String message) {
        String[] values = message.split(" ");
        String agentId = values[0];
        MainController.setNonVerbals(values[1], values[3]);
        mainController.addContinousStates( null );
    }

    private void extractNonVerbalsBEAT(String message) {
        String[] values = message.split(" ");
        String agentId = values[0];
        MainController.setNonVerbals(values[1], values[2]);
        mainController.addContinousStates( null );
    }

    private void extractNLUOutput(String nluOutputString) {
        if( !MainController.useFakeNLU ) {
            String agentId = nluOutputString.substring(0, 1);
            String json = nluOutputString.substring(2);
            if( json.contains("vrNlu") ){       //ignore
                json = json.substring(json.lastIndexOf("vrNlu") + 8);
            }
            NLUOutput nluOutput = Utils.fromJsonString(json, NLUOutput.class);
            //TaskReasoner.getInstance().processNLUOutput(nluOutput);
        }else{
            receiver.unsubscribeMessage(receiveNLU);
        }
    }

    private void extractASRoutput(String message) {
        MainController.userUtterance = message.substring(2);
//        if( MainController.useFakeNLU ) {
//            message = message.replace(".", "");
//            message = message.substring(2);
//            if( MainController.useSRPlot ) {
//                inputEmulator.extractInputa(message);
//            }
//        }
    }

    private void extractTaskReasonerControl(String message){
        System.out.println("TaskReasonerControl message: " + message);
        String[] input = message.split(" ");
        MainController.useTRNotWoZ = Boolean.valueOf(input[1]);
        if( MainController.useTRNotWoZ ) { //input.length > 2 && !input[2].equals("-")
//            TaskReasoner.inputFromUser = input[2];
//            System.out.println("Wozing: " + TaskReasoner.inputFromUser);
//            mainController.intentsQueue.clear();
            MainController.useWoZFlag = true;
//            TaskReasoner.getInstance().processIntent();
            MainController.noInputFlag = false;
        }
    }

    private void extractTaskReasonerIntent(String message) {
        if( !message.contains("topintents") ) {
            mainController.addSystemIntent(message);
        }
    }

    private void extractRapportEstimator(String message) {
        String[] values = message.split(" ");
        String agentId = values[0];
        MainController.calculateRapScore(values[1]);
        mainController.addContinousStates( null );
    }

    private void extractConversationalStrategy(String message) {
        mainController.calculateUserConvStrategy( message );
        mainController.addContinousStates( null );
    }

    public void sendToNLG(String json){
        if( !simulateVHT ) {
            sender.sendMessage(sendMsgToNLG + " " + json);
            if (verbose) System.out.println("Sending to NLG: " + json + "\n\n");
        }
    }

    public void sendToClassifier(String convStrategy){
        if( !simulateVHT ) {
            sender.sendMessage(sendMsgToClassifier + " 0 " + convStrategy);
        }
    }

    public void sendMessage(String message) {
        if( simulateVHT ) {
            processMessage(message);
        }else{
            sender.sendMessage(message);
        }
    }
}
