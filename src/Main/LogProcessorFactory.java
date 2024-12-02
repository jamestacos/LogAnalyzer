package Main;

public class LogProcessorFactory {
    public static AbstractLogProcessor createLogProcessorChain() {
        AbstractLogProcessor apmProcessor = new APMLogProcessor();
        AbstractLogProcessor appProcessor = new ApplicationLogProcessor();
        AbstractLogProcessor requestProcessor = new RequestLogProcessor();

        // chain the processors together
        apmProcessor.setNextProcessor(appProcessor);
        appProcessor.setNextProcessor(requestProcessor);

        return apmProcessor;
    }
}
