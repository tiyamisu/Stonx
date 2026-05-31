package com.stonx.ui.components;

import com.stonx.model.Stock;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Custom UI Component.
 * Upgrades JFreeChart to render beautiful fintech-grade Area Charts with glowing outlines
 * and vertical color gradients.
 */
public class StockChartPanel extends JPanel {
    private final XYSeries series;
    private final JFreeChart chart;
    private final ChartPanel chartPanel;
    private String currentSymbol = "";

    public StockChartPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Initialize series and dataset
        series = new XYSeries("Price");
        XYSeriesCollection dataset = new XYSeriesCollection(series);

        // Create standard XYPlot chart manually for fine control
        NumberAxis xAxis = new NumberAxis();
        xAxis.setTickLabelsVisible(false);
        xAxis.setAxisLinePaint(new Color(60, 60, 60));
        xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickLabelPaint(new Color(200, 200, 200));
        yAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
        yAxis.setAxisLinePaint(new Color(60, 60, 60));
        yAxis.setNumberFormatOverride(new DecimalFormat("₹#,##0.00"));
        yAxis.setAutoRangeIncludesZero(false);

        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        plot.setBackgroundPaint(new Color(25, 25, 25));
        plot.setDomainGridlinePaint(new Color(45, 45, 45));
        plot.setRangeGridlinePaint(new Color(45, 45, 45));
        plot.setDomainGridlineStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, new float[]{2.0f, 2.0f}, 0.0f));
        plot.setRangeGridlineStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, new float[]{2.0f, 2.0f}, 0.0f));
        plot.setOutlineVisible(false);

        // Define dual renderers (Renderer 0 = Faded Area, Renderer 1 = Thick line on top)
        XYAreaRenderer areaRenderer = new XYAreaRenderer(XYAreaRenderer.AREA);
        XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer();
        lineRenderer.setSeriesShapesVisible(0, false);
        lineRenderer.setSeriesStroke(0, new BasicStroke(3.0f));

        plot.setRenderer(0, areaRenderer);
        plot.setRenderer(1, lineRenderer);
        
        // Link dataset to both renderers
        plot.setDataset(0, dataset);
        plot.setDataset(1, dataset);

        chart = new JFreeChart(null, null, plot, false);
        chart.setBackgroundPaint(new Color(25, 25, 25));
        chart.setBorderVisible(false);

        // Wrap in ChartPanel
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 260));
        chartPanel.setBackground(new Color(25, 25, 25));
        chartPanel.setPopupMenu(null); // disable popup menu
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        
        add(chartPanel, BorderLayout.CENTER);
    }

    /**
     * Updates chart line and area gradient to match stock performance.
     */
    public synchronized void updateChart(Stock stock) {
        if (stock == null) {
            series.clear();
            return;
        }

        boolean symbolChanged = !stock.getSymbol().equalsIgnoreCase(currentSymbol);
        currentSymbol = stock.getSymbol();

        List<Double> history = stock.getPriceHistory();

        // Avoid redraw if data size is same and last price hasn't shifted significantly
        if (!symbolChanged && history.size() == series.getItemCount()) {
            if (!history.isEmpty()) {
                double lastVal = history.get(history.size() - 1);
                Number currentLast = series.getY(series.getItemCount() - 1);
                if (currentLast != null && Math.abs(currentLast.doubleValue() - lastVal) < 0.0001) {
                    return;
                }
            }
        }

        series.clear();
        for (int i = 0; i < history.size(); i++) {
            series.add(i + 1, history.get(i));
        }

        // Apply colors: Green if stock return is positive, Red if negative
        XYPlot plot = chart.getXYPlot();
        XYAreaRenderer areaRenderer = (XYAreaRenderer) plot.getRenderer(0);
        XYLineAndShapeRenderer lineRenderer = (XYLineAndShapeRenderer) plot.getRenderer(1);

        Color baseColor;
        if (stock.getDailyChangePercent() >= 0) {
            baseColor = new Color(46, 204, 113); // Emerald green
        } else {
            baseColor = new Color(231, 76, 60); // Red
        }

        // Apply vertical gradient for premium faded area effect
        GradientPaint gp = new GradientPaint(
                0.0f, 0.0f, new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 75),
                0.0f, 250.0f, new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 0)
        );

        areaRenderer.setSeriesPaint(0, gp);
        lineRenderer.setSeriesPaint(0, baseColor);
    }
}
