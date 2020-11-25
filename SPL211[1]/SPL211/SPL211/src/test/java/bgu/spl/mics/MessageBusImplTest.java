package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private MessageBusImpl messageBus;
    private MicroService microService1;
    private MicroService microService2;
    private MicroService microService3;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        microService1 = new MicroService("micro1") {
            @Override
            protected void initialize() {

            }
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testSubscribeEvent() {
        messageBus.register(microService1);
        messageBus.register(microService2);
        AttackEvent attackEvent1 = new AttackEvent();
        AttackEvent attackEvent2 = new AttackEvent();
        Class<AttackEvent> eventType = AttackEvent.class;
        messageBus.subscribeEvent(eventType, microService1);
        messageBus.subscribeEvent(eventType, microService2);
        messageBus.sendEvent(attackEvent1);
        messageBus.sendEvent(attackEvent2);
       // Message message1 = messageBus.awaitMessage(microService1);
        //Message message2 = messageBus.awaitMessage(microService2);
    }

    @Test
    void testSubscribeBroadcast() {
        messageBus.register(microService1);
        messageBus.register(microService2);
        messageBus.register(microService3);
        Broadcast broadcast = new Broadcast() {};
        Class<? extends Broadcast> eventType = broadcast.getClass();
        messageBus.subscribeBroadcast(eventType, microService1);
        messageBus.subscribeBroadcast(eventType, microService2);
        messageBus.sendBroadcast(broadcast);
        Message message1 = messageBus.awaitMessage(microService1);
        Message message2 = messageBus.awaitMessage(microService2);
        Message message3 = messageBus.awaitMessage(microService3);

    }

    @Test
    void testComplete() {
        messageBus.register(microService1);
        Class<AttackEvent> eventType = AttackEvent.class;
        messageBus.subscribeEvent(eventType, microService1);
        AttackEvent attackEvent = new AttackEvent();
        Future<Boolean> future = messageBus.sendEvent(attackEvent);
        messageBus.awaitMessage(microService1);
        Boolean eventResult = true;
        messageBus.complete(attackEvent, eventResult);
        assertTrue(future.isDone());
        assertEquals(future.get(), eventResult);

    }

    @Test
    void testSendBroadcast() {

    }

    @Test
    void testSendEvent() {
    }

    @Test
    void testRegister() {
    }

    @Test
    void testUnregister() {
    }

    @Test
    void testAwaitMessage() {
    }
}