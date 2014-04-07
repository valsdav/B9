package it.b.test;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

import it.b.data.ClassSet;
import it.b.data.Variable;
import it.b.data.VariableXY;
import it.b.fitter.LinearFitter;

/**
 * Classe che si occupa dei test sul chi
 * 
 * @author archdav
 * 
 */
public class ChiSquaredTest {

	/**
	 * Metodo che esegue il test del chi quadro per la compatibilità di una
	 * variabile casuale con una gaussiana attraverso il metodo dell'istogramma.
	 * 
	 * @param var
	 *            Variabile su cui eseguire il test
	 * @return chi_quadro|chi_ridotto|probabilità comulativa
	 */
	public static double[] testChiSquared_Histogram(Variable var) {
		// per ogni classe di frequenza si va a calcolare il valore attesa sulla
		// gaussiana
		NormalDistribution dist = var.getNormalDistribution();
		ClassSet set = var.getLast_class_set();
		// numero totale di misure
		double N = (double) var.getN();
		double l_grades = set.getNumberOfBins();
		double[][] data = set.getDataForHistogram();
		// chi quadro
		double chi_squared = 0;
		for (int i = 0; i < set.getNumberOfBins(); i++) {
			// per ogni classe si calcola lo scarto tra valori attesi e no
			// estremi per la gaussiana
			double min, max;
			min = data[1][i];
			max = data[2][i];
			double p = dist.probability(min, max);
			double ak = p * N;
			// valore aspettato
			double nk = data[3][i];
			// si aggiunge un valore al chi
			chi_squared += (Math.pow((ak - nk), 2)) / ak;
		}
		// si legge la probabilità di questo chi
		ChiSquaredDistribution chi_dist = new ChiSquaredDistribution(
				l_grades - 3);
		double chi_p = (1 - chi_dist.cumulativeProbability(chi_squared)) * 100;
		double chi_ridotto = chi_squared / l_grades;
		// si salva il risultato
		double[] res = new double[3];
		res[0] = chi_squared;
		res[1] = chi_ridotto;
		res[2] = chi_p;
		return res;
	}

	/**
	 * Metodo che esegue il test del chi quadro sull'interpolazione lineare
	 * delle variabili memorizzate nel fitter passato come parametro.
	 * 
	 * @param fitter
	 *            LinearFitter che contine i dati su cui eseguire il test.
	 * @return chi_quadro|chi_ridotto|probabilità comulativa
	 */
	public static double[] testChiSquared_LinearFitting(LinearFitter fitter) {
		// si deve calcolare la quantità chi
		double chi_squared = 0;
		double A = fitter.getA();
		double B = fitter.getB();
		// ciclo su tutte le variabili
		for (VariableXY v : fitter.getVariables().values()) {
			double y = v.getMean();
			double x = v.getX();
			double var = v.getVar();
			chi_squared += Math.pow((y - A - B * x), 2) / var;
		}
		// si legge la probabilità di questo chi
		double l_grades = fitter.getN() - 2;
		double[] res = new double[3];
		if (l_grades > 0) {
			ChiSquaredDistribution chi_dist = new ChiSquaredDistribution(
					l_grades);
			double chi_p = (1 - chi_dist.cumulativeProbability(chi_squared)) * 100;
			double chi_ridotto = chi_squared / l_grades;
			// si salva il risultato
			res[0] = chi_squared;
			res[1] = chi_ridotto;
			res[2] = chi_p;
		} else {
			res[0] = 0;
			res[1] = 0;
			res[2] = 0;
		}
		return res;
	}
}
