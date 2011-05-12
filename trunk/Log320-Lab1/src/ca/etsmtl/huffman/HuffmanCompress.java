package ca.etsmtl.huffman;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HuffmanCompress {

	private static final String TXT_EXTENSION = ".txt";
	private static final String HUF_EXTENSION = ".huf";
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private static String FILE_NAME;
	
	private static byte[] FILE_BYTES;

	private static final Map<Integer, Integer> FREQUENCIES = new LinkedHashMap<Integer, Integer>();
	
	private static final List<HuffmanNode> SORTED_FREQUENCIES = new ArrayList<HuffmanNode>();
	
	private static HuffmanNode ROOT_NODE = new HuffmanNode();
	
	private static final Map<Integer, String> ENCODED_CHARACTERS = new HashMap<Integer, String>();
	
	private static StringBuilder RESULT = new StringBuilder();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			FILE_NAME = args[0];
			if (FILE_NAME.endsWith(TXT_EXTENSION)) {
				System.out.println("Compressing " + FILE_NAME);
				
				long before = Calendar.getInstance().getTimeInMillis();
				
				readFile();
				
				createFrequencyTable();

				sortFrequencyTable();

				createTree();
				
				encodeTree();
				
				toResult();
				
				writeFile();
				
				long after = Calendar.getInstance().getTimeInMillis();
				
				System.out.println((long) after - before);
			} else {
				System.out.println("Can't compress file, extension is not " + TXT_EXTENSION);
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

	private static void createFrequencyTable() {
		Integer integer;
		Integer value;
		for (byte b : FILE_BYTES) {
			integer = new Integer(b);
			value = FREQUENCIES.get(integer);
			if (value != null) {
				FREQUENCIES.put(integer, value + 1);
			} else {
				FREQUENCIES.put(integer, 1);
			}
		}
	}

	private static void sortFrequencyTable() {
		for (Entry<Integer, Integer> entry : FREQUENCIES.entrySet()) {
			SORTED_FREQUENCIES.add(new HuffmanNode(entry.getKey(), entry.getValue()));
		}
		Collections.sort(SORTED_FREQUENCIES);
	}
	
	private static void createTree() {
		while (SORTED_FREQUENCIES.size() > 1) {
			HuffmanNode leftLeaf = SORTED_FREQUENCIES.remove(0);
			HuffmanNode rightLeaf = SORTED_FREQUENCIES.remove(0);
			HuffmanNode mergeNode = new HuffmanNode(-1, leftLeaf.frequency + rightLeaf.frequency, leftLeaf, rightLeaf);

			int index = Collections.binarySearch(SORTED_FREQUENCIES, mergeNode);
			
			if (index < 0) {
				index = Math.abs(index)-1;
			}
			SORTED_FREQUENCIES.add(index, mergeNode);
		}
		ROOT_NODE = SORTED_FREQUENCIES.get(0);
	}
	
	private static void encodeTree() { 
		encodeTree(ROOT_NODE, "");
	}
	
	private static void encodeTree(HuffmanNode current, String position) {
		if (current.value != -1) {
			ENCODED_CHARACTERS.put(new Integer(current.value), position);
		} else {
			encodeTree(current.left, position + "0");
			encodeTree(current.right, position + "1");
		}
	}
	
	private static void toResult() {
		for (Entry<Integer, String> entry : ENCODED_CHARACTERS.entrySet()) {
			RESULT.append(entry.getKey() + "-" + entry.getValue() + LINE_SEPARATOR);
		}
		RESULT.append(LINE_SEPARATOR);
		for (byte fileByte : FILE_BYTES) {
			RESULT.append(ENCODED_CHARACTERS.get(new Integer(fileByte)) + LINE_SEPARATOR);
		}
		System.out.println(RESULT);
	}

	private static void writeFile() {
		String resultFileName = FILE_NAME.replaceAll(TXT_EXTENSION, HUF_EXTENSION);
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
	
	// http://snippets.dzone.com/posts/show/93
    public static byte[] intToByteArray(int value) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }
	
}