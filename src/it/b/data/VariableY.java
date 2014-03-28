package it.b.data;

/**
 * Variabile Y che memorizza la X a cui è riferita. Serve per l'interpolazione
 * dei dati.
 * 
 * @author Davide Valsecchi
 * 
 */
public class VariableY extends Variable {

	private double X;

	public VariableY(String id) {
		super(id);
	}

	public double getX() {
		return X;
	}

	public void setX(double x) {
		X = x;
	}
}
