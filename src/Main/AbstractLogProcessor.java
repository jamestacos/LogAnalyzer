package Main;

import java.util.List;

public abstract class AbstractLogProcessor {
    protected AbstractLogProcessor nextProcessor;

    public abstract void processLine(String line, List<String> logs);

    public void setNextProcessor(AbstractLogProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    protected void passToNext(String line, List<String> logs) {
        if (nextProcessor != null) {
            nextProcessor.processLine(line, logs);
        }
    }
}
