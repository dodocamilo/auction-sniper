package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;

import javax.swing.*;

public class SwingThreadSniperListener implements SniperListener {
    private final SniperListener delegate;

    public SwingThreadSniperListener(SniperListener delegate) {
        this.delegate = delegate;
    }

    public void sniperStateChanged(SniperSnapshot snapshot) {
        SwingUtilities.invokeLater(() -> delegate.sniperStateChanged(snapshot));
    }
}
