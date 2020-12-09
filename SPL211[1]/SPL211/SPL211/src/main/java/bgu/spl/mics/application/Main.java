package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
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
		ensureArgsLength(args);
		Input input = tryGetInputFromFile(args[0]);
		runProgram(input);
		tryWriteOutputToFile(args[1]);
	}

	private static void ensureArgsLength(String[] args){
		if (args.length != 2){
			throw new IllegalArgumentException("Exactly 2 arguments are required. Received: " + args.length);
		}
	}

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

	private static Input getInputFromFile(String filePath) throws IOException {
		Gson gson = new Gson();
		Reader reader = new FileReader(filePath);
		Input input = gson.fromJson(reader, Input.class);
		reader.close(); // TODO: should we close it?
		return input;
	}

	private static void runProgram(Input input){
		initializePassiveObjects(input);
		Thread[] microServicesThreads = initializeMicroservicesInThreads(input);
		runThreadsAndWaitToFinish(microServicesThreads);
	}

	private static void initializePassiveObjects(Input input){
		Ewoks.initialize(input.getEwoks());
	}

	private static Thread[] initializeMicroservicesInThreads(Input input){
		return new Thread[] {
				new Thread(new LeiaMicroservice(input.getAttacks())),
				new Thread(new HanSoloMicroservice()),
				new Thread(new C3POMicroservice()),
				new Thread(new R2D2Microservice(input.getR2D2())),
				new Thread(new LandoMicroservice(input.getLando()))
		};
	}

	private static void runThreadsAndWaitToFinish(Thread[] threads) {
		for (Thread thread : threads){
			thread.start();
		}
		for (Thread thread : threads){
			try{
				thread.join();
			}
			catch (InterruptedException interruptedException){
				// doing nothing
			}
		}
	}

	private static void tryWriteOutputToFile(String filePath) {
		try{
			writeObjectToFile(filePath, Diary.getInstance());
		}
		catch (IOException ioException){
			throw new IllegalArgumentException("A java IO exception was thrown on the file " +
					"path: \"" + filePath + "\": " + ioException.getMessage());
		}
	}

	private static void writeObjectToFile(String filePath, Object object) throws IOException{
		Gson gson = new Gson();
		Writer writer = new FileWriter(filePath);
		writer.write(gson.toJson(object));
		writer.close();
	}
}
