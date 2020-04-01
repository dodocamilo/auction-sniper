package auctionsniper;

import auctionsniper.AuctionEventListener.PriceSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static auctionsniper.SniperState.*;
import static org.mockito.Mockito.*;

public class AuctionSniperTest {
    private static final String ITEM_ID = "item-id";
    private static final Item ITEM = new Item(ITEM_ID, 1234);

    private SniperListener sniperListener = mock(SniperListener.class);
    private Auction auction = mock(Auction.class);
    private AuctionSniper sniper = new AuctionSniper(ITEM, auction);

    @BeforeEach
    void attachListener() {
        sniper.addSniperListener(sniperListener);
    }

    @Test
    void reportsLostIfAuctionClosesImmediately() {
        sniper.auctionClosed();

        verify(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, LOST));
    }

    @Test
    void reportsLostIfAuctionClosesWhenBidding() {
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();

        verify(sniperListener, atLeast(1)).sniperStateChanged(new SniperSnapshot(ITEM_ID, 123, 123 + 45, LOST));
    }

    @Test
    void reportsWonIfAuctionClosesWhenWinning() {
        sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
        sniper.currentPrice(135, 45, PriceSource.FromSniper);
        sniper.auctionClosed();

        verify(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 135, 135, WON));
    }

    @Test public void doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice() {
        final int price = 1233;
        final int increment = 25;

        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);

        verify(sniperListener, atLeast(1)).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, 0, LOSING));
    }

    @Test public void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
        int bid = 123 + 45;

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);

        verify(sniperListener, atLeast(1)).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, bid, LOSING));
    }

    @Test public void doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() {
        final int price = 1233;
        final int increment = 25;
        int bid = 123 + 45;

        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(168, 45, PriceSource.FromSniper);
        sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);

        verify(sniperListener, atLeast(1)).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, LOSING));
    }

    @Test
    void continuesToBeLosingOnceStopPriceHasBeenReached() {
        final int price1 = 1233;
        final int price2 = 1258;

        sniper.currentPrice(price1, 25, PriceSource.FromOtherBidder);
        sniper.currentPrice(price2, 25, PriceSource.FromOtherBidder);

        verify(sniperListener, atLeast(1)).sniperStateChanged(new SniperSnapshot(ITEM_ID, price1, 0, LOSING));
        verify(sniperListener, atLeast(1)).sniperStateChanged(new SniperSnapshot(ITEM_ID, price2, 0, LOSING));
    }

    @Test public void reportsLostIfAuctionClosesWhenLosing() {
        sniper.currentPrice(1230, 456, PriceSource.FromOtherBidder);
        sniper.auctionClosed();

        verify(sniperListener, atLeast(1)).sniperStateChanged(new SniperSnapshot(ITEM_ID, 1230, 0, LOST));
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
