package markovChain;
import java.util.ArrayList;   // Imports needed for lists/arrays
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;   // Imports JFrame
import javax.swing.*;       // Import JFrame modules
import java.awt.*;
import java.util.HashMap;  // Import hashmap

public class Node {
	// Properties of each node
	List<Node> out_verticies = new ArrayList<Node>();  // List of node objects this node goes out to
	List<Integer> rates = new ArrayList<Integer>();  // List of rates to outgoing nodes
	JButton self_map = new JButton();    // We will use this to connect the object to its place on the JFrame visual
	int recovery_counter = 0;   // This is the experimental recovery variable each node has in infection walks
	boolean immunity = false;   // Another experimental variable to see if the node was previously infected
	
	
	public static void walk_node(Node current, int iterations, boolean time_delay) {
		// Performs a markov chain walk for given number of iterations
		for (int j=0;j<iterations;j++) {
			System.out.println(current);
			current.self_map.setBackground(Color.GREEN);    // Print and change colour of current location to green
			Node new_location = fetch_next_node(current);
			// For authenticity, we delay by the rate of the node we're going to (if we choose to)
			if (time_delay == true) {
				try {
					TimeUnit.MILLISECONDS.sleep(current.rates.get(current.out_verticies.indexOf(new_location)));
				}
				catch(InterruptedException e) {
					// Catch needed as Java fussy with potential errors of time sleep
					Thread.currentThread().interrupt();
					throw new AssertionError(e);
				}
			}
			// Finally, move to the new node (set colour of node we're leaving to gray)
			current.self_map.setBackground(Color.GRAY);
			current = new_location;
		}
		System.out.println("Done all iterations");
	}
	
	
	private static Node fetch_next_node(Node current) {
		// Returns next location for a node 
		Node next_location = current;
		
		// Check for next location (if any) and choose at random using rates
		Random rand = new Random();
		int sum_rates = current.rates.stream().mapToInt(Integer::intValue).sum();
		int n = rand.nextInt(sum_rates)+1;
		int tracker = 0;
		// We have random number from 0 to sum of rates. We iterate through elements of rates until tracker is greater than n
		// The index responsible is the same index of the vertex we choose
		for (int i=0; i<current.out_verticies.size(); i++) {
			tracker += current.rates.get(i);
			if (tracker>=n) {
				next_location = current.out_verticies.get(i);
				return next_location;
			}
		}
		
		// Return next node
		return next_location;
	}
	
	
	public static HashMap<String, Object> infection_walk(List<Node> chain, Node start, boolean time_delay, boolean recovery, int recovery_threshold, int cutoff) {
		// Performs an infection walk until all nodes infected
		// This walk collects data to be returned at the end
		List<Node> infected_nodes = new ArrayList<Node>();
		infected_nodes.add(start);
		start.self_map.setBackground(Color.RED);	
		
		// These are results we will collect (as well as final result and summary at the end)
		// As we collect a variety of data types we will use an object data type hash map
		HashMap<String, Object> results = new HashMap<String, Object>();
		List<Integer> summary = new ArrayList<Integer>();
		summary.add(infected_nodes.size());
		results.put("peak number of infections", 1);
        
		// Repeat until all nodes infected
		Random rand = new Random();
		int iterations = 1;
		while ((infected_nodes.size() < chain.size()) && ((int) 0 < infected_nodes.size()) && (iterations < cutoff)) {
			// Pick any node at random and a node it connects to at random
			// Then simply spread the infection to it if the first one is infected
			Node spreading_node = chain.get(rand.nextInt(chain.size()));
			Node target_node = spreading_node.out_verticies.get(rand.nextInt(spreading_node.out_verticies.size()));
			iterations++;
			
			// If we want a time delay we do it here
			if (time_delay == true) {
				try {
					TimeUnit.MILLISECONDS.sleep(10);
				}
				catch(InterruptedException e) {
					// Catch needed as Java fussy with potential errors of time sleep
					Thread.currentThread().interrupt();
					throw new AssertionError(e);
				}
			}
			
			if ((infected_nodes.contains(target_node) == false) && (infected_nodes.contains(spreading_node) == true)) {
				if ((target_node.immunity == false) || ((target_node.immunity == true) && (rand.nextInt(3) == 0))) {
					// 1/3 chance to re-infect immune node
					infected_nodes.add(target_node);
					target_node.self_map.setBackground(Color.RED);
				}
			}
			
			// This is a little something I'm experimenting
			// Each node has a counter for how long it has been infected. If it exceeds a threshold it 'recovers' and is no longer infected
			if (recovery == true) {
				for (int i=0; i<infected_nodes.size(); i++) {
					if (infected_nodes.get(i).recovery_counter >= recovery_threshold) {
						// Threshold on node has been reached so it 'recovers'. Reset its recovery counter and immunity is true
						infected_nodes.get(i).self_map.setBackground(Color.GRAY);
						infected_nodes.get(i).recovery_counter = 0;
						infected_nodes.get(i).immunity = true;
						infected_nodes.remove(i);
					}
					else {
						// Increment recovery counter
						infected_nodes.get(i).recovery_counter++;
					}
				}
			}
			
			// Update results at end of iteration
			summary.add(infected_nodes.size());
			if (infected_nodes.size() > (int) results.get("peak number of infections")) {
				results.put("peak number of infections", infected_nodes.size());
			}
		}
		// Now we return the observed results
		results.put("summary", summary);
		if (infected_nodes.size() == 0){
			// Virus quarantined
			results.put("final result", 1);
			return results;
		}
		else if (infected_nodes.size() == chain.size()) {
			// All nodes infected
			results.put("final result", 2);
			return results;
		}
		else {
			// Cutoff exceeded (stalemate)
			results.put("final result", 0);
			return results;
		}
		
	}
	
	
	public static HashMap<String, Object> analyse_infection_walk(List<Node> chain, int num_trials, Node start, int cutoff, boolean recovery, int recovery_threshold) {
		// This does repeated trials on a chain with infection walks and analyses results
		// These are the subjects we will analyse
		HashMap<String, Object> analysis = new HashMap<String, Object>();
		analysis.put("total full quarantines", 0);
		analysis.put("total full infections", 0);
		analysis.put("total inconclusive", 0);
		analysis.put("percentage full quarantines", 0.);
		analysis.put("percentage full infections", 0.);
		analysis.put("percentage inconclusive", 0.);
		// Our average summaries will be added at the end for simplicity
		List<Double> average_summary_quarantines = new ArrayList<Double>();
		List<Double> average_summary_infections = new ArrayList<Double>();
		List<Double> average_summary_inconclusive = new ArrayList<Double>();
		for (int i = 0; i < cutoff; i++) {
			// Need to set size of arrays before loop
			average_summary_quarantines.add(0.);
			average_summary_infections.add(0.);
			average_summary_inconclusive.add(0.);
		}
		
		
		// Iterate through infection walks and compile results
		for (int i=0; i<num_trials; i++) {
			HashMap<String, Object> new_data = start.infection_walk(chain, start, false, recovery, recovery_threshold, cutoff);
				
			
			// Analysis of final results and percentage final results
			if ((int) new_data.get("final result") == 1) {
				analysis.put("total full quarantines", (int) analysis.get("total full quarantines") + 1);
				analysis.put("percentage full quarantines", (double) analysis.get("percentage full quarantines") + (1./num_trials));
			}
			else if ((int) new_data.get("final result") == 2) {
				analysis.put("total full infections", (int) analysis.get("total full infections") + 1);
				analysis.put("percentage full infections", (double) analysis.get("percentage full infections") + (1./num_trials));
			}
			else {
				analysis.put("total inconclusive", (int) analysis.get("total inconclusive") + 1);
				analysis.put("percentage inconclusive", (double) analysis.get("percentage inconclusive") + (1./num_trials));
			}
			// Analysis of average summaries (we add them to hashmap at the end)
			for (int j=0; j < ((List<Integer>) new_data.get("summary")).size(); j++) {
				if ((int) new_data.get("final result") == 1) {
					average_summary_quarantines.set(j, average_summary_quarantines.get(j) + (double) (((List<Integer>) new_data.get("summary")).get(j)));
				}
				else if ((int) new_data.get("final result") == 2) {
					average_summary_infections.set(j, average_summary_infections.get(j) + (double) (((List<Integer>) new_data.get("summary")).get(j)));
				}
				else {
					average_summary_inconclusive.set(j, average_summary_inconclusive.get(j) + (double) (((List<Integer>) new_data.get("summary")).get(j)));
				}
			}
			
			
			for (Node j : chain) {
				// Need to reset node variables that may have been changed in previous infection walk
				// Note that deep copying the chain would be more effective but idk how to do that
				j.immunity = false;
				j.recovery_counter = 0;
			}
		}
		// Finally return compiled analysis (with average summaries (remembering to divide them))
		for (int i=0; i<cutoff; i++) {
			average_summary_quarantines.set(i, average_summary_quarantines.get(i) / Math.max(1, (int) analysis.get("total full quarantines")));
			average_summary_infections.set(i, average_summary_infections.get(i) / Math.max(1, (int) analysis.get("total full infections")));
			average_summary_inconclusive.set(i, average_summary_inconclusive.get(i) / Math.max(1, (int) analysis.get("total inconclusive")));
		}	
		analysis.put("average summary quarantines", average_summary_quarantines);
		analysis.put("average summary infections", average_summary_infections);
		analysis.put("average summary inconclusive", average_summary_inconclusive);
		// JAVA CANNOT PLOT LINES EASILY SO EXPORT SUMMARY TO PYTHON FOR A PLOT!!!!!
		return analysis;
	}
	
}
