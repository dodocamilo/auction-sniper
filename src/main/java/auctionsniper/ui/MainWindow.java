package auctionsniper.ui;

import auctionsniper.Column;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

public class MainWindow extends JFrame {

    private static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_BIDDING = "Bidding";
    public static final String STATUS_WINNING = "Winning";
    public static final String STATUS_WON = "Won";
    private static final String SNIPERS_TABLE_NAME = "Snipers Table";

    private final SnipersTableModel snipers = new SnipersTableModel();

    public MainWindow() {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable());
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void showStatusText(String statusText) {
        snipers.setStatusText(statusText);
    }

    private void fillContentPane(JTable snipersTable) {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable() {
        JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    public void sniperStateChanged(SniperSnapshot snapshot) {
        snipers.sniperStatusChanged(snapshot);
    }

    public static class SnipersTableModel extends AbstractTableModel {
        private static final SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
        private static final String[] STATUS_TEXT  = {
            MainWindow.STATUS_JOINING,
            MainWindow.STATUS_BIDDING,
            MainWindow.STATUS_WINNING
        };
        private String state = STATUS_JOINING;
        private SniperSnapshot snapshot = STARTING_UP;

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount() {
            return Column.values().length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (Column.at(columnIndex)) {
                case ITEM_IDENTIFIER:
                    return snapshot.itemId;
                case LAST_PRICE:
                    return snapshot.lastPrice;
                case LAST_BID:
                    return snapshot.lastBid;
                case SNIPER_STATE:
                    return state;
                default:
                    throw new IllegalArgumentException("No column at " + columnIndex);
            }
        }

        public void setStatusText(String newStatusText) {
            this.state = newStatusText;
            fireTableRowsUpdated(0, 0);
        }

        public void sniperStatusChanged(SniperSnapshot newSniperSnapshot) {
            this.snapshot = newSniperSnapshot;
            this.state = STATUS_TEXT[newSniperSnapshot.state.ordinal()];
            fireTableRowsUpdated(0, 0);
        }
    }
}
