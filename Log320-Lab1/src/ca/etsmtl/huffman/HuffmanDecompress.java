package ca.etsmtl.huffman;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class HuffmanDecompress {

	private static final String TXT_EXTENSION = ".txt";
	private static final String HUF_EXTENSION = ".huf";
	private static final String UNCOMPRESSED = "_uncompressed";
	
	private static String FILE_NAME;
	
	private static byte[] FILE_BYTES;
	
	private static final Map<String, Character> TREE = new HashMap<String, Character>();
	
	private static final StringBuilder RESULT = new StringBuilder();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			FILE_NAME = args[0];
			if (FILE_NAME.endsWith(HUF_EXTENSION)) {
				System.out.println("Decompressing " + FILE_NAME);
				
				long before = Calendar.getInstance().getTimeInMillis();
				
				readFile();

				decodeTree();
				
				System.out.println(TREE);
				
				decodeText();
				
				writeFile();
				
				long after = Calendar.getInstance().getTimeInMillis();
				
				System.out.println((long) after - before);
			} else {
				System.out.println("Can't compress file, extension is not " + HUF_EXTENSION);
			}
		} else {
			System.out.println("Can't compress file, name is not specified");
		}
	}
	
	private static void readFile() {
		File file = new File(FILE_NAME);
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
			FILE_BYTES = new byte[(int) file.length()];
			fileInputStream.read(FILE_BYTES);
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void decodeTree() {
		int entriesNumber = FILE_BYTES[0] & 0xFF;
		for (int i = 0; i < entriesNumber; i++) {
			int character = FILE_BYTES[i * 3 + 1] & 0xFF;
			int lenght = FILE_BYTES[i * 3 + 2] & 0xFF;
			System.out.println(lenght);
			int path = FILE_BYTES[i * 3 + 3] & 0xFF;
			StringBuilder pathValue = new StringBuilder(Integer.toBinaryString(path));
			while (pathValue.length() < lenght) {
				pathValue.insert(0, "0");
			}
			TREE.put(pathValue.toString(), new Character((char) character));
		}
		StringBuilder character = new StringBuilder();
		for (int i = entriesNumber * 3 + 1; i < FILE_BYTES.length; i++) {
			StringBuilder text = new StringBuilder(Integer.toBinaryString(FILE_BYTES[i] & 0xFF));
			while (text.length() < 8) {
				text.insert(0, "0");
			}
			for (char c : text.toString().toCharArray()) {
				character.append(c);
				if (TREE.containsKey(character.toString())) {
					RESULT.append(TREE.get(character.toString()));
					character.setLength(0);
				}
			}
		}
	}
	
	private static void decodeText() {

	}
	
	private static void writeFile() {
		String resultFileName = FILE_NAME.replaceAll(HUF_EXTENSION, UNCOMPRESSED + TXT_EXTENSION);
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(resultFileName);
	        BufferedWriter out = new BufferedWriter(fileWriter);
		    out.write(RESULT.toString());
		    System.out.println(RESULT);
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
