package it.b.data;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.NormalDistribution;
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
	private int N;
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
		double[] r = new double[6];
		r[0] = N;
		// media
		double[] ms = new double[N];
		for (int i = 0; i <= N - 1; i++) {
			ms[i] = this.measures.get(i);
		}
		this.mean.setData(ms);
		r[1] = this.mean.evaluate();
		// varianza
		this.var.setData(ms);
		r[2] = this.var.evaluate();
		// varianza della media
		r[3] = this.var_avg = r[2] / (double) N;
		// deviazione standard
		this.dev_stan.setData(ms);
		r[4] = this.dev_stan.evaluate();
		// deviazione della media
		r[5] = this.dev_stand_avg = r[4] / Math.sqrt(N);
		return r;
	}

	/**
	 * Metodo che restituisce la gaussiana ideale ricata dalla variabile
	 * 
	 * @return
	 */
	public NormalDistribution getNormalDistribution() {
		return new NormalDistribution(this.mean.getResult(),
				this.dev_stan.getResult());
	}

	public double getMean() {
		return mean.evaluate();
	}

	public double getVar() {
		return var.evaluate();
	}

	public double getVar_avg() {
		return var_avg;
	}

	public double getDev_stan() {
		return dev_stan.evaluate();
	}

	public double getDev_stand_avg() {
		return dev_stand_avg;
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
}
