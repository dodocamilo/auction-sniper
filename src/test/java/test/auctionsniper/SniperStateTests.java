package test.auctionsniper;

import auctionsniper.Defect;
import auctionsniper.SniperState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SniperStateTests {

    @Test public void
    isWonWhenAuctionClosesWhileWinning() {
        assertEquals(SniperState.LOST, SniperState.JOINING.whenAuctionClosed());
        assertEquals(SniperState.LOST, SniperState.BIDDING.whenAuctionClosed());
        assertEquals(SniperState.WON, SniperState.WINNING.whenAuctionClosed());
    }

    @Test
    public void
    defectIfAuctionClosesWhenWon() {
        assertThrows(Defect.class, SniperState.WON::whenAuctionClosed);
    }

    @Test
    public void
    defectIfAuctionClosesWhenLost() {
        assertThrows(Defect.class, SniperState.LOST::whenAuctionClosed);
    }
}
