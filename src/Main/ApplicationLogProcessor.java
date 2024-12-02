package Main;

import java.util.List;

public class ApplicationLogProcessor extends AbstractLogProcessor {
    @Override
    public void processLine(String line, List<String> logs) {
        if (line.contains("level=")) {
            // process application log entry based on log level
            logs.add(line);  // add the line
        } else {
            passToNext(line, logs);  // forward to the next processor
        }
    }
}
