package bgu.spl.mics;

import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;



import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {

    private Future<String> future;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
    }

    /**
     * Testing get() function
     * resolving the current future object and expecting the result
     * of the get() function to be the object we sent as a parameter
     * on the resolve method.
     * checking that the future isDone() as well. (resolved, finished)
     */
    @Test
    public void testGet() throws InterruptedException {
        //Added an exception to the get() function, so this test had changed as well, and it now throws an exception.
        assertFalse(future.isDone());
        String result = "";
        future.resolve(result);
        assertTrue(future.isDone());
        assertTrue(result.equals(future.get()));
    }


    /**
     * Testing Resolve() function .
     * checking that the result object which was sent to the resolve
     * method is the current result of the future object.
     * Checking the object isDone().
     */
    @Test
    public void testResolve() throws InterruptedException {
        //Added an exception to the get() function, so this test had changed as well, and it now throws an exception.
        String result = "someResult";
        future.resolve(result);
        assertTrue(future.isDone());
        assertTrue(result.equals(future.get()));
    }

    /**
     * Testing isDone() function.
     * Checking that before resolving the future object ,
     * isDone() will return false, and after resolving
     * the future object , isDone() will return true.
     */
    @Test
    public void testIsDone(){
        String result = "someResult";
        assertFalse(future.isDone());
        future.resolve(result);
        assertTrue(future.isDone());
    }

    /**
     * Testing getWithTimeOut() function.
     * Checking that this function doesn't block even if the future
     * object is not resolved.
     * Checking that the amount of time that passed while executing the
     * function is the amount of time that was passed as a parameter.
     * Checking that after resolving the future object, the method returns
     * the correct result and the future object is resolved (isDone()).
     * @throws InterruptedException
     */
    @Test
    public void testGetWithTimeOut() throws InterruptedException {

        assertFalse(future.isDone());
        long startTimeInMilliSeconds = System.currentTimeMillis();
        future.get(100, TimeUnit.MILLISECONDS);
        assertTrue(System.currentTimeMillis() - startTimeInMilliSeconds >= 100);//Checking the right amount of time has passed.
        assertFalse(future.isDone());
        String result = "foo";
        future.resolve(result);
        assertTrue(result.equals(future.get(100, TimeUnit.MILLISECONDS)));
    }
}
