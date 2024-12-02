package Main;

import java.util.List;

public abstract class LogProcessor {
    // hold next processor in chain
    protected LogProcessor nextProcessor;

    // method to process each line
    public abstract void processLine(String line, List<String> logs);

    // set the next processor in the chain
    public void setNextProcessor(LogProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    // pass the log line to the next processor in the chain (if exists)
    protected void passToNext(String line, List<String> logs) {
        if (nextProcessor != null) {
            nextProcessor.processLine(line, logs);
        }
    }
}
