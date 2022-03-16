/*Algorithm: Genetic local search algorithm for coloring

Data: G, a graph
Result: the number of conlicts with k fixed colors
 % f,f : fitness function and its best value encountered so far
 % s : best individual encountered so far
 % i, STOP : the current and maximum number of iterations allowed
 % best(P) : returns the best individual of the population P

begin
	iter=0
	generate(P0)
	sol=best(P0)
	f*=f(sol)

	while ( f*>0 and iter<STOP) do
		Pi=crossing(Pi,Tx);x //using UIS crossover
		Pi+1=mutation(Pi), //using tabu search
		if (f(best(Pi+1))<f*) then
			sol=best(Pi+1)
			f*=f(sol)
		i=i+1
	return f*
end

NB: this pseudocode is an indication, not an exact description of the implementation
*/
package UpperBound.GA;

import java.util.Arrays;
import java.lang.Math;
import java.util.ArrayList;

import Graph.Graph;
import UpperBound.TabucolForGA;

import static Constants.Constants.DEBUG;
import static Constants.Constants.TIMELIMITGA;
import static Constants.Constants.POPSIZE;
import static Constants.Constants.CROSSRATIO;
import static Constants.Constants.MAXITER;
import static Constants.Constants.ALTERNATIVECROSSOVER;

public abstract class GenAl
{
	private static Graph graph;

	/**
     * This method operates a genetic search of the graph to find a valid coloring
     * @param gra the graph to colorize
     * @param upperBound the number of colors we attempt to colorize the graph with.
     * @param start the start time of the upperbound calculation process
     * @param time the time limit
     * @return the best colouring found so far
     */
	public static int[] getSolution(Graph gra, int upperBound,  long start, long time)
	{
		graph=gra;
		TabucolForGA.setGraph(graph);
		Individual[] population=new Individual[POPSIZE];

		//generate(P0) with random colors
		int length=graph.getNodes();
		for (int i=0;i<POPSIZE;i++)
		{
			int[] chromosome=new int[length];
			for (int j=0;j<length;j++)
			{
				chromosome[j]=(int)(Math.random()*upperBound);//choose a random int in the range [0, upperbound[
			}
			population[i]=new Individual(chromosome);
		}

		//sol=best(P0)
		getFitness(population);
		if(DEBUG) for(int i=0;i<population.length;i++)
		{
			System.out.println(population[i].getFitness());
		}
		HeapSort.sort(population);

		Individual sol=population[0];

		//f*=f(sol)
		int f=sol.getFitness();

		//while ( f*>0 and iter<STOP) do
		while(f>0)
		{
			if((System.nanoTime()-start)>TIMELIMITGA*time)
				return null;
			//Pi=crossing(Pi,Tx);x //using UIS crossover
			//Pi+1=mutation(Pi), //using tabu search
			getNextGeneration(population,upperBound);
			
			/*if (f(best(Pi+1))<f*) then
				sol=best(Pi+1)
				f*=f(sol)
			*/
			if(population[0].getFitness()<f)
			{
				sol=population[0];
				f=population[0].getFitness();
			}
		}

		if(DEBUG)System.out.println("f="+f);

		if(f==0)
		{
			return sol.getChromosome();
		}
		return null;
	}

	/**
	 * Creates a new generation:
	 * Pi=crossing(Pi,Tx);x //using UIS crossover
	 * Pi+1=mutation(Pi), //using tabu search
	 * @param population all the population to create a new generation from
	 * @param upperBound the number of colors we attempt to colorize the graph with.
	 */
	private static void getNextGeneration(Individual[] population, int upperBound)
	{
		/*
		Crosses two individuals from time to time and then it mutates the individuals of the population.
		More precisely, at each generation, the UIS crossover is applied with probability T to each possible pair (Ind1, Ind2)
		(determined randomly) of individuals of the population: p/2 pairs for a population of p individuals.
		If a crossover takes place on (Ind1 , Ind2), the offsprings e1 & e2 replace Ind1 and Ind2 regardless of their fitness.
		Otherwise, Ind1 and Ind2 remain unchanged.
		*/
		ArrayList<Integer> popID=new ArrayList<Integer>();//used to avoid double selection
		for(int i=0;i<population.length;i++)
		{
			popID.add(i);
		}

		while(popID.size()>0)
		{
			int id=(int)(Math.random()*popID.size());
			int id1=popID.get(id);
			Individual ind1=population[id1];
			popID.remove(id);

			id=(int)(Math.random()*popID.size());
			int id2=popID.get(id);
			Individual ind2=population[id2];
			popID.remove(id);

			if(Math.random()<CROSSRATIO)
			{
				//Crossover
				int[] chromInd1=ind1.getChromosome();
				int[] chromInd2=ind2.getChromosome();
				crossover(chromInd1,chromInd2,upperBound);
				ind1.setChromosome(chromInd1);
				ind2.setChromosome(chromInd2);
				population[id1]=ind1;
				population[id2]=ind2;
			}
		}

		//Mutation by tabu search
		mutate(population, upperBound);
		getFitness(population);
		HeapSort.sort(population);
	}

	/**
	 * Crossover of two individuals, the childs replace the parents
	 * @param chromInd1 first parent
	 * @param chromInd2 second parent
	 * @param k the number of colors we attempt to colorize the graph with.
	 */
	private static void crossover(int[] chromInd1, int[] chromInd2, int k)
	{
		//UIS crossover
		ArrayList<ArrayList<Integer>> colorClass1=new ArrayList<ArrayList<Integer>>(k);
		//intitializing
		for(int i=0;i<k;i++)
		{
			colorClass1.add(new ArrayList<Integer>());
		}

		for(int i=0;i<chromInd1.length;i++)
		{
			if(graph.isColValid(chromInd1[i],i))
				colorClass1.get(chromInd1[i]).add(i);
		}

		ArrayList<ArrayList<Integer>> colorClass2=new ArrayList<ArrayList<Integer>>(k);
		//intitializing
		for(int i=0;i<k;i++)
		{
			colorClass2.add(new ArrayList<Integer>());
		}

		for(int i=0;i<chromInd2.length;i++)
		{
			if(graph.isColValid(chromInd2[i],i))
				colorClass2.get(chromInd2[i]).add(i);
		}

		if(ALTERNATIVECROSSOVER)
		{
			//child1
			boolean[] replaced=new boolean[chromInd1.length];
			for(int i=0;i<k;i++)
			{
				for(int j=0;j<colorClass1.get(i).size();j++)
				{
					int id=colorClass1.get(i).get(j);
					if(!replaced[id])
					{
						replaced[id]=true;
					}
				}

				int col=i-1;//wrong!
				if(col==-1)
					col=k-1;
				for(int j=0;j<colorClass2.get(col).size();j++)
				{
					int id=colorClass2.get(col).get(j);
					if(!replaced[id])
					{
						chromInd1[id]=i;
						replaced[id]=true;
					}
				}
			}

			//child2
			replaced=new boolean[chromInd2.length];
			for(int i=0;i<k;i++)
			{
				for(int j=0;j<colorClass2.get(i).size();j++)
				{
					int id=colorClass2.get(i).get(j);
					if(!replaced[id])
					{
						replaced[id]=true;
					}
				}

				int col=i+1;
				if(col==k)
					col=0;
				for(int j=0;j<colorClass1.get(col).size();j++)
				{
					int id=colorClass1.get(col).get(j);
					if(!replaced[id])
					{
						chromInd2[id]=i;
						replaced[id]=true;
					}
				}
			}
		}

		else
		{
			//child1
			boolean[] replaced=new boolean[chromInd1.length];
			for(int i=0;i<k;i++)
			{
				boolean[] elem=new boolean[chromInd1.length];
				for(int j=0;j<colorClass1.get(i).size();j++)
				{
					int id=colorClass1.get(i).get(j);
					if(!replaced[id])
					{
						replaced[id]=true;
					}
					elem[id]=true;
				}

				int commonElem=0;
				int prev=0;
				int best=0;
				for(int j=0;j<k;j++)
				{
					for(int l=0;l<colorClass2.get(j).size();l++)
					{
						if(elem[colorClass2.get(j).get(l)]==true)
							commonElem++;
					}
					if(commonElem>prev || (commonElem==prev && Math.random()<0.5))
					{
						prev=commonElem;
						best=j;
					}
					commonElem=0;
				}

				for(int j=0;j<colorClass2.get(best).size();j++)
				{
					int id=colorClass2.get(best).get(j);
					if(!replaced[id])
					{
						chromInd1[id]=i;
						replaced[id]=true;
					}
				}
			}

			//child2
			replaced=new boolean[chromInd2.length];
			for(int i=0;i<k;i++)
			{
				boolean[] elem=new boolean[chromInd2.length];
				for(int j=0;j<colorClass2.get(i).size();j++)
				{
					int id=colorClass2.get(i).get(j);
					if(!replaced[id])
					{
						replaced[id]=true;
					}
					elem[id]=true;
				}

				int commonElem=0;
				int prev=0;
				int best=0;
				for(int j=0;j<k;j++)
				{
					for(int l=0;l<colorClass1.get(j).size();l++)
					{
						if(elem[colorClass1.get(j).get(l)]==true)
							commonElem++;
					}
					if(commonElem>prev || (commonElem==prev && Math.random()<0.5))
					{
						prev=commonElem;
						best=j;
					}
					commonElem=0;
				}

				for(int j=0;j<colorClass1.get(best).size();j++)
				{
					int id=colorClass1.get(best).get(j);
					if(!replaced[id])
					{
						chromInd2[id]=i;
						replaced[id]=true;
					}
				}
			}
		}
	}

	/**
	 * Mutation of the population
	 * @param population all the population to mutate
	 * @param upperBound the number of colors we attempt to colorize the graph with.
	 */
	private static void mutate(Individual[] population,int upperBound)
	{
		int maxiter=MAXITER;
		for(int i=0;i<population.length;i++)
		{
			population[i].setChromosome(TabucolForGA.getSolution(upperBound, population[i].getChromosome(), maxiter));
		}
	}

	/**
	 * Calculates the fitness of all individuals of the population
	 * @param population all the individuals
	 */
	private static void getFitness(Individual[] population)
	{
		for(int i=0;i<population.length;i++)
		{
			population[i].setFitness(TabucolForGA.costFunction(population[i].getChromosome()));
		}
	}
}