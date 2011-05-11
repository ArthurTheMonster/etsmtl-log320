package ca.etsmtl.huffman;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HuffmanDecompress {

	private static final String TXT_EXTENSION = "txt";
	private static final String HUF_EXTENSION = "huf";
	
	private static String FILE_NAME;
	
	private static final List<String> HEADER = new ArrayList<String>();
	
	private static final List<String> TEXT = new ArrayList<String>();
	
	private static final StringBuilder RESULT = new StringBuilder();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			FILE_NAME = args[0];
			if (FILE_NAME.endsWith(HUF_EXTENSION)) {
				readFile();

				decodeTree();
				
				decodeText();
				
				//writeFile();
			} else {
				System.out.println("Can't compress file, extension is not " + HUF_EXTENSION);
			}
		} else {
			System.out.println("Can't compress file, name is not specified");
		}
	}
	
	private static void readFile() {
		File file = new File(FILE_NAME);
		try {
			Scanner scanner = new Scanner(file);
			boolean header = true;
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.equals("\n")) {
					header = false;
				} else {
					if (header) {
						HEADER.add(line);
					} else {
						TEXT.add(line);
					}
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void decodeTree() {
		// TODO Auto-generated method stub
		
	}
	
	private static void decodeText() {
		
	}
	
	private static void writeFile() {
		String resultFileName = FILE_NAME.replaceAll(HUF_EXTENSION, TXT_EXTENSION);
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(resultFileName);
	        BufferedWriter out = new BufferedWriter(fileWriter);
		    out.write(RESULT.toString());
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
