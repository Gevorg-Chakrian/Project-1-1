package ExactCalculation;

import Graph.Graph;
import Graph.Vertex;

import java.util.Arrays;

import static Constants.Constants.*;

public class Backtracking
{
    int[] hintArrayColor;
    int GlobalIndex;
    boolean time = false;
    int countTry = 0;
    int glLowerBound;
    int glUpperBound;
    int glChromaticNumber = 0;
    int timelimit;

    /**
     * This constructor calls the other methods in order to calculate the chromatic number. 
     * @param g a Graph, containing the information of the graph
     * @param lowerBound an integer, containing a lower bound
     * @param upperBound an integer, containing an upper bound
     * @param timelimit the maximum time of running if the boolean Constants.TIMEFLAG is set to true
     */
    public Backtracking(Graph g, int lowerBound, int upperBound, int timelimit){
        long start = System.nanoTime();
        this.timelimit=timelimit;
        this.glUpperBound = upperBound;
        this.glLowerBound = lowerBound;
        mainGraphColoring(g, lowerBound, upperBound-1, start);
        if(countTry == upperBound-lowerBound){
            glChromaticNumber = glUpperBound;
        }
    }

    /**
     * This constructor calls the other methods in order to calculate the chromatic number in the case a graph reducing. 
     * @param g a Graph, containing the information of the graph
     * @param lowerBound an integer, containing a lower bound
     * @param imitationLowerBound candidate to new lower bound
     * @param upperBound an integer, containing an upper bound
     * @param origin original graph
     */
    public Backtracking(Graph g, int lowerBound, int imitationLowerBound, int upperBound, Vertex[] origin){
        long start = System.nanoTime();
        this.glUpperBound = upperBound;
        this.glLowerBound = lowerBound;
        reduceGraphColoring(g, imitationLowerBound, upperBound-1, start, true);
        g.setGraph(origin);
        reduceGraphColoring(g, lowerBound, upperBound-1, start, false);
        if(glLowerBound == glUpperBound){
            glChromaticNumber = glUpperBound;
        }
        if(glLowerBound != lowerBound){
            System.out.println("NEW BEST LOWER BOUND = " + glLowerBound);
        }
    }
    
    /**
     * This method calculates the exact chromatic number or @return -2 if the calculation lasts more than one minute 
     * @param g a Graph, containing the information about the graph
     * @param lowB an integer, containing a minimum value
     * @param uppB an integer, containing as an upper bound
     * @param start a long, containing the time it started the calculation
     * @param graphReduction boolean to change the status of the process
     */
    private void reduceGraphColoring(Graph g, int lowB, int uppB, long start, boolean graphReduction) {
        time = false;
        if (lowB > uppB) return;

        //Creating array to give index:vertex exact color
        int[] arrayColor = new int[g.v.length];
        Arrays.fill(arrayColor, -1);
        int[] vertex = g.getDegreeArray();
        int index = 0;

        for (int i = 0; i < g.getNodes(); i++) {
            if (g.v[i] == null) {
                index++;
            }
        }

        int baseCase = g.getNodes();

        if (!graphReduction) {
            if (getHintArrayColor() != null) arrayColor = getHintArrayColor();
            baseCase = GlobalIndex;
        } else {
            GlobalIndex = index;
        }

        if (DEBUG && !graphReduction) System.out.println("Graph Reduction try with " + uppB + " colors");
        if (generatorGraphColoring(arrayColor, g, uppB, vertex, start, index, baseCase)) {
            if (!graphReduction) {
                countTry++;
                if(DEBUG)System.out.println("SUCCESSFUL TRY WITH " + uppB);
                glUpperBound = uppB;
                System.out.println("NEW BEST UPPER BOUND = " + uppB);
            } else {
                hintArrayColor = arrayColor;
            }
            uppB--;
            reduceGraphColoring(g, lowB, uppB, start, graphReduction);
        } else {
            if(!graphReduction) {
                if (!time) {
                    if(DEBUG)System.out.println("UNSUCCESSFUL TRY WITH = " + uppB);
                    countTry++;
                } else {
                    if(DEBUG)System.out.println("Time is up");
                }
            } else {
                if(glLowerBound <= uppB) {
                    glLowerBound = uppB+1;
                }
            }
        }
    }

    /**
     * This method calculates the exact chromatic number or @return -2 if the calculation is too long
     * @param g a Graph, containing the information about the graph
     * @param lowB on integer, containing a minimum value
     * @param uppB an integer, containing as an upper bound
     * @param start a long, containing the time it started the calculation
     * @return the chromatic number or -2 if it can't find it
     */
    private void mainGraphColoring(Graph g, int lowB, int uppB, long start) {

        time = false;
        if (lowB > uppB) return;

        //Creating array to give index:vertex exact color
        int[] arrayColor = new int[g.v.length];
        Arrays.fill(arrayColor,-1);
        int[] vertex = g.getDegreeArray();
        int index = 0;
        int baseCase = g.getNodes();

        if (generatorGraphColoring(arrayColor, g, uppB, vertex, start, index, baseCase))
        {
                countTry++;
                if(DEBUG)System.out.println("SUCCESSFUL TRY WITH " + uppB);
                glUpperBound = uppB;
                System.out.println("NEW BEST UPPER BOUND = " + uppB);
        } else {
            if (time) {
                if(DEBUG)System.out.println("Time is up");
                return;
            } else {
                countTry++;
                if(DEBUG)System.out.println("UNSUCCESSFUL TRY WITH = " + uppB);
                if(glLowerBound <= uppB){
                    System.out.println("NEW BEST LOWER BOUND = " + (uppB+1));
                    glLowerBound = (uppB+1);
                }
                return;
            }
        }

        uppB--;
        start = System.nanoTime();
        mainGraphColoring(g, lowB, uppB, start);
    }

    /**
     * This is a recursive method that generates a possible coloring for the graph.
     * @param clr an array of integers, containing the colors
     * @param g a Graph, containing the information of the graph
     * @param upperBound an integer, containing the maximum value
     * @param vertex an integer array to colourize,
     * @param start a long, containing the time it started the calculation
     * @param index the stage
     * @param baseCase the original case
     * @return true if a solution is found
     */
    private boolean generatorGraphColoring(int[] clr, Graph g, int upperBound, int[] vertex, long start, int index, int baseCase)
    {
    	if (index >= baseCase)
        	return true;

    	//Condition, if we can color the graph in 4 colors, we try again with 3 colors
        for (int i = 0; i < upperBound; i++)
        {
            //Check time limit
            if(TIMEFLAG && (System.nanoTime()-start)/1000000.0>=timelimit*1000) {
                time = true;
                return false;
            }

            if(isColValid(clr, i, g, vertex[index])) {
                // if vertex doesn't have color we give him color i
                clr[vertex[index]] = i;

                //If we can give some vertex a color, go to the next vertex
                if (generatorGraphColoring(clr, g, upperBound, vertex, start, index+1, baseCase))
                	return true;

                //if we have false, we remove color of vertex, in order to try a new combination of coloring
                clr[vertex[index]] = -1;
            }
        }
        //if the algorithm can't find any possible solution, return false
        return false;
    }

    /**
     * This method checks the color of the connected vertices.
     * @param clr an array of integers, containing
     * @param col an integer, containing
     * @param g the graph
     * @param vertex an integer, containing the index of the current vertex
     * @return true if the coloring is allowed
     */
    private static boolean isColValid(int[] clr, int col, Graph g, int vertex) {
        int[] connected = g.getConnected(vertex);
            for (int k : connected) {
                //We have our i = color, from the generateGraphColoring, we check, if this vertex has the same color as another vertex
                if (col == clr[k - 1]) {
                    return false;
                }

            }
        return true;
    }

    /**
     * Getter of the chromatic number
     * @return chromatic number
     */
    public int getChromaticNumber() {
        return glChromaticNumber;
    }

    /**
     * Getter of the lower bound
     * @return lower bound
     */
    public int getLowerBound() {
        return glLowerBound;
    }

    /**
     * Getter of the upper bound
     * @return upper bound
     */
    public int getUpperBound() {
        return glUpperBound;
    }

    /**
     * Getter of the hint array
     * @return hint array
     */
    public int[] getHintArrayColor() {
        return hintArrayColor;
    }
}
