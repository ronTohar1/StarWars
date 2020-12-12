package bgu.spl.mics.application.passiveObjects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {

    private Ewok availableEwok;
    private Ewok unavailableEwok;

    @BeforeEach
    void setUp() {
        availableEwok = new Ewok(1, true);
        unavailableEwok = new Ewok(2, false);
    }

    @Test
    void testAcquire() throws InterruptedException {
        //Added 'throws InterruptedException' to the method signature because we decided to make the acquire method blocking.

        // test acquire available Ewok:
        availableEwok.acquire();
        assertFalse(availableEwok.isAvailable());

        // test acquire unavailable Ewok:
        //Removed this test becuase we decided to make the acquire method blocking.
    }

    @Test
    void testRelease() {
        // test release unavailable Ewok:
        unavailableEwok.release();
        assertTrue(unavailableEwok.isAvailable());
        // test release available Ewok:
        //Removed this test because we decided that if the Ewok is available, the release method will do nothing instead of throwing an exception.
    }
}