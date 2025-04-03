import java.util.*;

interface Tree {
    void insert(int key);
    boolean search(int key);
    void delete(int key);
}

public class TreePerformanceTest {
    public static void main(String[] args) {
        int[] inputSizes = {10000, 20000, 30000, 40000, 50000};
        Random rand = new Random();

        for (int size : inputSizes) {
            System.out.println("\nTesting for input size: " + size);
            int[] input = generateInput(size);
            testTree(new BST(), "BST", input, rand);
            testTree(new RBTree(), "RBTree", input, rand);
            testTree(new SegmentTree(input), "SegmentTree", input, rand);
            testTree(new TernaryTree(), "TernaryTree", input, rand);
            testTree(new NaryTree(5), "NaryTree", input, rand);
        }
    }

    private static void testTree(Tree tree, String treeName, int[] input, Random rand) {
        System.out.println("\nTesting " + treeName + ":");
        measureTime(tree::insert, input, "Insertion");
        measureTime(tree::search, input, "Search");
        measureTime(tree::delete, Arrays.copyOfRange(input, 0, input.length / 2), "Deletion");
    }

    private static void measureTime(Consumer<int[]> operation, int[] input, String operationName) {
        long start = System.nanoTime();
        for (int key : input) {
            operation.accept(key);
        }
        long end = System.nanoTime();
        System.out.println(operationName + " time: " + (end - start) + " ns");
    }

    private static int[] generateInput(int size) {
        Random rand = new Random();
        return rand.ints(size, 0, size * 10).toArray();
    }
}

class BST implements Tree {
    private class Node {
        int key;
        Node left, right;
        Node(int key) { this.key = key; }
    }

    private Node root;

    public void insert(int key) {
        root = insertRec(root, key);
    }

    private Node insertRec(Node root, int key) {
        if (root == null) return new Node(key);
        if (key < root.key) root.left = insertRec(root.left, key);
        else if (key > root.key) root.right = insertRec(root.right, key);
        return root;
    }

    public boolean search(int key) {
        return searchRec(root, key);
    }

    private boolean searchRec(Node root, int key) {
        if (root == null) return false;
        if (root.key == key) return true;
        return key < root.key ? searchRec(root.left, key) : searchRec(root.right, key);
    }

    public void delete(int key) {
        root = deleteRec(root, key);
    }

    private Node deleteRec(Node root, int key) {
        if (root == null) return root;
        if (key < root.key) root.left = deleteRec(root.left, key);
        else if (key > root.key) root.right = deleteRec(root.right, key);
        else if (root.left == null) return root.right;
        else if (root.right == null) return root.left;
        else {
            root.key = minValue(root.right);
            root.right = deleteRec(root.right, root.key);
        }
        return root;
    }

    private int minValue(Node root) {
        int minValue = root.key;
        while (root.left != null) {
            root = root.left;
            minValue = root.key;
        }
        return minValue;
    }
}

class RBTree implements Tree {
    private class Node {
        int key;
        Node left, right, parent;
        boolean isRed;
        Node(int key) { this.key = key; this.isRed = true; }
    }

    private Node root, TNULL;

    public RBTree() {
        TNULL = new Node(0);
        TNULL.isRed = false;
        root = TNULL;
    }

    public void insert(int key) {
        Node node = new Node(key);
        node.left = node.right = TNULL;
        insertFixup(node);
    }

    public boolean search(int key) {
        return searchRec(root, key) != TNULL;
    }

    private Node searchRec(Node root, int key) {
        if (root == TNULL || key == root.key) return root;
        return key < root.key ? searchRec(root.left, key) : searchRec(root.right, key);
    }

    public void delete(int key) {
        deleteRec(root, key);
    }

    private void insertFixup(Node node) { /* RB Tree balancing code omitted for brevity */ }
    private void deleteRec(Node node, int key) { /* Deletion and balancing omitted for brevity */ }
}

class SegmentTree implements Tree {
    private int[] segmentTree, input;

    public SegmentTree(int[] input) {
        this.input = input;
        this.segmentTree = new int[input.length * 4];
        build(0, 0, input.length - 1);
    }

    public void insert(int key) { /* Not applicable for SegmentTree */ }
    public boolean search(int key) { return false; }
    public void delete(int key) { update(key, 0); }

    private void build(int node, int start, int end) {
        if (start == end) segmentTree[node] = input[start];
        else {
            int mid = (start + end) / 2;
            build(2 * node + 1, start, mid);
            build(2 * node + 2, mid + 1, end);
            segmentTree[node] = segmentTree[2 * node + 1] + segmentTree[2 * node + 2];
        }
    }

    private void update(int index, int value) {
        updateTree(0, 0, input.length - 1, index, value);
    }

    private void updateTree(int node, int start, int end, int index, int value) {
        if (start == end) {
            input[start] = value;
            segmentTree[node] = value;
        } else {
            int mid = (start + end) / 2;
            if (index <= mid) updateTree(2 * node + 1, start, mid, index, value);
            else updateTree(2 * node + 2, mid + 1, end, index, value);
            segmentTree[node] = segmentTree[2 * node + 1] + segmentTree[2 * node + 2];
        }
    }
}

class TernaryTree implements Tree {
    private class Node {
        int key;
        Node left, mid, right;
        Node(int key) { this.key = key; }
    }

    private Node root;

    public void insert(int key) { root = insertRec(root, key); }
    private Node insertRec(Node node, int key) { /* Logic for inserting omitted */ return node; }
    public boolean search(int key) { return searchRec(root, key); }
    private boolean searchRec(Node node, int key) { return node != null && (node.key == key || searchRec(node.left, key)); }
    public void delete(int key) { root = deleteRec(root, key); }
    private Node deleteRec(Node node, int key) { /* Logic for deleting omitted */ return node; }
}

class NaryTree implements Tree {
    private class Node {
        int key;
        List<Node> children;
        Node(int key) { this.key = key; children = new ArrayList<>(); }
    }

    private Node root;
    private int maxDegree;

    public NaryTree(int maxDegree) {
        this.maxDegree = maxDegree;
        this.root = null;
    }

    public void insert(int key) { root = insertRec(root, key); }
    private Node insertRec(Node node, int key) { return node; }
    public boolean search(int key) { return searchRec(root, key); }
    private boolean searchRec(Node node, int key) { return node != null && node.key == key; }
    public void delete(int key) { root = deleteRec(root, key); }
    private Node deleteRec(Node root, int key) { return root; }
}
