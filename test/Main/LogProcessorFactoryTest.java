package Main;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogProcessorFactoryTest {

    @Test
    public void testCreateLogProcessorChain() {
        AbstractLogProcessor chain = LogProcessorFactory.createLogProcessorChain();

        assertNotNull(chain);
        assertTrue(chain instanceof APMLogProcessor);

        AbstractLogProcessor secondProcessor = chain.nextProcessor;
        assertNotNull(secondProcessor);
        assertTrue(secondProcessor instanceof ApplicationLogProcessor);

        AbstractLogProcessor thirdProcessor = secondProcessor.nextProcessor;
        assertNotNull(thirdProcessor);
        assertTrue(thirdProcessor instanceof RequestLogProcessor);
    }
}
