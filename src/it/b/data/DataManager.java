package it.b.data;

import java.util.HashMap;
import java.util.Map;

public class DataManager {

	private Map<String, Variable> variables;

	public DataManager() {
		this.variables = new HashMap<>();
	}

	public void addVariable(String id) {
		if (!variables.containsKey(id)) {
			variables.put(id, new Variable(id));
		}
	}

	public Variable getVariable(String id) {
		return variables.get(id);
	}

	public boolean containsVariable(String id) {
		return variables.containsKey(id);
	}

	public Map<String, Variable> getVariables() {
		return variables;
	}
}
