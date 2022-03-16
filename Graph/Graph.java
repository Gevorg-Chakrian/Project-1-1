package Graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static Constants.Constants.DEBUG;

public class Graph
{
    private final static String COMMENT = "//";
    public ArrayList<Integer> MaxClique = new ArrayList<>();
	public Vertex[] v=null;
	private int[] degreeArray;
    private int edg;

    /** 
     * This constructor creates an array of Vertex containing the graph given as adjacency matrix
     * @param matrix the adjacency matrix containing the graph
     */
    public Graph(int[][] matrix)
    {
        v=new Vertex[matrix.length];
        for(int i=0;i<matrix.length;i++)
        {
            v[i]=new Vertex();
            for(int j=0;j<matrix[i].length;j++)
            {
                if(matrix[i][j]==1)
                {
                    v[i].addElemToCon(j+1);
                    edg++;
                }

            }
        }
        edg/=2;

        degreeArray=findDegree();
    }

    /** 
     * This constructor creates an array of Vertex containing the graph given as file
     * @param inputfile path to the file containing the graph
     */
    public Graph(String inputfile)
    {
        boolean[] seen = null;

        //! n is the number of vertices in the graph
        int n = -1;

        //! m is the number of edges in the graph
        int m = -1;

        try
        { 
            FileReader fr = new FileReader(inputfile);
            BufferedReader br = new BufferedReader(fr);

            String record;

            //! The first few lines of the file are allowed to be comments, staring with a // symbol.
            //! These comments are only allowed at the top of the file.

            //! -----------------------------------------
            while ((record = br.readLine()) != null)
            {
                if( record.startsWith("//") ) continue;
                break; // Saw a line that did not start with a comment -- time to start reading the data in!
            }

            assert record != null;
            if( record.startsWith("VERTICES = ") )
            {
                n = Integer.parseInt( record.substring(11) );                   
                if(DEBUG) System.out.println(COMMENT + " Number of vertices = "+n);
            }
            seen = new boolean[n+1];    

            record = br.readLine();

            if( record.startsWith("EDGES = ") )
            {
                m = Integer.parseInt( record.substring(8) );                    
                if(DEBUG) System.out.println(COMMENT + " Expected number of edges = "+m);
            }

            v = new Vertex[n];

            for(int i=0;i<n;i++)
            {
                v[i] = new Vertex();
            }

            for( int d=0; d<m; d++)
            {
                if(DEBUG) System.out.println(COMMENT + " Reading edge "+(d+1));
                record = br.readLine();
                String[] data = record.split(" ");
                if( data.length != 2 )
                {
                    System.out.println("Error! Malformed edge line: "+record);
                    System.exit(0);
                }

                int vertex1 = Integer.parseInt(data[0]);
                int vertex2 = Integer.parseInt(data[1]);

                v[vertex1-1].addElemToCon(vertex2);
                v[vertex2-1].addElemToCon(vertex1);

                seen[ vertex1 ] = true;
                seen[ vertex2 ] = true;
            }

            String surplus = br.readLine();
            if( surplus != null )
            {
                if( surplus.length() >= 2 ) if(DEBUG) System.out.println(COMMENT + " Warning: there appeared to be data in your file after the last edge: '"+surplus+"'");                      
            }
        }

        catch (IOException ex)
        { 
            // catch possible io errors from readLine()
            System.out.println("Error! Problem reading file "+inputfile);
            System.exit(0);
        }

        for( int x=1; x<=n; x++ )
        {
            if(!seen[x])
            {
                if(DEBUG) System.out.println(COMMENT + " Warning: vertex "+x+" didn't appear in any edge : it will be considered a disconnected vertex on its own.");
            }
        }

        edg=m;

        degreeArray=findDegree();
    }

    /** 
     * This constructor creates an array of Vertex containing a graph randomly created
     * @param vertices number of vertices contained in the graph
     * @param edges number of edges contained in the graph
     */
    public Graph(int vertices, int edges)
    {

        ArrayList<Integer> checker = new ArrayList<>();
        int counter =0;

        v=new Vertex[vertices];

        for(int i=0;i<vertices;i++)
        {
            v[i] = new Vertex();
        }

        int d=0;
        int[] temp;
        boolean flag=true;
        while(d<edges){
            int vertex1 = (int)(Math.random()*vertices+1);
            int vertex2 = (int)(Math.random()*vertices+1);

            if(vertex1!=vertex2)
            {
                //test if edge already exists
                temp=v[vertex1-1].getConnected();
                for (int j : temp) {
                    if (j == vertex2) {
                        flag = false;
                        break;
                    }
                }
                if(flag) {
                    if(counter == 0){
                        checker.add(vertex1);
                        checker.add(vertex2);

                        v[vertex1-1].addElemToCon(vertex2);
                        v[vertex2-1].addElemToCon(vertex1);
                        d++;
                    }
                    else if (checker.contains(vertex1) && checker.contains(vertex1) && checker.size() < vertices);
                    else if (checker.contains(vertex1)) {
                        checker.add(vertex2);

                        v[vertex1 - 1].addElemToCon(vertex2);
                        v[vertex2 - 1].addElemToCon(vertex1);
                        d++;
                    }
                    else if (checker.contains(vertex2))  {
                        checker.add(vertex1);

                        v[vertex1 - 1].addElemToCon(vertex2);
                        v[vertex2 - 1].addElemToCon(vertex1);
                        d++;
                    }
                    counter++;

                }
                flag=true;
            }
        }
        edg=edges;

        //Create the file
        try {
            new WriterGraph(v,edges);
        } catch (IOException e) {
            e.printStackTrace();
        }

        degreeArray=findDegree();
    }

    /** 
     * Getter of the array of degrees sorted, by recalculating it
     * @return sorted by highest degree array of vertices
     */
    public int[] findDegree()
    {
        //find highest degree
        int degree = 0;
        for (int i = 0; i < this.getNodes(); i++) {
            degree=Math.max(degree,this.getDegree(i));
        }

        //sort the degree array
        int[] sort = new int[this.getNodes()];
        int count=0;
        for(int j=degree;j>=0;j--)
        {
            for (int i = 0; i < this.getNodes(); i++) {
                if (this.getDegree(i)==j) {
                    sort[count]=i;
                    count++;
                }
            }
        }
        this.setDegreeArray(sort);
        return sort;
    }

    /** 
     * Getter returning the number of edges
     * @return edg, the number of edges in the graph
     */
    public int getEdges()
    {
        return edg;
    }

    /** 
     * Getter returning the number of nodes
     * @return the number of nodes in the graph
     */
    public int getNodes()
    {
        return v.length;
    }

    /** 
     * Getter returning the degree of a node
     * @param id the id of the node
     * @return the degree of a node in the graph
     */
    public int getDegree(int id)
    {
        if(v[id] != null) {
            return v[id].getDegree();
        }
        return 0;
    }

    /** 
     * checker of the validity of a color
     * @param col value of the colour
     * @param id number of the vertex-1
     * @return true if color is valid, false otherwise
     */
	public boolean isColValid(int col, int id)
	{
		if(col==-1)
			return true;
		if(v[id] != null) {
            int[] connected = v[id].getConnected();
            for (int i = 0; i < connected.length; i++) {
                if (v[connected[i] - 1].getColor() == col)
                    return false;
            }
        }
		return true;
	}

	/** 
     * setter of the color of a vertex
     * @param col value of the colour
     * @param id number of the vertex-1
     * @param legal boolean asking for the colourizing to be authorized or not (illegal colouring is used for example in the tabu search)
     * @return true if the color was changed
     */
	public boolean setColor(int col, int id, boolean legal)
	{
		if(legal)
		{
			if(isColValid(col,id))
			{
                if(v[id] != null) v[id].setColor(col);
                    return true;
			}
			else {
			    return false;
            }
		}
		else
		{
            if(v[id] != null) v[id].setColor(col);
			return true;
		}
	}

    /** 
     * getter of the color of a vertex
     * @param id number of the vertex-1
     * @return the color of the vertex id
     */
    public int getColor(int id)
    {
        if(v[id] != null) {
            return v[id].getColor();
        }
        return -1;
    }

    /** 
     * Setter of the color of all vertices
     * @param col array with the color of all vertices
     */
    public void setColorArray(int[] col)
    {
        if(col.length==v.length)
            for(int i=0;i<col.length;i++)
            {
                v[i].setColor(col[i]);
            }
    }

	/** 
     * checker of the validity of a graph coloring (used for example in tabu search)
     * @param vertices an ArrayList that will contain all conflicting vertices
     * @param edges an ArrayList that will contain all conflicting edges
     */
	public void checkColoring(ArrayList<Integer> vertices, ArrayList<Edge> edges)
	{
        boolean[] checked=new boolean[v.length];
		for(int i=0;i<v.length;i++)
        {
            int col=v[i].getColor();
            if(!isColValid(col,i))
            {
                vertices.add(i);
                int[] connected=v[i].getConnected();
                for(int j=0;j<connected.length;j++)
                {
                    if(!checked[connected[j]-1] && v[connected[j]-1].getColor()==col)
                    {
                        edges.add(new Edge(i,connected[j]-1));
                    }
                }
            }
            checked[i]=true;
        }
	}

    /** 
     * checker of the gamma factor of a vertex (cf Tabucol)
     * @param id value of the vertex
     * @param col number of the color
     * @return the number of adjacent vertices of color col
     */
    public int getNumberAdjVertCol(int id,int col)
    {
        int[] connected=v[id].getConnected();
        int count=0;
        for(int i=0;i<connected.length;i++)
        {
            if(v[connected[i]-1].getColor()==col)
                count++;
        }
        return count;
    }

    /** 
     * Checks if two vertices are connected
     * @param id1 first vertex
     * @param id2 second vertex
     * @return true if connected, false otherwise
     */
    public boolean isConnected(int id1, int id2)
    {
        int deg1=v[id1].getDegree();
        int deg2=v[id2].getDegree();
        
        if(deg1>deg2)
        {
            int temp=id1;
            id1=id2;
            id2=temp;
        }
            
        int[] connected=v[id1].getConnected();
        for(int i=0;i<connected.length;i++)
        {
            if((connected[i]-1)==id2)
                return true;
        }
        return false;
    }

    /** 
     * Getter of the connection array of a vertex
     * @param id the id of the node
     * @return connection array
     */
    public int[] getConnected(int id){
        if(v[id] != null) {
            return v[id].getConnected();
        }
        return null;
    }

    /** 
     * Setter of the array of degrees sorted
     * @param degreeArray degree array of vertices
     */
    public void setDegreeArray(int[] degreeArray){
        this.degreeArray = degreeArray;
    }

    /** 
     * Getter of the array of degrees sorted
     * @return degree array of vertices
     */
    public int[] getDegreeArray(){
        return  degreeArray;
    }

    /** 
     * Getter of the array of vertices
     * @return array of vertices
     */
    public Vertex[] getV() {
        return v;
    }

    /** 
     * Getter of a specific vertex
     * @param index id of the vertex
     * @return vertex
     */
    public Vertex getExactVertex(int index){
        return v[index];
    }

    /** 
     * Setter of the array of vertices
     * @param newGraph array of vertices
     */
    public void setGraph(Vertex[] newGraph){
        this.v = newGraph;
    }

    /** 
     * Setter of the maximum clique array
     * @param MaxClique arrayList of vertices containing the maximum clique
     */
    public void setMaxClique(ArrayList<Integer> MaxClique){
        this.MaxClique = MaxClique;
    }

    /** 
     * Getter of the maximum clique array
     * @return arrayList of vertices containing the maximum clique
     */
    public ArrayList<Integer> getMaxClique() {
        return MaxClique;
    }

    /** 
     * Cloner of the vertex array
     * @return copy of the array
     */
    @Override
    public Vertex[] clone() {
        Vertex[] vertexClone = new Vertex[v.length];
        for(int i = 0; i < v.length; i++) {
                vertexClone[i] = new Vertex();
                vertexClone[i].setConnected(v[i].getConnected());
        }
        return vertexClone;
    }
}


