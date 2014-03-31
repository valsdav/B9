package it.b.main;

import it.b.data.ClassSet;
import it.b.data.DataManager;
import it.b.data.Variable;
import it.b.view.HistogramGaussViewer;
import it.b.view.HistogramViewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DecimalFormat;

import javax.swing.JFrame;

import org.jfree.ui.RefineryUtilities;

public class B9_Main {

	private static PrintStream out = System.out;
	private static BufferedReader in = new BufferedReader(
			new InputStreamReader(System.in));
	private static DataManager data;
	private static DecimalFormat df = new DecimalFormat("####.#####");

	public static void main(String[] args) throws IOException {
		// inizializzazione dataManager
		data = new DataManager();
		out.println("############### B9 ###############");
		boolean ok = true;
		while (ok) {
			out.print(">> ");
			String cmd = in.readLine();

			switch (cmd) {
			case "data":
				data_cycle();
				break;
			case "graph":
				// si avvia la parte grafici
				graph_cycle();
				break;
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
			String cmd2 = in.readLine();
			if (cmd2.equals("")) {
				continue;
			} else if (cmd2.equals("exit")) {
				data_cycle = false;
				out.println("exit data menu...\n");
				continue;
			}
			String[] p = cmd2.split(" ");
			if (p.length < 2) {
				out.println("\tInserire parametro variable\n");
				continue;
			}
			String v = p[1];
			switch (p[0]) {
			case "create":
				// si aggiunge la variabile
				data.addVariable(v);
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
				out.println("Media: ................" + df.format(e[1]));
				out.println("Varianza: ............." + df.format(e[2]));
				out.println("Varianza Media: ......." + df.format(e[3]));
				out.println("Deviazione ST: ........" + df.format(e[4]));
				out.println("Deviazione ST Media: .." + df.format(e[5]));
				out.println();
				break;
			case "add-values":
				if(!data.containsVariable(v)){
					out.println("Creare prima la variabile...");
					continue;
				}
				Variable var2 = data.getVariable(v);
				out.println("Inserire un valore e premere invio. (exit) per uscire...");
				// si inseriscono i valori
				boolean ok = true;
				while (ok) {
					out.print("\tvalue-" + var2.getId() + ">> ");
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
				ClassSet class_set2 = var4.getClassSetNIntervals(inte2, fr_rel2);
				out.println(class_set2);
				break;case "class-set-s":
					// si legge il terzo parametro
					if (p.length < 4) {
						out.println("data>> Inserire parametri:\n"
								+ " larghezza intervallo, freq relativa (true/false)\n");
						continue;
					}
					double sigma_factor = Double.parseDouble(p[2]);
					boolean fr_rel3 = Boolean.parseBoolean(p[3]);
					Variable var5 = data.getVariable(v);
					ClassSet class_set3 = var5.getClassSetSigmaFactor(sigma_factor, fr_rel3);
					out.println(class_set3);
					break;
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
			String cmd2 = in.readLine();
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

			switch (p[0]) {
			case "histogram":
				// si crea l'istogramma
				final Variable var = data.getVariable(v);
				if (p.length < 4) {
					out.println("data>> Inserire parametri:\n"
							+ " larghezza intervallo, freq relativa (true/false)\n");
					continue;
				}
				double inte = Double.parseDouble(p[2]);
				boolean fr_rel = Boolean.parseBoolean(p[3]);
				final ClassSet class_set = var.getClassSet(inte, fr_rel);
				// si avvia in un nuovo thread
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						HistogramViewer hist = new HistogramViewer(var.getId(),
								var, class_set);
						hist.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
						hist.setBounds(10, 10, 500, 500);
						RefineryUtilities.centerFrameOnScreen(hist);
						hist.setVisible(true);
					}
				});
				thread.start();
				break;
			case "histogram-c":
				// si crea l'istogramma
				final Variable var2 = data.getVariable(v);
				if (p.length < 4) {
					out.println("data>> Inserire parametri:\n"
							+ " larghezza intervallo, freq relativa (true/false)\n");
					continue;
				}
				double inte2 = Double.parseDouble(p[2]);
				boolean fr_rel2 = Boolean.parseBoolean(p[3]);
				final ClassSet class_set2 = var2.getClassSet(inte2, fr_rel2);
				// si avvia in un nuovo thread
				Thread thread2 = new Thread(new Runnable() {
					@Override
					public void run() {
						HistogramGaussViewer hist = new HistogramGaussViewer(var2.getId(),
								var2, class_set2);
						hist.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
						hist.setBounds(10, 10, 500, 500);
						RefineryUtilities.centerFrameOnScreen(hist);
						hist.setVisible(true);
					}
				});
				thread2.start();
				break;
			}
		}
	}
}
