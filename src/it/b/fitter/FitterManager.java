package it.b.fitter;

import it.b.data.VariableXY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**Gestore dei fitter.
 * @author archdav
 *
 */
public class FitterManager {

	private Map<String, LinearFitter> fitters;

	public FitterManager() {
		this.fitters = new HashMap<>();
	}

	/**
	 * Si crea il linear_fitter con lista di variabili
	 * 
	 * @param id
	 * @param vars
	 * @return
	 */
	public LinearFitter addLinearFitter(String id, List<VariableXY> vars) {
		if (!fitters.containsKey(id)) {
			this.fitters.put(id, new LinearFitter(id, vars));
		}
		return fitters.get(id);
	}
	
	/** Si crea il linear_fitter senza lista di variabili.
	 * @param id
	 * @return
	 */
	public LinearFitter addLinearFitter(String id) {
		if (!fitters.containsKey(id)) {
			this.fitters.put(id, new LinearFitter(id));
		}
		return fitters.get(id);
	}
	
	public LinearFitter getLinearFitter(String id){
		return this.fitters.get(id);
	}

	public  boolean containsFitter(String id) {
		if (fitters.containsKey(id)) {
			return true;
		} else {
			return false;
		}
	}
	
	public Map<String, LinearFitter> getFitters(){
		return this.fitters;
	}
}
