package ca.etsmtl.huffman;

public class ByteTable {
	protected byte[] byteTable;
	protected int currentIndex = 0;
	
	public ByteTable() {
		byteTable = new byte[10];
	}

	private ByteTable(byte[] tblByte, int index) {
		byteTable = tblByte;
		currentIndex = index; 
	}
	
	public ByteTable addByte(byte b) {
		ByteTable newByteTable = this.clone();
		newByteTable.byteTable[newByteTable.currentIndex] = b;
		newByteTable.increaseIndex();
		
		return newByteTable;
	}
	
	public ByteTable addBytes(ByteTable bytes) {
		ByteTable newByteTable = this.clone();
		for (int i = 0; i < bytes.currentIndex;i++) {
			newByteTable.byteTable[newByteTable.currentIndex] = bytes.byteTable[i];
			newByteTable.increaseIndex();
		}
		return newByteTable;
	}
	
	public ByteTable addBytes(byte[] bytes) {
		ByteTable newByteTable = this.clone();
		for (int i = 0; i < bytes.length;i++) {
			newByteTable.byteTable[newByteTable.currentIndex] = bytes[i];
			newByteTable.increaseIndex();
		}
		return newByteTable;
	}
	
	protected void increaseIndex() {
		if (currentIndex+1 >= byteTable.length) {
			byte[] newTable = new byte[byteTable.length*2];
			for (int i = 0; i < currentIndex;i++) {
				newTable[i] = byteTable[i];
			}
			byteTable = newTable;
		}
		currentIndex++;
	}
	
	protected ByteTable clone() {
		return new ByteTable(byteTable, currentIndex);
	}
	
}
