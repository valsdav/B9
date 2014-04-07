package it.b.main;

import it.b.data.ClassSet;
import it.b.data.DataManager;
import it.b.data.Variable;
import it.b.data.VariableXY;
import it.b.fitter.FitterManager;
import it.b.fitter.LinearFitter;
import it.b.test.ChiSquaredTest;
import it.b.view.HistogramGaussViewer;
import it.b.view.HistogramViewer;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jfree.ui.RefineryUtilities;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class B9_Main {

	private static String path = "";
	private static String project = "";
	private static PrintStream out = System.out;
	private static BufferedReader in = new BufferedReader(
			new InputStreamReader(System.in));
	private static DataManager data;
	private static FitterManager fitter_man;
	private static DecimalFormat df = new DecimalFormat("####.#####");

	public static void main(String[] args) throws IOException {
		// inizializzazione dataManager
		data = new DataManager();
		fitter_man = new FitterManager();
		out.println("############### B9 ###############");
		boolean ok = true;
		while (ok) {
			out.print(">> ");
			String cmd = in.readLine().trim();

			switch (cmd) {
			case "data":
				data_cycle();
				break;
			case "fit":
				fit_cycle();
				break;
			case "graph":
				// si avvia la parte grafici
				graph_cycle();
				break;
			case "test":
				// ciclo per i test
				test_cycle();
				break;
			case "settings":
				settings_cycle();
				break;
			case "save":
				// salvataggio
				if (project.equals("")) {
					out.print("Project name: ");
					String name = in.readLine();
					if (name.equals("")) {
						out.println("Inserire nome progetto!\n");
						continue;
					} else {
						project = name;
						// se si cambia nome si azzera la path
						path = "";
					}
				}
				saveData(path, project);
				break;
			case "load":
				out.print("Project path: ");
				String p = in.readLine();
				try {
					loadData(p);
				} catch (Exception e) {
					out.println(e.getMessage());
				}
				break;
			case "quit":
				// si salva e si chiude
				out.print("Uscire veramente? (y/n): ");
				String a = in.readLine();
				if (a.equals("y")) {
					return;
				}
				break;
			default:
				out.println("Inserire comando valido!");
			}
		}
	}

	/**
	 * Ciclo di gestione delle variabili.
	 * 
	 * @throws IOException
	 */
	private static void data_cycle() throws IOException {
		boolean data_cycle = true;
		while (data_cycle) {
			out.print("data>> ");
			String cmd2 = in.readLine().trim();
			if (cmd2.equals("")) {
				continue;
			} else if (cmd2.equals("exit")) {
				data_cycle = false;
				out.println("exit data menu...\n");
				continue;
			}
			String[] p = cmd2.split(" ");
			// si stampano le variabili
			if (p[0].equals("variables")) {
				// si stampano le variabili
				for (Variable varia : data.getVariables().values()) {
					out.println("\tID: " + varia.getId() + " - N: "
							+ varia.getN());
				}
				continue;
			}
			if (p.length < 2) {
				out.println("\tInserire parametro variable\n");
				continue;
			}
			String v = p[1];
			try {
				// comandi
				switch (p[0]) {
				case "create":
					// si aggiunge la variabile
					data.addVariable(v);
					out.println("\tCreata variabile: " + v + "\n");
					break;
				case "create-xy":
					if (p.length < 3) {
						out.println("data>> Inserire parametri:\n"
								+ " nome variabile, valore di X\n");
						continue;
					}
					double x = Double.parseDouble(p[2]);
					// si aggiunge la variabile
					data.addVariableXY(v, x);
					out.println("\tCreata variabile: " + v + "\n");
					break;
				case "values":
					// si stampano i valori
					Variable va = data.getVariable(v);
					for (double d : va.getMeasures()) {
						out.println(df.format(d));
					}
					out.println();
					break;
				case "evaluate":
					// si stampano i valori statistici
					Variable var = data.getVariable(v);
					double[] e = var.evaluate();
					out.println("N: ...................." + df.format(e[0]));
					if (var instanceof VariableXY) {
						out.println("X: ...................."
								+ df.format(((VariableXY) var).getX()));
					}
					out.println("Media: ................" + df.format(e[1]));
					out.println("Varianza: ............." + df.format(e[2]));
					out.println("Varianza Media: ......." + df.format(e[3]));
					out.println("Deviazione ST: ........" + df.format(e[4]));
					out.println("Deviazione ST Media: .." + df.format(e[5]));
					out.println();
					break;
				case "add-values":
					if (!data.containsVariable(v)) {
						out.println("Creare prima la variabile...");
						continue;
					}
					Variable var2 = data.getVariable(v);
					out.println("Inserire un valore e premere invio. (exit) per uscire...");
					// si inseriscono i valori
					boolean ok = true;
					int i = 1;
					while (ok) {
						out.print("\tvalue-" + var2.getId() + "-" + i + ">> ");
						String s = in.readLine();
						if (s.equals("")) {
							continue;
						}
						if (s.equals("exit")) {
							ok = false;
							break;
						}
						double m = Double.parseDouble(s);
						var2.addMeasure(m);
						i++;
					}
					out.println();
					break;
				case "class-set":
					// si legge il terzo parametro
					if (p.length < 4) {
						out.println("data>> Inserire parametri:\n"
								+ " larghezza intervallo, freq relativa (true/false)\n");
						continue;
					}
					double inte = Double.parseDouble(p[2]);
					boolean fr_rel = Boolean.parseBoolean(p[3]);
					Variable var3 = data.getVariable(v);
					ClassSet class_set = var3.getClassSet(inte, fr_rel);
					out.println(class_set);
					break;
				case "class-set-n":
					// si legge il terzo parametro
					if (p.length < 4) {
						out.println("data>> Inserire parametri:\n"
								+ " numero di intervalli, freq relativa (true/false)\n");
						continue;
					}
					int inte2 = Integer.parseInt(p[2]);
					boolean fr_rel2 = Boolean.parseBoolean(p[3]);
					Variable var4 = data.getVariable(v);
					ClassSet class_set2 = var4.getClassSetNIntervals(inte2,
							fr_rel2);
					out.println(class_set2);
					break;
				case "class-set-s":
					// si legge il terzo parametro
					if (p.length < 4) {
						out.println("data>> Inserire parametri:\n"
								+ " larghezza intervallo, freq relativa (true/false)\n");
						continue;
					}
					double sigma_factor = Double.parseDouble(p[2]);
					boolean fr_rel3 = Boolean.parseBoolean(p[3]);
					Variable var5 = data.getVariable(v);
					ClassSet class_set3 = var5.getClassSetSigmaFactor(
							sigma_factor, fr_rel3);
					out.println(class_set3);
					break;
				case "gauss-y":
					Variable var8 = data.getVariable(v);
					ClassSet set = var8.getLast_class_set();
					List<Double > y = var8.getGaussianY();
					Set<Double> xs = set.getFreq_map().keySet();
					Iterator it = xs.iterator();
					
					for(int j =0; j< set.getNumberOfBins();j++){
						out.println("\t"+ df.format(it.next())+" - "+ df.format(y.get(j)));
					}
					out.println();
					break;
				case "set-x":
					if (p.length < 3) {
						out.println("data>> Inserire parametri:\n"
								+ " id variabile, X da inserire\n");
						continue;
					}
					Variable var6 = data.getVariable(v);
					if (var6 instanceof VariableXY) {
						((VariableXY) var6).setX(Double.parseDouble(p[2]));
					} else {
						out.println("data>> Inserire variabile di tipo XY\n");
						continue;
					}
					break;
				case "export-cvs":
					if (p.length < 2) {
						out.println("data>> Inserire parametri:\n"
								+ " id variabile\n");
						continue;
					}
					Variable var7 = data.getVariable(v);
					var7.exportCVS(path);
					out.println("Salvata variabile " + var7.getId() + " in "
							+ path + File.separator + var7.getId());
					break;
				default:
					out.println("Inserire comando valido!");
				}
			} catch (Exception e) {
				out.println(e.getMessage());
				continue;
			}
		}
	}

	/**
	 * Ciclo che gestisce i comandi sui grafici
	 * 
	 * @throws IOException
	 */
	private static void graph_cycle() throws IOException {
		boolean graph_cycle = true;
		while (graph_cycle) {
			out.print("graph>> ");
			String cmd2 = in.readLine().trim();
			if (cmd2.equals("exit")) {
				graph_cycle = false;
				out.println("exit graph menu...\n");
				continue;
			}
			String[] p = cmd2.split(" ");
			if (p.length < 2) {
				out.println("\tInserire parametro variable\n");
				continue;
			}
			String v = p[1];
			try {
				switch (p[0]) {
				case "histogram":
					// si crea l'istogramma con il class-set già caricato
					final Variable var1 = data.getVariable(v);
					if (var1.getLast_class_set() == null) {
						out.println("\tEseguire prima un class-set sulla variabile!\n");
						continue;
					}
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								HistogramViewer hist = new HistogramViewer(var1
										.getId(), var1, var1
										.getLast_class_set());
								hist.setBounds(10, 10, 500, 500);
								RefineryUtilities.centerFrameOnScreen(hist);
								hist.setVisible(true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					break;
				case "histogram-c":
					// si crea l'istogramma creando un class-set co i parametri
					// passati
					Variable var = data.getVariable(v);
					if (p.length < 4) {
						out.println("data>> Inserire parametri:\n"
								+ " larghezza intervallo, freq relativa (true/false)\n");
						continue;
					}
					double inte = Double.parseDouble(p[2]);
					boolean fr_rel = Boolean.parseBoolean(p[3]);
					ClassSet class_set = var.getClassSet(inte, fr_rel);
					HistogramViewer hist1 = new HistogramViewer(var.getId(),
							var, class_set);
					hist1.setBounds(10, 10, 500, 500);
					RefineryUtilities.centerFrameOnScreen(hist1);
					hist1.setVisible(true);
					break;
				case "histogram-g":
					// si crea l'istogramma
					Variable var3 = data.getVariable(v);
					if (var3.getLast_class_set() == null) {
						out.println("\tEseguire prima un class-set sulla variabile!\n");
						continue;
					}
					if (p.length < 4) {
						out.println("data>> Inserire parametri:\n"
								+ " larghezza intervallo, freq relativa (true/false)\n");
						continue;
					}
					double inte3 = Double.parseDouble(p[2]);
					boolean fr_rel3 = Boolean.parseBoolean(p[3]);
					HistogramGaussViewer hist3= new HistogramGaussViewer(
							var3.getId(), var3, var3.getLast_class_set());
					hist3.setBounds(10, 10, 500, 500);
					RefineryUtilities.centerFrameOnScreen(hist3);
					hist3.setVisible(true);
					break;
				case "histogram-g-c":
					// si crea l'istogramma
					Variable var2 = data.getVariable(v);
					if (p.length < 4) {
						out.println("data>> Inserire parametri:\n"
								+ " larghezza intervallo, freq relativa (true/false)\n");
						continue;
					}
					double inte2 = Double.parseDouble(p[2]);
					boolean fr_rel2 = Boolean.parseBoolean(p[3]);
					ClassSet class_set2 = var2.getClassSet(inte2, fr_rel2);
					HistogramGaussViewer hist2 = new HistogramGaussViewer(
							var2.getId(), var2, class_set2);
					hist2.setBounds(10, 10, 500, 500);
					RefineryUtilities.centerFrameOnScreen(hist2);
					hist2.setVisible(true);
					break;
				default:
					out.println("Inserire comando valido!");
				}
			} catch (Exception e) {
				out.println(e.getMessage());
				continue;
			}
		}
	}

	/**
	 * Ciclo che gestisce i comandi per i test del chi quadro
	 * 
	 * @throws IOException
	 */
	private static void test_cycle() throws IOException {
		boolean test_cycle = true;
		while (test_cycle) {
			out.print("test>> ");
			String cmd2 = in.readLine().trim();
			if (cmd2.equals("exit")) {
				test_cycle = false;
				out.println("exit test menu...\n");
				continue;
			}
			String[] p = cmd2.split(" ");
			if (p.length < 2) {
				out.println("\tInserire id oggetto\n");
				continue;
			}
			String v = p[1];
			try {
				switch (p[0]) {
				case "histogram-test-c":
					// si crea un nuovo class-set con i parametri passati.
					Variable var1 = data.getVariable(v);
					if (p.length < 4) {
						out.println("data>> Inserire parametri:\n"
								+ " larghezza intervallo, freq relativa (true/false)\n");
						continue;
					}
					double inte = Double.parseDouble(p[2]);
					boolean fr_rel = Boolean.parseBoolean(p[3]);
					var1.getClassSet(inte, fr_rel);
					double[] res1 = ChiSquaredTest
							.testChiSquared_Histogram(var1);
					// si stampano i risultati
					out.println("Chi Quadro: ........................"
							+ df.format(res1[0]));
					out.println("Chi Quadro ridotto: ................"
							+ df.format(res1[1]));
					out.println("Probabilità: ......................."
							+ df.format(res1[2]) + "%");
					out.println();
					break;
				case "histogram-test":
					// si esegue il test del chi per la variabile utilizzando il
					// class-set già presente, se c'è
					Variable var = data.getVariable(v);
					if (var.getLast_class_set() == null) {
						out.println("\tEseguire prima un class-set sulla variabile!\n");
						continue;
					}
					double[] res = ChiSquaredTest.testChiSquared_Histogram(var);
					// si stampano i risultati
					out.println("Chi Quadro: ........................"
							+ df.format(res[0]));
					out.println("Chi Quadro ridotto: ................"
							+ df.format(res[1]));
					out.println("Probabilità: ......................."
							+ df.format(res[2]) + "%");
					out.println();
					break;
				case "fitting-test":
					// si esegue il test del chi per un'interpolazione lineare.
					LinearFitter fitter = fitter_man.getLinearFitter(v);
					if (fitter.isFitted()) {
						double[] re = ChiSquaredTest
								.testChiSquared_LinearFitting(fitter);
						// si stampano i risultati
						out.println("Chi Quadro: ........................"
								+ df.format(re[0]));
						out.println("Chi Quadro ridotto: ................"
								+ df.format(re[1]));
						out.println("Probabilità: ......................."
								+ df.format(re[2]) + "%");
						out.println();
					} else {
						out.println("\tEseguire prima un fitting sui dati!\n");
						continue;
					}
					break;
				default:
					out.println("Inserire comando valido!");
				}
			} catch (Exception e) {
				out.println(e.getMessage());
				continue;
			}
		}
	}

	/**
	 * Ciclo dei comandi per il fitting.
	 * 
	 * @throws IOException
	 */
	private static void fit_cycle() throws IOException {
		boolean fit_cycle = true;
		while (fit_cycle) {
			out.print("fit>> ");
			String cmd2 = in.readLine().trim();
			if (cmd2.equals("exit")) {
				fit_cycle = false;
				out.println("exit fit menu...\n");
				continue;
			}
			String[] p = cmd2.split(" ");
			// si stampano i fitters
			if (p[0].equals("fitters")) {
				for (LinearFitter fit : fitter_man.getFitters().values()) {
					out.print("\tID: " + fit.getId() + " - N: " + fit.getN());
					for (Variable v : fit.getVariables().values()) {
						out.print("\n\t\tVariable: " + v.getId() + " - N: "
								+ v.getN());
					}
					out.println();
				}
				continue;
			}

			if (p.length < 2) {
				out.println("\tInserire nome del fitter\n");
				continue;
			}
			String f = p[1];
			try {
				// comandi
				switch (p[0]) {
				case "create":
					fitter_man.addLinearFitter(f);
					out.println("\tCreato fitter: " + f + "\n");
					break;
				case "add-variables":
					LinearFitter fitter = fitter_man.getLinearFitter(f);
					out.println("Inserire un id variabile e premere invio. (exit) per uscire...");
					// si inseriscono i valori
					boolean ok = true;
					int i = 1;
					while (ok) {
						out.print("\tvalue-" + f + "-" + i + ">> ");
						String s = in.readLine();
						if (s.equals("")) {
							continue;
						}
						if (s.equals("exit")) {
							ok = false;
							break;
						}
						if (data.containsVariable(s)) {
							Variable v = data.getVariable(s);
							if (v instanceof VariableXY) {
								// si aggiunte
								fitter.addVariable((VariableXY) v);
							} else {
								out.println("Inserire una variabile di tipo XY...");
							}
						} else {
							out.println("Inserire un id variable valido...");
						}
						i++;
					}
					out.println();
					break;
				case "fit-sigma-costant":
					// fitting con sigma per y costante
					// si legge il terzo parametro
					if (p.length < 3) {
						out.println("data>> Inserire parametri:\n"
								+ " id fitter, sigma da utilizzare per le Y\n");
						continue;
					}
					double sigmay = Double.parseDouble(p[2]);
					// si effettua il fitting
					LinearFitter fitt = fitter_man.getLinearFitter(f);
					double[] e = fitt.fit_sigma_costant(sigmay);
					// si stampano i risultati
					out.println("A: ....................." + df.format(e[0]));
					out.println("B: ....................." + df.format(e[1]));
					out.println("Sigma-A: ..............." + df.format(e[2]));
					out.println("Sigma-B: ..............." + df.format(e[3]));
					out.println("Sigma-AB: .............." + df.format(e[4]));
					out.println();
					break;
				case "fit":
					// fittin pesato con i sigma delle variabili
					// si effettua il fitting
					LinearFitter fitte = fitter_man.getLinearFitter(f);
					double[] g = fitte.fit();
					// si stampano i risultati
					out.println("A: ....................." + df.format(g[0]));
					out.println("B: ....................." + df.format(g[1]));
					out.println("Sigma-A: ..............." + df.format(g[2]));
					out.println("Sigma-B: ..............." + df.format(g[3]));
					out.println("Sigma-AB: .............." + df.format(g[4]));
					out.println();
					break;
				default:
					out.println("Inserire comando valido!");
				}
			} catch (Exception e) {
				out.println(e.getMessage());
				continue;
			}
		}
	}

	private static void settings_cycle() throws IOException {
		boolean settings_cycle = true;
		while (settings_cycle) {
			out.print("settings>> ");
			String cmd2 = in.readLine().trim();
			if (cmd2.equals("exit")) {
				settings_cycle = false;
				out.println("exit settings menu...\n");
				continue;
			}
			String[] p = cmd2.split(" ");
			// si stampano le settings correnti
			if (p[0].equals("settings")) {
				out.println("Decimal format: " + df.toPattern());
				if (!path.equals("")) {
					out.println("Current path: " + path);
				} else {
					out.println("Current path: not set");
				}
				if (!project.equals("")) {
					out.println("Current project name: " + project);
				} else {
					out.println("Current project name: not set");
				}
				continue;
			}
			if (p.length < 2) {
				out.println("\tInserire parametro da modificare\n");
				continue;
			}
			String pat = p[1];
			try {
				// comandi
				switch (p[0]) {
				case "decimal-format":
					// si imposta il pattern
					df = new DecimalFormat(pat);
					break;
				case "path":
					path = pat;
					break;
				case "project-name":
					project = pat;
					break;
				default:
					out.println("Inserire comando valido!");
				}
			} catch (Exception e) {
				out.println(e.getMessage());
				continue;
			}
		}
	}

	/**
	 * Metodo che salva i dati su disco. Si salvano in un unico file tutte le
	 * variabili.
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private static void saveData(String path, String name) throws IOException {
		// si crea un oggetto json
		JSONObject json = new JSONObject();
		json.put("project", name);
		LinkedHashMap<String, LinkedList<Double>> map_v = new LinkedHashMap<>();
		LinkedHashMap<String, Double> X_map = new LinkedHashMap<>();
		for (Variable v : data.getVariables().values()) {
			LinkedList<Double> ms = new LinkedList<>();
			for (double m : v.getMeasures()) {
				ms.add(m);
			}
			map_v.put(v.getId(), ms);
			// Se c'è la x si aggiunge
			if (v instanceof VariableXY) {
				X_map.put(v.getId(), ((VariableXY) v).getX());
			}
		}
		// mappa delle variabili
		json.put("variables", map_v);
		json.put("x_map", X_map);
		// si inseriscono i fitter
		LinkedHashMap<String, LinkedList<String>> map_f = new LinkedHashMap<>();
		for (LinearFitter v : fitter_man.getFitters().values()) {
			LinkedList<String> ms = new LinkedList<>();
			for (VariableXY va : v.getVariables().values()) {
				ms.add(va.getId());
			}
			map_f.put(v.getId(), ms);
		}
		// mappa dei fitter
		json.put("fitters", map_f);
		String path_touse;
		// scrittura su file
		if (path.equals("")) {
			path_touse = System.getProperty("user.home") + File.separator
					+ name + ".b9";
		} else {
			path_touse = path + File.separator + name + ".b9";
		}
		// si scrive l'oggetto
		FileWriter w = new FileWriter(path_touse);
		w.write(json.toJSONString());
		w.close();
		out.println("Progetto salvato in: " + path_touse);
	}

	/**
	 * Metodo che carica i dati da un file B9. Carica i valori contenuti nella
	 * variabile e le valuta.
	 * 
	 * @param path
	 * @throws Exception
	 */
	private static void loadData(String path) throws Exception {
		// si legge l'oggetto json
		BufferedReader reader = new BufferedReader(new FileReader(path));
		JSONParser parser = new JSONParser();
		JSONObject json = (JSONObject) parser.parse(reader.readLine());
		// si leggono le variabili
		JSONObject vars = (JSONObject) json.get("variables");
		JSONObject x_map = (JSONObject) json.get("x_map");
		JSONObject fitts = (JSONObject) json.get("fitters");
		project = (String) json.get("project");
		// si salva il percorso relativo
		B9_Main.path = path
				.substring(0, path.length() - (project.length() + 4));
		// si leggono le variabili
		for (Object u : vars.keySet()) {
			String id = (String) u;
			// si crea la variabile
			data.addVariable(id);
			Variable v = data.getVariable(id);
			JSONArray arr = (JSONArray) vars.get(id);
			for (Object mea : arr) {
				double m = (double) mea;
				// si aggiunge la misura
				v.addMeasure(m);
			}
			// si valuta la variabile
			v.evaluate();
			out.println("Loaded variable: " + id + " - N: " + v.getN());
		}
		// si leggono le x delle varibili
		for (Object m : x_map.keySet()) {
			String id = (String) m;
			// si aggiunge alla variabile la sua x
			double x = (double) x_map.get(m);
			data.addX_toVariable(id, x);
		}
		// si leggono i fitter
		for (Object f : fitts.keySet()) {
			String id = (String) f;
			// si crea la variabile
			fitter_man.addLinearFitter(id);
			LinearFitter fit = fitter_man.getLinearFitter(id);
			JSONArray arr = (JSONArray) fitts.get(id);
			for (Object v : arr) {
				String m = (String) v;
				// si aggiunge la misura
				VariableXY var = (VariableXY) data.getVariable(m);
				fit.addVariable(var);
			}
			out.println("Loaded fitter: " + id + " - N: " + fit.getN());
		}
		reader.close();
	}
}
