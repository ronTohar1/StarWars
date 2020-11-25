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
    void testAcquire() {
        // test acquire available Ewok:
        availableEwok.acquire();
        assertFalse(availableEwok.isAvailable());
        // test acquire unavailable Ewok:
        try{
            unavailableEwok.acquire();
            fail();
        }
        catch (Exception e){
            assertFalse(unavailableEwok.isAvailable());
        }
    }

    @Test
    void testRelease() {
        // test release unavailable Ewok:
        unavailableEwok.release();
        assertTrue(unavailableEwok.isAvailable());
        // test release available Ewok:
        try{
            availableEwok.release();
            fail();
        }
        catch (Exception e){
            assertTrue(availableEwok.isAvailable());
        }
    }
}