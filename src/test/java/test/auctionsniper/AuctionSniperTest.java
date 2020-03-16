package test.auctionsniper;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class AuctionSniperTest {
    private SniperListener sniperListener = mock(SniperListener.class);
    private Auction auction = mock(Auction.class);
    private AuctionSniper sniper = new AuctionSniper(auction, sniperListener);

    @Test
    void reportsLostIfAuctionClosesImmediately() {
        sniper.auctionClosed();

        verify(sniperListener, times(1)).sniperLost();
    }

    @Test
    void reportsLostIfAuctionClosesWhenBidding() {
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();

        verify(sniperListener).sniperLost();
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();

        verify(sniperListener).sniperWon();
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        int price = 1001;
        int increment = 25;

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);

        verify(auction).bid(price + increment);
        verify(sniperListener, times(1)).sniperBidding();
    }

    @Test
    void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 45, PriceSource.FromSniper);

        verify(sniperListener, times(1)).sniperWinning();
    }
}
