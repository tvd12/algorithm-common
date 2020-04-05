package com.tvd12.algorithm.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import lombok.Getter;

public class GraphSimple {

	private final Map<Vertex, List<Vertex>> adjVertices; // adjacency matrix
	
	public GraphSimple() {
		this.adjVertices = new HashMap<>();
	}
	
	public void addVertex(String label) {
		Vertex vertex = new Vertex(label);
		adjVertices.computeIfAbsent(vertex, k -> new ArrayList<>());
	}
	
	public void removeVertex(String label) {
		Vertex vertex = new Vertex(label);
		adjVertices.values().stream().forEach(e -> e.remove(vertex));
		adjVertices.remove(vertex);
	}
	
	public void addEdge(String label1, String label2) {
		Vertex v1 = new Vertex(label1);
	    Vertex v2 = new Vertex(label2);
	    adjVertices.computeIfAbsent(v1, k -> new ArrayList<>()).add(v2);
	    adjVertices.computeIfAbsent(v2, k -> new ArrayList<>()).add(v1);
	}
	
	public void removeEdge(String label1, String label2) {
	    Vertex v1 = new Vertex(label1);
	    Vertex v2 = new Vertex(label2);
	    List<Vertex> eV1 = adjVertices.get(v1);
	    List<Vertex> eV2 = adjVertices.get(v2);
	    if (eV1 != null)
	        eV1.remove(v2);
	    if (eV2 != null)
	        eV2.remove(v1);
	}
	
	public List<Vertex> getAdjVertices(String label) {
		List<Vertex> answer = adjVertices.get(new Vertex(label));
		if(answer != null)
			return answer;
		throw new IllegalArgumentException("has no adjacency for: " + label);
	}
	
	public Set<String> depthFirstTraversal(String root) {
		Set<String> visited = new LinkedHashSet<>();
		Stack<String> stack = new Stack<>();
		stack.push(root);
		while(stack.size() > 0) {
			String vertex = stack.pop();
			if(!visited.contains(vertex)) {
				visited.add(vertex);
				for(Vertex v : getAdjVertices(vertex)) {
					stack.push(v.label);
				}
			}
		}
		return visited;
	}
	
	public Set<String> breadthFirstTraversal(String root) {
		Set<String> visited = new LinkedHashSet<String>();
	    Queue<String> queue = new LinkedList<String>();
	    queue.add(root);
	    visited.add(root);
	    while(queue.size() > 0) {
	    	String vertex = queue.poll();
	    	for(Vertex v : getAdjVertices(vertex)) {
	    		if(!visited.contains(v.label)) {
	    			visited.add(v.label);
	    			queue.offer(v.label);
	    		}
	    	}
	    }
	    return visited;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(Vertex vertex : adjVertices.keySet()) {
			builder.append(vertex).append(" => ").append(adjVertices.get(vertex));
			builder.append("\n");
		}
		return builder.toString();
	}
	
	@Getter
	private static class Vertex {
		private String label;
		
		public Vertex(String label) {
			if(label == null)
				throw new NullPointerException("label can not be null");
			this.label = label;
		}
		
		@Override
		public boolean equals(Object obj) {
			return label.equals(((Vertex)obj).label);
		}
		
		@Override
		public int hashCode() {
			return label.hashCode();
		}
		
		@Override
		public String toString() {
			return label;
		}
	}
	
	public static void main(String[] args) {
		GraphSimple graph = new GraphSimple();
	    graph.addVertex("Bob");
	    graph.addVertex("Alice");
	    graph.addVertex("Mark");
	    graph.addVertex("Rob");
	    graph.addVertex("Maria");
	    graph.addEdge("Bob", "Alice");
	    graph.addEdge("Bob", "Rob");
	    graph.addEdge("Alice", "Mark");
	    graph.addEdge("Rob", "Mark");
	    graph.addEdge("Alice", "Maria");
	    graph.addEdge("Rob", "Maria");
	    System.out.println(graph);
	    System.out.println(graph.depthFirstTraversal("Bob"));
	    System.out.println(graph.breadthFirstTraversal("Bob"));
	}
	
}
