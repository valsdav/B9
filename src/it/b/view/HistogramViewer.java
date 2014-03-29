package it.b.view;

import it.b.data.ClassSet;
import it.b.data.Variable;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.DefaultIntervalXYDataset;
import org.jfree.ui.ApplicationFrame;

public class HistogramViewer extends ApplicationFrame {

	private static final long serialVersionUID = 1L;
	private Variable variable;
	private ClassSet set;

	public HistogramViewer(String title, Variable var, ClassSet set) {
		super(title);
		this.variable = var;
		this.set = set;
		DefaultIntervalXYDataset data_set = getDataSet(var, set);
		JFreeChart chart = createChart(var.getId(), data_set);
		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);
	}

	private DefaultIntervalXYDataset getDataSet(Variable var, ClassSet set) {
		// si crea una nuova serie
		DefaultIntervalXYDataset data_set = new DefaultIntervalXYDataset();
		// si aggiungono i dati
		data_set.addSeries(1, set.getDataForHistogram());
		return data_set;
	}

	private JFreeChart createChart(String title, DefaultIntervalXYDataset dataset) {
		JFreeChart chart = ChartFactory.createHistogram(title, "x", "p",
				dataset, PlotOrientation.VERTICAL, false, false, false);
		return chart;
	}
}
