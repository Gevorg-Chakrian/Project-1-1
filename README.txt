The program can be compiled by compiling the file Main.java and
executed with as argument the path of the graph file to use it with.

The documentation can be found in the folder docs, and some test graphs in the folder graphs
The other folders are packages of the program containing the java files of the program.

The program can be modified through the Constants/Constants.java where the constants control the following:
DEBUG_GRAPH: display of debug messages on the graph reduction part
DEBUG: display of debug messages
TOTALTIME: Total time of run expected
TIMELIMITBT: ratio of time on the time left doing backtracking before stopping
TIMEFLAG: if true stops the program after reaching TOTALTIME
LAMBDA: Constant defining the age of a tabu move in Tabucol
TIMELIMITUB: ratio of time on the time left doing Tabucol before stopping
USEGA: if true use a genetic algorithm instead of a Tabucol
TIMELIMITGA: ratio of time on the time left doing a Genetic Algorithm before stopping
MAXITER: Number of Tabucol iteration in the mutation of a genetic algorithm
POPSIZE: Population of the genetic algorithm
CROSSRATIO: Probability of a crossing over
RANDOMWALK: Probability of choosing a random move instead of the best one in the tabucol for the genetic algorithm
ALTERNATIVECROSSOVER: false to use the one defined by R. Dorne and J. K. Hao,
“A new genetic local search algorithm for graph coloring,”
Lect. Notes Comput. Sci. (including Subser. Lect. Notes Artif. Intell. Lect.
Notes Bioinformatics), vol. 1498 LNCS, no. March 2013, pp. 745–754, 1998,
doi: 10.1007/bfb0056916. Or true to use the one created by us.