package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	
	private static MessageBusImpl instance = new MessageBusImpl(); // initializing the instance now to make the
	// singleton threaded safe

	private Map<Class<? extends Message>, Queue<BlockingQueue<Message>>> typesToQueues; // this map will concurrent an
	// will be used to assign messages to their registered microservices' queues
	private Map<Event,Future> eventsToFutures; // used to resolve the Future object of an Event when Complete is called
	// with the Event
	private Map<MicroService, List<Queue<BlockingQueue<Message>>>> microservicesToTypeQueues; // used to delete the
	// microservice's blocking queue from the message types' Queues
	private Map<MicroService, BlockingQueue<Message>> microservicesToQueues; // maps the microservice to its blocking
	// queue of messages to fetch

	/**
	 * a private constructor for the singleton design pattern
	 */
	private MessageBusImpl(){
		// initializing the maps with empty new maps:
		typesToQueues = new ConcurrentHashMap<>();
		eventsToFutures = new ConcurrentHashMap<>();
		microservicesToTypeQueues = new ConcurrentHashMap<>();
		microservicesToQueues = new ConcurrentHashMap<>();
	}

	/**
	 * A getter for the instance of the MessageBusImpl according to the Singleton design pattern. Creates
	 * a the instance if doesn't already exist
	 * @return The instance of the MessageBusImpl singleton
	 */
	public static MessageBusImpl getInstance(){
		return instance; // the instance is initialized when the field is defined
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// Assuming m is not subscribed to type yet
		if (!typesToQueues.containsKey(type)) // TODO: add contains key on the locks map? // if this is the first microservice to register to this type of Message
			addQueueOfMessageTypeIfNotExist(type); //create a Queue for the BlockingQueues of the microservices registered to type
		Queue<BlockingQueue<Message>> queueOfType = typesToQueues.get(type); // the queue of the blocking Queues of the
		// microservices registered to type
		queueOfType.add(microservicesToQueues.get(m)); // adding the blocking queue of m
		microservicesToTypeQueues.get(m).add(queueOfType); // adding the queue which the blocking queue was added to
		//the List of Queues of blocking queues that contain the blocking q of m
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// Assuming m is not subscribed to type yet
		if (!typesToQueues.containsKey(type)) // if this is the first microservice to register to this type of Message
			addQueueOfMessageTypeIfNotExist(type); //create a Queue for the BlockingQueues of the microservices registered to type
		Queue<BlockingQueue<Message>> queueOfType = typesToQueues.get(type); // the queue of the blocking Queues of the
		// microservices registered to type
		queueOfType.add(microservicesToQueues.get(m)); // adding the blocking queue of m
		microservicesToTypeQueues.get(m).add(queueOfType); // adding the queue which the blocking queue was added to
		// to the List of Queues of blocking queues that contain the blocking q of m
    }

	/**
	 * This method adds a new ConcurrentQueue, for the BlockingQueues of microservices registered to the given type, to
	 * the typesToQueues field, mapped by the given type, if not already exists. If already exists does nothing
	 * @param type the type of Message that the microservices that their BlockingQueues will be in the new new Queue
	 *             need to be registered to. The new queue will be mapped by this type
	 */
	private void addQueueOfMessageTypeIfNotExist(Class<? extends Message> type) {
		//Version check
		typesToQueues.putIfAbsent(type,new ConcurrentLinkedQueue<>());
	}

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		eventsToFutures.get(e).resolve(result); // resolving the Future instance of e with result
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		Queue<BlockingQueue<Message>> queueOfBroadcast = typesToQueues.get(b.getClass()); // the queue of the blocking
		// queues of the microservices registered to Broadcasts of the type of b
		// adding b to every blocking queue in the queue:
		for (BlockingQueue<Message> queue : queueOfBroadcast){
			queue.add(b);
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Class<? extends Event> eventType = e.getClass();
		// adding e to the blocking queue of the next microservice registered to it in the round robin manner:
		// the queue of the blocking
		// TODO: 06/12/2020  should we check if the queueOfEvent==null?? (we get null if no one subscribed to this event. why not check it?)
		// queues of the microservices registered to Events of the type of e

		Future<T> futureOfE;
		if (!typesToQueues.containsKey(eventType)) // doesn't need to be synchronized because the queues of events are never deleted (even in empty)
			return null;
		Queue<BlockingQueue<Message>> queueOfEvent = typesToQueues.get(eventType);
		synchronized (queueOfEvent) {
			if (queueOfEvent.isEmpty()) // if no microservice subscribed to the type of e
				return null;
			// creating a Future instance for e, saving it with e and returning it:
			futureOfE = new Future<>();
			eventsToFutures.put(e, futureOfE);
			// removing the blocking queue from the beginning of the queue. adding e to it and then returning it to the end
			// of the queue of Event:
			BlockingQueue<Message> blockingQueueToReceiveE = queueOfEvent.remove();
			blockingQueueToReceiveE.add(e);
			queueOfEvent.add(blockingQueueToReceiveE);
		}
        return futureOfE;
	}

	@Override
	public void register(MicroService m) {
		// creating a Blocking Queue for the new microservice and saving it with it:
		microservicesToQueues.put(m, new LinkedBlockingQueue<>());
		// creating a new empty List for the queues that include the blocking queue of the microservice m
		microservicesToTypeQueues.put(m, new LinkedList<>());
	}

	@Override
	public void unregister(MicroService m) {

		if (isRegistered(m)) {
			// first removing the BlockingQueue of m from all the queues of types it was in:
			BlockingQueue<Message> blockingQueueOfM = microservicesToQueues.get(m);
			List<Queue<BlockingQueue<Message>>> subscribedTypesQueues = microservicesToTypeQueues.get(m);
			for (Queue<BlockingQueue<Message>> currentTypeQueue : subscribedTypesQueues) {
				synchronized (currentTypeQueue) {
					currentTypeQueue.remove(blockingQueueOfM);
				}
			}
			// now removing m and its content from the map fields:
			microservicesToTypeQueues.remove(m);
			microservicesToQueues.remove(m);
		}

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		if (!isRegistered(m)) // can't await for message if not registered
			throw new IllegalStateException("The given microservice is not registered to the MessageBus");
		// removing the first Message from the BlockingQueue of Messages of m. Because it's a blocking queue, it a
		// message does not exist there yet, it waits until there is a Message in the queue:
		return microservicesToQueues.get(m).take();
	}

	private boolean isRegistered(MicroService microService){
		return microservicesToQueues.containsKey(microService);
	}
}
