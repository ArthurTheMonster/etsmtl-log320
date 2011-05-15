package ca.etsmtl.huffman;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.NClob;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
			int character = FILE_BYTES[i * 5 + 1] & 0xFF;
			int lenght = FILE_BYTES[i * 5 + 2] & 0xFF;
			int path1 = FILE_BYTES[i * 5 + 3] & 0xFF;
			int path2 = FILE_BYTES[i * 5 + 4] & 0xFF;
			int path3 = FILE_BYTES[i * 5 + 5] & 0xFF;
			StringBuilder path1Value = new StringBuilder(Integer.toBinaryString(path1));
			while (path1Value.length() < 8) {
				path1Value.insert(0, "0");
			}
			StringBuilder path2Value = new StringBuilder(Integer.toBinaryString(path2));
			while (path2Value.length() < 8) {
				path2Value.insert(0, "0");
			}
			StringBuilder path3Value = new StringBuilder(Integer.toBinaryString(path3));
			while (path3Value.length() < 8) {
				path3Value.insert(0, "0");
			}
			String path = path1Value.toString() + path2Value.toString() + path3Value.toString();
			System.out.println("pathlenght " + path.length());
			System.out.println("read lenght" + lenght);
			TREE.put(path.substring(path.length() - lenght, path.length()), new Character((char) character));
		}
		
		StringBuilder sTextLength = new StringBuilder();
		for (int i = entriesNumber * 5 + 1; i<entriesNumber * 5 + 4; i++) {
			StringBuilder text = new StringBuilder(Integer.toBinaryString(FILE_BYTES[i] & 0xFF));
			while (text.length() < 8) {
				text.insert(0, "0");
			}
			sTextLength.append(text);
			System.out.println("text lenght " + text);
		}
		int nbCharacterTotal = Integer.parseInt(sTextLength.toString(),2);
		
		System.out.println(nbCharacterTotal);
		
		StringBuilder character = new StringBuilder();
		
		int nbCharacterReaded = 0;
		
		for (int i = entriesNumber * 5 + 4; i < FILE_BYTES.length; i++) {
			StringBuilder text1 = new StringBuilder(Integer.toBinaryString(FILE_BYTES[i] & 0xFF));
			while (text1.length() < 8) {
				text1.insert(0, "0");
			}
			for (char c : text1.toString().toCharArray()) {
				character.append(c);
				if (TREE.containsKey(character.toString())) {
					RESULT.append(TREE.get(character.toString()));
					character.setLength(0);
					
					nbCharacterReaded++;
					if (nbCharacterReaded == nbCharacterTotal) {
						return;
					}
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
