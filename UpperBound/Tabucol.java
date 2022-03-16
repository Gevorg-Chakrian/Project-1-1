/*
Algorithm Tabucol.
Input: A graph G= (V, E), an integer k> 0.
Parameters: MAXITER, L and LAMBDA.
Output: Solution c∗.

Build a random solution c;
Set the tabu list to the empty set; /∗ No move is tabu ∗/
Set c∗ := c and iter = 0;

Repeat until f(c) = 0 or iter = MAXITER /∗ stopping criterion ∗/
	Set iter := iter + 1;
	Choose a candidate 1-move (v, i) with minimum value delta(v, i);
	Introduce move (v, c(v)) into the tabu list for L + LAMBDAF(c) iterations;
	Set c := c + (v, i);if f(c)<f(c∗) then set c∗ := c; 

*/

package UpperBound;

import java.util.ArrayList;
import java.lang.Math;

import Graph.Graph;
import Graph.Edge;

import static Constants.Constants.DEBUG;
import static Constants.Constants.LAMBDA;
import static Constants.Constants.TIMELIMITUB;

public abstract class Tabucol
{
	private static int L=1;
	private static Graph graph;

	/**
     * This method operates a Tabucol search on the graph to improve a coloring
     * @param gra the graph to colorize
     * @param k the number of colors we attempt to colorize the graph with.
     * @param start the start time of the upperbound calculation process
     * @param time the time limit
     * @return the best colouring found so far
     */
	public static int[] getSolution(Graph gra, int k, long start, long time)
	{
		graph=gra;
		int length=graph.getNodes();
		int[] c=new int[length];
		int[] cBest=new int[length];
		int f;
		ArrayList<Move> tabu=new ArrayList<Move>();
		ArrayList<Integer> conflictingVertices=new ArrayList<Integer>();

		L=(int)Math.random()*10;

		int[][] gammaMatrix=new int[length][k];

		for(int i=0;i<c.length;i++)
		{
			c[i]=(int)(Math.random()*k);//assign a random colour to all vertices, among k colours, from 0 to k-1
			cBest[i]=c[i];
		}

		for(int v=0;v<length;v++)
		{
			graph.setColorArray(c);
			for(int i=0;i<k;i++)
			{
				gammaMatrix[v][i]=getGamma(v,i);
			}
		}

		while((f=costFunction(c,conflictingVertices))!=0)
		{
			if((System.nanoTime()-start)>(TIMELIMITUB*time))
				return null;
			//remove nonTabu moves and change iteration number
			for(int i=0;i<tabu.size();i++)
			{
				int it=tabu.get(i).getIteration();
				if(it==0)
					tabu.remove(i);
				else
					tabu.get(i).setIteration(it--);
			}
			//Choose a candidate 1-move (v, i) with minimum value delta(v, i);
			Move bestMove=calculateDeltas(f,c,conflictingVertices,k,tabu,gammaMatrix);
			
			if(bestMove!=null)
			{
				//Introduce move (v, c(v)) into the tabu list for L + LAMBDAF(c) iterations;
				tabu.add(bestMove);
				
				//Set c := c + (v, i);if f(c)<f(c∗) then set c∗ := c;
				int oldCol=c[bestMove.getVertex()];
				c[bestMove.getVertex()]=bestMove.getColor();
				if(costFunction(c)<costFunction(cBest))
				{
					cBest[bestMove.getVertex()]=bestMove.getColor();
				}
				updateGammaMatrix(bestMove,oldCol,gammaMatrix);
			}
		}

		return cBest;
	}

	/**
     * This method returns the number of conflicting edges of a colouring
     * @param c the colouring to test
     * @param conflictingVertices arrayList of vertices in conflict that will be updated
     * @return number of conflicting edges
     */
	private static int costFunction(int[] c, ArrayList<Integer> conflictingVertices)
	{
		graph.setColorArray(c);
		ArrayList<Edge> conflictingEdges=new ArrayList<Edge>();
		for(int i=conflictingVertices.size()-1;i>=0;i--)
			conflictingVertices.remove(i);
		graph.checkColoring(conflictingVertices, conflictingEdges);

		return conflictingEdges.size();
	}

	/**
     * This method returns the number of conflicting edges of a colouring
     * @param c the colouring to test
     * @return number of conflicting edges
     */
	private static int costFunction(int[] c)
	{
		graph.setColorArray(c);
		ArrayList<Edge> conflictingEdges=new ArrayList<Edge>();
		graph.checkColoring(new ArrayList<Integer>(), conflictingEdges);

		return conflictingEdges.size();
	}

	/**
     * This method returns the best 1-move to do to solve the colouring
     * @param f the current cost function of c
     * @param c the colouring to solve
     * @param conflictingVertices arrayList of vertices in conflict
     * @param k number of different colours
     * @param tabu ArrayList of 1-moves that are currently tabu
     * @param gammaMatrix matrix of adjacent vertices by colour
     * @return 1-move that reduces the most f
     */
	private static Move calculateDeltas(int f, int[] c, ArrayList<Integer> conflictingVertices, int k, ArrayList<Move> tabu, int[][] gammaMatrix)
	{
		Move bestMove=null;
		boolean first=true;
		int bestDelta=0;

		for(int i=0;i<conflictingVertices.size();i++)
		{
			int id=conflictingVertices.get(i);
			//try every conflicting vertex with every different color
			for(int j=0;j<k;j++)
			{
				if(c[id]!=j)
				{
					boolean checkTabu=notTabu(tabu,new Move(id,j));
					if(checkTabu)
					{
						//calculate delta
						int delta=gammaMatrix[id][j]-gammaMatrix[id][c[id]];
	
						if(first || delta<bestDelta || (delta==bestDelta && Math.random()<1))
						{
							bestDelta=delta;
							bestMove=new Move(id,j);
							first=false;
						}
					}
					else//test if one of the tabu members solves the problem
					{
						//calculate delta
						int delta=gammaMatrix[id][j]-gammaMatrix[id][c[id]];
						if(delta==-f)
						{
							bestDelta=delta;
							bestMove=new Move(id,j);
							return bestMove;
						}
					}
				}
			}
		}
		if(bestMove!=null)
			bestMove.setIteration((int)(L+LAMBDA*conflictingVertices.size()));
		return bestMove;
	}

	/**
     * This method checks if a move is in the tabu list
     * @param tabu the tabu list
     * @param toTest the move to test
     * @return true if the move is not tabu
     */
	private static boolean notTabu(ArrayList<Move> tabu, Move toTest)
	{
		for(int i=0;i<tabu.size();i++)
		{
			if(tabu.get(i).equals(toTest))
			{
				return false;
			}
		}
		return true;
	}

	/**
     * This method returns the gamma value of a vertex for one color
     * @param v the ID of the vertex
     * @param col the color to get
     * @return the number of adjacent vertices of color col
     */
	private static int getGamma(int v, int col)
	{
		return graph.getNumberAdjVertCol(v,col);
	}

	/**
     * This method updates the gamma Matrix to adjust to a specific move
     * @param bestMove the move to update from
     * @param oldCol the previous colour
     * @param gammaMatrix matrix of adjacent vertices by colour
     */
	private static void updateGammaMatrix(Move bestMove, int oldCol, int[][] gammaMatrix)
	{
		int v=bestMove.getVertex();
		int col=bestMove.getColor();
		
		for(int i=0;i<gammaMatrix.length;i++)
		{
			if(i!=v && graph.isConnected(v,i))
			{
				gammaMatrix[i][oldCol]--;
				gammaMatrix[i][col]++;
			}
		}
	}
}