package wef.articulab.control.controllers;

import wef.articulab.control.bn.BehaviorPlus;
import wef.articulab.control.bn.BehaviorNetworkPlus;
import wef.articulab.model.blackboard.Blackboard;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by oscarr on 4/28/16.
 */
public class BehaviorNetworkController {
    protected BehaviorNetworkPlus network;
//    protected List<String> states = new Vector<>();
    protected CopyOnWriteArrayList<String> states = new CopyOnWriteArrayList<>();
    protected List<String> goals = new Vector<>();
    protected List<BehaviorPlus> modules = new Vector<>();
    protected int NUM_BEHAVIORS;
    protected int NUM_VARIABLES;
    protected String title;
    protected String[] series;
    protected Blackboard blackboard = Blackboard.getInstance();

    public BehaviorNetworkController() {}

    protected String name;

    public BehaviorNetworkPlus getBN() {
        return network;
    }

    public void setBN(BehaviorNetworkPlus bn) {
        this.network = bn;
    }

    public BehaviorNetworkPlus createBN(){
        return null;
    }

    public String[] getSeries() {
        return series;
    }

    public String getTitle() {
        return title;
    }

    public String getName(){
        return name;
    }

    public String extractState( String state ){return null;};

    public String removeState( String state ){return null;}

    public String getStates() {
        String stateString = "";
        for( String state : states ){
            stateString += state + ":";
        }
        return stateString;
    }

    public String getDelList(){
        String deleteString = "";
        try {
            if (network.isRemovePrecond()) {
                for (List<String> preconds : network.getModules().get(network.getIdxBehActivated()).getPreconditions()) {
                    for (String precond : preconds) {
                        deleteString += precond + ":";
                    }
                }
            } else {
                List<String> deleteList = network.getModules().get(network.getIdxBehActivated()).getDeleteList();
                for (String state : deleteList) {
                    deleteString += state + ":";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return deleteString;
    }

    public String getAddList(){
        List<String> addList = network.getModules().get( network.getIdxBehActivated() ).getAddList();
        String addString = "";
        for( String state : addList ){
            if( !state.contains("_history") ) {
                addString += state + ":";
            }
        }
        return addString;
    }

    public boolean isOverThreshold(){
        double threshold = network.getTheta();
        for(double activation : network.getActivations()){
            if( activation > threshold){
                return true;
            }
        }
        return false;
    }

    public CopyOnWriteArrayList<String> getStatesList() {
        return network.getState();
    }

    public BehaviorNetworkPlus getNetwork() {
        return network;
    }

    public int getSize() {
        return network.getModules().size();
    }
}
