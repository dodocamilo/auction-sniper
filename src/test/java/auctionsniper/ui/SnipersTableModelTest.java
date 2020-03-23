package auctionsniper.ui;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.util.Defect;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class SnipersTableModelTest {
    private static final String ITEM_ID = "item 0";
    private TableModelListener listener = mock(TableModelListener.class);
    private SnipersTableModel model = new SnipersTableModel();
    private final AuctionSniper sniper = new AuctionSniper(ITEM_ID, null);

    @BeforeEach
    void attachModelListener() {
        model.addTableModelListener(listener);
    }

    @Test
    void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    void setsSniperValuesInColumns() {
        SniperSnapshot bidding = sniper.getSnapshot().bidding(555, 666);

        model.sniperAdded(sniper);
        model.sniperStateChanged(bidding);
        assertRowMatchesSnapshot(0, bidding);
    }

    @Test
    public void setsUpColumnHeadings() {
        for (Column column : Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    @Test
    void notifiesListenersWhenAddingASniper() {
        assertEquals(0, model.getRowCount());

        model.sniperAdded(sniper);

        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(0, SniperSnapshot.joining(ITEM_ID));
    }

    @Test
    void holdsSnipersInAdditionOrder() {
        AuctionSniper sniper2 = new AuctionSniper("item 1", null);

        model.sniperAdded(sniper);
        model.sniperAdded(sniper2);

        assertEquals(ITEM_ID, cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
    }

    @Test
    void updatesCorrectRowForSniper() {
        AuctionSniper sniper2 = new AuctionSniper("item 1", null);

        model.sniperAdded(sniper);
        model.sniperAdded(sniper2);

        SniperSnapshot winning1 = sniper2.getSnapshot().winning(123);
        model.sniperStateChanged(winning1);

        assertRowMatchesSnapshot(1, winning1);
    }

    @Test
    void throwsDefectIfNoExistingSniperForAnUpdate() {
        assertThrows(Defect.class, () ->
            model.sniperStateChanged(new SniperSnapshot("item 1", 123, 234, SniperState.WINNING)));
    }

    private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
        assertEquals(snapshot.itemId, cellValue(row, Column.ITEM_IDENTIFIER));
        assertEquals(snapshot.lastPrice, cellValue(row, Column.LAST_PRICE));
        assertEquals(snapshot.lastBid, cellValue(row, Column.LAST_BID));
        assertEquals(SnipersTableModel.textFor(snapshot.state), cellValue(row, Column.SNIPER_STATE));
    }

    private Object cellValue(int rowIndex, Column column) {
        return model.getValueAt(rowIndex, column.ordinal());
    }

    private void assertColumnEquals(Column column, Object expected) {
        int rowIndex = 0;
        int columnIndex = column.ordinal();
        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }

    private Matcher<TableModelEvent> aRowChangedEvent() {
        return Matchers.samePropertyValuesAs(new TableModelEvent(model, 0));
    }
}
