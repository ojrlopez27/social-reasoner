package wef.articulab.control.emulators;

import wef.articulab.control.MainController;
import wef.articulab.control.util.Utils;
import wef.articulab.model.Constants;

import java.util.ArrayList;

/**
 * Created by oscarr on 9/12/16.
 */
public class Emulator extends Thread {
    private ArrayList<EmulationStep> steps = new ArrayList();
    private boolean stop = false;

    @Override
    public void run() {
//        // incremental engagement
//        steps.add( new EmulationStep( 1, Constants.ASN_USER_CS, false, false, 1 ) );
//        steps.add( new EmulationStep( 2, Constants.ASN_USER_CS, false, true, 2 ) );
//        steps.add( new EmulationStep( 2, Constants.ASN_USER_CS, false, false, 3 ) );
//        steps.add( new EmulationStep( 3, Constants.ASN_USER_CS, false, true, 4 ) );
//        steps.add( new EmulationStep( 3, Constants.PR_USER_CS, false, false, 5 ) );
//        steps.add( new EmulationStep( 4, Constants.SD_USER_CS, false, true, 6 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, true, true, 7 ) );
//        steps.add( new EmulationStep( 4, Constants.SD_USER_CS, false, false, 8 ) );
//        steps.add( new EmulationStep( 5, Constants.SD_USER_CS, false, true, 9 ) );
//        steps.add( new EmulationStep( 5, Constants.PR_USER_CS, false, true, 10 ) );
//        steps.add( new EmulationStep( 5, Constants.VSN_USER_CS, true, true, 11 ) );
//        steps.add( new EmulationStep( 5, Constants.SD_USER_CS, false, true, true, 12 ) );
//        steps.add( new EmulationStep( 5, Constants.PR_USER_CS, true, true, 13 ) );
//        steps.add( new EmulationStep( 6, Constants.SD_USER_CS, false, true, 14 ) );
//        steps.add( new EmulationStep( 6, Constants.RSE_USER_CS, false, true, 15 ) );
//        steps.add( new EmulationStep( 6, Constants.VSN_USER_CS, true, true, 16 ) );
//        steps.add( new EmulationStep( 6, Constants.SD_USER_CS, false, true, 17 ) );
//        steps.add( new EmulationStep( 7, Constants.SD_USER_CS, true, true, 18 ) );
//        steps.add( new EmulationStep( 7, Constants.VSN_USER_CS, false, true, 19 ) );
//        steps.add( new EmulationStep( 7, Constants.PR_USER_CS, true, true, 20 ) );

        //flat user
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, false, 1 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, true, 2 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, false, 3 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, true, 4 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, false, 5 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, true, 6 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, true, 7 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, false, 8 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, true, 9 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, true, 10 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, true, 11 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, true, 12 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, true, 13 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, true, 14 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, true, 15 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, true, 16 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, true, 17 ) );
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, true, 18 ) );
//        steps.add( new EmulationStep(4, Constants.ASN_USER_CS, false, true, 19));
//        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, true, 20 ) );

        // low rapport
        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, false) );
        steps.add( new EmulationStep( 5, Constants.ASN_USER_CS, false, false ) );
        steps.add( new EmulationStep( 6, Constants.ASN_USER_CS, false, false ) );
        steps.add( new EmulationStep( 6, Constants.ASN_USER_CS, false, false) );
        steps.add( new EmulationStep( 7, Constants.PR_USER_CS, false, false ) );
        steps.add( new EmulationStep( 7, Constants.ASN_USER_CS, false, false ) );
        steps.add( new EmulationStep( 6, Constants.ASN_USER_CS, false, false ) );
        steps.add( new EmulationStep( 6, Constants.ASN_USER_CS, false, false ) );
        steps.add( new EmulationStep( 5, Constants.ASN_USER_CS, false, false ) );
        steps.add( new EmulationStep( 5, Constants.PR_USER_CS, false, false ) );
        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, false ) );
        steps.add( new EmulationStep( 4, Constants.ASN_USER_CS, false, false ) );
        steps.add( new EmulationStep( 3, Constants.PR_USER_CS, false, false ) );
        steps.add( new EmulationStep( 3, Constants.ASN_USER_CS, false, false ) );
        steps.add( new EmulationStep( 2, Constants.ASN_USER_CS, false, false ) );
        steps.add( new EmulationStep( 2, Constants.VSN_USER_CS, false, false ) );
        steps.add( new EmulationStep( 2, Constants.ASN_USER_CS, false, false ) );
        steps.add( new EmulationStep( 1, Constants.ASN_USER_CS, false, false ) );
        steps.add( new EmulationStep( 1, Constants.ASN_USER_CS, false, false ) );
        steps.add( new EmulationStep( 1, Constants.PR_USER_CS, false, false ) );
        while( !stop ){
            Utils.sleep(50);
        }
    }

    public EmulationStep execute(int step){
        EmulationStep scriptResult = null;
        if( !steps.isEmpty() ){
            EmulationStep scriptStep = steps.get(0);
            MainController.calculateRapScore(String.valueOf(scriptStep.getRapportScore()));
            MainController.setNonVerbals(scriptStep.isSmiling(), scriptStep.isGazeAtPartner());
            MainController.userConvStrategy = scriptStep.getUserCS();
            MainController.userCSHistory.add( );
            MainController.setAvailableSE(scriptStep.isAvailableSharedExperiences());
            scriptResult = scriptStep;
            steps.remove( scriptStep );
        }
        return scriptResult;
    }
}
