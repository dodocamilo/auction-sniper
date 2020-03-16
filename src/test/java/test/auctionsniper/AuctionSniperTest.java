package test.auctionsniper;

import auctionsniper.Auction;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class AuctionSniperTest {
    private SniperListener sniperListener = mock(SniperListener.class);
    private Auction auction = mock(Auction.class);
    private AuctionSniper sniper = new AuctionSniper(auction, sniperListener);

    @Test
    void reportsLostWhenAuctionCloses() {
        sniper.auctionClosed();

        verify(sniperListener).sniperLost();
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        int price = 1001;
        int increment = 25;

        sniper.currentPrice(price, increment);

        verify(auction).bid(price + increment);
        verify(sniperListener, times(1)).sniperBidding();
    }
}
