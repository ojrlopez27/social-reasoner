package wef.articulab.control.emulators;

import wef.articulab.control.vht.VHTConnector;
import wef.articulab.model.intent.SystemIntent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by oscarr on 1/4/17.
 */
public class TianjinEmulator {
    protected boolean stop = false;
    protected StringBuffer stringBuffer;
    protected int position = 0;
    protected VHTConnector vhtConnector;
    private static TianjinEmulator instance;
    private String fileName;

    protected TianjinEmulator(){}

    public static TianjinEmulator getInstance() {
        if (instance == null) {
            instance = new TianjinEmulator();
        }
        return instance;
    }

    private void initialize(){
        position = 0;
        stop = false;
        stringBuffer = null;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void loadFile(String filePath){
        try {
            fileName = filePath;
            initialize();
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                int idx = line.indexOf("vr");
                //System.out.println(line.substring(0, idx > 0? idx : line.length()));
                if( idx != -1 ) {
                    stringBuffer.append(line.substring(idx));
                    stringBuffer.append("\n");
                }else{
                    if( line.contains("strategy") ){
                        stringBuffer.deleteCharAt( stringBuffer.length() -1 );
                        stringBuffer.append(line);
                        stringBuffer.append("\n");
                    }
                }
            }
            fileReader.close();
            vhtConnector = VHTConnector.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendMessages(){
        while (!stop) {
            int posTemp = stringBuffer.indexOf("\n", position);
            if (posTemp >= 0) {
                String message = stringBuffer.substring(position, posTemp);
                //System.out.println("(" + position + ", " + posTemp + ", " + stringBuffer.length() + ") " + message);
                vhtConnector.sendMessage(message);
                if (message.startsWith("vrTaskReasoner set ")) {
                    stop = true;
                }
                if( posTemp >= stringBuffer.length() - 1 ){ //message.contains("vrTaskReasonerExecution reset")
                    return true;
                }
            }
            position = posTemp + 1;
        }
        return false;
    }
}
