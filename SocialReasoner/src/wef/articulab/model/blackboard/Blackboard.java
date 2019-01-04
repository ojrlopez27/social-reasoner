package wef.articulab.model.blackboard;

import wef.articulab.control.controllers.BehaviorNetworkController;
import wef.articulab.model.Constants;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by oscarr on 4/29/16.
 */
public class Blackboard {
    private CopyOnWriteArrayList<String> model;
    private List<BlackboardListener> subscribers;
    private static Blackboard instance;
    private Lock lock;

    public static Blackboard getInstance(){
        if( instance == null ){
            instance = new Blackboard();
            instance.lock = new ReentrantLock();
        }
        return instance;
    }

    private Blackboard(){
        subscribers = new ArrayList<>();
    }

    public void setModel(CopyOnWriteArrayList<String> model) {
        this.model = model;
    }

    private void postMessages(String messagesString, String subscriberName){
        lock.lock();
        try {
            String[] messages = messagesString.split(":");
            for (String message : messages) {
                if (message != null && !message.equals("") && !message.equals("null")) {
                    model.remove(message);
                    model.add(message);
                }
            }
            notifySubscribers();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void removeMessages(String messagesString){
        lock.lock();
        try {
            if (messagesString.contains(Constants.REMOVE_ALL)) {
                model.clear();
            } else {
                String[] messages = messagesString.split(":");
                for (String message : messages) {
                    model.remove(message);
                }
            }
            notifySubscribers();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void removeMessagesContain(String historyState) {
        lock.lock();
        try {
            for (String state : model) {
                if (state.contains(historyState)) {
                    model.remove(state);
                }
            }
            notifySubscribers();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void removeStatesAndPhases(Collection<String> states ) {
        lock.lock();
        try {
            for (String state : states) {
                model.remove(state);
            }
            for (String element : model) {
                if (element.startsWith("phase_")) {
                    model.remove(element);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public CopyOnWriteArrayList<String> getModel() {
        return model;
    }

    public void subscribe(BlackboardListener subscriber){
        subscribers.add( subscriber );
    }

    public boolean unsubscribe(BlackboardListener subscriber){
        return subscribers.remove( subscriber );
    }

    public void notifySubscribers(){
        for( BlackboardListener subscriber : subscribers ){
            subscriber.updateModel( model );
        }
    }

    public BlackboardListener[] getSubscribers() {
        return subscribers.toArray( new BlackboardListener[ subscribers.size()] );
    }

    public void setStatesString(String state, String subscriber, BlackboardListener... controllers){
        if( state != null && !state.equals("")) {
            postMessages(state, subscriber);
            String extracted;
            if (controllers.length == 0) {
                controllers = getSubscribers();
            }
            String[] states = state.split(":");
            for (BlackboardListener controller : controllers) {
                if (controller instanceof BehaviorNetworkController) {
                    for (String stateString : states) {
                        if( stateString != null && !stateString.equals("null") ){
                            extracted = ((BehaviorNetworkController) controller).removeState(stateString);
                            if (extracted != null && !extracted.isEmpty()) {
                                removeMessages(extracted);
                            }
                            extracted = ((BehaviorNetworkController) controller).extractState(stateString);
                            if (extracted != null && !extracted.isEmpty()) {
                                postMessages(extracted, subscriber);

                            }
                        }
                    }
                }
            }
        }
    }


    public static void reset(){
        instance.model.clear();
    }

    public String search(String partialMatch) {
        String result = "NOT_" + partialMatch;
        try {
            lock.lock();
            for( String state : model ){
                if( state.equals(partialMatch) || state.startsWith(partialMatch) || state.startsWith("NOT_" + partialMatch) ){
                    result = state;
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
            return result;
        }
    }
}
