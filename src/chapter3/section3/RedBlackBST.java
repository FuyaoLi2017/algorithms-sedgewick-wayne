package chapter3.section3;

import edu.princeton.cs.algs4.Queue;

import java.util.NoSuchElementException;

/**
 * Created by rene on 18/06/17.
 */
public class RedBlackBST<Key extends Comparable<Key>, Value> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private class Node {
        Key key;
        Value value;
        Node left, right;

        boolean color;
        int size;

        Node(Key key, Value value, int size, boolean color) {
            this.key = key;
            this.value = value;

            this.size = size;
            this.color = color;
        }
    }

    private Node root;

    public int size() {
        return size(root);
    }

    protected int size(Node node) {
        if(node == null) {
            return 0;
        }

        return node.size;
    }

    private boolean isRed(Node node) {
        if(node == null) {
            return false;
        }

        return node.color == RED;
    }

    private Node rotateLeft(Node node) {
        if(node == null || node.right == null) {
            return node;
        }

        Node newRoot = node.right;

        node.right = newRoot.left;
        newRoot.left = node;

        newRoot.color = node.color;
        node.color = RED;

        newRoot.size = node.size;
        node.size = size(node.left) + 1 + size(node.right);

        return newRoot;
    }

    private Node rotateRight(Node node) {
        if(node == null || node.left == null) {
            return node;
        }

        Node newRoot = node.left;

        node.left = newRoot.right;
        newRoot.right = node;

        newRoot.color = node.color;
        node.color = RED;

        newRoot.size = node.size;
        node.size = size(node.left) + 1 + size(node.right);

        return newRoot;
    }

    private void flipColors(Node node) {
        if(node == null || node.left == null || node.right == null) {
            return;
        }

        node.color = RED;
        node.left.color = BLACK;
        node.right.color = BLACK;
    }

    private void put(Key key, Value value) {
        if(key == null) {
            return;
        }

        root = put(root, key, value);
        root.color = BLACK;
    }

    private Node put(Node node, Key key, Value value) {
        if(node == null) {
            return new Node(key, value, 1, RED);
        }

        int compare = key.compareTo(node.key);

        if(compare < 0) {
            node.left = put(node.left, key, value);
        } else if(compare > 0) {
            node.right = put(node.right, key, value);
        } else {
            node.value = value;
        }

        if(isRed(node.right) && !isRed(node.left)) {
            node = rotateLeft(node);
        }
        if(isRed(node.left) && isRed(node.left.left)) {
            node = rotateRight(node);
        }
        if(isRed(node.left) && isRed(node.right)) {
            flipColors(node);
        }

        node.size = size(node.left) + 1 + size(node.right);
        return node;
    }

    public Value get(Key key) {
        if(key == null) {
            return null;
        }

        return get(root, key);
    }

    private Value get(Node node, Key key) {
        if(node == null) {
            return null;
        }

        int compare = key.compareTo(node.key);
        if(compare < 0) {
            return get(node.left, key);
        } else if(compare > 0) {
            return get(node.right, key);
        } else {
            return node.value;
        }
    }

    public boolean contains(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("Argument to contains() cannot be null");
        }
        return get(key) != null;
    }

    public Key min() {
        if(root == null) {
            throw new NoSuchElementException("Empty binary search tree");
        }

        return min(root).key;
    }

    private Node min(Node node) {
        if(node.left == null) {
            return node;
        }

        return min(node.left);
    }

    public Key max() {
        if(root == null) {
            throw new NoSuchElementException("Empty binary search tree");
        }

        return max(root).key;
    }

    private Node max(Node node) {
        if(node.right == null) {
            return node;
        }

        return max(node.right);
    }

    public Key floor(Key key) {
        Node node = floor(root, key);
        if(node == null) {
            return null;
        }

        return node.key;
    }

    private Node floor(Node node, Key key) {
        if(node == null) {
            return null;
        }

        int compare = key.compareTo(node.key);

        if(compare == 0) {
            return node;
        } else if(compare < 0) {
            return floor(node.left, key);
        } else {
            Node rightNode = floor(node.right, key);
            if(rightNode != null) {
                return rightNode;
            } else {
                return node;
            }
        }
    }

    public Key ceiling(Key key) {
        Node node = ceiling(root, key);
        if(node == null) {
            return null;
        }

        return node.key;
    }

    private Node ceiling(Node node, Key key) {
        if(node == null) {
            return null;
        }

        int compare = key.compareTo(node.key);

        if(compare == 0) {
            return node;
        } else if(compare > 0) {
            return ceiling(node.right, key);
        } else {
            Node leftNode = ceiling(node.left, key);
            if(leftNode != null) {
                return leftNode;
            } else {
                return node;
            }
        }
    }

    public Key select(int index) {
        if(index >= size()) {
            throw new IllegalArgumentException("Index is higher than tree size");
        }

        return select(root, index).key;
    }

    private Node select(Node node, int index) {
        int leftSubtreeSize = size(node.left);

        if(leftSubtreeSize == index) {
            return node;
        } else if (leftSubtreeSize > index) {
            return select(node.left, index);
        } else {
            return select(node.right, index - leftSubtreeSize - 1);
        }
    }

    public int rank(Key key) {
        return rank(root, key);
    }

    private int rank(Node node, Key key) {
        if(node == null) {
            return 0;
        }

        //Returns the number of keys less than node.key in the subtree rooted at node
        int compare = key.compareTo(node.key);
        if(compare < 0) {
            return rank(node.left, key);
        } else if(compare > 0) {
            return size(node.left) + 1 + rank(node.right, key);
        } else {
            return size(node.left);
        }
    }

    public Iterable<Key> keys() {
        return keys(min(), max());
    }

    public Iterable<Key> keys(Key low, Key high) {
        Queue<Key> queue = new Queue<>();
        keys(root, queue, low, high);
        return queue;
    }

    private void keys(Node node, Queue<Key> queue, Key low, Key high) {
        if(node == null) {
            return;
        }

        int compareLow = low.compareTo(node.key);
        int compareHigh = high.compareTo(node.key);

        if(compareLow < 0) {
            keys(node.left, queue, low, high);
        }

        if(compareLow <= 0 && compareHigh >= 0) {
            queue.enqueue(node.key);
        }

        if(compareHigh > 0) {
            keys(node.right, queue, low, high);
        }
    }

}