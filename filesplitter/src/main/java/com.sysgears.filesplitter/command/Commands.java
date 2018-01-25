package com.sysgears.filesplitter.command;

public enum Commands {

    SPLIT("split"),
    MERGE("merge");

    /**
     * Command name.
     */
    private final String name;

    /**
     * Initializes the name variable.
     *
     * @param name name of the command
     */
    Commands(String name) {
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }
}
