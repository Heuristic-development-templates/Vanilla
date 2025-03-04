import Algorithms.Algorithm;
import io.DataOutput;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

/**
 * This is the start point for regular experiments
 * You must configure:
 * - Number of cores: the amount or percent of cores that the experiment is going to use, running one instance/algorithm on each
 * - Folder: the folder that contains the instances that are going to be used for the experiment
 * - Algorithm: the algorithm that is going to be executed
 * - Logger level: the messages that are going to be output, by default warning, error, severe...
 * It is your decision how to configure those, you can read them from command line, from a file, or just hardcode them in this class or
 * in a specific method.
 * <p>
 * TODO modify as needed
 */
@CommandLine.Command(name = "experiment", mixinStandardHelpOptions = true, version = "1.0",
        description = "Run the experiments")
public class Experiment {

    @CommandLine.Option(names = {"-s", "--sequentialRun"}, description = "Indicates whether to use concurrency or not")
    public static boolean sequentialRun = false;

    @CommandLine.Option(names = {"-n", "--numberOfCores"}, description = "Number of cores used if run in parallel")
    public static int numberOfCores = 1;

    @CommandLine.Option(names = {"-p", "--percentOfCores"}, description = "Percentage of cores available used if run in parallel")
    public static int percentOfCores = 80;

    @CommandLine.Option(names = {"-i", "--instancesFolderName"}, description = "The name of the folder containing the instances for this experiment")
    public static String instancesFolderName = "Instances/";

    @CommandLine.Option(names = {"-l", "--loggingLevel"}, description = "The logger level")
    public static String loggingLevel = "INFO";

    @CommandLine.Option(names = {"-r", "--randomSeed"}, description = "The random seed")
    public static int randomSeed = 42;

    static Logger logger = Logger.getLogger("Experiment");
    ExecutorService ex;


    public static void main(String[] args) {
        new Experiment().run(); // Just run the experiment in main
    }

    public void run() {
        startLoggingLevel();
        int cores = getMaxNumberOfCores();
        ex = Executors.newFixedThreadPool(cores);
        logger.log(Level.INFO, "Using " + cores + " cores");


        closeExperimentation();
    }


    /**
     * Run an experiment that involves one algorithm
     *
     * @param folder         The name of the folder that contains the instances
     * @param algorithm      The algorithm to execute
     * @param experimentName The name of the experiment and CSV
     */
    private void executeAlgorithm(String folder, Algorithm algorithm, String experimentName) {
        List<Algorithm> dummyList = new LinkedList<>();
        dummyList.add(algorithm);
        executeAlgorithm(folder, dummyList, experimentName);
    }

    /**
     * Run an experiment that involves multiple algorithms
     *
     * @param folder         The name of the folder that contains the instances
     * @param algorithmList  The algorithms to execute
     * @param experimentName The name of the experiment and CSV
     */
    private void executeAlgorithm(String folder, List<Algorithm> algorithmList, String
            experimentName) {
        List<String> files = getFiles(folder);


        //The headers of the CSV
        List<String> headersOfCSV = new LinkedList<>();
        headersOfCSV.add("Algorithm ID");
        headersOfCSV.add("Filename");
        headersOfCSV.add("O.F.");
        headersOfCSV.add("T. CPU (s)");
        DataOutput dataOutput = new DataOutput(experimentName, headersOfCSV);

        for (String file : files) {
            for (Algorithm algorithm : algorithmList) {
                ExperimentAlgorithmRunner experimentAlgorithmRunnerGreedyRCL = new ExperimentAlgorithmRunner(file, algorithm, dataOutput, randomSeed);
                if (sequentialRun) {
                    experimentAlgorithmRunnerGreedyRCL.run();
                } else {
                    ex.submit(experimentAlgorithmRunnerGreedyRCL);
                }
            }
        }

    }

    /**
     * This method stops waiting for receiving more experiments to run.
     * This is the Java way to avoid having zombie threads.
     */
    private void closeExperimentation() {
        ex.shutdown();
        try {
            if (!ex.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
                ex.shutdownNow();
            }
        } catch (InterruptedException e) {
            ex.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }


    private static List<String> getFiles(String folderPath) {
        File folder = new File(folderPath);

        List<String> filePaths = listFiles(folder);
        return filePaths;
    }

    /**
     * This method recursively explores a directory and returns all the file paths in it.
     * @param folder The folder to explore
     * @return The list of files (not directories) contained in the folder and subfolders
     */
    private static List<String> listFiles(File folder) {
        List<String> filePaths = new ArrayList<>();
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.isDirectory()) {
                    filePaths.addAll(listFiles(file)); // Recursively add files in subfolders
                } else {
                    filePaths.add(file.getAbsolutePath());
                }
            }
        }
        return filePaths;
    }

    /**
     * This method serves as a safe to avoid using more cores than those that the machine.
     * @return A number of cores C such that 0 < C < NumberOfCoresOfTheMachine
     */
    public int getMaxNumberOfCores() {
        if (sequentialRun) {
            return 1;
        } else {
            return Math.min(numberOfCores, percentOfCores * Runtime.getRuntime().availableProcessors() / 100);
        }
    }

    /**
     * This method reads the CLI variable -l and sets the logging level across all the project to the one indicated.
     */
    public static void startLoggingLevel() {
        Level level = Level.parse(loggingLevel.toUpperCase());
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(level);

        // Remove existing handlers
        for (Handler h : rootLogger.getHandlers()) {
            rootLogger.removeHandler(h);
        }

        // Add a FileHandler
        try {
            FileHandler fileHandler = new FileHandler("logs.log", true);
            fileHandler.setLevel(level);
            fileHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
