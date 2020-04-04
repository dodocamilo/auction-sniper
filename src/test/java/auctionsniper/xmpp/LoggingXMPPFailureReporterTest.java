package auctionsniper.xmpp;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LoggingXMPPFailureReporterTest {

    final Logger logger = mock(Logger.class);
    final LoggingXMPPFailureReporter reporter = new LoggingXMPPFailureReporter(logger);

    @AfterAll
    static void resetLogging() {
        LogManager.getLogManager().reset();
    }

    @Test
    void writesMessageTranslationFailureToLog() {
        reporter.cannotTranslateMessage("auction id", "bad message", new Exception("an exception"));

        verify(logger).severe("<auction id> " +
                "Could not translate message \"bad message\" " +
                "because \"java.lang.Exception: an exception\"");
    }
}