package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private MessageBusImpl messageBus;
    private MicroService microService1;
    private MicroService microService2;
    private MicroService microService3;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        microService1 = createAnonymousMicroService("One");
        microService1 = createAnonymousMicroService("Two");
        microService1 = createAnonymousMicroService("Three");
    }

    @AfterEach
    void tearDown() {
        unregisterMicroService(microService1);
        unregisterMicroService(microService2);
        unregisterMicroService(microService3);
    }

    private static MicroService createAnonymousMicroService(String name){
        return new MicroService(name) {
            @Override
            protected void initialize() {

            }
        };
    }

    private void unregisterMicroService(MicroService microService){
        // unregister if not already unregistered:
        try{
            messageBus.unregister(microService);
        }
        catch (Exception exception){

        }
    }

    @Test
    void testSubscribeEvent() throws InterruptedException {
        messageBus.register(microService1);
        messageBus.register(microService2);
        AttackEvent attackEvent1 = new AttackEvent();
        AttackEvent attackEvent2 = new AttackEvent();
        Class<AttackEvent> eventType = AttackEvent.class;
        messageBus.subscribeEvent(eventType, microService1);
        messageBus.subscribeEvent(eventType, microService2);
        messageBus.sendEvent(attackEvent1);
        messageBus.sendEvent(attackEvent2);
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> {
            Message message1 = messageBus.awaitMessage(microService1);
            Message message2 = messageBus.awaitMessage(microService2);
            assertEquals(message1.getClass(), eventType);
            assertEquals(message2.getClass(), eventType);
        });
    }

    @Test
    void testSubscribeBroadcast() throws InterruptedException {
        messageBus.register(microService1);
        messageBus.register(microService2);
        messageBus.register(microService3);
        Broadcast broadcast = new Broadcast() {};
        Class<? extends Broadcast> eventType = broadcast.getClass();
        messageBus.subscribeBroadcast(eventType, microService1);
        messageBus.subscribeBroadcast(eventType, microService2);
        messageBus.sendBroadcast(broadcast);
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> {
            Message message1 = messageBus.awaitMessage(microService1);
            Message message2 = messageBus.awaitMessage(microService2);
            assertEquals(message1.getClass(), eventType);
            assertEquals(message2.getClass(), eventType);
        });
        try{
            assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> {
                messageBus.awaitMessage(microService3);
            });
            fail();
        }
        catch (AssertionError e){

        }
    }

    @Test
    void testComplete() throws InterruptedException {
        messageBus.register(microService1);
        Class<AttackEvent> eventType = AttackEvent.class;
        messageBus.subscribeEvent(eventType, microService1);
        AttackEvent attackEvent = new AttackEvent();
        Future<Boolean> future = messageBus.sendEvent(attackEvent);
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> {
            messageBus.awaitMessage(microService1);
        });
        Boolean eventResult = true;
        messageBus.complete(attackEvent, eventResult);
        assertTrue(future.isDone());
        assertEquals(future.get(), eventResult);
    }

    @Test
    void testSendBroadcast() {
        messageBus.register(microService1); // registers to the Broadcast
        messageBus.register(microService2); // registers to the Broadcast
        messageBus.register(microService3); // doesn't register to the Broadcast
        Broadcast broadcast = new Broadcast() {};
        Class<? extends Broadcast> eventType = broadcast.getClass();
        messageBus.subscribeBroadcast(eventType, microService1);
        messageBus.subscribeBroadcast(eventType, microService2);
        messageBus.sendBroadcast(broadcast);
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> {
            Message message1 = messageBus.awaitMessage(microService1);
            Message message2 = messageBus.awaitMessage(microService2);
            assertTrue(broadcast.equals(message1));
            assertTrue(broadcast.equals(message2));
        });
        try{
            assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> {
                messageBus.awaitMessage(microService3);
            });
            fail();
        }
        catch (AssertionError e){

        }
    }

    @Test
    void testSendEvent() {
        messageBus.register(microService1);
        messageBus.register(microService2);
        AttackEvent attackEvent1 = new AttackEvent();
        AttackEvent attackEvent2 = new AttackEvent();
        Class<AttackEvent> eventType = AttackEvent.class;
        messageBus.subscribeEvent(eventType, microService1);
        messageBus.subscribeEvent(eventType, microService2);
        messageBus.sendEvent(attackEvent1);
        messageBus.sendEvent(attackEvent2);
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> {
            Message message1 = messageBus.awaitMessage(microService1);
            Message message2 = messageBus.awaitMessage(microService2);
            assertTrue(attackEvent1.equals(message1));
            assertTrue(attackEvent2.equals(message2));
        });
    }

    @Test
    void testRegister() {
        messageBus.register(microService1);
        AttackEvent attackEvent = new AttackEvent();
        Class<AttackEvent> eventType = AttackEvent.class;
        messageBus.subscribeEvent(eventType, microService1);
        messageBus.sendEvent(attackEvent);
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> {
            messageBus.awaitMessage(microService1);
        });
    }

    @Test
    void testUnregister() {
        try{
            messageBus.unregister(microService1);
            fail();
        }
        catch (Exception exception){
        }
        messageBus.register(microService1);
        messageBus.unregister(microService1);
        try{
            AttackEvent attackEvent = new AttackEvent();
            Class<AttackEvent> eventType = AttackEvent.class;
            messageBus.subscribeEvent(eventType, microService1);
            messageBus.sendEvent(attackEvent);
            assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> {
                messageBus.awaitMessage(microService1);
            });
            fail();
        }
        catch (AssertionError assertionError){
        }
    }

    @Test
    void testAwaitMessage() {
        messageBus.register(microService1);
        try{
            assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> {
                messageBus.awaitMessage(microService1);
            });
            fail();
        }
        catch (AssertionError e){

        }
        AttackEvent attackEvent = new AttackEvent();
        Class<AttackEvent> eventType = AttackEvent.class;
        messageBus.subscribeEvent(eventType, microService1);
        messageBus.sendEvent(attackEvent);
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> {
            Message message = messageBus.awaitMessage(microService1);
            assertTrue(attackEvent.equals(message));
        });
    }
}
