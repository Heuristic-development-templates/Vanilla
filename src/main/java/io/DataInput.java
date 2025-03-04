package io;

import dataStructures.Instance;

import java.io.*;

public class DataInput {

    /**
     * Generate a Graph object from a file
     *
     * @param instanceFile The File object where the graph is
     * @return A graph object
     */
    public static Instance readDataToGraph(File instanceFile) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(instanceFile));

            //TODO put your code here to read the instance file.

            var instance = new //TODO instantiate your instance class here. NOTE: you must firsts create an Instance class.

            // IMPORTANT! Remember that instance data must be immutable from this point
            return instance;
        } catch (IOException e) {//In case the file doesn't exist
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }
}
