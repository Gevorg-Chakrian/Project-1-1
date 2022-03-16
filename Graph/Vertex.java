package Graph;

import java.util.Arrays;

public class Vertex
{
	private int color;
	private int[] connected;

	/** 
     * This constructor creates a Vertex object containing the color
     * of a node and an array containing it's connected vertices
     * NB: v.connected[i] is the i+1 vertex (they start at 1 but are stored from 0).
     */
	public Vertex()
	{
		color=-1;
		connected=new int[0];
	}

	/** 
     * Setter of the colour of a Vertex, checks the validity the color before assigning the colour.
     * @param col int, color to assign
     */
	public void setColor(int col)
	{
		color=col;
	}

	/** 
     * Setter of the connected array of a Vertex, adds one element to the array
     * @param toAdd int, element to add
     */
	public void addElemToCon(int toAdd)
	{
		int[] newnb=new int[connected.length+1];
		System.arraycopy(connected, 0, newnb, 0, connected.length);
		newnb[connected.length]=toAdd;
		connected=newnb;
	}

	/** 
     * Getter of the colour of a Vertex
     * @return color, an int, the value of the id of the colour
     */
	public int getColor()
	{
		return color;
	}

	/** 
     * Getter of the connected array of a Vertex
     * @return an int array, containing the values of the connected vertices
     */
	public int[] getConnected()
	{
		//connected is copied to avoid modifications
		return Arrays.copyOf(connected,connected.length);
	}

	/** 
     * Getter returning the degree of a node
     * @return the degree of a node
     */
    public int getDegree()
    {
    	return connected.length;
    }

    /** 
     * Modifier of the connections
     * @param vertex the vertices to isolate as -1 in an array
     */
    public void removeConnectionWith(int[] vertex){
    	int count = 0;
		for (int i = 0; i < connected.length; i++) {
			for (int j = 0; j < vertex.length; j++) {
				if (vertex[j] != -1) {
					if (connected[i] == vertex[j]) {
						connected[i] = -1;
						count++;
					}
				}
			}
		}
		int[] newConnection = new int[connected.length-count];
		int point = 0;
		for (int i = 0; i < connected.length; i++) {
			if(connected[i] != -1){
				newConnection[point] = connected[i];
				point++;
			}
		}
		connected = newConnection;
	}

	/** 
     * Setter of the connected array of a Vertex, as a global array
     * @param connected new connection array
     */
	public void setConnected(int[] connected) {
		this.connected = connected;
	}
}