package auctionsniper;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.*;

class SniperLauncherTest {

    private final Auction auction = mock(Auction.class);
    private final AuctionHouse auctionHouse = mock(AuctionHouse.class);
    private final SniperCollector sniperCollector = mock(SniperCollector.class);
    private final SniperLauncher launcher = new SniperLauncher(auctionHouse, sniperCollector);

    @Test
    void addsNewSniperToCollectorAndThenJoinsAuction() {
        String itemId = "item 123";
        when(auctionHouse.auctionFor(itemId)).thenReturn(auction);

        launcher.joinAuction(itemId);

        verify(auction).addAuctionEventListener(any());
        verify(sniperCollector).addSniper(any());
    }

    protected Matcher<AuctionSniper> sniperForItem(String itemId) {
        return new FeatureMatcher<AuctionSniper, String>(equalTo(itemId), "sniper with item id", "item") {
            @Override protected String featureValueOf(AuctionSniper actual) {
                return actual.getSnapshot().itemId;
            }
        };
    }
}