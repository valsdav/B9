package it.b.data;

/**
 * Variabile Y che memorizza la X a cui è riferita. Serve per l'interpolazione
 * dei dati.
 * 
 * @author Davide Valsecchi
 * 
 */
public class VariableXY extends Variable {

	private double X;

	public VariableXY(String id, double x) {
		super(id);
		this.X = x;
	}

	public VariableXY(Variable v, double x) {
		super(v.getId());
		super.addMeasures(v.getMeasures());
		this.X = x;
		this.evaluate();
	}

	public double getX() {
		return X;
	}

	public void setX(double x) {
		X = x;
	}
}
