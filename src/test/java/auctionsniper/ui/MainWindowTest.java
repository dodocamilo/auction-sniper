package auctionsniper.ui;

import auctionsniper.UserRequestListener;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import endtoend.auctionsniper.AuctionSniperDriver;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;

class MainWindowTest {
    private final SnipersTableModel tableModel = new SnipersTableModel();
    private final MainWindow mainWindow = new MainWindow(tableModel);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(200);

    @Test
    void makesUserRequestWhenJoinButtonClicked() {
        ValueMatcherProbe<String> buttonProbe =
                new ValueMatcherProbe<>(equalTo("an item-id"), "join request");

        mainWindow.addUserRequestListener(
            new UserRequestListener() {
                public void joinAuction(String itemId) {
                    buttonProbe.setReceivedValue(itemId);
                }
            }
        );

        driver.startBiddingFor("aan item-id");
        driver.check(buttonProbe);
    }
}