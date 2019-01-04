package wef.articulab.control;

import wef.articulab.control.controllers.*;
import wef.articulab.control.emulators.DavosEmulator;
import wef.articulab.control.emulators.ExcelReaderWriter;
import wef.articulab.control.emulators.SystemIntentEmulator;
import wef.articulab.control.reasoners.*;
import wef.articulab.control.util.Utils;
import wef.articulab.control.vht.VHTConnector;
import wef.articulab.model.analisys.DataPoint;
import wef.articulab.model.history.UserCSHistory;
import wef.articulab.model.intent.SystemIntent;
import wef.articulab.view.emulators.InputController;
import wef.articulab.model.*;
import wef.articulab.model.blackboard.Blackboard;
import wef.articulab.model.history.SocialHistory;
import wef.articulab.view.ui.Visualizer;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**Questions
 * Created by oscarr on 4/22/16.
 */
public class MainController {
    public static boolean verbose = false;
    public static boolean isBeginningConversation = true;
    public static boolean isNonVerbalWindowON = true;
    private static int smileCount;
    private static int gazeCount;
    private static int noSmileCount;
    private static int noGazeCount;
    public static String userUtterance = "";
    public static String systemUtterance = "";
    private BehaviorNetworkController bnController;
    private SocialReasoner socialReasoner;
    public static UserCSHistory userCSHistory;
    public static SocialHistory socialHistory;

    private static Visualizer visualizer;
    private static boolean isFirstNonVerbal = true;
    private ExcelReaderWriter excelReaderWriter;
    public static boolean noInputFlag = true;
    public static Blackboard blackboard;
    public static String[] conversationalStrategies = new String[7];
    public static String behavior;
    public static boolean pause;
    public static MainController mainController;
    public static InputController inputController;
    public static boolean stop = false;
    public static Queue<SystemIntent> intentsQueue;
    private Lock lock;
    private Condition modelModifiedByTR;
    public static Properties properties = new Properties();
    public static int numberOfTurnsThreshold = 10;
    public static int currentTurn = 0;
    public static String outputResults = "";
    public static long vectorID = 0L;


    //flags
    public static boolean useWoZFlag = false;
    public static boolean flagStart = false;
    public static boolean flagStop = false;
    public static boolean flagReset = false;
    public static boolean flagResetTR = false;
    public static boolean useVHTConnnector;
    public static boolean useTianjinEmulator;
    public static boolean useFakeNLU;
    public static boolean useTRNotWoZ;
    public static boolean useFSM;
    public static boolean useSRPlot;
    public static boolean useManualMode;
    public static boolean useDummyGoals;
    private static boolean useEmulator;

    // preconditions
    public static String rapportDelta;
    public static String rapportLevel; // = Constants.HIGH_RAPPORT;
    public static double rapportScore = 2.0; // = 6;
    public static String userConvStrategy; // = Constants.VIOLATION_SOCIAL_NORM;
    public static String smile;
    public static String eyeGaze;

    //delays
    public static long delayMainLoop;
    public static long delayUserIntent;


    private boolean isFirstTime = true;
    private static SystemIntentEmulator emulator;
    //private static TianjinEmulator tianjinEmulator;
    private static DavosEmulator davosEmulator;
    private boolean isProcessingIntent;
    private SystemIntent previousIntent;
    private String jsonResults;
    public static SystemIntent trIntent;
    private static String availableSharedExp = "NOT_AVAILABLE";
    private static Double percSmileWindow;
    private static Double percGazeWindow;
    private String pathToAnnotatedLog;
    public static String pathToDavosData;
    private String pathToExcelOutput;
    private ArrayList<String> listOfDavosLogs;
    public static String wozerOutput;
    private List<DataPoint> listOfDataPoints;

    public static void main(String args[]){
        if( args.length > 0){
            useVHTConnnector = true;
            VHTConnector.serverIP = args[0];
            if( args.length > 1){
                delayMainLoop = Long.valueOf(args[1]);
            }
        }
        //tianjinEmulator = TianjinEmulator.getInstance();
        davosEmulator = DavosEmulator.getInstance();
        mainController = new MainController();
        mainController.loadDavosData();
        mainController.loadProperties();
        mainController.listOfDataPoints = new ArrayList<>();
        while( !stop ) {
            mainController.start();
            stop = mainController.listOfDavosLogs.isEmpty();
        }
        saveDataPoints();
        System.err.println("\nBye bye...");
        System.exit(0);
    }

    private static void saveDataPoints() {
        ExcelReaderWriter excel = new ExcelReaderWriter();
        excel.openWorkbook("DataPoints.xlsx", false);
        Collections.sort(mainController.listOfDataPoints );
        excel.writeSheet( mainController.listOfDataPoints );
        excel.writeAndClose();
    }

    public void start(){
        checkStart();
        while ( !flagReset && !stop){
            if( !pause ) {
                if( useEmulator ){
                    trIntent = emulator.execute();
                    addContinousStates( trIntent );
                    socialReasoner.execute( trIntent );
                    printOutput();
                    resetStates();
                    flagReset = emulator.checkReset();
                    stop = !flagReset && emulator.isEmpty();
                }
                if( useTianjinEmulator ){
                    //tianjinEmulator.sendMessages();
                    flagReset = davosEmulator.sendMessages();
                }

                try {
                    lock.lock();
                    if( flagReset ){
                        printOutput();
                    }
                    while (intentsQueue.size() > 0) {
                        isProcessingIntent = true;
                        trIntent = intentsQueue.poll();
                        setNonVerbalWindow(false);
                        addContinousStates(trIntent);
                        socialReasoner.execute(trIntent);
                        printOutput();
                        resetStates();
                        //tianjinEmulator.setStop(false);
                        davosEmulator.setStop(false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    isProcessingIntent = false;
                    modelModifiedByTR.signal();
                    lock.unlock();
                }
                isBeginningConversation = false;
                Utils.sleep(10);
            }
            checkReset();
        }
    }

    private void resetStates() {
        if(verbose) System.out.println("*** resetStates");
        smile = null;
        eyeGaze = null;
    }

    private void printOutput() {
        String output = vectorID +"\t"+ removeSufix(trIntent.getIntent() + "\t" + rapportScore  + "\t" + rapportLevel
                + "\t" + rapportDelta + "\t" + userConvStrategy  + "\t" + smile  + "\t" + eyeGaze + "\t" + availableSharedExp
                + "\t" + blackboard.search("NUM_TURNS") + "\t" + blackboard.search(Constants.ASN_HISTORY_SYSTEM)
                + "\t" + blackboard.search(Constants.VSN_HISTORY_SYSTEM) + "\t" + blackboard.search(Constants.SD_HISTORY_SYSTEM)
                + "\t" + blackboard.search(Constants.QESD_HISTORY_SYSTEM) + "\t" + blackboard.search(Constants.PR_HISTORY_SYSTEM)
                + "\t" + blackboard.search(Constants.ACK_HISTORY_SYSTEM) + "\t" + blackboard.search(Constants.RSE_HISTORY_SYSTEM)
                + "\t" + blackboard.search(Constants.ASN_HISTORY_USER) + "\t" + blackboard.search(Constants.VSN_HISTORY_USER)
                + "\t" + blackboard.search(Constants.SD_HISTORY_USER) + "\t" + blackboard.search(Constants.QESD_HISTORY_USER)
                + "\t" + blackboard.search(Constants.PR_HISTORY_USER) + "\t" + blackboard.search(Constants.ACK_HISTORY_USER)
                + "\t" + blackboard.search(Constants.RSE_HISTORY_USER) + "\t" + (wozerOutput + "_WOZER") + "\t" + (getConvStrategyFormatted() + "_SR")
                + "\t" + Arrays.toString(conversationalStrategies) + "\t" + socialReasoner.getOutput())
                + "\t" + socialReasoner.getStates() + "\t" + socialReasoner.getMatches() + "\t" + userUtterance
                + "\t" + systemUtterance + "\n";
        userUtterance = "";
        systemUtterance = "";
        checkIsDataPoint(output);
        if( verbose ) {
            System.out.print(output);
        }
        outputResults += output;
        vectorID++;
        if( useEmulator ){
            if( trIntent.getIntent().equals(Constants.FAREWELL) ) {
                excelReaderWriter.writeSheet( outputResults );
            }
        }
        if( useTianjinEmulator ){
            if( flagReset ) {
                excelReaderWriter.writeSheet(outputResults);
                excelReaderWriter.writeAndClose();
                System.out.println( (228-listOfDavosLogs.size()) + " Writing file: " +  excelReaderWriter.getFileName());
                stop = true;
            }
        }
    }

    private void checkIsDataPoint(String vector) {
        String[] dimensions = vector.split("\\t");
        if( (rapportDelta.equals(Constants.RAPPORT_INCREASED) || ( rapportDelta.equals(Constants.RAPPORT_MAINTAINED)
                && rapportLevel.equals(Constants.HIGH_RAPPORT))) && !dimensions[5].startsWith("NONE") && !dimensions[5].equals("null")
                && !dimensions[5].isEmpty() && !dimensions[24].startsWith("NONE") && !dimensions[24].equals("null") && !dimensions[24].isEmpty()){
            String id = excelReaderWriter.getFileName().substring( excelReaderWriter.getFileName().indexOf("/DavosResults/") +14 ) + "-" + dimensions[0];
            listOfDataPoints.add(new DataPoint( id, dimensions[1], dimensions[2], dimensions[3], dimensions[4],
                    dimensions[5], dimensions[6], dimensions[7], dimensions[8], dimensions[9], dimensions[10], dimensions[11],
                    dimensions[12], dimensions[13], dimensions[14], dimensions[15], dimensions[16], dimensions[17],
                    dimensions[18], dimensions[19], dimensions[20], dimensions[21], dimensions[22], dimensions[23],
                    dimensions[24], dimensions[25], dimensions[26], dimensions[27], dimensions[28], dimensions[29],
                    dimensions[30], dimensions[31], vector.replace(dimensions[0], id)));
        }
    }

    private String getConvStrategyFormatted() {
        if( conversationalStrategies == null || conversationalStrategies[0] == null ){
            return Constants.NONE;
        }
        return conversationalStrategies[0].equals( Constants.ACK_SYSTEM_CS )? Constants.ACK_SYSTEM_CS + " -> "
                + conversationalStrategies[1] : conversationalStrategies[0];
    }

    private String removeSufix(String s) {
        return s.replace("_NONVERBAL", "");//.replace("_SYSTEM_CS", "").replace("_USER_CS", "");
    }

    private void createSocialReasoner() {
        socialReasoner = SocialReasoner.getInstance(bnController, "ConversationalStrategyBN");
    }

    private void checkStart() {
        // waiting for confirmation to start the reasoning process
        while( !flagStart ){
            Utils.sleep( 100 );
        }
        if( flagStart || isFirstTime ){
            System.out.println("\nRe-starting...");
            intentsQueue = new LinkedList<>();
            lock = new ReentrantLock();
            modelModifiedByTR = lock.newCondition();

            MainController.userCSHistory = UserCSHistory.getInstance();
            excelReaderWriter = new ExcelReaderWriter();
            if( !useVHTConnnector && !useManualMode ){
                useEmulator = true;
            }
            if( useVHTConnnector ) {
                VHTConnector.getInstance( mainController );
                if( useTianjinEmulator ){
                    //tianjinEmulator.loadFile( pathToAnnotatedLog );
                    if( listOfDavosLogs.isEmpty() ){
                        flagStop = true;
                        stop = true;
                    }
                    String fileName = "";
                    fileName = listOfDavosLogs.remove(0);
                    davosEmulator.loadFile( fileName );
                    excelReaderWriter = new ExcelReaderWriter();
                    //excelReaderWriter.openWorkbook(pathToExcelOutput);
                    excelReaderWriter.openWorkbook(fileName, true );
                    excelReaderWriter.setColOffset(0);
                    System.out.println( (228-listOfDavosLogs.size()) + " Loading file: " +  fileName);
                }
            }else if( useEmulator ){
                emulator = new SystemIntentEmulator();
                if( !emulator.createScript( excelReaderWriter ) ){
                    emulator.writeComparison();
                    flagReset = true;
                    flagStop = true;
                    stop = true;
                    return;
                }
            }

            //singletons
            blackboard = Blackboard.getInstance();
            socialHistory = SocialHistory.getInstance();
            System.out.println("Creating a user model...... Done!!!!!!!!!!!!!!\n\n");
            visualizer = Visualizer.getInstance();
            //taskReasoner = TaskReasoner.getInstance(lock, modelModifiedByTR, this);

            bnController = new ConversationalStrategyBN();
            blackboard.setModel( bnController.getStatesList() );
            blackboard.subscribe((ConversationalStrategyBN) bnController);
            //model = taskReasoner.getModel();
            userConvStrategy = Constants.NONE_USER_CS; //model.getInitialUserConvStrat();
            rapportLevel = calculateRapScore( "2" );//model.getInitialRapportLevel()
            if( useSRPlot ) {
                visualizer.initializePlot(this, bnController, null);
                inputController = visualizer.getInputController();
            }else{
                inputController = new InputController(null);
            }
            if( useVHTConnnector ){
                VHTConnector.setInputController(inputController);
                //taskReasoner.setVhtConnector();
            }
            createSocialReasoner();
            isFirstTime = false;
            flagReset = false;
        }
    }

    private void checkReset() {
        if( flagReset ){
            Blackboard.reset();
            //TaskReasoner.reset();
            SocialReasoner.reset();
            SocialHistory.reset();
            UserCSHistory.reset();
            noInputFlag = true;
            conversationalStrategies = new String[7];
            pause = false;
            stop = false;
            intentsQueue.clear();
            intentsQueue = null;
            flagStop = false;
            flagStart = false;
            flagResetTR = false;
            System.out.println("Reseting...");
            reset();
            System.gc();
            mainController.loadProperties();
            outputResults = "";
        }
    }

    private void reset() {
        modelModifiedByTR = null;
        lock = null;
        bnController = null;
        socialReasoner = null;
        visualizer = null;
        noInputFlag = true;
        socialHistory = null;
        conversationalStrategies = new String[7];
        intentsQueue = null;
        currentTurn = 0;
    }

    public static String calculateRapScore(String score) {
        try {
            double scoreTemp = Double.parseDouble(score);
            rapportDelta = scoreTemp >= (rapportScore + .05)? Constants.RAPPORT_INCREASED : scoreTemp <= (rapportScore - .05)?
                    Constants.RAPPORT_DECREASED : Constants.RAPPORT_MAINTAINED;
            rapportScore = scoreTemp;
            rapportLevel = (rapportScore > 4.4? Constants.HIGH_RAPPORT : rapportScore < 3 ? Constants.LOW_RAPPORT
                    : Constants.MEDIUM_RAPPORT);
            if(mainController.useSRPlot ) {
                inputController.getInputEmulator().rapportScoreTA.setText(mainController.rapportScore + " : " + mainController.rapportLevel);
            }
            return rapportLevel;
        }catch (Exception e){
            return null;
        }
    }

    public static void setNonVerbals(String isSmiling, String whereEyeGaze ){
        if( isNonVerbalWindowON ){
            if( isSmiling.equals("smile") || isSmiling.equals("true") ){
                smileCount++;
            }else{
                noSmileCount++;
            }
            if( whereEyeGaze.equals("gaze_partner") || whereEyeGaze.equals("true") ){
                gazeCount++;
            }else{
                noGazeCount++;
            }
        }
    }

    public static void calculateNonVerbals(){
        if( isFirstNonVerbal ){
            smile = Constants.NOT_SMILE_NONVERBAL;
            eyeGaze = Constants.GAZE_ELSEWHERE_NONVERBAL;
            isFirstNonVerbal = false;
        }else {
            smile = smileCount >= (smileCount + noSmileCount) * percSmileWindow / 100.0 ? Constants.SMILE_NONVERBAL
                    : Constants.NOT_SMILE_NONVERBAL;
            eyeGaze = gazeCount >= (gazeCount + noGazeCount) * percGazeWindow / 100.0 ? Constants.GAZE_PARTNER_NONVERBAL
                    : Constants.GAZE_ELSEWHERE_NONVERBAL;
        }
        if(verbose)
            System.out.println("*** smileCount: " + smileCount + " noSmileCount: " + noSmileCount + " gazeCount: "
                + gazeCount + " noGazeCount: " + noGazeCount + " smile: " + smile + " eyeGaze: " + eyeGaze);
        smileCount = 0;
        gazeCount = 0;
        noSmileCount = 0;
        noGazeCount = 0;
    }

    /** we need a time window in order to calculate smile, gaze, etc. as user's non-verbals last several seconds**/
    public static void setNonVerbalWindow( boolean flag ){
        if(verbose) System.out.println("*** set window: " + flag);
        isNonVerbalWindowON = flag;
        if( !isNonVerbalWindowON ){
            calculateNonVerbals();
        }
    }

    public static void setNonVerbals(boolean isSmiling, boolean isGazeAtPartner ){
        smile = isSmiling? Constants.SMILE_NONVERBAL : Constants.NOT_SMILE_NONVERBAL;
        eyeGaze = isGazeAtPartner? Constants.GAZE_PARTNER_NONVERBAL : Constants.GAZE_ELSEWHERE_NONVERBAL;
    }

    public void addContinousStates( SystemIntent intent ) {
        if(intent != null ) {
            addNewIntent(intent);
            previousIntent = intent;
        }
        //We don't need current system CS, just system history CS
        //addSystemCSstates();
        socialHistory.addStates();
        userCSHistory.addStates();
        addUserCSstates();
        addRapportstates();
        addTurnstates();
        addNonVerbals();
    }

    private void addNewIntent(SystemIntent intent) {
        if( previousIntent != null ) {
            blackboard.removeMessages(previousIntent.getIntent() + ":" + previousIntent.getPhase() + ":" + "greeting" + ":" + "greetings");
        }
        blackboard.setStatesString(intent.getIntent() + ":" + intent.getPhase(), "MainController" );
    }

    private void addUserCSstates() {
        blackboard.removeMessagesContain( "USER_CS");
        blackboard.setStatesString( userConvStrategy, "mainController");
    }

    private void addSystemCSstates() {
        blackboard.removeMessagesContain( "SYSTEM_CS");
        blackboard.setStatesString( conversationalStrategies[0], "mainController");
    }

    private void addRapportstates() {
        blackboard.removeMessagesContain( "RAPPORT");
        blackboard.setStatesString( rapportDelta + ":" + rapportLevel, "mainController");
    }

    private void addTurnstates() {
        blackboard.removeMessagesContain( "NUM_TURNS");
        blackboard.setStatesString( currentTurn <= numberOfTurnsThreshold ? Constants.NUM_TURNS_LOWER_THAN_THRESHOLD
                : Constants.NUM_TURNS_HIGHER_THAN_THRESHOLD, "DMMain");
        if( currentTurn > numberOfTurnsThreshold){
            currentTurn = 0;
        }
    }

    private void addNonVerbals() {
        if( smile != null && eyeGaze != null ){
            blackboard.removeMessagesContain( "NONVERBAL");
            blackboard.setStatesString( smile + ":" + eyeGaze, "RapportEstimator");
        }
    }

    private void loadProperties(){
        InputStream input = null;
        try {
            input = new FileInputStream("config.properties");
            // load a properties file
            properties.load(input);

            // get the property value and print it out
            useVHTConnnector = Boolean.valueOf(properties.getProperty("useVHTConnnector"));
            useTianjinEmulator = Boolean.valueOf(properties.getProperty("useTianjinEmulator"));
            useFakeNLU = Boolean.valueOf(properties.getProperty("useFakeNLU"));
            useTRNotWoZ = Boolean.valueOf(properties.getProperty("useTRNotWoZ"));
            useFSM = Boolean.valueOf(properties.getProperty("useFSM"));
            useSRPlot = Boolean.valueOf(properties.getProperty("useSRPlot"));
            useManualMode = Boolean.valueOf(properties.getProperty("useManualMode"));
            delayMainLoop = Long.valueOf(properties.getProperty("delayMainLoop"));
            delayUserIntent = Long.valueOf(properties.getProperty("delayUserIntent"));
            useDummyGoals = Boolean.valueOf(properties.getProperty("useDummyGoals"));
            flagStart = Boolean.valueOf(properties.getProperty("shouldStartAutomatically"));
            numberOfTurnsThreshold = Integer.valueOf(properties.getProperty("numberOfTurnsThreshold"));
            percSmileWindow = Double.valueOf(properties.getProperty("percSmileWindow"));
            percGazeWindow = Double.valueOf(properties.getProperty("percGazeWindow"));
            verbose = Boolean.valueOf(properties.getProperty("verbose"));
            pathToAnnotatedLog = properties.getProperty("pathToAnnotatedLog");
            pathToDavosData = properties.getProperty("pathToDavosData");
            pathToExcelOutput = properties.getProperty("pathToExcelOutput");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //TODO: we still need to receive ACK, QESD and Hedging
    public void calculateUserConvStrategy(String message) {
        String[] values = message.split(" ");
        String agentId = values[0];
        boolean sd = Boolean.parseBoolean( values[2] );
        boolean se = Boolean.parseBoolean( values[5] );
        boolean pr = Boolean.parseBoolean( values[8] );
        boolean vsn = Boolean.parseBoolean( values[11] );
        boolean asn = Boolean.parseBoolean( values[14] );
        userConvStrategy =  sd? Constants.SD_USER_CS : se? Constants.RSE_USER_CS : pr? Constants.PR_USER_CS : vsn?
                Constants.VSN_USER_CS : asn? Constants.ASN_USER_CS : Constants.NONE_USER_CS;
        blackboard.setStatesString( userConvStrategy, "ConversationalStrategyClassifier");
        if( !userConvStrategy.equals(Constants.PR_USER_CS) ){
            blackboard.setStatesString( Constants.NOT_PR_USER_CS, "ConversationalStrategyClassifier");
        }

        String winner = "NONE";
        double max = 0;
        for(int i = 3; i < 11; i=i+3) {
            if (values[i] != null && Double.valueOf(values[i]) > max) {
                max = Double.valueOf(values[i]);
                winner = values[i-2];
            }
            if (mainController.useSRPlot){
                inputController.getInputEmulator().ucsTextArea.setText(winner);
            }
        }
        userCSHistory.add( System.currentTimeMillis(), winner, rapportLevel, rapportScore);
    }

    public static void setAvailableSE(boolean availableSharedExperiences) {
        if( availableSharedExperiences ) {
            blackboard.setStatesString( Constants.AVAILABLE_SHARED_EXPERIENCES, "DMMain");
            availableSharedExp = "AVAILABLE";
            return;
        }
        availableSharedExp = "NOT_AVAILABLE";
    }

    public void addSystemIntent(String message) {
        try {
            lock.lock();
            while ( isProcessingIntent ) {
                modelModifiedByTR.await();
            }
            String[] split = message.split(" ");
            boolean isSet = extractTypeOfTRmessage(split[0]);
            if(verbose) System.out.println("message: " + split[0]);
            if( isSet) {
                intentsQueue.add(new SystemIntent(split[2], split[1]));
                jsonResults = split[3];
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally{
            lock.unlock();
        }
    }

    private boolean extractTypeOfTRmessage(String message) {
        if( message.contains("start") ) {
            MainController.flagStart = true;
        }else if( message.contains("set") ){
            return true;
        } else if( message.contains( "reset" ) ){
            MainController.flagReset = true;
            return true;
        }else if( message.contains("stop") ){
            MainController.stop = true;
        }
        return false;
    }


    private void loadDavosData(){
        File directory = new File("/Users/oscarr/Development/WEF/WEF-SocialReasoner/Execution/DavosData/");
        if( directory.isDirectory() ){
            listOfDavosLogs = new ArrayList( Arrays.asList(directory.list()));
            List<String> remove = new ArrayList<>();
            for(String fileName : listOfDavosLogs){
                if( !fileName.endsWith(".txt") ){
                    remove.add(fileName);
                }
            }
            for(String fileToRemove : remove){
                listOfDavosLogs.remove(fileToRemove);
            }
        }
    }
}
