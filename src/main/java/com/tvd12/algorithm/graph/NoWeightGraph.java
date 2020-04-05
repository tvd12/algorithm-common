package com.tvd12.algorithm.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// class to represent a graph object
public class NoWeightGraph {
	
	// data structure to store graph edges
	private static class Edge {
		private int src;
		private int dest;

		private Edge(int src, int dest) {
			this.src = src;
			this.dest = dest;
		}
	};

	// A list of lists to represent adjacency list
	private List<List<Integer>> adj = new ArrayList<>();

	// Constructor to construct graph
	public NoWeightGraph(List<Edge> edges) {
		// allocate memory for adjacency list
		for (int i = 0; i < edges.size(); i++)
			adj.add(i, new ArrayList<>());

		// add edges to the undirected graph
		for (Edge current : edges) {
			// allocate new node in adjacency List from src to dest
			adj.get(current.src).add(current.dest);

			// Uncomment line 38 for undirected graph
	
			// allocate new node in adjacency List from dest to src
			// adj.get(current.dest).add(current.src);
		}
	}
	
	// print adjacency list representation of graph
	@Override
	public String toString() {
		int src = 0;
		int n = adj.size();
		StringBuilder builder = new StringBuilder();
		while (src < n) {
			// print current vertex and all its neighboring vertices
			for (int dest : adj.get(src))
				builder.append("(" + src + " --> " + dest + ")\t");

			builder.append("\n");
			src++;
		}
		return builder.toString();
	}

	
	// Directed Graph Implementation in Java
	public static void main(String[] args) {
		// Input: List of edges in a digraph (as per above diagram)
		List<Edge> edges = Arrays.asList(
				new Edge(0, 1), 
				new Edge(1, 2), 
				new Edge(2, 0), 
				new Edge(2, 1), 
				new Edge(3, 2),
		        new Edge(4, 5), 
		        new Edge(5, 4));

		// construct graph from given list of edges
		NoWeightGraph graph = new NoWeightGraph(edges);

		// print adjacency list representation of the graph
		System.out.println(graph);
	}
}