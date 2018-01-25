package com.sysgears.filesplitter;

public class FileSplitterApplication {

    public static void main(String[] args) {
        try {
            Runner runner = new Runner();
            runner.run();
        } catch (Throwable tr) {
            System.out.println("Something went wrong.");
        }
    }
}
