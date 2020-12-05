package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    // the register method of MessageBusImpl won't be tested alone, because there is no way to test it according
    // to the assignments instructions. But it will be used in the other tests

    private MessageBusImpl messageBus;
    // more fields to help test the MessageBusImpl:
    private MicroService microService1;
    private MicroService microService2;
    private final AttackEvent attackEvent = new AttackEvent(null);
    private final Class<AttackEvent> eventType = AttackEvent.class;
    private final Broadcast broadcast = new Broadcast() {};
    private final Class<? extends Broadcast> broadcastType = broadcast.getClass();

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        microService1 = createAnonymousMicroService("One");
        microService2 = createAnonymousMicroService("Two");
    }

    @AfterEach
    void tearDown() {
        // this tear down is required because the instance of MessageBusImpl doesn't change (because it's a singleton)
        unregisterMicroService(microService1);
        unregisterMicroService(microService2);
    }

    private static MicroService createAnonymousMicroService(String name){
        // no method needs to be implemented because no method of the microservice will be called during the tests. It
        // will be used only by passing it as a parameter
        return new MicroService(name) {
            @Override
            protected void initialize() {

            }
        };
    }

    private void unregisterMicroService(MicroService microService){
        // unregister if registered (assuming that the unregister method will work iff the microService is registered,
        // and otherwise will throw an exception)
        try{
            messageBus.unregister(microService);
        }
        catch (Exception exception){
            // doing nothing
        }
    }

    @Test
    void testSubscribeEvent() throws InterruptedException {
        Message message = registerSubscribeToEventSendEventAndAwaitMessageReturnsMessage();
        // checking that received the message from the type the microservice subscribed to:
        assertEquals(message.getClass(), eventType);
    }

    @Test
    void testSubscribeBroadcast() throws InterruptedException {
        Message[] messages = registerSubscribeToBroadCastSendBroadcastAndAwaitMessage();
        // checking that the received messages are from the type the microservices subscribed to:
        assertEquals(messages[0].getClass(), broadcastType);
        assertEquals(messages[1].getClass(), broadcastType);
    }

    @Test
    void testComplete() throws InterruptedException {
        Future<Boolean> future = registerSubscribeToEventSendEventAndAwaitMessageReturnsFuture();
        // awaiting message because if not, the complete method of MessageBusImpl will be called on a mission before it
        // is fetched. That situation can't normally happen, and the MessageBusImpl might throw an exception
        Boolean eventResult = true;
        messageBus.complete(attackEvent, eventResult);
        // checking that the future object is resolved, and resolved with the right result:
        assertTrue(future.isDone());
        assertEquals(eventResult, future.get());
    }

    @Test
    void testSendBroadcast() throws InterruptedException {
        Message[] messages = registerSubscribeToBroadCastSendBroadcastAndAwaitMessage();
        // checking that the received messages are equal to the broadcast that has been sent:
        assertTrue(broadcast.equals(messages[0]));
        assertTrue(broadcast.equals(messages[1]));
    }

    @Test

    void testSendEventAndAwaitMessage() throws InterruptedException {
        // This test checks the sendEvent and awaitMessage methods, because they can't each be tested separately
        Message message = registerSubscribeToEventSendEventAndAwaitMessageReturnsMessage();
        // checking that the received message is equal to the event that has been sent:
        assertTrue(attackEvent.equals(message));
    }

    /**
     * a private method that registers microService1, subscribes it to AttackEvent events, sends the attackEvent,
     * awaits for the message as microservice1 and reruns the message
     * @return the message received after awaiting for a message for microservice1
     */
    private Message registerSubscribeToEventSendEventAndAwaitMessageReturnsMessage() throws InterruptedException {
        messageBus.register(microService1);
        messageBus.subscribeEvent(eventType, microService1);
        messageBus.sendEvent(attackEvent);
        return messageBus.awaitMessage(microService1);
    }

    /**
     * a private method that registers microService1, subscribes it to AttackEvent events, sends the attackEvent,
     * awaits for the message as microservice1 and returns future object received when sent the attack event
     * @return the Future<Boolean> object received after sending the attack event
     */
    private Future<Boolean> registerSubscribeToEventSendEventAndAwaitMessageReturnsFuture()
            throws InterruptedException {
        messageBus.register(microService1);
        messageBus.subscribeEvent(eventType, microService1);
        Future<Boolean> futureObject = messageBus.sendEvent(attackEvent);
        messageBus.awaitMessage(microService1);
        return futureObject;
    }

    /**
     * a private method that registers microService1 and microservice2, subscribes them to the broadcasts of the field
     * broadcast's type, sends the broadcast,awaits for the message as microservice1 and microservice2 and reruns the
     * messages in an array  (which its length is 2)
     * @return A Message array that its length is 2, which contains the messages received after awaiting for a message
     * for microservice1 and microservice2. The first element in the array is the message of microservice1 and the
     * second is the message of microservice2
     */
    private Message[] registerSubscribeToBroadCastSendBroadcastAndAwaitMessage() throws InterruptedException {
        messageBus.register(microService1);
        messageBus.register(microService2);
        messageBus.subscribeBroadcast(broadcastType, microService1);
        messageBus.subscribeBroadcast(broadcastType, microService2);
        messageBus.sendBroadcast(broadcast);
        Message message1 = messageBus.awaitMessage(microService1);
        Message message2 = messageBus.awaitMessage(microService2);
        return new Message[] {message1, message2};
    }
}
