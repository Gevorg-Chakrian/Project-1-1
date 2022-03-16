import ExactCalculation.Backtracking;
import Graph.Graph;
import LowerBound.LowerBound;
import PatternRecognition.SubgraphExtraction;
import PatternRecognition.SubgraphTypes;
import UpperBound.GA.GenAl;
import UpperBound.Tabucol;
import UpperBound.WelshPowell;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

import static Constants.Constants.*;

public class Main
{
	private static final int GETUB=0;
	private static final int GETLB=1;
	private static final int GETCN=2;
	
	/** 
     * Main method, organizes the process of searching of the chromatic number
     * @param args the path to the graph file
     */
	public static void main(String[] args)
	{
		long start=System.nanoTime();

		Scanner in = new Scanner(System.in);
		Graph graph=null;
		
		if(args.length==1)
		{
			graph = new Graph(args[0]);
		}
		else
		{
			System.out.println("Graph file is missing");
			return;
		}
		if(DEBUG)System.out.println("Graph created");

		//////////////////////START OF THE COMPUTING//////////////////////

		System.out.println("NEW BEST LOWER BOUND = 1");
		System.out.println("NEW BEST UPPER BOUND = "+graph.getNodes());

		//extract subgraphs
		ArrayList<Graph> subgraphs=SubgraphExtraction.findSubGraphs(graph);
		ArrayList<ArrayList<Integer>> bounds=new ArrayList<ArrayList<Integer>>();
		//intialize boundaries
		for(int i=0;i<subgraphs.size();i++)
		{
			bounds.add(new ArrayList<Integer>(2));
			bounds.get(i).add(subgraphs.get(i).getNodes());//set upperbound to nb of vertices
			bounds.get(i).add(1);//set lowerbound to 1
			bounds.get(i).add(-1);//set chromatic number to -1
		}

		//find global maximum
		int maxUb=filterSubgraphsGetUB(subgraphs,bounds);

		//recognize known patterns
		if(DEBUG)System.out.println("Recognize pattern for "+subgraphs.size()+" subgraphs:");
		int maxCN=-1;
		for(int i=0;i<subgraphs.size();i++)
		{
			//RECOGNIZE PATTERNS OF subgraphs.get(i)
			SubgraphTypes gr = new SubgraphTypes(subgraphs.get(i));
			int cnCandidate=gr.chromaticNumberKnownGraph();
			
			if(DEBUG)
				System.out.println("Chromatic number candidate: " +cnCandidate);
			//if pattern recognize store it and compare to the max candidate
			if(cnCandidate!=-1)
			{
				maxCN=Math.max(cnCandidate,maxCN);
				bounds.get(i).set(GETCN,cnCandidate);
			}
		}
		if(DEBUG)System.out.println("END");

		//find global minimum
		int maxLb=filterSubgraphsGetLB(subgraphs,bounds);

		if(maxLb==maxUb)
		{
			System.out.println("CHROMATIC NUMBER = " + maxLb);
			return;
		}

		//remove all subgraphs that are below the max lower bound possible
		for(int i=0;i<subgraphs.size();i++)
		{
			if(bounds.get(i).get(GETCN)!=-1)
			{
				if(bounds.get(i).get(GETCN)<maxLb)//ie just a local chromatic number
					bounds.get(i).set(GETUB,0);//provoke deleting of the subgraph as 0<maxLB
			}
		}

		//if upperbound of a subgraph is below the global lowerbound, delete subgraph
		deleteObsoleteGraphs(subgraphs,bounds,maxLb);
		
		//if chromatic number found for some subgraphs: check if it's >= upperbound (or chromatic number) of all the others, then it's a solution
		for(int i=0;i<bounds.size();i++)
		{
			int cnCandidate=bounds.get(i).get(GETCN);
			if(cnCandidate!=-1)
			{
				if(cnCandidate<maxCN)//if a candidate CN bigger has been found we can discard the others
				{
					bounds.get(i).set(GETUB,0);//provoke deleting of the subgraph as 0<maxLB
					continue;
				}
			}
			else if(bounds.get(i).get(GETUB)<maxCN)
			{
				bounds.get(i).set(GETUB,0);//provoke deleting of the subgraph as 0<maxLB
			}
		}

		deleteObsoleteGraphs(subgraphs,bounds,maxLb);

		if(subgraphs.size()==1)
		{
			if(bounds.get(0).get(GETCN)!=-1)
			{
				System.out.println("CHROMATIC NUMBER = " + bounds.get(0).get(GETCN));
				return;
			}
		}
		else
		{
			int cand=bounds.get(0).get(GETCN);
			if(cand!=-1)
			{
				boolean allEqual=true;
				for(int i=0;i<bounds.size();i++)
				{
					if(cand!=bounds.get(i).get(GETCN))
						allEqual=false;
				}
				if(allEqual)
				{
					System.out.println("CHROMATIC NUMBER = " + cand);
					return;
				}
			}
		}
		
		//calculate how much time we decide to spend on every subgraph for the lowering of upper bound and exact calculation:
		//(here by percentage of nodes compared to total nodes left)
		int totalNodes=0;
		for(int i=0;i<subgraphs.size();i++)
		{
			totalNodes+=subgraphs.get(i).getNodes();
		}

		long[] timeByGraph=new long[subgraphs.size()];
		
		long timeLeft=(long)(TOTALTIME*1000000000.0)-(System.nanoTime()-start);
		for(int i=0;i<subgraphs.size();i++)
		{
			timeByGraph[i]=timeLeft*subgraphs.get(i).getNodes()/totalNodes;
		}

		//try to lower upperbound for a certain amount of time
		maxUb=calculateUpperBound(subgraphs,bounds,maxLb,maxUb,start,timeByGraph);

		if(maxLb==maxUb)
		{
			System.out.println("CHROMATIC NUMBER = " + maxLb);
			return;
		}

		//try an exact calculation
		if(DEBUG)System.out.println("size: "+subgraphs.size());
		
		graph=SubgraphExtraction.reassembleSubgraphs(subgraphs);

		if(DEBUG)for(int i=0;i<graph.getNodes();i++)
			System.out.println(Arrays.toString(graph.getConnected(i)));

		if(DEBUG)System.out.println("Backtracking...");

		int timelimit = (int)(((long)(TOTALTIME*1000000000.0)-(System.nanoTime()-start))/1000000000.0);
		Backtracking  backtracking = new Backtracking(graph, maxLb, maxUb, timelimit);
		int cn=backtracking.getChromaticNumber();
		if(cn!=0)
			System.out.println("CHROMATIC NUMBER = " + cn);
	}

	/** 
     * Methods that calculates and compare the upper bounds of all subgraphs
     * @param subgraphs arraylist of all the graphs
     * @param bounds arraylist containing the temporary results of upper bound; lower bound and chromatic number
     * @param maxLb highest lower bound among all graphs
     * @param maxUb highest upperbound among all graphs
     * @param start starting time
     * @param timeByGraph time given to each graph
     * @return the new upperbound
     */
	private static int calculateUpperBound(ArrayList<Graph> subgraphs, ArrayList<ArrayList<Integer>> bounds, int maxLb, int maxUb, long start,  long[] timeByGraph)
	{
		for(int j=0;j<subgraphs.size();j++)
		{
			if(DEBUG)System.out.println("Calculation of upperbound...");
			for(int i=bounds.get(j).get(GETUB)-1;i>=2;i--)
			{
				if(i<maxLb-1)
				{
					bounds.get(j).set(GETUB,0);//to provoke the deleting of the subgraph later, as the local max<global min anyway
					break;
				}
				if(i<bounds.get(j).get(GETLB))
				{
					break;
				}
				if(DEBUG)System.out.println("Testing of "+i+":");
				int[] sol;
				if(USEGA)
					sol=GenAl.getSolution(subgraphs.get(j),i,start,timeByGraph[j]);
				else
					sol=Tabucol.getSolution(subgraphs.get(j),i,start,timeByGraph[j]);
				if(sol!=null)
				{
					bounds.get(j).set(GETUB,i);
					if(subgraphs.size()==1)
					{
						maxUb=bounds.get(j).get(GETUB);
						System.out.println("NEW BEST UPPER BOUND = "+maxUb);
					}
				}
				else break;
			}
			if(DEBUG)System.out.println("No lower upperBound found");							
		}
		if(subgraphs.size()!=1)
		{
			deleteObsoleteGraphs(subgraphs,bounds,maxLb);
			maxUb=findMaxUB(subgraphs,bounds);
		}

		return maxUb;
	}

	/** 
     * Methods that calculates and compare the initial upper bounds of all subgraphs
     * @param subgraphs arraylist of all the graphs
     * @param bounds arraylist containing the temporary results of upper bound; lower bound and chromatic number
     * @return the new upperbound
     */
	private static int filterSubgraphsGetUB(ArrayList<Graph> subgraphs, ArrayList<ArrayList<Integer>> bounds)
	{
		for(int i=0;i<subgraphs.size();i++)
		{
			if(DEBUG)System.out.println("Calculation of upperbound...");
			bounds.get(i).set(GETUB,WelshPowell.getSolution(subgraphs.get(i)));
		}

		int maxUb=findMaxUB(subgraphs,bounds);
		return maxUb;
	}

	/** 
     * Methods that compares the upper bounds of all subgraphs
     * @param subgraphs arraylist of all the graphs
     * @param bounds arraylist containing the temporary results of upper bound; lower bound and chromatic number
     * @return the biggest upperbound
     */
	private static int findMaxUB(ArrayList<Graph> subgraphs, ArrayList<ArrayList<Integer>> bounds)
	{
		int maxUb=0;
		for(int i=0;i<bounds.size();i++)
		{
			if(maxUb<bounds.get(i).get(GETUB))
			{
				maxUb=bounds.get(i).get(GETUB);
			}
		}

		System.out.println("NEW BEST UPPER BOUND = "+maxUb);
		return maxUb;
	}

	/** 
     * Methods that calculates and compare the initial lower bounds of all subgraphs
     * @param subgraphs arraylist of all the graphs
     * @param bounds arraylist containing the temporary results of upper bound; lower bound and chromatic number
     * @return the new lower bound
     */
	private static int filterSubgraphsGetLB(ArrayList<Graph> subgraphs, ArrayList<ArrayList<Integer>> bounds)
	{
		for(int i=0;i<subgraphs.size();i++)
		{
			if(DEBUG)System.out.println("Calculation of lowerbound...");
			bounds.get(i).set(GETLB,LowerBound.getSolution(subgraphs.get(i)));
		}

		int maxLb=0;
		for(int i=0;i<bounds.size();i++)
		{
			if(maxLb<bounds.get(i).get(GETLB))
			{
				maxLb=bounds.get(i).get(GETLB);
			}
		}

		System.out.println("NEW BEST LOWER BOUND = "+maxLb);
		return maxLb;
	}

	/**
     * Methods that deletes all subgraphs with a lower upperbound than the maximum lower bound
     * @param subgraphs arraylist of all the graphs
     * @param bounds arraylist containing the temporary results of upper bound; lower bound and chromatic number
     * @param maxLb the actual biggest lower bound
     */
	private static void deleteObsoleteGraphs(ArrayList<Graph> subgraphs, ArrayList<ArrayList<Integer>> bounds, int maxLb)
	{
		//if upperbound of a subgraph is below the global lowerbound, delete subgraph
		boolean[] delete=new boolean[subgraphs.size()];
		for(int i=0;i<bounds.size();i++)
		{
			if(bounds.get(i).get(GETCN)!=-1 && bounds.get(i).get(GETCN)<maxLb)
				delete[i]=true;
			else if(bounds.get(i).get(GETUB)<maxLb)
				delete[i]=true;
		}

		for(int i=delete.length-1;i>=0;i--)
		{
			if(delete[i])
			{
				subgraphs.remove(i);
				bounds.remove(i);
				if(DEBUG)System.out.println(i+" deleted");
			}	
		}
	}
}