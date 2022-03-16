package UpperBound;

public class Move
{
	private int vertex;
	private int color;
	private int iteration;

	/**
     * Constructor of a Move object
     * @param vertex id of the moving vertex
     * @param color the new colour
     * @param iteration the number of iterations the move will stay in the tabu list
     */
	public Move(int vertex, int color, int iteration)
	{
		this.vertex=vertex;
		this.color=color;
		this.iteration=iteration;
	}

	/**
     * Constructor of a Move object
     * @param vertex id of the moving vertex
     * @param color the new colour
     */
	public Move(int vertex, int color)
	{
		this(vertex,color,0);
	}

	/**
     * Getter of the vertex id
     * @return vertex
     */
	public int getVertex()
	{
		return vertex;
	}

	/**
     * Getter of the color
     * @return color
     */
	public int getColor()
	{
		return color;
	}

	/**
     * Getter of the number of iterations
     * @return iteration
     */
	public int getIteration()
	{
		return iteration;
	}

	/**
     * Setter of the number of iterations
     * @param iteration the new number of iterations
     */
	public void setIteration(int iteration)
	{
		this.iteration=iteration;
	}

	/**
     * Tests if two moves are identic
     * @param obj Move to test
     * @return true if identic
     */
	public boolean equals(Object obj)
	{
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }

        Move m = (Move) obj;
        return vertex == m.vertex &&
               color == m.color;
    }
}