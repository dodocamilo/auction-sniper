package auctionsniper.xmpp;

import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionEventListener.PriceSource;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuctionMessageTranslatorTest {
    private static final String SNIPER_ID = "sniper id";
    private static final Chat UNUSED_CHAT = null;
    private AuctionEventListener listener = mock(AuctionEventListener.class);
    private XMPPFailureReporter failureReporter = mock(XMPPFailureReporter.class);
    private AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener, failureReporter);

    @Test
    void notifiesAuctionClosedWhenCloseMessageReceived() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE:");

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener).auctionClosed();
    }

    @Test
    void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener, times(1)).currentPrice(192, 7, PriceSource.FromOtherBidder);
    }

    @Test
    void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";");

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener, times(1)).currentPrice(234, 5, PriceSource.FromSniper);
    }

    @Test
    void notifiesAuctionFailedWhenBadMessageReceived() {
        String badMessage = "a bad message";
        translator.processMessage(UNUSED_CHAT, message(badMessage));
        expectFailureWithMessage(badMessage);
    }

    @Test
    void notifiesAuctionFailedWhenEventTypeMissing() {
        String badMessage = "SOLVersion: 1.1; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";";
        translator.processMessage(UNUSED_CHAT, message(badMessage));
        expectFailureWithMessage(badMessage);
    }

    private Message message(String body) {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";");
        message.setBody(body);
        return message;
    }

    private void expectFailureWithMessage(String badMessage) {
        verify(listener, times(1)).auctionFailed();
        verify(failureReporter, times(1)).cannotTranslateMessage(any(), any(), any());
    }
}
