package ca.etsmtl.huffman;

public class BinaryTree {
	// Node class
	private class Node {
		public HuffmanNode data; // node data
		public Node right; // right child
		public Node left; // left child

		public Node(HuffmanNode data, Node left, Node right) {
			this.data = data;
			this.left = left;
			this.right = right;
		}
	}

	private Node root;
	private int size;

	public BinaryTree() {
		size = 0;
		root = null;
	}

	public void printTree() {
		if (size == 0) {
			System.out.println("Empty");
		} else {
			System.out.println("Tree contents:");
			inorder(root, 0, 0);
		}
	}

	public void inorder(Node current, int i, int j) {
		if (current != null) {
			inorder(current.left, ++i, j);
			System.out.println(current.data + " = " + i + ", " + j);
			inorder(current.right, i, ++j);
		}
	}

	public void insert(HuffmanNode data) {
		root = insert(data, root);
	}

	private Node insert(HuffmanNode data, Node current) {
		if (current == null) {
			size++;
			current = new Node(data, null, null);
		} else if (data.compareTo(current.data) < 0) {
			current.left = insert(data, current.left);
		} else if (data.compareTo(current.data) > 0) {
			current.right = insert(data, current.right);
		}
		return current;
	}

	/*
	 * Searches binary tree for a comparable object equal to the given object.
	 * Returns true if object is found and false otherwise.
	 */
	public boolean search(HuffmanNode data) {
		return search(data, root);
	}

	// Search helper method
	private boolean search(HuffmanNode data, Node current) {
		if (current == null) {
			return false;
		} else if (current.data == data) {
			return true;
		} else if (data.compareTo(current.data) < 0) {
			return search(data, current.left);
		} else {
			return search(data, current.right);
		}
	}

	// Returns true if node with target data was deleted, false otherwise
	public boolean remove(HuffmanNode key) {
		if (search(key)) {
			delete(key);
			return true;
		} else {
			return false;
		}
	}

	public void delete(HuffmanNode key) {
		// Algorithm note: There are four cases to consider:
		// 1. The node is a leaf.
		// 2. The node has no left child.
		// 3. The node has no right child.
		// 4. The node has two children.

		// initialize parent and current to root
		Node current = root;
		Node parent = root;

		boolean isLeftChild = true;

		// while loop to search for node to delete
		while (current.data.compareTo(key) != 0) {
			// assign parent to current
			parent = current;
			if (current.data.compareTo(key) > 0) {
				isLeftChild = true; // current is a left child
				current = current.left;
			} else {
				isLeftChild = false; // current is a right child
				current = current.right;
			}
			if (current == null) {
				return;
			}
		}
		// test for a leaf
		if (current.left == null && current.right == null) {
			if (current == root) {
				root = null;
			} else if (isLeftChild) {
				parent.left = null;
			} else {
				parent.right = null;
			}
		}
		// test for no right child
		else if (current.right == null) {
			if (current == root) {
				root = current.left;
			} else if (isLeftChild) {
				parent.left = current.left;
			} else {
				parent.right = current.left;
			}
		// test for no left child
		} else if (current.left == null) {
			if (current == root) {
				root = current.right;
			} else if (isLeftChild) {
				parent.left = current.right;
			} else {
				parent.right = current.right;
			}
		} else {
			Node successor = getSuccessor(current); // get successor
			if (current == root) {
				root = successor;
			} else if (isLeftChild) {
				parent.left = successor; // set node to delete to successor
			} else {
				parent.right = successor;
			}
			// attach current's left to successor's left since successor has no
			// left child
			successor.left = current.left;
		}
	}

	// This method searches the successor of a node to be deleted
	private Node getSuccessor(Node delNode) {
		Node successorParent = delNode;
		Node successor = delNode;
		Node current = delNode.right;

		while (current != null) {
			successorParent = successor;
			successor = current;
			current = current.left;
		}
		if (successor != delNode.right) {
			successorParent.left = successor.right;
			successor.right = delNode.right;
		}
		return successor;
	}

}