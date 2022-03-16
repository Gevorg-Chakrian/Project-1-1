package LowerBound;

import Graph.Graph;

import java.util.ArrayList;
import java.util.Arrays;

import static Constants.Constants.DEBUG_GRAPH;

public abstract class LowerBound
{
	/** 
     * Getter of the maximum clique size
     * @param graph graph to extract the maximum clique size from
     * @return the size of the maximmum clique
     */
	public static int getSolution(Graph graph)
	{
		//calculates the degree of each vertex
    	int highest = 0;
        for (int i = 0; i < graph.getNodes(); i++) {
            highest=Math.max(highest,graph.getDegree(i));
        }

    	//sorts the degree array
        ArrayList<Integer> order = new ArrayList<Integer>();
        for(int j=0;j<=highest;j++)
        {
            for (int i = 0; i < graph.getNodes(); i++) {
                if (graph.getDegree(i)==j) {
                    order.add(i);
                }
            }
        }
        int max=clique(graph,order,0,0);
        return max;
	}

	/** 
     * recursive exploration of the graph to find the maximum clique size
     * @param graph graph to extract the maximum clique size from
     * @param u vertices to explore
     * @param size size of the actual considered clique
     * @param max best solution encountered so far
     */
	private static int clique(Graph graph, ArrayList<Integer> u, int size, int max)
	{
		if(u.size()==0)
		{
			if(size>max)
			{
				max=size;
			}
			return max;
		}
		while(u.size()!=0)
		{
			if(size+u.size()<=max)
				return max;
			int v=u.get(0);
			ArrayList<Integer> uCopy=new ArrayList<Integer>();
			for(int i=0;i<u.size();i++)
			{
				uCopy.add(u.get(i));
			}
			u.remove(0);
			//create intersection with u
			int[] connected=graph.getConnected(v);
				for (int i = 0; i < uCopy.size(); i++) {
					boolean isIn = false;
					if(connected != null) {
						for (int j = 0; j < connected.length; j++) {
							if (connected[j] - 1 == uCopy.get(i)) {
								isIn = true;
								break;
							}
						}
					}
					if (!isIn) {
						uCopy.remove(i);
						i--;
					}
				}
			graph.setMaxClique(u);
			max=clique(graph,uCopy,size+1,max);
		}
		return max;
	}
}
