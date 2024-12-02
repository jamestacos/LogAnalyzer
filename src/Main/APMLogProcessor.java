package Main;

import java.util.List;

public class APMLogProcessor extends AbstractLogProcessor {
    @Override
    public void processLine(String line, List<String> logs) {
        if (line.contains("metric")) {
            // process APM log entry
            logs.add(line);  // add the line
        } else {
            passToNext(line, logs);  // forward to the next processor
        }
    }
}
