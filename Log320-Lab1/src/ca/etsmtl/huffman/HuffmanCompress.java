package ca.etsmtl.huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
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
	
	private static String FILE_NAME;
	
	private static long BEFORE;
	
	private static byte[] FILE_BYTES;

	private static final Map<Integer, Integer> FREQUENCIES = new LinkedHashMap<Integer, Integer>();
	
	private static final List<HuffmanNode> SORTED_FREQUENCIES = new ArrayList<HuffmanNode>();
	
	private static HuffmanNode ROOT_NODE = new HuffmanNode();
	
	private static final Map<Integer, String> ENCODED_CHARACTERS = new HashMap<Integer, String>();
	
	private static List<Integer> aa = new ArrayList<Integer>();
	
	private static ByteBuffer RESULT = ByteBuffer.allocate(10);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			FILE_NAME = args[0];
			if (FILE_NAME.endsWith(TXT_EXTENSION)) {
				System.out.println("Compressing " + FILE_NAME);
				
				BEFORE = Calendar.getInstance().getTimeInMillis();
				
				readFile();
				
				System.out.println("File readed: " + ((long)Calendar.getInstance().getTimeInMillis() - BEFORE));
				
				createFrequencyTable();

				sortFrequencyTable();
				
				System.out.println("Create sorted table: " + ((long)Calendar.getInstance().getTimeInMillis() - BEFORE));
				
				createTree();
				
				System.out.println("Create tree: " + ((long)Calendar.getInstance().getTimeInMillis() - BEFORE));
					
				encodeTree();
				
				System.out.println("Encode tree: " + ((long)Calendar.getInstance().getTimeInMillis() - BEFORE));
				
				toResult();
				
				System.out.println("Prepare result: " + ((long)Calendar.getInstance().getTimeInMillis() - BEFORE));
				
				writeFile();
				
				System.out.println("Write result: " + ((long)Calendar.getInstance().getTimeInMillis() - BEFORE));
				
				long after = Calendar.getInstance().getTimeInMillis();
				
				System.out.println((long) after - BEFORE);
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
		/*
		 * The first 7 bits are reserved to count how many entries has the tree.
		 * For each entry of the tree (we loop ?? (based on the number from the first 7 bits) time):
		 * 		First 7 bits tell us the ASCII code
		 * 		The next 4 bits tell us how many bit to read for the path (2^4=16 (path length)-> 2^16=65536 leaf in the tree)
		 * 		The next ??  (based on the last number) bits tell us the path
		 * The rest of the file is the text. We know that the prefix of a path will never have the same
		 * value of an other path, so we don't need to know the length of each path.
		 */
		
		//Note, limit of 2^7=128 different character in the text
		int nbEntry = ENCODED_CHARACTERS.entrySet().size();
		StringBuilder sNbEntry = new StringBuilder(Integer.toBinaryString(nbEntry));
		while (sNbEntry.length() < 8) {
			sNbEntry.insert(0, "0");
		}
		aa.add(nbEntry);
		
		System.out.println("nbEntry: " + ((long)Calendar.getInstance().getTimeInMillis() - BEFORE));
		
		for (Entry<Integer, String> entry : ENCODED_CHARACTERS.entrySet()) {
			aa.add(entry.getKey() & 0xFF);
 			aa.add(entry.getValue().length() & 0xFF);
			StringBuilder path = new StringBuilder(Integer.toBinaryString(Integer.parseInt(entry.getValue(), 2)));
			while (path.length() < 24) {
				path.insert(0, "0");
			}
 			aa.add(Integer.parseInt(path.substring(0, 8), 2));
 			aa.add(Integer.parseInt(path.substring(8, 16), 2));
 			aa.add(Integer.parseInt(path.substring(16, 24), 2));
		}
		
		System.out.println("tree: " + ((long)Calendar.getInstance().getTimeInMillis() - BEFORE));
		
		String value = Integer.toBinaryString(FILE_BYTES.length);
		StringBuilder path = new StringBuilder(value);
		while (path.length() < 24) {
			path.insert(0, "0");
		}
		aa.add(Integer.parseInt(path.substring(0, 8), 2));
		aa.add(Integer.parseInt(path.substring(8, 16), 2));
		aa.add(Integer.parseInt(path.substring(16, 24), 2));
		
		System.out.println("caracter lenght: " + ((long)Calendar.getInstance().getTimeInMillis() - BEFORE));
		
		StringBuilder builder = new StringBuilder();
		
		long byteBuff = 0;
		int byteIndex = 0;
		
		for (byte fileByte : FILE_BYTES) {
			int compresedLenght =  ENCODED_CHARACTERS.get(new Integer(fileByte)).length();
			int compressedValue = Integer.parseInt(ENCODED_CHARACTERS.get(new Integer(fileByte)), 2);
			
			byteBuff = (byteBuff << compresedLenght) | compressedValue;
			byteIndex += compresedLenght;
			
			if(byteIndex >= 8){
				int remaining = (int)(byteBuff & ((2 << (byteIndex % 8)) - 1));
				long toAppend = byteBuff >> (byteIndex % 8);
				int nbOfBytes = byteIndex / 8;
				for(int i = 0;i < nbOfBytes;i++){
					aa.add((int)(toAppend >> (((nbOfBytes - 1) - i) * 8)));
				}
				byteBuff = remaining;
				byteIndex = byteIndex % 8;
			}
		}
		
		if(byteIndex > 0){
			byteBuff = byteBuff << (8 - byteIndex);
			aa.add((int)byteBuff);
		}
		
		System.out.println("ENCODED_CHARACTERS: " + ((long)Calendar.getInstance().getTimeInMillis() - BEFORE));
		
		while (builder.length() % 8 != 0) {
			builder.append("0");
		}
		
		System.out.println("buffer: " + ((long)Calendar.getInstance().getTimeInMillis() - BEFORE));
		
		int i = 0;
		while (i <= builder.toString().length() - 8) {
			aa.add(Integer.parseInt(builder.substring(i, i + 8), 2));
			i += 8; 
		}
		
		System.out.println("text: " + ((long)Calendar.getInstance().getTimeInMillis() - BEFORE));
		
		System.out.println(ENCODED_CHARACTERS);
	}

	private static void writeFile() {
		String resultFileName = FILE_NAME.replaceAll(TXT_EXTENSION, HUF_EXTENSION);
		try {
			FileOutputStream output = new FileOutputStream(resultFileName);  
			/*while (RESULT.length() % 8 != 0) {
				RESULT.append("0");
			}*/
			//int i = 0;
			//char[] bytes = RESULT.toString().toCharArray();
			/*while (i <= bytes.length - 8) {
				String bob = bytes[i]
				bytes[i/8] = (byte) Integer.parseInt(bytes[i],2);
				i += 8; 
				System.out.println(i);
			}*/
			for (int j : aa.toArray(new Integer[aa.size()])) {
				output.write(j & 0xFF);
				//System.out.println(j);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}