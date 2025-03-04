import Algorithms.Algorithm;
import dataStructures.Solution;
import dataStructures.Instance;
import io.DataOutput;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.DataInput.readDataToGraph;

public class ExperimentAlgorithmRunner implements Runnable {


    private final File instanceFile;
    private final String instancePath;
    private final Algorithm algorithm;
    private final int seed;
    private final DataOutput dataOutput;
    public static boolean exportImage;

    public ExperimentAlgorithmRunner(String filePath, Algorithm algorithmToExecute, DataOutput dataOutput, int seed) {
        this.instanceFile = new File(filePath);
        this.algorithm = algorithmToExecute;
        this.dataOutput = dataOutput;
        this.instancePath = filePath;
        this.seed = seed;
    }

    static Logger logger = Logger.getLogger("Instance");

    @Override
    public void run() {
        logger.log(Level.INFO, "Starting algorithm" + algorithm.getId()+"::"+ instanceFile.getAbsolutePath() );
        Instance g = readDataToGraph(instanceFile);

        if (g == null) {
            logger.log(Level.SEVERE, "Error opening the file:" + instanceFile.getAbsolutePath());
        }

        try {
            long startTime = System.nanoTime(); //Start measuring the time
            Solution solution = algorithm.run(); //Execute the algorithm
            float timeSinceStart = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime);
            //The data to export to CSV after the execution is over
            List<String> dataToExport = new LinkedList<>();
            dataToExport.add(algorithm.getId());
            dataToExport.add(g.getName());
            dataToExport.add(String.valueOf(solution.evaluateObjectiveFunction()).replace(".", ","));
            dataToExport.add(String.valueOf(timeSinceStart).replace(".", ","));

            dataOutput.exportCSV(dataToExport);
            logger.log(Level.FINE, algorithm.getId() + "::" + instanceFile.getAbsolutePath() + ": Correct end of execution");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during the execution of the algorithm " + algorithm.getId() + "::" + instanceFile.getAbsolutePath());
            logger.log(Level.SEVERE, algorithm.getId() + "::" + instanceFile.getAbsolutePath() + e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
