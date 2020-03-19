package auctionsniper.ui;

import auctionsniper.Column;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;

public class MainWindow extends JFrame {

    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    private static final String SNIPERS_TABLE_NAME = "Snipers Table";

    private final SnipersTableModel snipers;

    public MainWindow(SnipersTableModel snipers) {
        super(APPLICATION_TITLE);
        this.snipers = snipers;
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable());
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
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

    public static class SnipersTableModel extends AbstractTableModel implements SniperListener {
        private static final SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
        private static final String[] STATUS_TEXT  = {
            "Joining", "Bidding", "Winning", "Lost", "Won"
        };
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
            return Column.at(columnIndex).valueIn(snapshot);
        }

        @Override
        public String getColumnName(int column) {
            return Column.at(column).name;
        }

        @Override
        public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
            this.snapshot = newSniperSnapshot;
            fireTableRowsUpdated(0, 0);
        }

        public static String textFor(SniperState state) {
            return STATUS_TEXT[state.ordinal()];
        }
    }
}
