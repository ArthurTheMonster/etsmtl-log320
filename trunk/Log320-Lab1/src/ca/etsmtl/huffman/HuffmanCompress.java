package ca.etsmtl.huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class HuffmanCompress {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			System.out.println("Compressing " + args[0]);
			File file = new File(args[0]);
			
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				
				byte[] fileBytes = new byte[(int) file.length()];
				
				fileInputStream.read(fileBytes);
				
				System.out.println(Arrays.toString(fileBytes));
				
				for (byte fileByte : fileBytes) {
					System.out.println(new String(new byte[] {fileByte}));
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//fileInputStream.read(b);
		} else {
			System.out.println("Can't compress file, name is not specified");
		}
	}

}
