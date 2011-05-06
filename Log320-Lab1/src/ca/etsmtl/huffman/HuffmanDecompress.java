package ca.etsmtl.huffman;

public class HuffmanDecompress {

	private static final String TXT_EXTENSION = "txt";
	private static final String HUF_EXTENSION = "huf";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			String fileName = args[0];
			if (fileName.endsWith(HUF_EXTENSION)) {

				
				
			} else {
				System.out.println("Can't compress file, extension is not " + HUF_EXTENSION);
			}
		} else {
			System.out.println("Can't compress file, name is not specified");
		}
	}

}
