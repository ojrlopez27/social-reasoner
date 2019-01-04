package wef.articulab.model;

/**
 * Created by oscarr on 6/13/16.
 */
public class SocialReasonerOutput {
    private String[] names;
    private double[] activations;
    private double threshold;

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public double[] getActivations() {
        return activations;
    }

    public void setActivations(double[] activations) {
        this.activations = activations;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
