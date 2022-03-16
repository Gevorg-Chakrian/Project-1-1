package PatternRecognition;

import Graph.Graph;

import java.util.ArrayList;

import static Constants.Constants.DEBUG;

public class SubgraphTypes {
	
	private int[] visited;
	private int comp =1;
	private Graph g;
	private int n,m;
	private int cnt=0;
	
	//Class constructor
	public SubgraphTypes(Graph g)
	{
		this.g=g;
		m=g.getEdges();
		n=g.getNodes();
	}
	
	/**
	 * method that checks if it's a graph containing only one vertex and no edges
	 * @return true if it's an isolated graph or false if it is not
	 */
	private boolean isSingle()
	{
		//an isolated graph has one vertex and no edges
		if(n ==1 && m==0)
			return true;
		return false;
	}
	
	/**
	 * method that checks if it's a complete graph
	 * @return true if it's a complete graph or false if it is not
	 */
	private boolean isComplete()
	{
		//formula for complete graph
		if(m==(n*(n-1)/2))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * method that checks if it's a tree graph
	 * @return true if it's a tree graph or false if it is not
	 */
	private boolean isTree()
	{
		//for the graph to have the possibility of being a tree the number of edges is equal to the number of nodes-1
		if(n-1 == m)
		{
		//check if it's a star graph as that is a particular type of tree. If it's true then return true
		if(isStar()==true)
			return true;
		visited = new int[n];
		//as it is an undirected graph it doesn't matter with which node we start the search so we always start with the first vertex
		dfs1(0);
		for(int i=0;i<n;i++)
			if(visited[i]==0)
				return false;
		return true;
		}
	return false;
	}
	
	/**
	 * depth-first search method used for checking for tree types
	 * @return
	 */
	private void dfs1(int k)
	{
		visited[k]= comp;
		for(int i=0;i<n;i++)
		{	
			if(g.isConnected(i, k) && visited[i] ==0)
				dfs1(i);
		}
	}
	
	/**
	 * depth-first search method used for checking for cycle graphs
	 * @return true if it's a tree graph or false if it is not
	 */
	private boolean dfs(int k, int init)
	{
		 visited[k]= comp;
		 int[] con = g.getConnected(k);
		  //for(int i=0;i<g.getNodes();i++)
			  for(int j=0; j<con.length;j++)
			  {
				 if(k!=init || cnt==0)
				 {
					if(g.isConnected(k, con[j]-1) && visited[con[j]-1] ==0)
					{
						cnt++;
						dfs(con[j]-1,init);
					}
				 }
				else
				 return true;
			  }
		  return false;
	}
	
	/**
	 * method that checks if it's a star graph
	 * @return true if it's a star graph or false if it is not
	 */
	private boolean isStar()
	{
		int nr=0;
		int[] degree = g.getDegreeArray();
		for(int i=0;i<degree.length;i++)
		{
			//if the degree is bigger than 1 or equal to 0 and the graph doesn't have a self loop
			if(degree[i]!=1)
				nr++;
		}
		//a star graph is a special type of tree that has one parent node of degree n-1 and the other are child nodes of degree 1
		if(nr>1)
			return false;
		return true;
	}
	
	/**
	 * method that checks if it's a bipartite graph
	 * @return true if it's a bipartite graph or false if it is not
	 */
	private boolean isBipartite() 
    {
    	//We will store 1 if the vertex is in the first set or 0 if it's in the second set.
    	int col[] = new int[n]; 
    	col[0]=1;
    	//We initialize the array with "-1" as the nodes haven't been divided into sets yet.
        for (int i=1;i<n;i++) 
            col[i] = -1;
        //add to queue all vertices to be tested and test if they are part of two sets and no more
    	ArrayList<Integer> queue=new ArrayList<Integer>();
    	queue.add(0);

    	while(queue.size()!=0)
    	{
    		int v=queue.get(0);
    		queue.remove(0);

    		for(int i=0;i<n;i++)
    		{
    			boolean connected=g.isConnected(v,i);
    			if(connected && col[i]==-1)
    			{
    				col[i]=1-col[v];
    				queue.add(i);
    			}
    			else if(connected && col[v]==col[i])
    				return false;
    		}
    	}
    	return true;
    }
	
	/**
	 * method that checks if it's cycle graph
	 * @return true if it's a cycle graph or false if it is not
	 */
	private boolean isCycle() 
    { 
		cnt=0;
		int[] degree = g.getDegreeArray();
		visited = new int[n];
        // Mark all the vertices as not visited  
        for (int i = 0; i < g.getNodes(); i++) 
            visited[i] = 0; 
        // check if the starting vertex is also the end vertex and all vertices were visited
        if(dfs(degree[0],degree[0])==true && cnt+1==n)
        	//check if the number of edges is bigger than the number of nodes 
        	if(m>n)
        		return false;
        	else
        		return true;
		return false;
    }
	
	/**
	 * method that checks if it's wheel graph
	 * @return true if it's a wheel graph or false if it is not
	 */
	private boolean isWheel() {
		// formula: No of edges from hub to all other vertices + No. of edges from all other nodes in cycle graph without a hub
		if(m==2*(n-1))
			//a graph can be a wheel if it has a cycle and the number of vertices is bigger or equal to 3
			if(isCycle()==false && n>=3)
				return true;
		return false;
	}
	
	/**
	 * method that checks for the graph type
	 * @return -1 if the graph type wasn't identified or the chromatic number if it's a known graph type
	 */
	public int chromaticNumberKnownGraph()
	{
		//check for isolated graph
		if(isSingle()==true)
		{
			if(DEBUG)System.out.println("single");
			return 1;
		}
		//check for complete graph
		if(isComplete()==true)
		{
			if(DEBUG)System.out.println("Complete");
			return n;
		}
		//check for tree graph
		if(isTree()==true)
		{
			if(DEBUG)System.out.println("Tree");
			return 2;
		}
		//check for cycle graph
		if(isCycle()==true)
		{
			if(DEBUG)System.out.println("Cycle");
			//if the cycle graph is even the chromatic number is 2 otherwise it's 3
			if(n%2==0)
				return 2;
			else return 3;
		}
		//check for wheel graph
		if(isWheel()==true)
		{
			if(DEBUG)System.out.println("Wheel");
			//if the wheel graph is even the chromatic number is 4 otherwise it's 3
			if(n%2==0)
				return 4;
			else return 3;
		}
		//check for bipartite graph
		if(isBipartite())
		{
			if(DEBUG)System.out.println("Bipartite");
			return 2;
		}
	return -1;
	}
	
}