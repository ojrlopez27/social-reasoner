package wef.articulab.view.emulators;

import wef.articulab.control.MainController;
import wef.articulab.model.Constants;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * Created by oscarr on 4/29/16.
 *
 * Rapport Estimator, Task Reasoner, Multisense, Conversational Strategy Classifier
 */
public class InputEmulator extends JPanel{
    private JLabel userCSLabel;
    private JComboBox<String> ucsComboBox;
    private JCheckBox gazeCheck;
    private JLabel emotionLabel;
    private JComboBox<String> emotionComboBox;
    private JCheckBox nodCheck;
    private JPanel verbalPanel;
    private JLabel phaseLabel = null;
    private JComboBox<String> phaseComboBox = null;
    private JComboBox<String> userIntentBox = null;
    private JLabel jitterLabel = null;
    private JTextField jitterText = null;
    private JLabel intentionLabel = null;
    private JLabel shimmerLabel = null;
    private JTextField intentionText = null;
    private JTextField shimmerText = null;
    public JTextArea userInput = null;
    private Scanner scanner = new Scanner(System.in);
    private JPanel nonVerbalPanel;
    private JLabel rapportScoreLabel;
    private JCheckBox smileCheck;
    private JComboBox<Integer> rapportComboBox;
    private InputController controller;
    private String input = "";
    private int numUtterance = 1;
    public JTextArea rapportScoreTA;
    public JTextArea ucsTextArea;
    private enum Mode { INSERT, COMPLETION };
    private List<ComparableString> words;
    private Mode mode = Mode.INSERT;

    public InputEmulator(){
        super();
        controller = new InputController(this);

        setBorder(BorderFactory.createTitledBorder("User's Utterance") );
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        GridBagConstraints gbc = new GridBagConstraints();

        nonVerbalPanel = new JPanel();
        nonVerbalPanel.setBorder(BorderFactory.createTitledBorder("Non-Verbal"));
        nonVerbalPanel.setLayout(new GridBagLayout());

        rapportScoreLabel = new JLabel("Rapport Score:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nonVerbalPanel.add(rapportScoreLabel, gbc);

        rapportComboBox = new JComboBox<>( new Integer[]{1, 2, 3, 4, 5, 6, 7} );
        rapportScoreTA = new JTextArea("4");
        rapportScoreTA.setPreferredSize( new Dimension(180, 15) );
        rapportScoreTA.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                e.consume();
                if( e.getKeyCode() == KeyEvent.VK_ENTER) {
                    MainController.calculateRapScore(rapportScoreTA.getText());
                }
            }
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
//        nonVerbalPanel.add(rapportComboBox, gbc);
        nonVerbalPanel.add(rapportScoreTA, gbc);

        smileCheck = new JCheckBox("Smile");
        smileCheck.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
//        nonVerbalPanel.add(smileCheck, gbc);

        userCSLabel = new JLabel("User Conv. Strategy:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nonVerbalPanel.add(userCSLabel, gbc);

        ucsComboBox = new JComboBox<>( new String[]{Constants.ADHERE_SOCIAL_NORM, Constants.SELF_DISCLOSURE,
                Constants.QUESTION_ELICIT_SD, Constants.SHARED_EXPERIENCES,
                Constants.PRAISE, Constants.VIOLATION_SOCIAL_NORM, Constants.BACK_CHANNEL} );
        if( MainController.userConvStrategy == null || MainController.userConvStrategy.isEmpty() ) {
            MainController.userConvStrategy = (String) ucsComboBox.getSelectedItem();
        }
        ucsComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainController.userConvStrategy = (String)ucsComboBox.getSelectedItem();
            }
        });
        ucsTextArea = new JTextArea();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.ipadx = 100;
        gbc.ipady = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nonVerbalPanel.add(ucsComboBox, gbc);
//        nonVerbalPanel.add(ucsTextArea, gbc)

        gazeCheck = new JCheckBox("Eye Gaze");
        gazeCheck.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nonVerbalPanel.add(gazeCheck, gbc);


        emotionLabel = new JLabel("Delay:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nonVerbalPanel.add(emotionLabel, gbc);

        emotionComboBox = new JComboBox( new String[]{"Happy", "Sad", "Angry", "Disgust", "Surprise", "Fear"} );
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextArea delay = new JTextArea("1000");
        delay.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    MainController.delayMainLoop = Long.valueOf(delay.getText());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
//        nonVerbalPanel.add(emotionComboBox, gbc);
        nonVerbalPanel.add(delay, gbc);

        nodCheck = new JCheckBox("Head Nod");
        nodCheck.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
//        nonVerbalPanel.add(nodCheck, gbc);
        
        verbalPanel = new JPanel();
        verbalPanel.setBorder(BorderFactory.createTitledBorder("Verbal"));
        verbalPanel.setLayout(new GridBagLayout());
        verbalPanel.setPreferredSize( new Dimension(500, 500) );

        phaseLabel = new JLabel("Phase:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        verbalPanel.add(phaseLabel, gbc);

        phaseComboBox = new JComboBox<>( new String[]{"Greeting", "Introduction", "Task", "Farewell"} );
        phaseComboBox.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.ipadx = 100;
        gbc.ipady = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        verbalPanel.add(phaseComboBox, gbc);

        jitterLabel = new JLabel("Jitter:");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        //verbalPanel.add(jitterLabel, gbc);

        jitterText = new JTextField("1.0");
        jitterText.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        //verbalPanel.add(jitterText, gbc);


        intentionLabel = new JLabel("Intention:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        verbalPanel.add(intentionLabel, gbc);

//        intentionText = new JTextField( "GREETING_WORD" );
        userIntentBox = new JComboBox<>( new String[]{
                Constants.SELF_NAMING,
                Constants.POSITIVE_CONFIRMATION,
                Constants.NEGATIVE_CONFIRMATION,
                Constants.RECOMMEND_SESSION,
                Constants.RECOMMEND_PEOPLE,
                Constants.MULTIPLE_GOALS,
                Constants.LIKE,
                Constants.WORK_INTEREST,
                Constants.DISLIKE,
                Constants.THANKS,
                Constants.FAREWELL,
                Constants.PRE_FAREWELL,
                Constants.FIND_SESSION_DETAIL,
                Constants.FIND_PERSON,
                Constants.RECOMMEND_FOOD,
                Constants.RECOMMEND_PARTY,
                Constants.REQUEST_FOOD_RECOMMENDATION,
                Constants.REQUEST_PERSON_RECOMMENDATION,
                Constants.REQUEST_SESSION_RECOMMENDATION,
                "not_recognized_user_intent"} );
        userIntentBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = (String)userIntentBox.getSelectedItem();
                //TaskReasoner.getInstance().createDummyGoals(input);
            }
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.ipadx = 100;
        gbc.ipady = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        verbalPanel.add(userIntentBox, gbc);


        shimmerLabel = new JLabel("Shimmer:");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        //verbalPanel.add(shimmerLabel, gbc);

        shimmerText = new JTextField("1.0");
        shimmerText.setEnabled(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        //verbalPanel.add(shimmerText, gbc);

        userInput = new JTextArea();
        userInput.setEnabled(false);
        DefaultCaret caret = (DefaultCaret)userInput.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPane = new JScrollPane(userInput);
        scrollPane.setPreferredSize( new Dimension(250, 250));
        userInput.setLineWrap(true);
        userInput.setWrapStyleWord(true);

        userInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                e.consume();
                String text = userInput.getText().toLowerCase();
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processInput(text);
                }else if(  e.getKeyCode() == KeyEvent.VK_BACK_SPACE ){
//                    pressBackSpace();
                }else if( e.getKeyCode() == KeyEvent.VK_DOWN) {
                    acceptAutoComplete();
                }else{
                    processAutoComplet(text);
                }
            }
        });
        userInput.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                input = userInput.getText();
                userInput.setText("");
            }

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.ipadx = 410;
        gbc.ipady = 120;
        gbc.fill = GridBagConstraints.BOTH;
        verbalPanel.add( scrollPane, gbc);

        add(Box.createRigidArea(new Dimension(0, 10)));
        add(nonVerbalPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(verbalPanel);

        words = new ArrayList( getComparableStrings(controller.mappings.keySet()));
        Collections.sort(words);
    }



    private void processInput(String text) {
        controller.extractPremises( text );
        String line = numUtterance + ". " + text;
        input += input.isEmpty()? line : "\n\n" + line;
        userInput.setText( input );
        numUtterance++;

        String[] result = controller.extractNonVerbal( MainController.behavior );
        rapportScoreTA.setText( result[0] );
        ucsTextArea.setText( result[1] );
        phaseComboBox.requestFocus();
    }

    private void processAutoComplet(String text){
        if (text.isEmpty()) {
            return;
        }

        int pos = text.length() - 1;
        String content = text.substring(0, pos + 1);

        // Find where the word starts
        int w;
        for (w = pos; w >= 0; w--) {
            if (! Character.isLetter(content.charAt(w))) {
                break;
            }
        }
        if (pos - w < 1) {
            // Too few chars
            return;
        }

//        ComparableString prefix = new ComparableString(content.substring(w + 1).toLowerCase());
//        int n = Collections.binarySearch(words, prefix);
//        if (n < 0 && -n <= words.size()) {
//            ComparableString match = words.get(-n - 1);
//            if (match.string.startsWith(prefix.string)) {
//                // A completion is found
//                String completion = match.string.substring(pos - w);
//                // We cannot modify Document from within notification,
//                // so we submit a task that does the change later
//                SwingUtilities.invokeLater(
//                        new CompletionTask(completion, pos + 1));
//            }
//        } else {
//            // Nothing found
//            mode = Mode.INSERT;
//        }
    }

    public void start(){
        while( true ) {
            System.out.println("Enter your input:");
            String input = scanner.nextLine();
            if (!input.equals(".")) {
                System.err.println("INPUT: " + input);
                controller.extractPremises(input);
            }
        }
    }

    public InputController getController(){
        return controller;
    }

    public static void main(String args[]){
        new InputEmulator();
    }


    private class CompletionTask implements Runnable {
        String completion;
        int position;

        CompletionTask(String completion, int position) {
            this.completion = completion;
            this.position = position;
        }

        public void run() {
            //pressBackSpace();
            userInput.insert(completion, position);
            userInput.setCaretPosition(position + completion.length());
            userInput.moveCaretPosition(position);
            mode = Mode.COMPLETION;
        }
    }

    private void pressBackSpace(){
        String text = userInput.getText();
        if( text != null && !text.isEmpty() ) {
            try {
                userInput.setText(text.substring(0, text.length() - 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void acceptAutoComplete(){
        if (mode == Mode.COMPLETION) {
            int pos = userInput.getSelectionEnd();
            userInput.insert(" ", pos);
            userInput.setCaretPosition(pos + 1);
            mode = Mode.INSERT;
        } else {
            userInput.replaceSelection("\n");
        }
    }

    private List getComparableStrings(Collection<String> strings) {
        List tranformed = new ArrayList<>();
        for( String string : strings ){
            tranformed.add( new ComparableString(string) );
        }
        return tranformed;
    }

    class ComparableString implements Comparable{
        String string;

        public ComparableString(String string) {
            this.string = string;
        }

        @Override
        public int compareTo(Object o) {
            if( o instanceof ComparableString){
                ComparableString input = (ComparableString)o;
                int isEqualOrContain = this.string.compareTo(input.string);
                if( isEqualOrContain != 0){
                    if(string.contains(input.string) && !string.startsWith(input.string) ){
                        return 0;
                    }else{
                        return  isEqualOrContain;
                    }
                }
                return 0;
            }
            throw new IllegalStateException("This is not a String");
        }
    }
}


