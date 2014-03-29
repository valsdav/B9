package it.b.data;

import java.text.DecimalFormat;
import java.util.List;
import java.util.TreeMap;

/**
 * Oggetto che rappresenta un set di classi di valori per la creazione
 * dell'istogramma.
 * 
 * @author Davide Valsecchi
 * 
 */
public class ClassSet {

	/** Mappa delle classi|frequenze */
	private TreeMap<Double, Double> freq_map;
	/** Lista degli estremi dei bin */
	private List<Double> bins;
	/** ampiezza degli intervalli */
	private double interval_size;
	/**
	 * Parametro che indica se il ClassSet contiene frequenze assolute o
	 * relative, di default è relative
	 */
	private boolean relative_freqs = true;
	/** id della variabile relativa */
	private String var_id;

	public ClassSet(String var_id, TreeMap<Double, Double> freq_map,
			List<Double> bins, double interval_size, boolean relative_freqs) {
		this.var_id = var_id;
		this.freq_map = freq_map;
		this.interval_size = interval_size;
		this.relative_freqs = relative_freqs;
		this.bins = bins;
	}

	public TreeMap<Double, Double> getFreq_map() {
		return freq_map;
	}

	public double[][] getDataForHistogram() {
		double[][] root = new double[6][];
		// si creano gli array
		double[] x = new double[freq_map.size()];
		double[] x_max = new double[freq_map.size()];
		double[] x_min = new double[freq_map.size()];
		double[] y = new double[freq_map.size()];
		// si inseriscono i dati
		for (int i = 0; i < this.freq_map.size(); i++) {
			double a = (double) freq_map.keySet().toArray()[i];
			double a_min = a - interval_size / 2;
			double a_max = a + interval_size / 2;
			x[i] = a;
			x_min[i] = a_min;
			x_max[i] = a_max;
			// si salva la y
			double b = this.freq_map.get(a);
			y[i] = b;
		}
		// si monta l'array
		root[0] = x;
		root[1] = x_min;
		root[2] = x_max;
		root[3] = y;
		root[4] = y;
		root[5] = y;
		return root;
	}

	public void setFreq_map(TreeMap<Double, Double> freq_map) {
		this.freq_map = freq_map;
	}

	public double getInterval_size() {
		return interval_size;
	}

	public void setInterval_size(double interval_size) {
		this.interval_size = interval_size;
	}

	public boolean isRelative_freqs() {
		return relative_freqs;
	}

	public void setRelative_freqs(boolean relative_freqs) {
		this.relative_freqs = relative_freqs;
	}

	public int getNumberOfBins() {
		return this.freq_map.size();
	}

	public double getMin() {
		return this.freq_map.firstKey();
	}

	public double getMax() {
		return this.freq_map.lastKey();
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("##.###");
		StringBuilder s = new StringBuilder();
		for (Double cl : this.freq_map.keySet()) {
			s.append(df.format(cl) + " -- " + df.format(this.freq_map.get(cl))
					+ "\n");
		}
		return s.toString();
	}

	public String getVar_id() {
		return var_id;
	}

	public List<Double> getBins() {
		return bins;
	}
}
