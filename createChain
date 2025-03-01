package markovChain;

import javax.swing.JFrame;   // Imports JFrame
import javax.swing.*;       // Import JFrame modules
import java.awt.*;
import java.util.ArrayList;   // Imports needed for lists/arrays
import java.util.HashMap;
import java.util.List;
import java.io.IOException;

public class createChain {
	
	// FOR ACTUAL DISSITATION USE PYTHON JUPYTER AS JAVA CAN'T DO PLOTS OR MATH VERY WELL!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// (Logic used here is the same though; just different syntax)
	
	
	public static void main(String[] args) {	
		// For visual aid we use JFrame (java version of tkinter)
		// Only useful if we keep time delay between node movements
		
		int num_nodes = 100;   // Number of nodes we will use
		int virus_potency = 9;  // More potency means the longer it takes for a node to recover
		List<Node> chain = get_complete_graph_chain(num_nodes, 1000);
		show_chain_jframe(chain);
		//chain.get(0).infection_walk(chain, chain.get(0), true, true, num_nodes*virus_potency, num_nodes*2000);
		//chain.get(0).walk_node(chain.get(0), 2000, true);
		HashMap<String, Object> results = chain.get(0).analyse_infection_walk(chain, 200, chain.get(0), num_nodes*400, true, num_nodes*virus_potency);
		//plot_average_summary((List<Double>)results.get("average summary infections"),(List<Double>)results.get("average summary quarantines"),(List<Double>)results.get("average summary inconclusive"));
	}
	
	

	private static List<Node> get_cycle_chain(int num_nodes, int rate){
		// Creating chain using iteration (this is a cycle graph)
		List<Node> chain = new ArrayList<Node>();   // List of nodes in chain
		
		// First node needs adding prior to loop
		chain.add(new Node());
		// Now iterate to add nodes
		for (int i=1;i<num_nodes;i++) {
			chain.add(chain.size(), new Node());  // Add new node to end of array
			chain.get(chain.size()-2).out_verticies.add(chain.get(chain.size()-1));   // Connect nodes in a cycle graph
			chain.get(chain.size()-2).rates.add(rate);
		}
		chain.get(chain.size()-1).out_verticies.add(chain.get(0));
		chain.get(chain.size()-1).rates.add(rate);            // Finally link last node to first node to complete cycle	
		// We return the chain array
		return chain;
	}
	
	
	private static void show_chain_jframe(List<Node> chain) {
		// We call method to display JFrame output of given chain
		JFrame chain_map = new JFrame("Markov Chain");
		chain_map.setSize(800,300);
		JPanel panel = new JPanel();
		
		// Iterate through each node in chain to add them to panel using their self_maps
		for (int i=0; i<chain.size(); i++) {
			panel.add(chain.get(i).self_map);
		}
		chain_map.add(panel);
		
		// Finally show the JFrame
		chain_map.setVisible(true);
	}
	
	
	private static List<Node> get_complete_graph_chain(int num_nodes, int rate){
		// Creating a complete graph markov chain (nodes don't self loop in this one so minimum of 2 nodes required)
		List<Node> chain = new ArrayList<Node>();   // List of nodes in chain
		// Now iterate to add nodes
		for (int i=0;i<num_nodes;i++) {
			chain.add(new Node());  // Add all nodes needed to array
		}
		// Now iterate again to link all nodes together
		for (Node i : chain) {
			for (Node j : chain) {
				if (i != j) {
					i.out_verticies.add(j);
					i.rates.add(rate);
				}
			}
		}
		// We return the chain array
		return chain;
	}
	
	
	private static List<Node> get_star_chain(int num_nodes, int rate){
		// Creating star graph chain around a central node
		List<Node> chain = new ArrayList<Node>();
		Node center = new Node();
		chain.add(center);
		for (int i=1; i<num_nodes; i++) {
			// Add and connect each node to central node
			Node point = new Node();
			chain.add(point);
			point.out_verticies.add(center);
			point.rates.add(rate);
			center.out_verticies.add(point);
			center.rates.add(rate);
		}
		// Return chain array
		return chain;
	}
	
	
	private static void plot_average_summary(List<Double> infection_array, List<Double> quarantine_array, List<Double> inconclusive_array) {
		// Need to convert List<Double> to strings to pass them through as parameters
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("C:/Users/thoma/OneDrive/Documents/Python/infection_walk_plotter.py", "val1", "val2", "val3");
			Process process = processBuilder.start();
		}
		catch (IOException e) {
        	System.err.println("An IOException occurred");
            e.printStackTrace();
        }
	}
	
	
	private static void plot_predator_prey(List<Double> array, int number_nodes) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("C:/Users/thoma/OneDrive/Documents/Python/predator_prey_infection_walk_plot.py", "val1", Integer.toString(number_nodes));
			Process process = processBuilder.start();
        }
        catch (IOException e) {
        	System.err.println("An IOException occurred");
            e.printStackTrace();
        }
	}
	
}
