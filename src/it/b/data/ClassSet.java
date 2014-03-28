package it.b.data;

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
	/** ampiezza degli intervalli */
	private double interval_size;
	/**
	 * Parametro che indica se il ClassSet contiene frequenze assolute o
	 * relative, di default è relative
	 */
	private boolean relative_freqs = true;
	

	public ClassSet(TreeMap<Double, Double> freq_map, double interval_size,
			boolean relative_freqs) {
		this.freq_map = freq_map;
		this.interval_size = interval_size;
		this.relative_freqs = relative_freqs;
	}

	public TreeMap<Double, Double> getFreq_map() {
		return freq_map;
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
}
