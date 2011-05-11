package ca.etsmtl.huffman;

public class HuffmanNode implements Comparable<HuffmanNode> {

	public int frequency;
	public int value;
	public HuffmanNode left;
	public HuffmanNode right;
	
	public HuffmanNode() {
		this(0, 0);
	}
	
	public HuffmanNode(int value, int frequency) {
		this(value, frequency, null, null);
	}
	
	public HuffmanNode(int value, int frequency, HuffmanNode left, HuffmanNode right) {
		this.value = value;
		this.frequency = frequency;
		this.left = left;
		this.right = right;
	}
	
	@Override
	public int compareTo(HuffmanNode otherHuffmanNode) {
		int result = frequency - otherHuffmanNode.frequency;
		//FIXME: tester
		if (result == 0) {
			return 0;
		} else {
			return result;
		}
	}
	
	@Override
	public String toString() {
		return "value " + value + " frequency " + frequency;
	}
	
}
