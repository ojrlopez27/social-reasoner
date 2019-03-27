/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2013, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * ------------------------
 * CombinedXYPlotDemo1.java
 * ------------------------
 * (C) Copyright 2008, 2009, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   ;
 *
 * Changes
 * -------
 * 05-May-2008 : Version 1 (DG);
 *
 */

package social.reasoner.view.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.plot.CombinedXYPlot;
import org.jfree.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A demonstration application showing a {@link CombinedXYPlot} with
 * two subplots.
 */
public class BNXYPlot extends ApplicationFrame {

    private XYSeriesCollection dataset;
    private String[] series;
    private ArrayList<Double>[] behaviors;
    private JFreeChart chart;
    private int maxNumSamples = 20;
    private ArrayList<Double> thresholds;
    private ArrayList<Object[]> activations;
    private int offset = 0;
    private IntervalMarker target;
    private double minThreshold = 99999;
    private double maxThreshold = 0;
    private double maxActivation = 0;

    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public BNXYPlot(String title, String[] series) {
        super(title);
        this.series = series;
        JPanel chartPanel = createPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(1100, 1000));
        setContentPane(chartPanel);
        thresholds = new ArrayList<>();
        activations = new ArrayList<>();
    }

    public IntervalXYDataset refreshDataset(){
        if (behaviors != null) {
            int sizeSeries = series.length - 1;

            //keep number of samples no bigger than maxNumSamples
            if (behaviors[0].size() > maxNumSamples) {
                for (int i = 0; i < behaviors.length; i++) {
                    behaviors[i].remove(0);
                }
                thresholds.remove(0);
                activations.remove(0);
                offset++;
            }

            //Activation thresholds
            XYSeries ts = dataset.getSeries(series[sizeSeries]);
            ts.clear();
            for (int j = 0; j < thresholds.size(); j++) {
                ts.addOrUpdate(j + offset, thresholds.get(j).doubleValue());
            }
            target.setStartValue( minThreshold );
            target.setEndValue( maxThreshold );

            //behaviors
            for (int i = 0; i < sizeSeries; i++) {
                int size = behaviors[i].size();
                ts = dataset.getSeries(series[i]);
                ts.clear();
                for (int j = 0; j < size; j++) {
                    ts.add(j + offset, behaviors[i].get(j).doubleValue());
                }
            }

            //who is activated?
            for (int x = 0; x < activations.size(); x++) {
                String name = (String)activations.get(x)[0];
                double y = (Double)activations.get(x)[1];
                if( name != null && !name.isEmpty() ) {
                    XYTextAnnotation annotation = new XYTextAnnotation( name, (x + offset), y);
                    annotation.setFont(new Font("SansSerif", Font.ITALIC, 11));
                    chart.getXYPlot().addAnnotation( annotation );
                }
            }
        }
        return dataset;
    }


    /**
     * Creates an overlaid chart.
     *
     * @return The chart.
     */
    private JFreeChart createChart() {
        createDataset();
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "Real Time Network Dynamics",
                "Time",
                "Activation",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        target = new IntervalMarker(14, 16);
        target.setLabel("Activation Threshold");
        target.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
        target.setLabelAnchor(RectangleAnchor.LEFT);
        target.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
        target.setPaint(new Color(222, 222, 255, 128));
        plot.addRangeMarker(target, Layer.BACKGROUND);
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        BasicStroke stroke = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        for(int i = 0; i < series.length-1; i++ ) {
            renderer.setSeriesStroke(i, stroke);
        }
        renderer.setSeriesStroke(series.length-1, new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f}, 0.0f ));
        plot.setRenderer(renderer);
        return chart;
    }

    /**
     * Creates a sample dataset.  You wouldn't normally hard-code the
     * population of a dataset in this way (it would be better to read the
     * values from a file or a database query), but for a self-contained demo
     * this is the least complicated solution.
     *
     * @return The dataset.
     */
    private void createDataset() {
        dataset = new XYSeriesCollection();
        for( String serieName : series ){
            XYSeries serie = new XYSeries( serieName );
            dataset.addSeries(serie);
        }
    }

    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     *
     * @return A panel.
     */
    public JPanel createPanel() {
        chart = createChart();
        return new ChartPanel(chart);
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {
        String title = "Social Reasoner Plot";
        startPlot(title, null);
    }

    public static BNXYPlot startPlot(String title, String[] series) {
        BNXYPlot plot = new BNXYPlot(title, series);
        plot.pack();
        RefineryUtilities.centerFrameOnScreen(plot);
        plot.setVisible(true);
        return plot;
    }

    public void setDataset(ArrayList<Double>[] behaviors, double threshhold, String nameBehActivated, double activation) {
        this.behaviors = behaviors;
        this.thresholds.add(threshhold);
        this.activations.add( new Object[]{nameBehActivated, activation} );

        maxThreshold = Collections.max(thresholds);
        minThreshold = Collections.min(thresholds);
        if( activation > maxActivation ){
            maxActivation = activation;
        }
        double portion = (maxThreshold - minThreshold) / 6;
        if( (portion * 6) > (maxActivation/4) ) {
            maxThreshold = threshhold + portion > maxThreshold? maxThreshold : threshhold + portion;
            minThreshold = threshhold - portion < minThreshold? minThreshold : threshhold - portion;
        }

        refreshDataset();
        chart.fireChartChanged();
    }
}
