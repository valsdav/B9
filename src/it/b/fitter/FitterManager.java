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

	private FitterManager() {
		this.fitters = new HashMap<>();
	}

	/**
	 * Si crea il linear_fitter con lista di variabili
	 * 
	 * @param id
	 * @param vars
	 * @return
	 */
	private LinearFitter addLinearFitter(String id, List<VariableXY> vars) {
		if (!fitters.containsKey(id)) {
			this.fitters.put(id, new LinearFitter(id, vars));
		}
		return fitters.get(id);
	}
	
	/** Si crea il linear_fitter senza lista di variabili.
	 * @param id
	 * @return
	 */
	private LinearFitter addLinearFitter(String id) {
		if (!fitters.containsKey(id)) {
			this.fitters.put(id, new LinearFitter(id));
		}
		return fitters.get(id);
	}
	
	private LinearFitter getLinearFitter(String id){
		return this.fitters.get(id);
	}

	private boolean containsFitter(String id) {
		if (fitters.containsKey(id)) {
			return true;
		} else {
			return false;
		}
	}
	
	private Map<String, LinearFitter> getFitters(){
		return this.fitters;
	}
}
