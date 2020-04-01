package auctionsniper.ui;

import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import endtoend.auctionsniper.AuctionSniperDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;

class MainWindowTest {
    private final MainWindow mainWindow = new MainWindow(new SniperPortfolio());
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    void makesUserRequestWhenJoinButtonClicked() {
        ValueMatcherProbe<Item> itemProbe =
                new ValueMatcherProbe<>(equalTo(new Item("an item-id", 789)), "item request");

        mainWindow.addUserRequestListener(
            new UserRequestListener() {
                public void joinAuction(Item item) {
                    itemProbe.setReceivedValue(item);
                }
            }
        );

        driver.startBiddingFor("an item-id", 789);
        driver.check(itemProbe);
    }

    @AfterEach
    void disposeDriver() {
        driver.dispose();
    }
}