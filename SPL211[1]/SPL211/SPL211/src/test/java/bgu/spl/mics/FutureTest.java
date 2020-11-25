package bgu.spl.mics;

import org.junit.jupiter.api.*;

import java.sql.Time;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {

    private Future<String> future;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
    }

    @Test
    public void testGet()
    {
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> {
            assertFalse(future.isDone());
            String result = "";
            future.resolve(result);
            assertTrue(future.isDone());
            assertEquals(future.get(), result);
        });
    }

//    @Timeout(value = 10, unit = TimeUnit.MILLISECONDS)

    @Test
    public void testResolve(){
        String result = "someResult";
        future.resolve(result);
        assertTrue(future.isDone());
        assertEquals(result, future.get());
    }

    @Test
    public void testIsDone(){
        String result = "someResult";
        assertFalse(future.isDone());
        future.resolve(result);
        assertTrue(future.isDone());
    }

    @Test
    public void testGetWithTimeOut() throws InterruptedException
    {
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> {
            assertFalse(future.isDone());
            long startTimeInMilliSeconds = System.currentTimeMillis();
            future.get(100, TimeUnit.MILLISECONDS);
            assertTrue(System.currentTimeMillis() - startTimeInMilliSeconds >= 100);
            assertFalse(future.isDone());
            String result = "foo";
            future.resolve(result);
            assertEquals(future.get(100,TimeUnit.MILLISECONDS), result);
        });
    }
}
