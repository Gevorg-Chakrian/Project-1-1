/**
* Breadth-First Search 
*
* Processes with the main goal of searching through a graph and
* extract the non-connected subgraphs that that graph might have.
*/
package PatternRecognition;

import java.util.ArrayList;
import java.util.Arrays;

import Graph.Graph;

import static Constants.Constants.DEBUG;

public abstract class SubgraphExtraction
{
	/** 
     * This method explores the graph to extract all te non connected subgraphs
     * @param graph the global graph
     * @return an ArrayList of Graph containing all the subgraphs, so that none of them are connected to each other
     */
	public static ArrayList<Graph> findSubGraphs(Graph graph)
	{
		int nbV=graph.getNodes();

		//contains all subgraphs found as vertices IDs
		ArrayList<ArrayList<Integer>> subs = new ArrayList<ArrayList<Integer>>();

		//contains all IDs of the non tested vertices
		ArrayList<Integer> remainingGraph = new ArrayList<Integer>();
		for(int i=0;i<nbV;i++)
			remainingGraph.add(i);

		//is equal to true when all vertices have been tested
		boolean done=false;

		while(done!=true)
		{
			done=true;//will be set to false if there are still vertices to test

			//by ID, tell us if vertex i as already been tested
			boolean[] checked = new boolean[nbV];
			checked[remainingGraph.get(0)]=true;//there is at least 1 graph, and so at least the first vertex of the ones to test is part of a (sub)graph

			//List of nodes to explore
			ArrayList<Integer> queue=new ArrayList<Integer>();
			queue.add(remainingGraph.get(0));

			//explore all nodes of a (sub)tree
			while(queue.size()!=0)
			{
				int i=queue.get(0);
				queue.remove(0);
				int[] connected = graph.getConnected(i);
				for(int j=0;j<connected.length;j++)
				{
					if(!checked[connected[j]-1])
					{
						queue.add(connected[j]-1);
						checked[connected[j]-1]=true;
					}
				}
			}

			//create the list of vertices of the subgraph found
			ArrayList<Integer> subgraph = new ArrayList<Integer>();
			for(int j=remainingGraph.size()-1;j>=0;j--)
			{
				int l=remainingGraph.get(j);
				if(!checked[l])
				{
					done=false;
				}
				else
				{
					subgraph.add(l);
					remainingGraph.remove(j);
				}
			}
			if(DEBUG)System.out.println("subgraph "+subgraph);
			subs.add(subgraph);
		}

		//convert the list of IDs in Graph object by extracting and normalizing the lists of connected vertices
		ArrayList<Graph> subGraphs = new ArrayList<Graph>(subs.size());
		for(int i=0;i<subs.size();i++)
		{
			//create adjacency matrix of the subgraph and normalize the vertices' ids (from 1 to number of vertices)
			//create a conversion table
			if(DEBUG)System.out.println("sub "+subs.get(i));
			int size=subs.get(i).size();
			if(DEBUG)System.out.println("size "+size);
			int[] corresp=new int[nbV];
			if(DEBUG)System.out.println("vertices= "+nbV);
			for(int j=0;j<size;j++)
			{
				if(DEBUG)System.out.println("point "+subs.get(i).get(j));
				corresp[subs.get(i).get(j)]=j+1;
			}
			if(DEBUG)System.out.println("corresp "+Arrays.toString(corresp));

			//convert the values using the table
			int[][] matrix=new int[size][size];
			for(int j=0;j<size;j++)
			{
				if(DEBUG)System.out.println("j="+j);
				int l=subs.get(i).get(j);
				if(DEBUG)System.out.println("l "+l);
				int[] connected=graph.getConnected(l);
				int[] connectedNormalized=new int[connected.length];
				if(DEBUG)System.out.println("connected "+Arrays.toString(connected));
				for(int k=0;k<connected.length;k++)
				{
					connectedNormalized[k]=corresp[connected[k]-1];
					if(DEBUG)System.out.println("connected k "+connected[k]+" cooresp "+connectedNormalized[k]);
				}
				//create the adjacency matrix
				for(int k=0;k<connectedNormalized.length;k++)
				{
					if(DEBUG)System.out.println("connectedNormalized[k]-1="+(connectedNormalized[k]-1));
					matrix[j][connectedNormalized[k]-1]=1;
				}
			}
			
			//Create the graph object from the adjacency matrix
			subGraphs.add(new Graph(matrix));
		}

		return subGraphs;
	}

	/** 
     * This method assembles disjointed graphs in one single graph object
     * @param subgraphs the subgraphs
     * @return a Graph containing all the subgraphs
     */
	public static Graph reassembleSubgraphs(ArrayList<Graph> subgraphs)
	{
		int totalNodes=0;
		for(int i=0;i<subgraphs.size();i++)
		{
			totalNodes+=subgraphs.get(i).getNodes();
		}

		//normalizes the IDs of the vertices from 1 to totalNodes and adds then to the adjacency matrix
		int[][] matrix=new int[totalNodes][totalNodes];
		int currentMinID=0;
		for(int i=0;i<subgraphs.size();i++)
		{
			for(int j=0;j<subgraphs.get(i).getNodes();j++)
			{
				int[] connected=subgraphs.get(i).getConnected(j);
				for(int k=0;k<connected.length;k++)
				{
					matrix[j+currentMinID][connected[k]+currentMinID-1]=1;
				}
			}
			currentMinID+=subgraphs.get(i).getNodes();//check if +1 is needed!!!
		}
		return new Graph(matrix);
	}
}