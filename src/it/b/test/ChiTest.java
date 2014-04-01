package it.b.test;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

import it.b.data.ClassSet;
import it.b.data.Variable;

/**
 * Classe che si occupa dei test sul chi
 * 
 * @author archdav
 * 
 */
public class ChiTest {

	/**
	 * Metodo che esegue il test del chi quadro per la compatibilità di una
	 * variabile casuale con una gaussiana attraverso il metodo dell'istogramma.
	 * 
	 * @param var
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
		ChiSquaredDistribution chi = new ChiSquaredDistribution(l_grades- 1);
		double chi_p =( 1- chi.cumulativeProbability(chi_squared))*100;
		double chi_ridotto = chi_squared / l_grades;
		// si salva il risultato
		double[] res = new double[3];
		res[0] = chi_squared;
		res[1] = chi_ridotto;
		res[2] = chi_p;
		return res;
	}
}
