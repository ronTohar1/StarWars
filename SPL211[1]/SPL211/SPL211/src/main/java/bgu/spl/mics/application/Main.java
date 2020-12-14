package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		ensureArgsLength(args); // ensuring that received exactly 2 args
		Input input = tryGetInputFromFile(args[0]);
		runProgram(input); // the results are saved in the Diary singleton
		tryWriteOutputToFile(args[1]);
	}

	/**
	 * A private method that ensures that the args received are from the length of exactly 2.
	 * If it isn't, throws an exception
	 * @param args The program's received args
	 * @throws IllegalArgumentException if the length of the given args is not exactly 2
	 */
	private static void ensureArgsLength(String[] args){
		if (args.length != 2){
			throw new IllegalArgumentException("Exactly 2 arguments are required. Received: " + args.length);
		}
	}

	/**
	 * A private method that tries to read the input from the file in the given path, and returns it as an Input object
	 * @param inputFilePath the path to the file to read the input from
	 * @return An Input object which contains the input data from the file in the given path
	 * @throws IllegalArgumentException if couldn't find or close the file in the given path
	 */
	private static Input tryGetInputFromFile(String inputFilePath){
		try{
			Input input = getInputFromFile(inputFilePath);
			return input;
		}
		catch (FileNotFoundException fileNotFoundException){
			throw new IllegalArgumentException("File in path: \"" + inputFilePath + "\" was not found");
		}
		catch (IOException ioException){
			throw new IllegalArgumentException("A java IO exception was thrown on the file " +
					"path: \"" + inputFilePath + "\": " + ioException.getMessage());
		}
	}

	/**
	 * A private method that reads the input from the file in the given path, and returns it as an Input object
	 * @param filePath the path to the file to read the input from
	 * @return An Input object which contains the input data from the file in the given path
	 * @throws FileNotFoundException if couldn't find the file in the given path
	 * @throws IOException if couldn't close the file in the given path
	 */
	private static Input getInputFromFile(String filePath) throws IOException {
		Gson gson = new Gson();
		Reader reader = new FileReader(filePath);
		Input input = gson.fromJson(reader, Input.class);
		reader.close();
		return input;
	}

	/**
	 * A private method that runs the program. The results are saved in the Diary singleton
	 * @param input An Input object with the input data of the program
	 */
	private static void runProgram(Input input){
		initializePassiveObjects(input);
		Thread[] microServicesThreads = initializeMicroservicesInThreads(input);
		runThreadsAndWaitToFinish(microServicesThreads);
	}

	/**
	 * A private method that initializes all the program's passive object according to the given input data
	 * @param input An Input object with the input data of the program
	 */
	private static void initializePassiveObjects(Input input){
		Ewoks.initialize(input.getEwoks()); // initializes the singleton Ewoks instance with the given number of Ewoks
	}

	/**
	 * A private method that initializes all the program's microservices in threads, and returns the threads in an array
	 * @param input An Input object with the input data of the program
	 * @return An array of threads which their missions is the run method of the program's microservices
	 */
	private static Thread[] initializeMicroservicesInThreads(Input input){
		// initializing anf retuning an array of threads, initialized with all the program's runnable microservices:
		return new Thread[] {
				new Thread(new LeiaMicroservice(input.getAttacks())),
				new Thread(new HanSoloMicroservice()),
				new Thread(new C3POMicroservice()),
				new Thread(new R2D2Microservice(input.getR2D2())),
				new Thread(new LandoMicroservice(input.getLando()))
		};
	}

	/**
	 * A private method that receives an array of threads, runs them all, and waits until their all finished, using join
	 * @param threads An array of the threads to run and wait until they finish
	 */
	private static void runThreadsAndWaitToFinish(Thread[] threads) {
		// running the threads:
		for (Thread thread : threads){
			thread.start();
		}
		// waiting for all the threads to finish, using join:
		for (Thread thread : threads){
			try{
				thread.join();
			}
			catch (InterruptedException interruptedException){
				// doing nothing
			}
		}
	}

	/**
	 * A private method that tries to write the results of the program run, saved in the Diary singleton, to the file
	 * in the given path
	 * @param filePath the path to the file to write the results in
	 * @throws IllegalArgumentException if couldn't write to or close the file in the given path
	 */
	private static void tryWriteOutputToFile(String filePath) {
		try{
			writeObjectToFile(filePath, Diary.getInstance());
		}
		catch (IOException ioException){
			throw new IllegalArgumentException("A java IO exception was thrown on the file " +
					"path: \"" + filePath + "\": " + ioException.getMessage());
		}
	}

	/**
	 * A private method that writes the data in the given Object, using json, to the file in the given path
	 * @param filePath the path to the file to write to
	 * @param object the Object to write its data, using json, to the file in the given path
	 * @throws IOException if couldn't write to or close the file in the given path
	 */
	private static void writeObjectToFile(String filePath, Object object) throws IOException{
		Gson gson = new Gson();
		Writer writer = new FileWriter(filePath);
		writer.write(gson.toJson(object));
		writer.close();
	}
}
