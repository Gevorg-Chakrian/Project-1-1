package Graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;            

public class WriterGraph {

    /**
     * This constructor creates a txt file containing the graph's informations
     * @param vert an array of Vertex, containing the graph
     * @param edg the number of edges in the graph
     * @throws IOException in case of writing error
     */
    public WriterGraph(Vertex[] vert,int edg) throws IOException {
        int ver = vert.length;
        File dir = new File(System.getProperty("user.dir"));
        dir.mkdirs();

        File graphFile = new File(dir,ver+"-"+edg);
        
        // cursor
        FileWriter writer = new FileWriter(graphFile);

        // writing the number of vertices and edges
        writer.write("VERTICES = " + ver + "\nEDGES = " + edg + "\n");

        // writing down all edges
        for (int i = 0; i < vert.length; i++) {
            int[] connection = vert[i].getConnected();
            for (int j = 0; j < connection.length; j++) {
                if(connection[j] >= (i+1)) {
                    writer.write((i + 1) + " " + (connection[j]) + "\n");
                }
            }
        }

        writer.close(); //closes the file
    }
}

