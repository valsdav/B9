package it.b.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

/**
 * @author archdav
 * 
 */
public class Variable {

	private String id;
	private List<Double> measures;
	private double[] measures_array;
	private TreeMap<Double, Integer> measures_freq;
	private Mean mean;
	private Variance var;
	private StandardDeviation dev_stan;
	private int N;
	// ultimi valori stimati
	private double mean_l;
	private double var_l;
	private double dev_stan_l;
	private double var_avg;
	private double dev_stan_avg;
	private double min;
	private double max;
	private ClassSet last_class_set;
	private boolean evaluated = false;

	public Variable(String id) {
		this.id = id;
		this.mean = new Mean();
		this.var = new Variance();
		this.dev_stan = new StandardDeviation();
		this.measures = new ArrayList<>();
		this.measures_freq = new TreeMap<>();
	}

	public void addMeasures(List<Double> ms) {
		for (Double m : ms) {
			this.addMeasure(m);
		}
	}

	public void addMeasure(double m) {
		// si aggiunge
		N += 1;
		measures.add(m);
		if (measures_freq.containsKey(m)) {
			// si aggiunge uno
			measures_freq.put(m, measures_freq.get(m) + 1);
		} else {
			measures_freq.put(m, 1);
		}
	}

	/**
	 * Metodo che esegue tutti i calcoli sulle misure.
	 */
	public double[] evaluate() {
		double[] r = new double[8];
		r[0] = N;
		// media
		double[] ms = new double[N];
		for (int i = 0; i <= N - 1; i++) {
			ms[i] = this.measures.get(i);
		}
		this.measures_array = ms;
		this.mean.setData(ms);
		r[1] = mean_l = this.mean.evaluate();
		// varianza
		this.var.setData(ms);
		r[2] = var_l = this.var.evaluate();
		// varianza della media
		r[3] = this.var_avg = r[2] / (double) N;
		// deviazione standard
		this.dev_stan.setData(ms);
		r[4] = dev_stan_l = this.dev_stan.evaluate();
		// deviazione della media
		r[5] = this.dev_stan_avg = r[4] / Math.sqrt(N);
		// min
		r[6] = min = StatUtils.min(ms);
		// max
		r[7] = max = StatUtils.max(ms);
		this.evaluated = true;
		return r;
	}

	/**
	 * Metodo che restituisce la gaussiana ideale ricata dalla variabile
	 * 
	 * @return
	 */
	public NormalDistribution getNormalDistribution() {
		return new NormalDistribution(this.mean_l, this.dev_stan_l);
	}

	/**
	 * Crea class-set dai dati creando intervalli dell'ampiezza di un multiplo
	 * di sigma. Il multiplo viene passato come parametro.
	 * 
	 * @param sigma_factor
	 * @param relative_freq
	 * @return
	 */
	public ClassSet getClassSetSigmaFactor(double sigma_factor,
			boolean relative_freq) {
		if (!evaluated) {
			this.evaluate();
		}
		this.last_class_set = this.getClassSet(this.dev_stan_l / sigma_factor,
				relative_freq);
		return last_class_set;
	}

	/**
	 * Crea class-set con i dati divisi nel numero di intervalli specificato.
	 * 
	 * @param n_intervals
	 * @param relative_freq
	 * @return
	 */
	public ClassSet getClassSetNIntervals(int n_intervals, boolean relative_freq) {
		if (!evaluated) {
			this.evaluate();
		}
		double n = (this.max - this.min) / (double) n_intervals;
		this.last_class_set = this.getClassSet(n, relative_freq);
		return last_class_set;
	}

	/**
	 * Crea class-set che contiene i dati divisi in intervallo dell'ampiezza
	 * specificata, intorno alla media.
	 * 
	 * @param interval_size
	 * @param relative_freq
	 * @return
	 */
	public ClassSet getClassSet(double interval_size, boolean relative_freq) {
		if (!evaluated) {
			this.evaluate();
		}
		// si parte dalla media e si divide in intervalli fino ad arrivare agli
		// estremi.
		TreeMap<Double, Double> classset = new TreeMap<>();
		List<Double> bins = new ArrayList<>();
		// prima valori dopo la media
		double halfint = interval_size / 2;
		double first_next, first_prev;
		first_next = mean_l + halfint;
		first_prev = mean_l - halfint;
		classset.put(mean_l, 0.0);
		double current = first_next;
		while (current <= max) {
			// si inseriscono le classi
			classset.put(current + halfint, 0.0);
			// si va alla classe successiva
			current = current + interval_size;
		}
		// si esegue la stessa operazione andando indietro
		current = first_prev;
		while (current >= min) {
			// si inseriscono le classi
			classset.put(current - halfint, 0.0);
			// si va alla classe successiva
			current = current - interval_size;
		}

		// ora si devono contare i valori all'interno delle classi
		double last = this.measures_freq.lastKey();
		for (double a : classset.keySet()) {
			double b_left = a - halfint;
			double b_right = a + halfint;
			// si aggiunge ai bin
			bins.add(b_left);
			if (b_left > last) {
				// si esce
				break;
			}
			// si contano tutte le misure comprese tra a e b
			int count = 0;
			for (Double d : this.measures_freq.keySet()) {
				if (d >= b_left && d < b_right) {
					count += this.measures_freq.get(d);
				}
			}
			double fr;
			if (relative_freq) {
				// frequenza relativa
				fr = (double) count / N;
			} else {
				fr = count;
			}
			// si inserisce nelle classi
			classset.put(a, fr);
			// si prosegue con altri a e b
			a = a + interval_size;
			b_left = a - halfint;
			b_right = a + halfint;
		}
		// si restituisce il classset
		this.last_class_set = new ClassSet(id, classset, bins, interval_size,
				relative_freq);
		return last_class_set;
	}

	/**
	 * Metodo che restituisce i valori di x per i valore centrali degli
	 * intervalli del class-set.
	 * 
	 * @return
	 */
	public List<Double> getGaussianY() {
		if (this.last_class_set == null) {
			return new ArrayList<>();
		}
		List<Double> xs = new ArrayList<>();
		NormalDistribution dist = getNormalDistribution();
		for (double x : this.last_class_set.getFreq_map().keySet()) {
			xs.add(dist.density(x));
		}
		return xs;
	}

	/**
	 * Restituisce l'ultimo valore stimato della media
	 * 
	 * @return
	 */
	public double getMean() {
		return mean_l;
	}

	/**
	 * Restituisce l'ultimo valore stimato della varianza
	 * 
	 * @return
	 */
	public double getVar() {
		return var_l;
	}

	/**
	 * Restituisce l'ultimo valore stimato della varianza della media.
	 * 
	 * @return
	 */
	public double getVar_avg() {
		return var_avg;
	}

	/**
	 * Restituisce l'ultimo valore stimato della deviazione
	 * 
	 * @return
	 */
	public double getDev_stan() {
		return dev_stan_l;
	}

	/**
	 * Restituisce l'ultimo valore stimato della deviazione della media
	 * 
	 * @return
	 */
	public double getDev_stand_avg() {
		return dev_stan_avg;
	}

	/**
	 * Metodo che esporta la variabile in formato cvs.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void exportCVS(String path) throws IOException {
		FileWriter wri = new FileWriter(path + File.separator + this.getId());
		for (double m : this.measures) {
			wri.write(Double.toString(m) + ";");
		}
		wri.close();
	}

	public String getId() {
		return id;
	}

	public int getN() {
		return N;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Double> getMeasures() {
		return measures;
	}

	public TreeMap<Double, Integer> getMeasures_freq() {
		return measures_freq;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double[] getMeasures_array() {
		return measures_array;
	}

	public boolean isEvaluated() {
		return evaluated;
	}

	public ClassSet getLast_class_set() {
		return last_class_set;
	}
}
