package auctionsniper.ui;

import auctionsniper.*;
import auctionsniper.util.Defect;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, PortfolioListener {
    private static final String[] STATUS_TEXT  = {
        "Joining", "Bidding", "Winning", "Lost", "Won"
    };
    private List<SniperSnapshot> snapshots = new ArrayList<>();

    @Override
    public int getRowCount() {
        return snapshots.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
        int row = rowMathing(newSniperSnapshot);
        snapshots.set(row, newSniperSnapshot);
        fireTableRowsUpdated(row, row);
    }

    private int rowMathing(SniperSnapshot snapshot) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (snapshot.isForSameItemAs(snapshots.get(i))) {
                return i;
            }
        }
        throw new Defect("Cannot find match for " + snapshot);
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    @Override
    public void sniperAdded(AuctionSniper sniper) {
        addSniperSnapshot(sniper.getSnapshot());
        sniper.addSniperListener(new SwingThreadSniperListener(this));
    }

    private void addSniperSnapshot(SniperSnapshot sniperSnapshot) {
        snapshots.add(sniperSnapshot);
        int row = snapshots.size() - 1;
        fireTableRowsUpdated(row, row);
    }
}
