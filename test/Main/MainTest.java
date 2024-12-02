package Main;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private List<String> logs;

    @BeforeEach
    public void setUp() {
        logs = Arrays.asList(
                "metric=cpu_usage value=70",
                "level=ERROR message=Something went wrong",
                "request_method=GET request_url=/api/users response_time_ms=120 response_status=200"
        );
    }

    @Test
    public void testCalculateMedian() {
        List<Integer> values = Arrays.asList(10, 20, 30, 40, 50);
        assertEquals(30, Main.calculateMedian(values));
    }

    @Test
    public void testCalculateAverage() {
        List<Integer> values = Arrays.asList(10, 20, 30, 40, 50);
        assertEquals(30.0, Main.calculateAverage(values), 0.001);
    }

    @Test
    public void testNormalizeStatusCode() {
        assertEquals("2XX", Main.normalizeStatusCode("200"));
        assertEquals("5XX", Main.normalizeStatusCode("500"));
        assertEquals("Unknown", Main.normalizeStatusCode(null));
    }

}
