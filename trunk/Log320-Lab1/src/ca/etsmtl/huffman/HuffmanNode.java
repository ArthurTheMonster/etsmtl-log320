package ca.etsmtl.huffman;

public class HuffmanNode implements Comparable<HuffmanNode> {

	public int value;
	public int frequency;
	
	public HuffmanNode() {
		this(0, 0);
	}
	
	public HuffmanNode(int value, int frequency) {
		this.value = value;
		this.frequency = frequency;
	}
	
	@Override
	public int compareTo(HuffmanNode otherHuffmanNode) {
		int result = frequency - otherHuffmanNode.frequency;
		if (result == 0) {
			return 1;
		} else {
			return result;
		}
	}
	
	@Override
	public String toString() {
		return "value " + value + " frequency " + frequency;
	}
	
}
