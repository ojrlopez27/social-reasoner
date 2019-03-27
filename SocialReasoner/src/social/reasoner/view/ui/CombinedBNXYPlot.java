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

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.plot.CombinedXYPlot;
import org.jfree.ui.*;
import social.reasoner.control.MainController;
import social.reasoner.view.emulators.InputController;
import social.reasoner.view.emulators.OutputEmulator;
import social.reasoner.view.emulators.InputEmulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A demonstration application showing a {@link CombinedXYPlot} with
 * two subplots.
 */
public class CombinedBNXYPlot extends ApplicationFrame{
    private int maxNumSamples = 20;
    private ChartContainer chartContainer1;
    private ChartContainer chartContainer2;
    private InputEmulator inputPanel;
    private OutputEmulator outputPanel;

    /**
     * Creates a new demo instance.
     */
    public CombinedBNXYPlot(String name1, String name2, String title1, String title2, String[] series1, String[] series2,
                            boolean shouldPlot) {
        super("Social Reasoner");
        Container content = getContentPane();
        content.setLayout( new GridBagLayout() );
        JPanel chartCSPanel = null;
        JPanel chartPhasePanel = null;

        if( shouldPlot ) {
            chartContainer1 = new ChartContainer(name1, title1, series1);
            chartCSPanel = createChartPanel(chartContainer1);
            if (name2 != null) {
                chartContainer2 = new ChartContainer(name2, title2, series2);
                chartPhasePanel = createChartPanel(chartContainer2);
            } else {
                chartCSPanel.setPreferredSize(new Dimension(970, 750));
            }
        }
        inputPanel = createInputPanel();
        outputPanel = createOutputPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add( outputPanel, gbc );
        add(Box.createRigidArea(new Dimension(0, 40)));

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        add( inputPanel, gbc );

        if( shouldPlot ) {
            if (chartPhasePanel != null) {
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 0;
                add(chartCSPanel, gbc);

                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 1;
                add(chartPhasePanel, gbc);
            } else {
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.gridheight = 2;
                add(chartCSPanel, gbc);
            }
        }

        pack();
        setVisible(true);
        setResizable(false);
    }

    public InputController getInputController() {
        return inputPanel.getController();
    }

    public OutputEmulator getOutputPanel() {
        return outputPanel;
    }

    public IntervalXYDataset refreshDataset(ChartContainer chartContainer){
        if (chartContainer.behaviors != null) {
            double max = 0;
            int sizeSeries = chartContainer.series.length - 1;

            //keep number of samples no bigger than maxNumSamples
            if (chartContainer.behaviors[0].size() > maxNumSamples) {
                for (int i = 0; i < sizeSeries; i++) {
                    chartContainer.behaviors[i].remove(0);
                }
                chartContainer.thresholds.remove(0);
                chartContainer.activations.remove(0);
                chartContainer.offset++;
            }

            //Activation thresholds
            XYSeries ts = chartContainer.dataset.getSeries(chartContainer.series[sizeSeries]);
            ts.clear();
            for (int j = 0; j < chartContainer.thresholds.size(); j++) {
                ts.addOrUpdate(j + chartContainer.offset, chartContainer.thresholds.get(j).doubleValue());
            }
            chartContainer.target.setStartValue(chartContainer.minThreshold);
            chartContainer.target.setEndValue(chartContainer.maxThreshold);

            //behaviors
            for (int i = 0; i < sizeSeries; i++) {
                int size = chartContainer.behaviors[i].size();
                ts = chartContainer.dataset.getSeries(chartContainer.series[i]);
                ts.clear();
                for (int j = 0; j < size; j++) {
                    double behActivation = chartContainer.behaviors[i].get(j);
                    if( behActivation > max ){
                        max = behActivation;
                    }
                    ts.add(j + chartContainer.offset, behActivation);
                }
            }

            //who is activated?
            for (int x = 0; x < chartContainer.activations.size(); x++) {
                String name = (String) chartContainer.activations.get(x)[0];
                double y = (Double) chartContainer.activations.get(x)[1];
                if( name != null && !name.isEmpty() ) {
                    XYTextAnnotation annotation = new XYTextAnnotation( name, (x + chartContainer.offset), y);
                    annotation.setFont(new Font("SansSerif", Font.ITALIC, 11));
                    chartContainer.plot.addAnnotation(annotation);
                }
            }

            // container range
            chartContainer.plot.getDomainAxis().setRange( chartContainer.offset, chartContainer.offset + maxNumSamples -1);
            chartContainer.plot.getRangeAxis().setRange( 0, max + 5);
            chartContainer.chart.fireChartChanged();
        }
        return chartContainer.dataset;
    }


    /**
     * Creates an overlaid chart.
     *
     * @return The chart.
     */
    private JFreeChart createChart( ChartContainer chartContainer ) {
        XYPlot plot = createPlot( chartContainer );

        // return a new chart containing the overlaid plot...
        JFreeChart chart = new JFreeChart( chartContainer.name,
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        chart.setBackgroundPaint(Color.white);
        return chart;
    }



    private XYPlot createPlot(ChartContainer chartContainer){
        createDataset( chartContainer );
        chartContainer.target = new IntervalMarker(14, 16);
        chartContainer.target.setLabel("Activation Threshold");
        chartContainer.target.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
        chartContainer.target.setLabelAnchor(RectangleAnchor.LEFT);
        chartContainer.target.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
        chartContainer.target.setPaint(new Color(222, 222, 255, 128));
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        BasicStroke stroke = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        for(int i = 0; i < chartContainer.series.length-1; i++ ) {
            renderer.setSeriesStroke(i, stroke);
        }
        renderer.setSeriesStroke(chartContainer.series.length - 1, new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, new float[]{10.0f}, 0.0f));

        NumberAxis rangeAxis = new NumberAxis("Activation");
        NumberAxis domainAxis = new NumberAxis("Time");
        XYPlot plot = new XYPlot( chartContainer.dataset, domainAxis, rangeAxis, renderer);
        plot.addRangeMarker(chartContainer.target, Layer.BACKGROUND);
        plot.setRenderer(renderer);
        plot.setDomainGridlinePaint(Color.white);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        chartContainer.plot = plot;
        return plot;
    }

    /**
     * Creates a sample dataset1.  You wouldn't normally hard-code the
     * population of a dataset1 in this way (it would be better to read the
     * values from a file or a database query), but for a self-contained demo
     * this is the least complicated solution.
     */
    private void createDataset(ChartContainer chartContainer) {
        chartContainer.dataset = new XYSeriesCollection();
        for( String serieName : chartContainer.series){
            XYSeries serie = new XYSeries( serieName );
            chartContainer.dataset.addSeries(serie);
        }
    }

    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     *
     * @return A panel.
     */
    public JPanel createChartPanel(ChartContainer chartContainer) {
        chartContainer.chart = createChart( chartContainer );
        ChartPanel panel = new ChartPanel( chartContainer.chart );
        panel.setPreferredSize(new Dimension(970, 370));
        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(MainController.pause ) {
                    MainController.pause = false;
                }else{
                    MainController.pause = true;
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
        return panel;
    }

    public InputEmulator createInputPanel(){
        InputEmulator inputPanel = new InputEmulator();
        inputPanel.setPreferredSize( new Dimension(470, 385) );
        return inputPanel;
    }

    public OutputEmulator createOutputPanel(){
        OutputEmulator outputPanel = new OutputEmulator();
        outputPanel.setPreferredSize( new Dimension(470, 385) );
        return outputPanel;
    }


    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {
        String title = "Social Reasoner Plot";
        startPlot("", "", title, "", null, null, true);
    }

    public static CombinedBNXYPlot startPlot(String name1, String name2, String title1, String title2, String[] series1,
                                             String[] series2, boolean shouldplot) {
        CombinedBNXYPlot plot = new CombinedBNXYPlot(name1, name2, title1, title2, series1, series2, shouldplot);
        plot.pack();
        RefineryUtilities.centerFrameOnScreen(plot);
        plot.setVisible(true);
        return plot;
    }

    public void setDataset(String datasetName, ArrayList<Double>[] behaviors, double threshhold, String nameBehActivated,
                           double activation) {
        try {
            if (datasetName.equals("ConversationalStrategyBN")) {
                setDataset(chartContainer1, behaviors, threshhold, nameBehActivated, activation);
            } else if (datasetName.equals("PhaseTaskBN")) {
                setDataset(chartContainer2, behaviors, threshhold, nameBehActivated, activation);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setDataset( ChartContainer chartContainer, ArrayList<Double>[] behaviors, double threshhold, String nameBehActivated,
                            double activation){
        chartContainer.behaviors = behaviors;
        chartContainer.thresholds.add(threshhold);
        chartContainer.activations.add(new Object[]{nameBehActivated, activation});

        chartContainer.maxThreshold = Collections.max(chartContainer.thresholds);
        chartContainer.minThreshold = Collections.min(chartContainer.thresholds);
        if( activation > chartContainer.maxActivation){
            chartContainer.maxActivation = activation;
        }
        double portion = (chartContainer.maxThreshold - chartContainer.minThreshold) / 6;
        if( (portion * 6) > (chartContainer.maxActivation /4) ) {
            chartContainer.maxThreshold = threshhold + portion > chartContainer.maxThreshold ? chartContainer.maxThreshold : threshhold + portion;
            chartContainer.minThreshold = threshhold - portion < chartContainer.minThreshold ? chartContainer.minThreshold : threshhold - portion;
        }
        refreshDataset(chartContainer);
    }

    class ChartContainer{
        IntervalMarker target;
        XYSeriesCollection dataset;
        String[] series;
        ArrayList<Double>[] behaviors;
        ArrayList<Double> thresholds;
        ArrayList<Object[]> activations;
        double minThreshold = 99999;
        double maxThreshold = 0;
        double maxActivation = 0;
        String title = "";
        String name;
        int offset;
        XYPlot plot;
        JFreeChart chart;

        public ChartContainer(String name, String title1, String[] series1) {
            this.name = name;
            this.title = title1;
            this.series = series1;
            activations = new ArrayList<>();
            thresholds = new ArrayList<>();
        }
    }
}
