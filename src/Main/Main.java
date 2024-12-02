package Main;

import com.google.gson.*;
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        // create the log processor chain
        AbstractLogProcessor processorChain = LogProcessorFactory.createLogProcessorChain();

        // array to hold logs
        List<String> logs = new ArrayList<>();

        // read from input file, cli did not work initially
        BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            // process each log line
            processorChain.processLine(line, logs);
        }
        reader.close();

        // data structures for aggregation
        Map<String, List<Integer>> apmMetrics = new HashMap<>();
        Map<String, Integer> applicationLogCounts = new HashMap<>();
        Map<String, Map<String, List<Integer>>> requestResponseTimes = new HashMap<>();
        Map<String, Map<String, Integer>> requestStatusCodes = new HashMap<>();

        // process logs for apm/application/request
        for (String log : logs) {
            // apm logs
            if (log.contains("metric") && log.contains("value")) {
                processApmLog(log, apmMetrics);
            }

            // application logs (level: ERROR, INFO, DEBUG, WARNING)
            if (log.contains("level")) {
                processApplicationLog(log, applicationLogCounts);
            }

            // request logs (API route, response times, status codes)
            if (log.contains("request_method") && log.contains("request_url")) {
                processRequestLog(log, requestResponseTimes, requestStatusCodes);
            }
        }

        // pretty printing but not working
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // apm logs aggregated
        Map<String, Map<String, Number>> apmJson = new HashMap<>();
        for (Map.Entry<String, List<Integer>> entry : apmMetrics.entrySet()) {
            String metric = entry.getKey();
            List<Integer> values = entry.getValue();
            apmJson.put(metric, calculateApmStats(values));
        }

        // application logs aggregated
        Map<String, Integer> applicationJson = new HashMap<>(applicationLogCounts);

        // request logs aggregated
        Map<String, Map<String, Object>> requestJson = new HashMap<>();
        for (Map.Entry<String, Map<String, List<Integer>>> entry : requestResponseTimes.entrySet()) {
            String route = entry.getKey();
            List<Integer> responseTimes = entry.getValue().get("response_times");

            Map<String, Object> routeData = new HashMap<>();
            routeData.put("response_times", getResponseTimeStats(responseTimes));

            Map<String, Integer> statusCodes = requestStatusCodes.get(route);
            routeData.put("status_codes", statusCodes);

            requestJson.put(route, routeData);
        }

        // write data to json files respectively
        writeJsonToFile("apm.json", apmJson, gson);
        writeJsonToFile("application.json", applicationJson, gson);
        writeJsonToFile("request.json", requestJson, gson);

        System.out.println("Log processing completed. JSON files are generated.");
    }

    // process apm log lines
    private static void processApmLog(String log, Map<String, List<Integer>> apmMetrics) {
        String[] parts = log.split(" ");
        String metric = Arrays.stream(parts)
                .filter(part -> part.startsWith("metric"))
                .findFirst()
                .orElse("")
                .split("=")[1];
        int value = Integer.parseInt(Arrays.stream(parts)
                .filter(part -> part.startsWith("value"))
                .findFirst()
                .orElse("")
                .split("=")[1]);

        apmMetrics.putIfAbsent(metric, new ArrayList<>());
        apmMetrics.get(metric).add(value);
    }

    // process application log lines
    private static void processApplicationLog(String log, Map<String, Integer> applicationLogCounts) {
        String level = Arrays.stream(log.split(" "))
                .filter(part -> part.startsWith("level"))
                .findFirst()
                .orElse("")
                .split("=")[1];

        applicationLogCounts.put(level, applicationLogCounts.getOrDefault(level, 0) + 1);
    }

    // process request log lines
    private static void processRequestLog(String log, Map<String, Map<String, List<Integer>>> requestResponseTimes,
                                          Map<String, Map<String, Integer>> requestStatusCodes) {
        String[] parts = log.split(" ");
        String url = Arrays.stream(parts)
                .filter(part -> part.startsWith("request_url"))
                .findFirst()
                .orElse("")
                .split("=")[1];
        int responseTime = Integer.parseInt(Arrays.stream(parts)
                .filter(part -> part.startsWith("response_time_ms"))
                .findFirst()
                .orElse("")
                .split("=")[1]);
        String statusCode = Arrays.stream(parts)
                .filter(part -> part.startsWith("response_status"))
                .findFirst()
                .orElse("")
                .split("=")[1];

        requestResponseTimes.putIfAbsent(url, new HashMap<>());
        requestResponseTimes.get(url).putIfAbsent("response_times", new ArrayList<>());
        requestResponseTimes.get(url).get("response_times").add(responseTime);

        String normalizedStatusCode = normalizeStatusCode(statusCode);
        requestStatusCodes.putIfAbsent(url, new HashMap<>());
        requestStatusCodes.get(url).put(normalizedStatusCode, requestStatusCodes.get(url).getOrDefault(normalizedStatusCode, 0) + 1);
    }

    // normalize status code followed by XX
    static String normalizeStatusCode(String statusCode) {
        return statusCode != null && !statusCode.isEmpty() ? statusCode.charAt(0) + "XX" : "Unknown";
    }

    // calc stats for apm logs
    private static Map<String, Number> calculateApmStats(List<Integer> values) {
        Collections.sort(values);
        Map<String, Number> stats = new HashMap<>();
        stats.put("minimum", values.get(0));
        stats.put("median", calculateMedian(values));
        stats.put("average", calculateAverage(values));
        stats.put("max", values.get(values.size() - 1));
        return stats;
    }

    // calc median
    static double calculateMedian(List<Integer> values) {
        int size = values.size();
        if (size % 2 == 0) {
            return (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0;
        } else {
            return values.get(size / 2);
        }
    }

    // calc avg
    static double calculateAverage(List<Integer> values) {
        return values.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    // get response time stats
    static Map<String, Integer> getResponseTimeStats(List<Integer> responseTimes) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("min", responseTimes.get(0));
        stats.put("max", responseTimes.get(responseTimes.size() - 1));
        stats.put("50_percentile", calculatePercentile(responseTimes, 50));
        stats.put("90_percentile", calculatePercentile(responseTimes, 90));
        stats.put("95_percentile", calculatePercentile(responseTimes, 95));
        stats.put("99_percentile", calculatePercentile(responseTimes, 99));
        return stats;
    }

    // calc percentile
    private static int calculatePercentile(List<Integer> values, int percentile) {
        int index = (int) Math.ceil(percentile / 100.0 * values.size()) - 1;
        return values.get(index);
    }

    // write json data to file
    private static void writeJsonToFile(String filename, Object data, Gson gson) {
        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
