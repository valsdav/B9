package it.b.fitter;

import it.b.data.VariableXY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe per eseguire fitting lineare
 * 
 * @author archdav
 * 
 */
public class LinearFitter {

	private String id;
	private Map<Double, VariableXY> vars_map;
	private double A;
	private double B;
	private double sigmaA;
	private double sigmaB;
	private double sigmaAB;
	private boolean fitted;

	public LinearFitter(String id) {
		this.id = id;
		this.vars_map = new HashMap<>();
	}

	public LinearFitter(String id, List<VariableXY> vars) {
		this.id = id;
		this.vars_map = new HashMap<>();
		for (VariableXY v : vars) {
			this.vars_map.put(v.getX(), v);
		}
	}

	public void addVariable(VariableXY var) {
		this.vars_map.put(var.getX(), var);
	}

	public Map<Double, VariableXY> getVariables() {
		return vars_map;
	}

	/**
	 * Metodo che esegue il fit con incertezza y costante e richiesta come
	 * parametro.
	 * 
	 * @param sigmay E' richiesta la deviazione standard della y da usare come costante.
	 * @return
	 */
	public double[] fit_sigma_costant(double sigmay) {
		double y = 0, x2 = 0, x = 0, xy = 0;
		// si calcolano le variabili
		for (double a : vars_map.keySet()) {
			double b = vars_map.get(a).getMean();
			y += b;
			x += a;
			x2 += Math.pow(a, 2);
			xy += a * b;
		}
		// calcolo 
		double[] r = new double[5];
		double delta = getN() * x2 - Math.pow(x, 2);
		r[0] = this.A = ((y * x2) - (x * xy)) / delta;
		r[1] = this.B = (getN() * xy - x * y) / delta;
		r[2] = this.sigmaA = Math.sqrt(x2 / delta) * sigmay;
		r[3] = this.sigmaB = Math.sqrt(getN() / delta) * sigmay;
		r[4] = this.sigmaAB = -(x / delta) * Math.pow(sigmay, 2);
		this.fitted= true;
		return r;
	}
	
	/**
	 * Metodo che eseguo il fit con incertezza pesata su tutte le incertezze delle y
	 * @return
	 */
	public double[] fit(){
		double y = 0, x2 = 0, x = 0, xy = 0,sigmay=0;
		// si calcolano le variabili
		for (double a : vars_map.keySet()) {
			double b = vars_map.get(a).getMean();
			double var =  vars_map.get(a).getVar();
			y += b/var;
			x += a/var;
			x2 += Math.pow(a, 2)/var;
			xy += (a * b)/var;
			sigmay+=1/var;
		}
		//calcolo
		double[] r = new double[5];
		double delta1 = sigmay * x2 - Math.pow(x,2);
		r[0] = this.A = ((y * x2) - (x * xy)) / delta1;
		r[1] = this.B = ((sigmay*xy)-(x*y))/delta1;
		r[2] = this.sigmaA = Math.sqrt(x2 / delta1) ;
		r[3] = this.sigmaB = Math.sqrt(sigmay / delta1);
		r[4] = this.sigmaAB = -(x / delta1);
		this.fitted= true;
		return r;
	}

	public Map<Double, VariableXY> getVars_map() {
		return vars_map;
	}

	public double getA() {
		return A;
	}

	public double getB() {
		return B;
	}

	public double getSigmaA() {
		return sigmaA;
	}

	public double getSigmaB() {
		return sigmaB;
	}

	public double getSigmaAB() {
		return sigmaAB;
	}

	public int getN() {
		return this.vars_map.size();
	}

	public String getId() {
		return id;
	}

	public boolean isFitted() {
		return fitted;
	}

}
