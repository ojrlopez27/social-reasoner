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
 * ------------------------------
 * CombinedCategoryPlotDemo1.java
 * ------------------------------
 * (C) Copyright 2008, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   ;
 *
 * Changes
 * -------
 * 05-May-2008 : Version 1 (DG);
 *
 */

package wef.articulab.view.ui;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.plot.CombinedCategoryPlot;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * A demo for the {@link CombinedCategoryPlot} class.
 */
public class BNCategoryPlot extends ApplicationFrame {

    private static CategoryDataset dataset;
    private static String[] series;
    private static int time = 0;
    private static ArrayList<Double>[] behaviors;
    private static JFreeChart chart;
    private static int maxNumSamples = 20;
    private static double threshold;
    private static int idxBehActivated = -1;
    private static double activation;

    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public BNCategoryPlot(String title, String[] series) {
        super(title);
        this.series = series;
        JPanel chartPanel = createDemoPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(1100, 1000));
        setContentPane(chartPanel);
    }

    /**
     * Creates a dataset.
     *
     * @return A dataset.
     */
    public static CategoryDataset createDataset() {
        return new DefaultCategoryDataset();
    }

    public static CategoryDataset refreshDataset(){
        if( behaviors[0].size() < maxNumSamples ) {
            if (behaviors != null) {
                int sizeSeries = series.length - 1;
                for (int i = 0; i < sizeSeries; i++) {
                    int size = behaviors[i].size();
                    for (int j = 0; j < size; j++) {
                        ((DefaultCategoryDataset) dataset).addValue((Number) behaviors[i].get(j), series[i], time);
                    }
                }
                //activation threshold
                ((DefaultCategoryDataset) dataset).addValue((Number) threshold, series[sizeSeries], time);

                chart.getCategoryPlot().addAnnotation(new CategoryTextAnnotation("Hi Oscar", series[idxBehActivated], activation));

                //keep number of samples no bigger than maxNumSamples
                if (behaviors[0].size() > maxNumSamples) {
                    for (int i = 0; i < behaviors.length; i++) {
                        behaviors[i].remove(0);
                    }
                    ((DefaultCategoryDataset) dataset).removeColumn(0);
                }
            }
        }else{
            chart.getCategoryPlot().addAnnotation(new CategoryTextAnnotation("Hi Oscar", 10, activation));
        }
        return dataset;
    }

    /**
     * Creates a chart.
     *
     * @return A chart.
     */
    private static JFreeChart createChart() {

        dataset = createDataset();
        NumberAxis rangeAxis1 = new NumberAxis("Value");
        rangeAxis1.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setBaseToolTipGenerator(
                new StandardCategoryToolTipGenerator());
        CategoryPlot subplot = new CategoryPlot(dataset, null, rangeAxis1,
                renderer);
        subplot.setDomainGridlinesVisible(true);

        CategoryAxis domainAxis = new CategoryAxis("Time");
        CombinedCategoryPlot plot = new CombinedCategoryPlot(
                domainAxis, new NumberAxis("Activation"));
        plot.add(subplot);

        return new JFreeChart("Social Reasoner Plot",
                new Font("SansSerif", Font.BOLD, 12), plot, true);
    }

    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     *
     * @return A panel.
     */
    public static JPanel createDemoPanel() {
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

    public static BNCategoryPlot startPlot(String title, String[] series) {
        BNCategoryPlot demo = new BNCategoryPlot(title, series);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
        return demo;
    }

    public static void setSeries(String[] series) {
        BNCategoryPlot.series = series;
    }

    public static void setTime(int time) {
        BNCategoryPlot.time = time;
    }

    public void setDataset(ArrayList<Double>[] behaviors, double threshhold, int idxBehActivated, double activation) {
        BNCategoryPlot.behaviors = behaviors;
        BNCategoryPlot.threshold = threshhold;
        BNCategoryPlot.idxBehActivated = idxBehActivated;
        BNCategoryPlot.activation = activation;
        refreshDataset();
        chart.fireChartChanged();
        time++;
    }
}
