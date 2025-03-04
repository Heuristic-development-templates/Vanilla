package Algorithms;

import dataStructures.Solution;

import java.util.List;

/**
 * This interface must be implemented for all the methods you want to execute
 * TODO check your methods implement this interface, modify as needed.
 */
public interface Algorithm {
    List<String> getExtraData();

    /**
     * Run the method
     *
     * @return the resulting Solution generated
     */
    Solution run();

    /**
     * Run the method with a given timeLimit
     *
     * @param timeInit The startTime
     * @return The resulting Solution generated
     */
    Solution run(Long timeInit);


    /**
     * Here you get your algorithm name
     * @return
     */
    String getId();

    /**
     * Here you set your algorithm name
     */
    void setId(String id);

}
