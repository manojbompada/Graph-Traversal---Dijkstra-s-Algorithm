import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeMap;

//Used to signal violations of preconditions for
//various shortest path algorithms.
class GraphException extends RuntimeException
{
	
	private static final long serialVersionUID = 1L;

	public GraphException(String name) {
		super(name);
	}
}

// Represents a vertex in the graph.
class Vertex implements Comparable<Vertex>
{

	public String name;            // Vertex name
	public List<Edge> adj;         // Adjacent vertices
	public Vertex prev;            // Previous vertex on shortest path
	public float dist;             // Distance of path
	public Boolean vstate = false; // Vertex State (down/up)
	public String vcolor;  // vertex color represents whether it is visited/not visited

	public Vertex(String nm) {
		name = nm;
		adj = new LinkedList<Edge>();

		reset();

	}

	public void reset()
	{
		dist = Graph.INFINITY;
		prev = null;
	}

	public float getDist()
	{
		return dist;
	}

	public void setDist(float dist)
	{
		this.dist = dist;
	}

	public int compareTo(Vertex other)
	{
		return dist < other.dist ? 1 : -1;
	}

	public Boolean getVstate()
	{
		return vstate;
	}

	public void setVstate(Boolean vstate)
	{
		this.vstate = vstate;
	}

	public String getVcolor()
	{
		return vcolor;
	}

	public void setVcolor(String color)
	{
		this.vcolor = color;
	}

	public Vertex getPrev()
	{
		return prev;
	}

	public void setPrev(Vertex prev)
	{
		this.prev = prev;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}

// Represents edges in a graph

class Edge implements Comparable<Edge>
{

	public Vertex vertex1;         // one end of the eedge
	public Vertex vertex2;         // other end of the edge
	public float edgetime;         // represents traverse time between two edges
	public Boolean estate = false; // edge State (down/up)
	public boolean erstate = false;

	public Edge(Vertex v1, Vertex v2, float wt) {
		vertex1 = v1;
		vertex2 = v2;
		edgetime = wt;

	}

	public Edge(Vertex edgetail, Vertex edgehead) {
		vertex1 = edgetail;
		vertex2 = edgehead;
		edgetime = 0;
	}

	public Vertex getVertex1()
	{
		return vertex1;
	}

	public void setVertex1(Vertex vertex1)
	{
		this.vertex1 = vertex1;
	}

	public Vertex getVertex2()
	{
		return vertex2;
	}

	public void setVertex2(Vertex vertex2)
	{
		this.vertex2 = vertex2;
	}

	public float getEdgetime()
	{
		return edgetime;
	}

	public void setEdgetime(float edgetime)
	{
		this.edgetime = edgetime;
	}

	public Boolean getEstate()
	{
		return estate;
	}

	public void setEstate(Boolean estate)
	{
		this.estate = estate;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((vertex1 == null) ? 0 : vertex1.hashCode());
		result = prime * result + ((vertex2 == null) ? 0 : vertex2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (vertex1 == null) {
			if (other.vertex1 != null)
				return false;
		} else if (!vertex1.equals(other.vertex1))
			return false;
		if (vertex2 == null) {
			if (other.vertex2 != null)
				return false;
		} else if (!vertex2.equals(other.vertex2))
			return false;
		return true;
	}

	public int compareTo(Edge other)
	{
		return this.vertex2.name.compareTo(other.vertex2.name);
	}
}

// implementation of min heap

class min_heap
{

	public Vertex[] heaparray;    // maintains heaparray of type vertex
	public int items = 0;         // housekeeping of no. of elements in the array
	public int maxsize;

	public min_heap(int maxsize) {
		this.maxsize = maxsize;
		heaparray = new Vertex[maxsize];
	}

	public int getMaxsize()
	{
		return maxsize;
	}

	public void setMaxsize(int maxsize)
	{
		this.maxsize = maxsize;
	}

	// This method inserts an object into an array
	public void insert(int index, Vertex newvertex)
	{
		heaparray[index] = newvertex;
		insert_heapify(index);
	}

	// This methods increments the count of elements in the heap array after insertion
	public void incrementArray()
	{

		items++;
	}

	public Vertex pop()
	{
		if (items != 0) {
			Vertex root = heaparray[0];
			heaparray[0] = heaparray[--items];
			pop_heapify(0);
			return root;
		}

		return null;
	}

	// This method will increase the priority of a key value based on distance of the vertices after insertion
	private void insert_heapify(int index)
	{

		while (index > 0) {
			int root = (int) Math.floor(index / 2);
			if (heaparray[root].dist > heaparray[index].dist) {
				Vertex temp = heaparray[root];
				heaparray[root] = heaparray[index];
				heaparray[index] = temp;
			}
			index = root;
		}
	}

	// pop_heapify will heapify the vertices based on the distances of the vertices by floating down
	public void pop_heapify(int index)
	{
		if (items > 2) {
			int smallestchild;
			Vertex root = heaparray[index];

			while (index < items / 2) {

				int leftchild = 2 * index + 1;
				int rightchild = leftchild + 1;

				if (items > 2) {
					if (leftchild < items
							&& heaparray[leftchild].dist < heaparray[rightchild].dist) {
						smallestchild = leftchild;
					} else {
						smallestchild = rightchild;
					}

					if (root.dist <= heaparray[smallestchild].dist)
						break;

					heaparray[index] = heaparray[smallestchild];
					index = smallestchild;
				} else {
					if ((leftchild) < items) {
						smallestchild = leftchild;

						if (root.dist <= heaparray[smallestchild].dist)
							break;

						heaparray[index] = heaparray[smallestchild];
						index = smallestchild;
					}
				}
				heaparray[index] = root;
			}
		}
	}
}

// Graph class:
// ******************PUBLIC OPERATIONS*******************************************************************
// void addEdge( String s, String d, float f )---->adds two directional weighted edges to graph
// void addedge( String s, String d, float f )---->adds unidirectional weighted edges to graph
// void deleteedge(String t, String h)----->delete edges from graph
// void vertexdown(String s)---->makes vertex of the graph inactive
// void vertexup(String s)---->makes vertex of the graph active
// void edgedown(String s, String d)---->an edge of a graph made to down
// void edgeup(String s, String d)---->makes edge of a graph active which is inactive before
// void reachable_vertices()---->finds all the reachable vertices from all the vertices of graph
// void DFS_Visit(Vertex v, Stack<Vertex> dfs, boolean b )---->traverse the graph through DFS using stack
// void printGraph()---->prints all the vertices with its corresponding edges of the graph
// void printPath( String w )---->Prints paths between two vertices
// void weighted( String s )---->Calculates shortest path between two vertices using dijikistra algorithm
// ******************ERRORS*******************************************************************************
// Some error checking is performed to make sure graph is ok,
// and to make sure graph satisfies properties needed by each
// algorithm. Exceptions are thrown if errors are detected.

public class Graph
{
	public static final int INFINITY = Integer.MAX_VALUE;
	public static Map<String, Vertex> vertexMap = new HashMap<String, Vertex>();

	/**
	 * Add a new weighted two directional edge to the graph.
	 */
	public void addEdge(String sourceName, String destName, float wt)
	{

		Vertex sourceVertex;
		Vertex destinationVertex;

		sourceVertex = vertexMap.get(sourceName);
		destinationVertex = vertexMap.get(destName);

		if (sourceVertex == null) {
			// System.out.println("Provided source is not present");
			sourceVertex = new Vertex(sourceName);
			vertexMap.put(sourceName, sourceVertex);
		}
		if (destinationVertex == null) {
			// System.out.println("Provided dest is not present");
			destinationVertex = new Vertex(destName);
			vertexMap.put(destName, destinationVertex);
		}
		sourceVertex.adj.add(new Edge(sourceVertex, destinationVertex, wt));
		destinationVertex.adj.add(new Edge(destinationVertex, sourceVertex, wt));
	}

	/**
	 * Adds a new weighted uni-directional edge to the graph from the command
	 * read from arguments/console.
	 */
	public void addedge(String tail, String head, Float time)
	{

		Vertex edgetail;
		Vertex edgehead;

		edgetail = vertexMap.get(tail);
		edgehead = vertexMap.get(head);

		if (edgetail == null) {
			// System.out.println("Provided tail is not present in graph and added now");
			edgetail = new Vertex(tail);
			vertexMap.put(tail, edgetail);
		}
		if (edgehead == null) {
			// System.out.println("Provided head is not present in graph and added now");
			edgehead = new Vertex(head);
			vertexMap.put(head, edgehead);
		}
		if (edgetail != null || edgehead != null) {
			edgetail.adj.remove(new Edge(edgetail, edgehead));
		}
		edgetail.adj.add(new Edge(edgetail, edgehead, time));
		// System.out.println("edge added/modified");
	}

	/** Deletes an edge from the graph **/
	public void deleteedge(String tail, String head)
	{
		Vertex edgetail;
		Vertex edgehead;

		edgetail = vertexMap.get(tail);
		edgehead = vertexMap.get(head);

		if (edgetail == null) {
			System.out.println("Provided tail is not present in graph");
		} else if (edgehead == null) {
			System.out.println("Provided head is not present in graph");
		} else {
			edgetail.adj.remove(new Edge(edgetail, edgehead));
			// System.out.println("edge from "+tail+" to  "+head+" is removed");
		}
	}

	// *****This method makes a vertex of the graph down/inactive
	public void vertexdown(String downvertex)
	{

		Vertex vertexdown = vertexMap.get(downvertex);

		if (vertexdown == null) {
			System.out.println("Provided vertex is not present in graph");
		} else {
			if (vertexdown.vstate == true) {
				System.out.println("Provided vertex already down");
			} else {
				vertexdown.vstate = true;
			}
		}
	}

	// ****This method makes an inactive/down edge active/up
	public void vertexup(String upvertex)
	{

		Vertex vertexup = vertexMap.get(upvertex);

		if (vertexup == null) {
			System.out.println("Provided vertex is not present in graph");
		} else {
			if (vertexup.vstate == false) {
				System.out.println("Provided vertex already active");
			} else {
				vertexup.vstate = false;
			}
		}
	}

	// ****This method makes an edge of the graph down/inactive
	public void edgedown(String tailvertex, String headvertex)
	{

		Vertex edgetail;
		Vertex edgehead;

		edgetail = vertexMap.get(tailvertex);
		edgehead = vertexMap.get(headvertex);

		if (edgetail == null) {
			System.out.println("Provided tail is not present in graph");
		} else if (edgehead == null) {
			System.out.println("Provided head is not present in graph");
		}

		else {
			Map<String, Vertex> map = new TreeMap<String, Vertex>(vertexMap);
			// An iterator class is used to run over all the vertices of the graph
			Iterator<Entry<String, Vertex>> iterate1 = map.entrySet()
					.iterator();
			while (iterate1.hasNext()) {
				Map.Entry<String, Vertex> pair = iterate1.next();
				Vertex tail = (pair.getValue());

				if (tail.name == edgetail.name) {

					for (Edge tempEdge : tail.adj) {

						if (tempEdge.getVertex2() == edgehead)
							// {
							if (tempEdge.estate == true) {
								System.out.println("The provided edge is already down");
							} else {
								tempEdge.estate = true;
							}
					}
				}
			}
		}
	}

	// *****This method makes an inactive/down edge of the graph active/up
	public void edgeup(String tailvertex, String headvertex)
	{

		Vertex edgetail;
		Vertex edgehead;

		edgetail = vertexMap.get(tailvertex);
		edgehead = vertexMap.get(headvertex);

		if (edgetail == null) {
			System.out.println("Provided tail is not present in graph");
		} else if (edgehead == null) {
			System.out.println("Provided head is not present in graph");
		} else {
			Map<String, Vertex> map = new TreeMap<String, Vertex>(vertexMap);
			Iterator<Entry<String, Vertex>> iterate1 = map.entrySet().iterator();
			while (iterate1.hasNext()) {
				Map.Entry<String, Vertex> pair = iterate1.next();
				Vertex tail = (pair.getValue());

				if (tail.name == edgetail.name) {

					for (Edge tempEdge : tail.adj) {

						if (tempEdge.getVertex2() == edgehead)

							if (tempEdge.estate == false) {
								System.out.println("The provided edge is already in  active state");
							} else {
								tempEdge.estate = false;
							}
					}
				}
			}
		}
	}

	/****
	 * Based on the state of the graph this method finds all the reachable
	 * vertices from all the vertices of the graph by using DFS by stack
	 * Initialization takes O(V) Running over all the edges of a vertex will
	 * take O(E) For Single vertex it will take O(V+E) Since this algorithm runs
	 * over all the vertices it will take O(V(V+E)) time
	 */

	ArrayList<String> verticesList = new ArrayList<String>();

	public void reachable_vertices()
	{
		// ****'dfs' stack maintains the list of vertices in the stack
		Stack<Vertex> dfs = new Stack<Vertex>();
		Map<String, Vertex> map = new TreeMap<String, Vertex>(vertexMap);
		Iterator<Entry<String, Vertex>> iterate1 = map.entrySet().iterator();
		// ***Iterates over all the vertices of the graph which takes time O(v)
		// where v-no. of vertices of graph
		while (iterate1.hasNext()) {
			// ***for loop initializes all the vertices which takes O(V) time
			// where v-no. of vertices
			for (Vertex w : vertexMap.values()) {
				w.setVcolor("white");
				w.setPrev(null);
			}

			Map.Entry<String, Vertex> pair = iterate1.next();
			Vertex temp = pair.getValue();

			// ***checks the state of vertices and edges whether it is up/down
			// and this will take O(V+E)
			if (temp.vstate == true) {
				for (Vertex v : vertexMap.values()) {
					for (Edge tempEdge : v.adj) {
						if ((tempEdge.getVertex2() == temp) || (tempEdge.getVertex1() == temp)) 
						{
							tempEdge.erstate = true;
						}
					}
				}
			}

			if ((temp.vcolor == "white") && (temp.vstate == false)) {
				// *** Sets a source vertex to gray, pushes onto the stack and calls DFS

				dfs.push(temp);
				verticesList.clear();
				temp.setVcolor("gray");
				DFS_Visit(temp, dfs, temp.vstate);
				System.out.println(temp.name);
				Collections.sort(verticesList);
				for (String tempVertex : verticesList) {
					System.out.println("     " + tempVertex);
				}

			}
			continue;
		}
	}

	// *** Implements DFS using a stack
	public void DFS_Visit(Vertex v, Stack<Vertex> dfs, boolean b)
	{

		Collections.sort(v.adj);
		// ***loops over the adjacent vertices of a source vertex which will take O(E)
		for (Edge tempEdge : v.adj) {
			// Collections.sort(v.adj);
			// ***Checks the edges of the graph if they are up/down
			if (tempEdge.erstate == false && tempEdge.estate == false) {

				if (tempEdge.getVertex2().vcolor == "white" && tempEdge.getVertex2().vstate == false) {
					dfs.push(tempEdge.getVertex2());
					tempEdge.getVertex2().setPrev(v);
					verticesList.add(tempEdge.getVertex2().name);
					tempEdge.getVertex2().setVcolor("gray");
					// ***Calls DFS recursively for all the adjacent vertices
					DFS_Visit((tempEdge.getVertex2()), dfs, b);
				}
				// ***Traverse in reverse direction to source vertex
				else if ((tempEdge.getVertex2().vcolor == "gray")
						&& (tempEdge.getVertex2() == v.getPrev())) {
					if (!dfs.isEmpty()) {
						dfs.pop();
						DFS_Visit((tempEdge.getVertex2()), dfs, b);
					}
				}
			}
		}
	}

	/**
	 * This method with print all the vertices and their corresponding edges
	 * along with their states
	 * 
	 */
	public void printGraph()
	{

		Map<String, Vertex> map = new TreeMap<String, Vertex>(vertexMap);
		Iterator<Entry<String, Vertex>> iterate1 = map.entrySet().iterator();
		while (iterate1.hasNext()) {
			Map.Entry<String, Vertex> pair = iterate1.next();

			Vertex vertexdown = vertexMap.get(pair.getKey());

			if (vertexdown.vstate == true)

			{
				System.out.println(pair.getKey() + " down");
			} else {
				System.out.println(pair.getKey());
			}
			;

			Vertex temp = pair.getValue();
			Collections.sort(temp.adj);
			for (Edge tempEdge : temp.adj) {
				if (tempEdge.estate == true) {
					System.out.println("         " + tempEdge.getVertex2().name
							+ " " + tempEdge.getEdgetime() + " down");
				} else {
					System.out.println("         " + tempEdge.getVertex2().name
							+ " " + tempEdge.getEdgetime());
				}
			}
		}
	}

	/**
	 * 
	 * It calls recursive routine to print shortest path to destNode after
	 * Dijikistra shortest path algorithm has run.
	 */
	public void printPath(String dest)
	{
		Vertex w = vertexMap.get(dest);
		if (w == null)
			System.out.println("Destination vertex not found");
		else if (w.dist == INFINITY)
			System.out.println(dest + " is unreachable");
		else {

			// String v = String.valueOf(w);
			printPath(w);
			System.out.println(" " + w.dist);
			// System.out.println( );
		}
	}

	//

	/**
	 * Recursive routine to print shortest path to dest after running shortest
	 * path algorithm. The path is known to exist.
	 */
	private void printPath(Vertex dest)
	{
		if (dest.prev != null) {
			printPath(dest.prev);
			System.out.print("  ");
		}
		System.out.print(dest.name);
	}

	/**
	 * Initializes the vertex output info prior to running any shortest path
	 * algorithm.
	 */
	private void clearAll()
	{
		for (Vertex v : vertexMap.values()) {
			v.reset();

		}
	}

	/**
	 * Single-source weighted shortest-path algorithm - Dijikistra algorithm
	 * This algorithm will calculate the smallest path distance between two
	 * vertices of the grpah
	 */
	public void weighted(String startName)
	{
		clearAll();

		// System.out.println("clear completd");
		Vertex startvertex = vertexMap.get(startName);
		// System.out.println(startvertex);
		if (startvertex == null) {
			System.out.println("Start vertex not found");
		}

		else if (startvertex.getVstate() == true) {
			System.out.println("This path cannot be there as " + startName
					+ " is down");
		} else {
			// ***A min_heap priority queue is maintains the vertices of the
			// graph based on their distances from
			// ***the source vertex
			min_heap p = new min_heap(Graph.vertexMap.size());

			// System.out.println("Queue generated");
			int index = p.items;
			p.insert(index, startvertex);
			startvertex.dist = 0;
			p.incrementArray();
			// System.out.println("enter into diji");
			while (p.items != 0) {
				Vertex v = p.pop();

				// System.out.println(v.name);
				for (Edge w : v.adj) {
					Float distance;
					if(w.estate==true ||w.getVertex2().getVstate() == true) {
						distance = (float) Graph.INFINITY;
					} else {
						distance = w.getEdgetime();
					}

					Vertex v1 = w.getVertex1();
					Vertex v2 = w.getVertex2();
					if (v2.dist != 0 && (v2.dist > (distance + v1.dist))) {
						v2.dist = v1.dist + distance;
						v2.prev = v1;
						p.insert(p.items, v2);
						p.incrementArray();
					}
				}
			}
		}
	}

	/**
	 * Process a request;calls various methods based on the input command
	 */
	public static void processRequest(Scanner in, Graph g)
	{
		try {
			System.out.println("Enter query to the graph with one of the below provided options  ");
			System.out.println("(path/addedge/deleteedge/edgedown/edgeup/vertexdown/vertexup/print/reachable) "
					+ "with valid arguments ");

			String command = "";
			String source = "";
			String dest = "";
			String time = "";
			Float tm = (float) 0;

			String change = in.nextLine();
			StringTokenizer st = new StringTokenizer(change);
			if (st.hasMoreTokens()) {
				command = st.nextToken();
			}
			if (st.hasMoreTokens()) {
				source = st.nextToken();
			}
			if (st.hasMoreTokens()) {
				dest = st.nextToken();
			}
			if (st.hasMoreTokens()) {
				time = st.nextToken();
				tm = Float.parseFloat(time);
			}

			String path       = "path";
			String addedge    = "addedge";
			String deledge    = "deleteedge";
			String edgedown   = "edgedown";
			String edgeup     = "edgeup";
			String vertexdown = "vertexdown";
			String vertexup   = "vertexup";
			String print      = "print";
			String reach      = "reachable";

			if (command.equals(path))
			{
				g.weighted(source);
				g.printPath(dest);
			} 
			else if (command.equals(addedge))
			{
				g.addedge(source, dest, tm);
			}
			else if (command.equals(deledge))
			{
				g.deleteedge(source, dest);
			} 
			else if (command.equals(vertexdown))
			{
				g.vertexdown(source);
			} 
			else if (command.equals(vertexup))
			{
				g.vertexup(source);
			} 
			else if (command.equals(edgedown)) 
			{
				g.edgedown(source, dest);
			} 
			else if (command.equals(edgeup)) 
			{
				g.edgeup(source, dest);
			}
			else if (command.equals(reach))
			{
				g.reachable_vertices();
			} 
			else if (command.equals(print)) 
			{
				g.printGraph();
			} else {
				processRequest(in, g);
			}
			processRequest(in, g);
		} catch (NoSuchElementException e) {
			System.err.println(e);
		}
		// { return false; }
		catch (GraphException e) {
			System.err.println(e);
		}
		// return true;
	}

	/**
	 * A main routine that: 1. Reads a file containing edges (supplied as a
	 * command-line parameter); 2. Forms the graph; 3. Repeatedly prompts for
	 * the proper commands which are to be given to run on the graph The data
	 * file is a sequence of lines of the format source destination weight
	 */
	public static void main(String[] args)
	{
		Graph g = new Graph();
		try {
			FileReader fin = new FileReader(args[0]);
			@SuppressWarnings("resource")
			Scanner graphFile = new Scanner(fin);

			// Read the edges and insert
			String line;
			while (graphFile.hasNextLine()) {
				line = graphFile.nextLine();
				StringTokenizer st = new StringTokenizer(line);

				try {
					if (st.countTokens() != 3) {
						System.err.println("Skipping ill-formatted line "
								+ line);
						continue;
					}
					String source = st.nextToken();
					String dest = st.nextToken();
					String weight = st.nextToken();
					Float wt = Float.parseFloat(weight);

					g.addEdge(source, dest, wt);

				} catch (NumberFormatException e) {
					System.err.println("Skipping ill-formatted line " + line);
				}
			}
		} catch (IOException e) {
			System.err.println(e);
		}

		// System.out.println( "File read..." );
		// System.out.println( Graph.vertexMap.size( ) + " vertices" );
		// g.printGraph();

		Scanner in = new Scanner(System.in);

		processRequest(in, g);
	}
}