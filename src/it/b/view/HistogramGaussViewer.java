package it.b.view;

import it.b.data.ClassSet;
import it.b.data.Variable;

import java.awt.Color;
import java.text.DecimalFormat;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * A simple demonstration application showing how to create a dual axis chart
 * based on data from two {@link CategoryDataset} instances.
 * 
 */
public class HistogramGaussViewer extends ApplicationFrame {

	private static final long serialVersionUID = 1L;
	private Variable variable;
	private ClassSet set;
	private static DecimalFormat df = new DecimalFormat("###.####");

	/**
	 * Creates a new demo instance.
	 * 
	 * @param title
	 *            the frame title.
	 */
	public HistogramGaussViewer(final String title, Variable var, ClassSet set) {
		super(title);
		final CategoryDataset dataset1 = createDataset1(set);
		// create the chart...
		final JFreeChart chart = ChartFactory.createBarChart(title, "X", "P", 
				dataset1, // data
				PlotOrientation.VERTICAL, false, false, // tooltips?
				false // URL generator? Not required...
				);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);
		// chart.getLegend().setAnchor(Legend.SOUTH);

		// get a reference to the plot for further customisation...
		final CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(new Color(0xEE, 0xEE, 0xFF));
		plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);

		// final CategoryDataset dataset2 = createDataset2(var, set);
		// plot.setDataset(1, dataset2);
		// plot.mapDatasetToRangeAxis(1, 1);

		final CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
		// final ValueAxis axis2 = new NumberAxis("Secondary");
		// plot.setRangeAxis(1, axis2);

		final LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
		plot.setRenderer(1, renderer2);
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
		// OPTIONAL CUSTOMISATION COMPLETED.

		// add the chart to a panel...
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);

	}

	/**
	 * Creates a sample dataset.
	 * 
	 * @return The dataset.
	 */
	private CategoryDataset createDataset1(ClassSet set) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (Double d : set.getFreq_map().keySet()) {
			dataset.addValue(set.getFreq_map().get(d), set.getVar_id(),
					df.format(d));
		}
		return dataset;
	}

	/**
	 * Creates a sample dataset.
	 * 
	 * @return The dataset.
	 */
	private CategoryDataset createDataset2(Variable var, ClassSet set) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		// si legge la gaussiana
		NormalDistribution dist = var.getNormalDistribution();
		for (Double d : set.getFreq_map().keySet()) {
			// si legge il valore della gaussiana e si aggiunge
			double g = 0;
			if (set.isRelative_freqs()) {
				g = dist.density(d);
			} else {
				g = dist.density(d) * var.getN();
			}
			dataset.addValue(g, "g", df.format(d));
		}
		return dataset;
	}
}
