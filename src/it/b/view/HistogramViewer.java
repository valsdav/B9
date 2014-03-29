package it.b.view;

import it.b.data.ClassSet;
import it.b.data.Variable;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.ui.ApplicationFrame;

public class HistogramViewer extends ApplicationFrame {

	private Variable variable;
	private ClassSet set;

	public HistogramViewer(String title, Variable var, ClassSet set) {
		super(title);
		this.variable = var;
		this.set = set;
		HistogramDataset data_set = getDataSet(var, set);
		JFreeChart chart = createChart(var.getId(), data_set);
		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);
	}

	private HistogramDataset getDataSet(Variable var, ClassSet set) {
		// si crea una nuova serie
		HistogramDataset data_set = new HistogramDataset();
		if (set.isRelative_freqs()) {
			data_set.setType(HistogramType.RELATIVE_FREQUENCY);
		} else {
			data_set.setType(HistogramType.FREQUENCY);
		}
		// si aggiungono i dati
		data_set.addSeries(1, var.getMeasures_array(), set.getNumberOfBins());
		return data_set;
	}

	private JFreeChart createChart(String title, HistogramDataset dataset) {
		JFreeChart chart = ChartFactory.createHistogram(title, "x", "p",
				dataset, PlotOrientation.VERTICAL, false, false, false);
		return chart;
	}
}
