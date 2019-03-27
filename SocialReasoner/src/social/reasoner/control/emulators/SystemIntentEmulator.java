package social.reasoner.control.emulators;

import social.reasoner.control.MainController;
import social.reasoner.model.intent.SystemIntent;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by oscarr on 11/29/16.
 */
public class SystemIntentEmulator {
    private List<SystemIntentStep> steps = new ArrayList<>();
    private boolean inputFromConsole = false;
    private Scanner scanner = new Scanner(System.in);
    private ExcelReaderWriter excelReaderWriter;

    public boolean createScript(ExcelReaderWriter excelReaderWriter){
        this.excelReaderWriter = excelReaderWriter;
        excelReaderWriter.openWorkbook("SocialReasonerTests.xlsx", true);
        steps = excelReaderWriter.readSheet();
        System.out.println("Scenario: " + excelReaderWriter.getScenarioName() );
        return !steps.isEmpty();
    }

    public SystemIntent execute(){
        SystemIntent scriptResult = null;
        if( inputFromConsole ){
            System.out.println("Enter your input (system_intent phase UserConvStrat smile gaze) :");
            String[] input = scanner.nextLine().split(" ");

        }else{
            if( !steps.isEmpty() ){
                SystemIntentStep scriptStep = steps.get(0);
                MainController.userCSHistory.add( );
                MainController.calculateRapScore(String.valueOf(scriptStep.getRapportScore()));
                MainController.setNonVerbals(scriptStep.isSmiling(), scriptStep.isGazeAtPartner());
                MainController.userConvStrategy = scriptStep.getUserCS();
                MainController.setAvailableSE(scriptStep.isAvailableSharedExperiences());
                scriptResult = new SystemIntent( scriptStep.getIntent(), scriptStep.getPhase() );
                if( MainController.verbose ) {
                    System.out.println("\n\n=============================================================\n" +
                            "Running Emulated Step: " + scriptStep.toString());
                }
                steps.remove( scriptStep );
//                scanner.nextLine();
            }
        }
        return scriptResult;
    }

    public boolean isEmpty() {
        return steps.isEmpty();
    }

    public boolean checkReset() {
        return isEmpty() && !excelReaderWriter.checkFinished();
    }

    public void writeComparison() {
        excelReaderWriter.writeComparison();
    }
}
