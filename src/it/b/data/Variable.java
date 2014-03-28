package it.b.data;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

public class Variable {

	private String id;
	private List<Double> measures;
	private TreeMap<Double, Integer> measures_freq;
	private Mean mean;
	private Variance var;
	private StandardDeviation dev_stan;
	private double N;
	private double var_avg;
	private double dev_stand_avg;

	public Variable(String id) {
		this.id = id;
		this.mean = new Mean();
		this.var = new Variance();
		this.dev_stan = new StandardDeviation();
		this.measures = new ArrayList<>();
		this.measures_freq = new TreeMap<>();
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
		// si aggiunge alla media
		this.mean.increment(m);
		// si aggiunge alla varianza
		this.var.increment(m);
		// si aggiunge alla varianza
		this.dev_stan.increment(m);
	}

	/**
	 * Metodo che esegue tutti i calcoli sulle misure.
	 */
	public double[] evaluate() {
		double[] r = new double[6];
		r[0] = N;
		// media
		r[1] = this.mean.evaluate();
		// varianza
		r[2] = this.var.evaluate();
		// varianza della media
		r[3] = this.var_avg = this.var.getResult() / N;
		// deviazione standard
		r[4] = this.dev_stan.evaluate();
		// deviazione della media
		r[5] = this.dev_stand_avg = this.dev_stan.getResult() / Math.sqrt(N);
		return r;
	}

	public double getMean() {
		return mean.evaluate();
	}

	public double getVar() {
		return var.getResult();
	}

	public double getVar_avg() {
		return var_avg;
	}

	public double getDev_stan() {
		return dev_stan.getResult();
	}

	public double getDev_stand_avg() {
		return dev_stand_avg;
	}

	public String getId() {
		return id;
	}

	public double getN() {
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
}
