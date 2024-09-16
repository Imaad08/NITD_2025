package com.nighthawk.spring_portfolio.hacks.streamChain;

import java.util.Arrays;
import java.util.List;

/**
 * Represents the states with their respective climates.
 *
 * <p>Each state is associated with a list of climates. This is done by passing the list of climates to the enum constructor.
 * The climates for each state are stored in a private field and can be accessed using the getClimates() method.</p>
 */
public enum State {
    /**
     * The state of California with its climates.
     */
    CALIFORNIA(Arrays.asList(Climate.SUBTROPICAL, Climate.TEMPERATE)),

    /**
     * The state of Florida with its climates.
     */
    FLORIDA(Arrays.asList(Climate.TROPICAL, Climate.SUBTROPICAL)),

    /**
     * The state of Washington with its climates.
     */
    WASHINGTON(Arrays.asList(Climate.TEMPERATE)),

    /**
     * The state of Hawaii with its climates.
     */
    HAWAII(Arrays.asList(Climate.TROPICAL));

    /**
     * The list of climates for this state.
     */
    private List<Climate> climates;

    /**
     * Constructs a new state with the given list of climates.
     *
     * @param climates the list of climates for this state
     */
    State(List<Climate> climates) {
        this.climates = climates;
    }

    /**
     * Returns the list of climates for this state.
     *
     * @return the list of climates for this state
     */
    public List<Climate> getClimates() {
        return climates;
    }
}