package pa1;
import java.io.*;
import java.util.*;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.process.*;

public class Preprocess {

	// the list contains UP_TRAIN, DOWN_TRAIN, UP_VALIDATION, DOWN_VALIDATION
	// and UPDOWN_TEST, respectively.
	private static List<DocumentPreprocessor> documents;

	public static void main(String[] args) {
		documents = new ArrayList<DocumentPreprocessor>();
		process("training.txt");
		process("validation.txt");
		processTest();
	}

	public static void process(String file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			// Lists to store contents of email
			List<String> downspeaks = new ArrayList<String>();
			List<String> upspeaks = new ArrayList<String>();
			// temporary String used for processing contents
			String temp = "";
			String current = "";
			// booleans used to indicate when to write
			boolean flag = false;
			// true means the contents are upspeak, false means they are downspeak
			boolean updown = false;
			current = br.readLine();
			while (current != null) {
				String[] parts = current.split("\\*\\*");
				// end of message: stop appending contents to String and store to List
				if (parts.length > 1 && parts[1].equals("EOM")) {
					flag = false;
					if (updown) {
						upspeaks.add(temp);
					} else {
						downspeaks.add(temp);
					}
					temp = "";
				} else if (flag) {
					temp += current + " ";
				} else if (parts.length > 1 && parts[1].equals("START")) {
					flag = true;
				} else if (current.equals("UPSPEAK")) {
					updown = true;
				} else if (current.equals("DOWNSPEAK")) {
					updown = false;
				}
				current = br.readLine();
			}
			br.close();
			// writing output in separate files to check the correctness of algorithm
			BufferedWriter bwu = new BufferedWriter(new FileWriter("upspeak.txt"));
			BufferedWriter bwd = new BufferedWriter(new FileWriter("downspeak.txt"));
			for (String email : upspeaks) {
				bwu.write(email + "\n\n");
			}
			for (String email : downspeaks) {	
				bwd.write(email + "\n\n");
			}
			bwu.close();
			bwd.close();
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
		
		DocumentPreprocessor dpu = new DocumentPreprocessor("upspeak.txt");
		for (List<HasWord> sentence : dpu) {
			sentence.add(0, new Word("<s>"));
			sentence.set(sentence.size() - 1, new Word("<\\s>"));
		}
		DocumentPreprocessor dpd = new DocumentPreprocessor("downspeak.txt");
		for (List<HasWord> sentence : dpd) {
			sentence.add(0, new Word("<s>"));
			sentence.set(sentence.size() - 1, new Word("<\\s>"));
		}
		documents.add(dpu);
		documents.add(dpd);
	}
	
	public static void processTest() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("test.txt"));
			// Lists to store contents of email
			List<String> emails = new ArrayList<String>();
			// temporary String used for processing contents
			String temp = "";
			String current = "";
			// booleans used to indicate when to write
			boolean flag = false;
			current = br.readLine();
			while (current != null) {
				String[] parts = current.split("\\*\\*");
				// end of message: stop appending contents to String and store to List
				if (parts.length > 1 && parts[1].equals("EOM")) {
					flag = false;
					emails.add(temp);
					temp = "";
				} else if (flag) {
					temp += current + " ";
				} else if (parts.length > 1 && parts[1].equals("START")) {
					flag = true;
				}
				current = br.readLine();
			}
			br.close();
			// writing output in separate files to check the correctness of algorithm
			BufferedWriter bw = new BufferedWriter(new FileWriter("processed.txt"));
			for (String email : emails) {	
				bw.write(email + "\n\n");
			}
			bw.close();
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
		
		DocumentPreprocessor dp = new DocumentPreprocessor("processed.txt");
		for (List<HasWord> sentence : dp) {
			sentence.add(0, new Word("<s>"));
			sentence.set(sentence.size() - 1, new Word("<\\s>"));
		}
		documents.add(dp);
	}
}
