package UpperBound;

import java.lang.Math;
import java.util.Arrays;

import Graph.Graph;

public abstract class WelshPowell {

    /**
     * This method returns the upperbound given by a welsh powell algorithm
     * @param graph the Graph object containing the graph
     * @return the number of color used
     */
    public static int getSolution(Graph graph) {

    	int upperBoundNum = 0;

        boolean colorized = false;

        //sort by highest degree
        int[] order=graph.getDegreeArray();

        //colorize using Welsh Powell algorithm
        //while there is at least one vertex that has not yet been colored I need to try coloring with an extra color
        while(!colorized)
        {
        	colorized=true;
            for (int j = 0; j < order.length; j++) 
            {
                if (graph.getColor(order[j]) == -1)//ie no color assigned yet
                {
                    if(!graph.setColor(upperBoundNum,order[j],true))
                    {
                    	colorized=false;
                    }
                }
            }
        	upperBoundNum++;
        }
        return upperBoundNum;
    }
}
