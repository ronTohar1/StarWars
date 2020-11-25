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

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        microService1 = new MicroService() {
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
        Class<AttackEvent> eventType = AttackEvent.class;
        AttackEvent attackEvent1 = new AttackEvent();
        AttackEvent attackEvent2 = new AttackEvent();
        messageBus.subscribeEvent(eventType, microService1);
        messageBus.subscribeEvent(eventType, microService2);
        messageBus.sendEvent(attackEvent1);
        messageBus.sendEvent(attackEvent2);
        Message message1 = messageBus.awaitMessage(microService1);
        Message message2 = messageBus.awaitMessage(microService2);
    }

    @Test
    void testSubscribeBroadcast() {
    }

    @Test
    void testComplete() {
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