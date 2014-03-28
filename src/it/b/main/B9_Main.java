package it.b.main;

import it.b.data.DataManager;
import it.b.data.Variable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DecimalFormat;

public class B9_Main {

	private static PrintStream out = System.out;
	private static BufferedReader in = new BufferedReader(
			new InputStreamReader(System.in));
	private static DataManager data;
	private static DecimalFormat df = new DecimalFormat("####.#####");

	public static void main(String[] args) throws IOException {
		// inizializzazione dataManager
		data = new DataManager();
		out.println("########## B9 ##########");
		out.print(">> ");
		boolean ok = true;
		while (ok) {
			String cmd = in.readLine();

			switch (cmd) {
			case "data":
				data_cycle();
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
			String[] p = cmd2.split(" ");
			if (p.length < 2) {
				out.println("data>> Inserire parametro variable\n");
				continue;
			}
			String v = p[1];
			switch (p[0]) {
			case "create":
				// si aggiunge la variabile
				data.addVariable(v);
				out.print("data>> Creata variabile: " + v + "\n");
				break;
			case "values":
				// si stampano i valori
				Variable va = data.getVariable(v);
				for (double d : va.getMeasures()) {
					out.println(df.format(d));
				}
				break;
			case "estimate":
				// si stampano i valori statistici
				Variable var = data.getVariable(v);
				double[] e = var.evaluate();
				out.println("N: ...................." + df.format(e[0]));
				out.println("Media: ................" + df.format(e[1]));
				out.println("Varianza: ............." + df.format(e[2]));
				out.println("Varianza Media: ......." + df.format(e[3]));
				out.println("Deviazione ST: ........" + df.format(e[4]));
				out.println("Deviazione ST Media: .." + df.format(e[5]));
				break;
			case "add":
				Variable var2 = data.getVariable(v);
				out.println("Inserire un valore e premere invio. (exit) per uscire...");
				// si inseriscono i valori
				boolean ok = true;
				while (ok) {
					out.print("\tvalue>> ");
					String s = in.readLine();
					if (s.equals("exit")) {
						ok = false;
						break;
					}
					double m = Double.parseDouble(s);
					var2.addMeasure(m);
				}
				break;
			}
		}
	}
}
