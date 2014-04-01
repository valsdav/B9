package it.b.data;

import java.util.HashMap;
import java.util.Map;

public class DataManager {

	private Map<String, Variable> variables;

	public DataManager() {
		this.variables = new HashMap<>();
	}

	/**
	 * Metodo che aggiunge una variabile normale
	 * 
	 * @param id
	 */
	public void addVariable(String id) {
		if (!variables.containsKey(id)) {
			variables.put(id, new Variable(id));
		}
	}

	/**
	 * Metodo che aggiunge una variabile xy
	 * 
	 * @param id
	 * @param x
	 */
	public void addVariableXY(String id, double x) {
		if (!variables.containsKey(id)) {
			variables.put(id, new VariableXY(id, x));
		}
	}

	public Variable getVariable(String id) throws Exception {
		if (variables.containsKey(id)) {
			return variables.get(id);
		} else {
			throw new Exception("Variabile non presente!");
		}
	}

	public boolean containsVariable(String id) {
		return variables.containsKey(id);
	}

	public Map<String, Variable> getVariables() {
		return variables;
	}
}
