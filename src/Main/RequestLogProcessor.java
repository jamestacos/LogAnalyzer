package Main;

import java.util.List;

public class RequestLogProcessor extends AbstractLogProcessor {
    @Override
    public void processLine(String line, List<String> logs) {
        if (line.contains("request_method") && line.contains("request_url")) {
            // process request log entries (request method and URL)
            logs.add(line);  // add the line
        } else {
            passToNext(line, logs);  // forward to the next processor
        }
    }
}
