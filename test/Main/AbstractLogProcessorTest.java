package Main;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AbstractLogProcessorTest {

    @Test
    public void testAPMLogProcessor() {
        AbstractLogProcessor apmProcessor = new APMLogProcessor();
        List<String> logs = new ArrayList<>();

        String logLine = "metric=cpu_usage value=70";
        apmProcessor.processLine(logLine, logs);

        assertEquals(1, logs.size());
        assertTrue(logs.contains(logLine));
    }

    @Test
    public void testApplicationLogProcessor() {
        AbstractLogProcessor appProcessor = new ApplicationLogProcessor();
        List<String> logs = new ArrayList<>();

        String logLine = "level=ERROR message=Something went wrong";
        appProcessor.processLine(logLine, logs);

        assertEquals(1, logs.size());
        assertTrue(logs.contains(logLine));
    }

    @Test
    public void testRequestLogProcessor() {
        AbstractLogProcessor requestProcessor = new RequestLogProcessor();
        List<String> logs = new ArrayList<>();

        String logLine = "request_method=GET request_url=/api/users response_time_ms=120 response_status=200";
        requestProcessor.processLine(logLine, logs);

        assertEquals(1, logs.size());
        assertTrue(logs.contains(logLine));
    }

    @Test
    public void testProcessorChain() {
        AbstractLogProcessor apmProcessor = new APMLogProcessor();
        AbstractLogProcessor appProcessor = new ApplicationLogProcessor();
        AbstractLogProcessor requestProcessor = new RequestLogProcessor();

        apmProcessor.setNextProcessor(appProcessor);
        appProcessor.setNextProcessor(requestProcessor);

        List<String> logs = new ArrayList<>();

        String apmLog = "metric=cpu_usage value=70";
        String appLog = "level=INFO message=System initialized";
        String requestLog = "request_method=POST request_url=/api/login response_time_ms=90 response_status=201";

        apmProcessor.processLine(apmLog, logs);
        apmProcessor.processLine(appLog, logs);
        apmProcessor.processLine(requestLog, logs);

        assertEquals(3, logs.size());
        assertTrue(logs.contains(apmLog));
        assertTrue(logs.contains(appLog));
        assertTrue(logs.contains(requestLog));
    }
}
