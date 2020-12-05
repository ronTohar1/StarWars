package bgu.spl.mics.application;

import bgu.spl.mics.application.services.HanSoloMicroservice;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		Thread t= new Thread(new HanSoloMicroservice());
		t.run();

	}
}
