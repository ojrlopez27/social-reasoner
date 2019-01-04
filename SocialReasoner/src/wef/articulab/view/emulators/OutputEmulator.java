package wef.articulab.view.emulators;

import wef.articulab.control.MainController;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by oscarr on 4/29/16.
 */
public class OutputEmulator extends JPanel{
    private JTextArea output;
    private JTextArea states;
    private JPanel outputPanel;
    private String sysUtterance = "";
    private int utteranceNum = 1;
    private String statesOutput = "";
    private int cont;

    public OutputEmulator(){
        setBorder(BorderFactory.createTitledBorder("System's Utterance") );
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        output = new JTextArea();
        DefaultCaret caret = (DefaultCaret)output.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPane = new JScrollPane(output);
        scrollPane.setPreferredSize( new Dimension(250, 250));
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.ipadx = 410;
        gbc.ipady = 120;
        gbc.fill = GridBagConstraints.BOTH;
        outputPanel = new JPanel();
        outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
        outputPanel.setLayout(new GridBagLayout());
        outputPanel.setPreferredSize( new Dimension(500, 700) );
        outputPanel.add( scrollPane, gbc);
        add(outputPanel);
        add(Box.createRigidArea(new Dimension(0, 10)));

        states = new JTextArea();
        caret = (DefaultCaret)states.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        states.setEnabled(false);
        scrollPane = new JScrollPane(states);
        scrollPane.setPreferredSize( new Dimension(250, 250));
        states.setLineWrap(true);
        states.setWrapStyleWord(true);
        states.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.ipadx = 410;
        gbc.ipady = 120;
        gbc.fill = GridBagConstraints.BOTH;
        outputPanel = new JPanel();
        outputPanel.setBorder(BorderFactory.createTitledBorder("States"));
        outputPanel.setLayout(new GridBagLayout());
        outputPanel.setPreferredSize( new Dimension(500, 700) );
        outputPanel.add( scrollPane, gbc);
        add(outputPanel);

    }

    public synchronized void printFSMOutput(String systemUtterance){
        String prefix = "\n\n" + utteranceNum + ". ";
        sysUtterance += prefix + systemUtterance;
        output.setText(sysUtterance);
        utteranceNum++;
    }

    public void printOutput(String behaviorName, String description) {
        String outputString = "Activated Behavior: " + description + "\n\nSystem's Utterances: ";
        String prefix = "\n\n" + utteranceNum + ". ";
        if( behaviorName.equals("GW") ) {
            sysUtterance +=  prefix + "Hi, I'm Rachel";
        }else if( behaviorName.equals("SN") ) {
            sysUtterance += prefix + "I'm Rachel";
        }else if( behaviorName.equals("PACT") ) {
            sysUtterance += prefix + "Nice to meet you";
        }else if( behaviorName.equals("GQ") ) {
            sysUtterance += prefix + "How are you today?";
        }else if( behaviorName.equals("WC") ) {
            sysUtterance += prefix + "Welcome to WEF";
        }else if( behaviorName.equals("WOI") ){
            sysUtterance += prefix + "I'm here in Tianjin to be your personal assistant and help you to get the maximum out of the World Economic Forum Experience.";
        }else if( behaviorName.equals("AFTA") ){
            sysUtterance += prefix + "Is this your first time attending, or have you been before?";
        }else if( behaviorName.equals("SGE") ){
            sysUtterance += prefix + "If you wouldn't mind sharing your goals for attending the WEF this year, I can try to help you achieve them.";
        }else if( behaviorName.equals("EG") ){
            sysUtterance += prefix + "Why are you here - what are your goals in attending the WEF this year?";
        }else if( behaviorName.equals("GAK") ){
            sysUtterance += prefix + "Great. Good goals. Let's start by finding some interesting people for you to meet, relevant to your interests.";
        }else if( behaviorName.equals("SPR") ){
            sysUtterance += prefix + "Let's start by finding some interesting people for you to meet, relevant to your interests.";
        }else if( behaviorName.equals("QAB") ){
            sysUtterance += prefix + "Can you tell me a little bit about your work?";
        }else if( behaviorName.equals("RAK") ){
            sysUtterance += prefix + "Wow, great.";
        }else if( behaviorName.equals("OR") ){
            sysUtterance += prefix + "Even I find it overwhelming to browse through the names of fifteen hundred participants - it's easier to have somebody make recommendations.";
        }else if( behaviorName.equals("LL") ){
            sysUtterance += prefix + "Let me look.";
        }else if( behaviorName.equals("RMP") ){
            sysUtterance += prefix + "There's a researcher here from CMU who works on Advanced Manufacturing. He works on 3D printing of large objects, such as cars. ";
        }else if( behaviorName.equals("ASM") ){
            sysUtterance += prefix + "Sound like somebody you'd like to meet?";
        }else if( behaviorName.equals("SMA") ){
            sysUtterance += prefix + "OK, I can do one of two things: I can send a message on your behalf suggesting a meeting, or I can give you his information and you can contact him directly.";
        }else if( behaviorName.equals("SMR") ){
            sysUtterance += prefix + "OK. I'll use the messaging app, and will make it clear that i'm writing on your behalf. I'll send you a follow up when I hear back to help you find a place to meet.";
        }else if( behaviorName.equals("YW") ){
            sysUtterance += prefix + "You're welcome. I'm pretty new to all this, so I'm glad you find me helpful.";
        }else if( behaviorName.equals("MTD") ){
            sysUtterance += prefix + "So, shall we find some more people for you to meet with, or would you like me to suggest some sessions?";
        }else if( behaviorName.equals("RI") ){
            sysUtterance += prefix + "OK. Now, this may sound crazy, but in my experience some of the most useful sessions seem like they don't have anything to do with your area of expertise.";
        }else if( behaviorName.equals("SFI") ){
            sysUtterance += prefix + "You seem like an adventurous person: are you willing to hear about a session on the fashion industry?";
        }else if( behaviorName.equals("WD") ){
            sysUtterance += prefix + "Well, perhaps I could have guessed that from the way you were dressed, but I was giving you the benefit of the doubt!";
        }else if( behaviorName.equals("ECS") ){
            sysUtterance += prefix + "OK, back to being serious, there is a session you might enjoy on collaborative robotics.";
        }else if( behaviorName.equals("DSG") ){
            sysUtterance += prefix + "OK, here is the description of the session and the presenters";
        }else if( behaviorName.equals("ALH") ){
            sysUtterance += prefix + "So . . . alright . . . this seems like a start. Is there anything else I can help you with?";
        }else if( behaviorName.equals("FFCB") ){
            sysUtterance += prefix + "Well, feel free to come back. In the meantime, enjoy the meeting, and it was nice working with you.";
        }

        output.setText(outputString + sysUtterance);
        utteranceNum++;
    }

    public void printStates(String state){
        states.setText(state + "\n\nSent to NLG. CS: " + Arrays.toString(MainController.conversationalStrategies) + "  Phase: " +
                MainController.inputController.phase + "  Intention: " + MainController.behavior);
    }

    public void printStates(String state, String phase, String intention){
        String prefix = (++cont) + ". ";
        statesOutput += prefix + "Sent to NLG --> Conversational Strategy: " +Arrays.toString(MainController.conversationalStrategies) + ",  Phase: " +
                phase + ",  Intention: " + intention + "\n\nStates:\n" + state + "\n\n";
        states.setText( statesOutput );
    }

    class TextAreaOutputStream extends OutputStream {
        private JTextArea textArea;

        public TextAreaOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            // redirects data to the text area
            textArea.append(String.valueOf( (char)b) );
            // scrolls the text area to the end of data
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

}
