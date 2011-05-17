package ca.etsmtl.huffman;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
				readFile();

				decodeFile();
				
				writeFile();
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
	
	private static void decodeFile() {
		int nbByteReaded = 0;
		
		int entriesNumber = FILE_BYTES[nbByteReaded] & 0xFF;
		nbByteReaded++;
		
		for (int i = 0; i < entriesNumber; i++) {
			int character = FILE_BYTES[nbByteReaded] & 0xFF;
			nbByteReaded++;
			int length = FILE_BYTES[nbByteReaded] & 0xFF;
			nbByteReaded++;
			StringBuilder path = new StringBuilder();
			int bob = (length - 1) / 8 + 1;
			for (int j=0 ; j < bob; j++) {
				StringBuilder currentpath = new StringBuilder(Integer.toBinaryString(FILE_BYTES[nbByteReaded] & 0xFF));
				nbByteReaded++;
				while (currentpath.length() < 8) {
					currentpath.insert(0, "0");
				}
				path.append(currentpath);
				
				if (path.length() > length) {
					path= new StringBuilder(path.substring(path.length() - length, path.length()));
				}
			}
			TREE.put(path.toString(), new Character((char) character));
		}
		
		StringBuilder sTextLength = new StringBuilder();
		for (int i = 0; i < 3; i++) {
			StringBuilder text = new StringBuilder(Integer.toBinaryString(FILE_BYTES[nbByteReaded] & 0xFF));
			nbByteReaded++;
			while (text.length() < 8) {
				text.insert(0, "0");
			}
			sTextLength.append(text);
		}
		
		int nbCharacterTotal = Integer.parseInt(sTextLength.toString(),2);
		
		StringBuilder character = new StringBuilder();
		
		int nbCharacterReaded = 0;
		
		while (nbCharacterReaded < nbCharacterTotal) {
			StringBuilder text1 = new StringBuilder(Integer.toBinaryString(FILE_BYTES[nbByteReaded] & 0xFF));
			nbByteReaded++;
			
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
	
	private static void writeFile() {
		String resultFileName = FILE_NAME.replaceAll(HUF_EXTENSION, UNCOMPRESSED + TXT_EXTENSION);
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
