package Constants;
public final class Constants
{
	private Constants()
	{

	}

	//GraphReduction
	public static final boolean DEBUG_GRAPH=false;

	//General
	public static final boolean DEBUG=false;
	public static final int TOTALTIME=120;

	//Backtracking
	public static final double TIMELIMITBT=0.1;
	public static final boolean TIMEFLAG=false;

	//TABUCOL
	public static final double LAMBDA=0.6;
	public static final double TIMELIMITUB=0.25;

	//Genetic Algorithm
	public static final boolean USEGA=true;
	public static final double TIMELIMITGA=0.1;
	public static final int MAXITER=500;
	public static final int POPSIZE=10;
	public static final double CROSSRATIO = 0.02;
	public static final double RANDOMWALK = 0.01;
	public static final boolean ALTERNATIVECROSSOVER = false;
}