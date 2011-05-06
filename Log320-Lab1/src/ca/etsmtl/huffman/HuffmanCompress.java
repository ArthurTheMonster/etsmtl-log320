package ca.etsmtl.huffman;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ca.etsmtl.huffman.BinaryTree.Node;

public class HuffmanCompress {

	private static final String TXT_EXTENSION = "txt";
	private static final String HUF_EXTENSION = "huf";
	
	private static byte[] fileBytes;

	private static final Map<Integer, Integer> FREQUENCIES = new LinkedHashMap<Integer, Integer>();
	
	private static final List<HuffmanNode> SORTED_FREQUENCIES = new ArrayList<HuffmanNode>();
	
	private static final BinaryTree BINARY_TREE = new BinaryTree();
	
	private static final String RESULT = new String();
	
	/*long before = Calendar.getInstance().getTimeInMillis();
	long after = Calendar.getInstance().getTimeInMillis();
	System.out.println((long) after - before);*/
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			String fileName = args[0];
			if (fileName.endsWith(TXT_EXTENSION)) {
				System.out.println("Compressing " + fileName);
				
				readFile(fileName);
				
				createFrequencyTable(fileBytes);

				sortFrequencyTable();

				createTree();

				encodeTree();
				
				writeFile(fileName);
			} else {
				System.out.println("Can't compress file, extension is not " + TXT_EXTENSION);
			}
		} else {
			System.out.println("Can't compress file, name is not specified");
		}
	}

	private static void readFile(String fileName) {
		File file = new File(fileName);
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
			fileBytes = new byte[(int) file.length()];
			fileInputStream.read(fileBytes);
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createFrequencyTable(byte[] fileBytes) {
		Integer integer;
		Integer value;
		for (byte b : fileBytes) {
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
		Collections.reverse(SORTED_FREQUENCIES);
	}

	private static void createTree() {
		Iterator<HuffmanNode> it = SORTED_FREQUENCIES.iterator();
		
		HuffmanNode previousNode = null;
		HuffmanNode currentNode = null;
		HuffmanNode newNode = null;
		
		if (it.hasNext()) {
			currentNode = it.next();
			BINARY_TREE.insert(currentNode);
		}
		while (it.hasNext()) {
			previousNode = currentNode;
			currentNode = it.next();
							
			// We create a new node with a frequency equal to the sum of the last two nodes
			newNode = new HuffmanNode(-1, previousNode.frequency + currentNode.frequency);
			
			previousNode = currentNode;
			currentNode = newNode;

			BINARY_TREE.insert(previousNode);
			BINARY_TREE.insert(currentNode);
		}

		/*for (HuffmanNode huffmanNode : SORTED_FREQUENCIES) {
			SORTED_FREQUENCIES.huffmanNode.
			BINARY_TREE.insert(huffmanNode);
		}*/
	}
	
	private static void encodeTree() {
		BINARY_TREE.printTree();
	
		Node root = BINARY_TREE.root;
		encodeNode(root, "");	
	}
	
	private static void encodeNode(Node node, String path) {
		if (node != null) {
			encodeNode(node.left, path + "0");
			// We write the path of that letter in the tree (0 = left, 1 = right)
			// RESULT += 
			// Then we write the character
			// RESULT +=
			encodeNode(node.right,  path + "1");
		}
	}

	private static void writeFile(String fileName) {
		String resultFileName = fileName.replaceAll(TXT_EXTENSION, HUF_EXTENSION);
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(resultFileName);
	        BufferedWriter out = new BufferedWriter(fileWriter);
		    out.write(RESULT);
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