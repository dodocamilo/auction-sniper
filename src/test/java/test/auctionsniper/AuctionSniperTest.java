package test.auctionsniper;

import auctionsniper.*;
import auctionsniper.AuctionEventListener.PriceSource;
import org.junit.jupiter.api.Test;

import static auctionsniper.SniperState.BIDDING;
import static auctionsniper.SniperState.WINNING;
import static org.mockito.Mockito.*;

public class AuctionSniperTest {
    private static final String ITEM_ID = "item-id";

    private SniperListener sniperListener = mock(SniperListener.class);
    private Auction auction = mock(Auction.class);
    private AuctionSniper sniper = new AuctionSniper(ITEM_ID, auction, sniperListener);

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
        verify(sniperListener).sniperStateChanged(any(SniperSnapshot.class));
    }

    @Test
    void reportsWonIfAuctionClosesWhenWinning() {
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.auctionClosed();

        verify(sniperListener).sniperWon();
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        int price = 1001;
        int increment = 25;
        int bid = price + increment;

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);

        verify(auction).bid(bid);
        verify(sniperListener, times(1)).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, BIDDING));
    }

    @Test
    void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
        sniper.currentPrice(135, 45, PriceSource.FromSniper);

        verify(sniperListener, times(1))
                .sniperStateChanged(new SniperSnapshot(ITEM_ID, 135, 135, WINNING));
    }
}
