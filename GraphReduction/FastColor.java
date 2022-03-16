package GraphReduction;

import ExactCalculation.Backtracking;
import Graph.Graph;
import Graph.Vertex;

import java.util.ArrayList;
import java.util.Arrays;

import static Constants.Constants.DEBUG_GRAPH;

public class FastColor {


    private static ArrayList<Integer> arrayOfMaxClique = new ArrayList<Integer>();

    /**
     * Getter of the arrayOfMaxClique
     * @return arraylist of maximum clique
     */
    public static ArrayList<Integer> getArrayOfMaxClique()
    {
        return arrayOfMaxClique;
    }

    /**
     * Method to reduce the graph
     * @param graph to reduce
     * @param glLowerBound current lower bound
     * @param glUpperBound current upper bound
     */
    public static void FastColor(Graph graph, int glLowerBound, int glUpperBound){
        //Origin Graph
        Vertex[] originGraph = graph.v;
        //Graph, with vertices which are staying, after reduction
        Vertex[] newGraph = graph.clone();
        int lowerBound = 0;
        int newLowerBound;

        int stopCond = 0;
            while (true/*stopCond != 3*/) {

                graph.setGraph(originGraph);
                //We create an array, where we store the full graph
                Vertex[] vertexWhichWasDeleted = graph.clone();

                graph.setGraph(newGraph);
                //Find a maximal Clique
                newLowerBound = LowerBound.LowerBound.getSolution(graph);
                if (newLowerBound > lowerBound) {
                    lowerBound = newLowerBound;
                }
                if(DEBUG_GRAPH) System.out.println("Lower Bound = " + lowerBound);
            /*To reduce the graph, we first find a maximal newLowerBound-degree bounded independent set.
             This is accomplished by traversing newGraph sequentially and adding the vertex if its degree is less than lbk
             and it is not adjacent to any vertex already in the independent set.  Then, newGraph is reduced by removing
             independentSet, according to the BIS-Rule.Along with this reduction, removed vertices (and the removed incident edges)are
             stored in to vertexWhichWasDeleted. Note that sometimes the BIS-Rule cannot remove any vertex, and in this case, newGraph is unchanged.
             */
                //Store a new maximal clique
                ArrayList<Integer> MaxClique = graph.getMaxClique();
                //Combine the previous maximal clique with a new maximal clique
                arrayOfMaxClique.addAll(MaxClique);

                if(DEBUG_GRAPH) System.out.println("SET MAX CLIQUE = "+Arrays.toString(MaxClique.toArray()));
                //Array with vertices, which are deleted
                int[] independentSet = new int[arrayOfMaxClique.size()];
                //Array with vertices, which are stayed
                int[] notIndependentSet = new int[graph.v.length];

                //Filling the independent array
                int pointSet = 0;
                for (int i = 0; i < arrayOfMaxClique.size(); i++) {
                    independentSet[pointSet] = arrayOfMaxClique.get(i)+1;
                    pointSet++;
                }

                //Delete vertices from reduced graph through the independent array
                for (int j : independentSet) {
                    newGraph[j - 1] = null;
                }

                //Remove connection for the new graph
                for (int i = 0; i < newGraph.length; i++) {
                    if (newGraph[i] != null) {
                        newGraph[i].removeConnectionWith(independentSet);
                    }
                }

                //Deleted from new graph all vertices, which do not have connection at all
                for (int i = 0; i < newGraph.length; i++) {
                    if(newGraph[i] != null){
                        if(newGraph[i].getConnected().length == 0){
                            newGraph[i] = null;
                        }
                    }
                }

                //Filling the not independent array
                for (int i = 1; i <= notIndependentSet.length; i++) {
                    if (newGraph[i-1] == null) {
                        notIndependentSet[i - 1] = -1;
                    } else {
                        notIndependentSet[i - 1] = i;
                    }
                }

                //Delete vertices from the array vertex, which are deleted through the not independent array
                for (int i = 0; i < notIndependentSet.length; i++) {
                    if(notIndependentSet[i] != -1){
                        vertexWhichWasDeleted[notIndependentSet[i]-1] = null;
                    }
                }

                graph.setGraph(newGraph);
                graph.findDegree();

                //Remove connection with new graph
                for (int i = 0; i < vertexWhichWasDeleted.length; i++) {
                    if(vertexWhichWasDeleted[i] != null) {
                        vertexWhichWasDeleted[i].removeConnectionWith(notIndependentSet);
                    }
                }
                //Starting backtracking for finding chromatic number, initially we try to paint vertices which are and was deleted, then with
                //the memory of color for deleted vertices, we try to paint the origin graph
                graph.setGraph(vertexWhichWasDeleted);
                //Restore connection with old vertices

                Backtracking backtracking = new Backtracking(graph, glLowerBound, newLowerBound, glUpperBound, originGraph);

                glUpperBound = backtracking.getUpperBound();
                glLowerBound = backtracking.getLowerBound();

                if (glLowerBound == glUpperBound) {
                    System.out.println("CHROMATIC NUMBER = " + backtracking.getChromaticNumber());
                    break;
                }
                //stopCond++;
            }
            
        graph.setGraph(originGraph);
        graph.findDegree();
    }
}
