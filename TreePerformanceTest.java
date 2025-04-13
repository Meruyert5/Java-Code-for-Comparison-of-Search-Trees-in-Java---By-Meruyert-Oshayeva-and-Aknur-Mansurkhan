import java.util.*;

interface Tree {
    void insert(int key);
    boolean search(int key);
    void delete(int key);
}

public class TreePerformanceTest {
    public static void main(String[] args) {
        int[] inpSz = {10000, 20000, 30000, 40000, 50000};
        Random rnd = new Random();

        for (int size : inpSz) {
            System.out.println("\nTesting for input size: " + size);

            int[] origInp = genInp(size);

            tstTr(new BST(), "BST", origInp, rnd);

            tstTr(new RBTree(), "RBTree", origInp, rnd);

            tstTr(new SegmentTree(origInp), "SegmentTree", origInp, rnd);

            tstTr(new TernaryTree(), "TernaryTree", origInp, rnd);

            tstTr(new NaryTree(5), "NaryTree", origInp, rnd);
        }
    }

    private static void tstTr(Tree tree, String trNm, int[] inp, Random rnd) {
        System.out.println("\nTesting " + trNm + ":");

        long insrtStart = System.nanoTime();
        for (int key : inp) {
            tree.insert(key);
        }
        long insrtEnd = System.nanoTime();
        System.out.println("Insertion time: " + (insrtEnd - insrtStart) + " ns");

        long srchStart = System.nanoTime();
        for (int i = 0; i < inp.length; i++) {
            int keySrch = inp[rnd.nextInt(inp.length)];
            tree.search(keySrch);
        }
        long srchEnd = System.nanoTime();
        System.out.println("Search time: " + (srchEnd - srchStart) + " ns");

        long delStart = System.nanoTime();
        for (int i = 0; i < inp.length / 2; i++) {
            int keyDel = inp[rnd.nextInt(inp.length)];
            tree.delete(keyDel);
        }
        long delEnd = System.nanoTime();
        System.out.println("Deletion time: " + (delEnd - delStart) + " ns");
    }

    private static int[] genInp(int size) {
        Random rnd = new Random();
        int[] inp = new int[size];
        for (int i = 0; i < size; i++) {
            inp[i] = rnd.nextInt(size * 10);
        }
        return inp;
    }
}

class BST implements Tree {
    static class Node {
        int key;
        Node left, right;

        public Node(int key) {
            this.key = key;
            left = right = null;
        }
    }

    Node root;

    public BST() {
        root = null;
    }

    public void insert(int key) {
        root = insRec(root, key);
    }

    private Node insRec(Node root, int key) {
        if (root == null) {
            root = new Node(key);
            return root;
        }
        if (key < root.key) {
            root.left = insRec(root.left, key);
        } else if (key > root.key) {
            root.right = insRec(root.right, key);
        }
        return root;
    }

    public boolean search(int key) {
        return srchRec(root, key);
    }

    private boolean srchRec(Node root, int key) {
        if (root == null) {
            return false;
        }
        if (root.key == key) {
            return true;
        }
        return key < root.key ? srchRec(root.left, key) : srchRec(root.right, key);
    }

    public void delete(int key) {
        root = delRec(root, key);
    }

    private Node delRec(Node root, int key) {
        if (root == null) {
            return root;
        }
        if (key < root.key) {
            root.left = delRec(root.left, key);
        } else if (key > root.key) {
            root.right = delRec(root.right, key);
        } else {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }
            root.key = minVal(root.right);
            root.right = delRec(root.right, root.key);
        }
        return root;
    }

    private int minVal(Node root) {
        int minVal = root.key;
        while (root.left != null) {
            root = root.left;
            minVal = root.key;
        }
        return minVal;
    }
}

class RBTree implements Tree {
    static class Node {
        int key;
        Node left, right, prnt;
        boolean isRd;

        public Node(int key) {
            this.key = key;
            this.isRd = true;
            this.left = this.right = this.prnt = null;
        }
    }

    private Node root;
    private Node TNULL;

    public RBTree() {
        TNULL = new Node(0);
        TNULL.isRd = false;
        root = TNULL;
    }

    public void insert(int key) {
        Node node = new Node(key);
        node.left = TNULL;
        node.right = TNULL;
        node.prnt = null;

        Node prnt = null;
        Node curr = root;

        while (curr != TNULL) {
            prnt = curr;
            if (key < curr.key) {
                curr = curr.left;
            } else {
                curr = curr.right;
            }
        }

        node.prnt = prnt;
        if (prnt == null) {
            root = node;
        } else if (key < prnt.key) {
            prnt.left = node;
        } else {
            prnt.right = node;
        }

        if (node.prnt == null) {
            node.isRd = false;
            return;
        }

        if (node.prnt.prnt == null) {
            return;
        }

        balInsert(node);
    }

    private void balInsert(Node node) {
        Node uncl;
        while (node.prnt.isRd) {
            if (node.prnt == node.prnt.prnt.left) {
                uncl = node.prnt.prnt.right;
                if (uncl.isRd) {
                    uncl.isRd = false;
                    node.prnt.isRd = false;
                    node.prnt.prnt.isRd = true;
                    node = node.prnt.prnt;
                } else {
                    if (node == node.prnt.right) {
                        node = node.prnt;
                        rotL(node);
                    }
                    node.prnt.isRd = false;
                    node.prnt.prnt.isRd = true;
                    rotR(node.prnt.prnt);
                }
            } else {
                uncl = node.prnt.prnt.left;
                if (uncl.isRd) {
                    uncl.isRd = false;
                    node.prnt.isRd = false;
                    node.prnt.prnt.isRd = true;
                    node = node.prnt.prnt;
                } else {
                    if (node == node.prnt.left) {
                        node = node.prnt;
                        rotR(node);
                    }
                    node.prnt.isRd = false;
                    node.prnt.prnt.isRd = true;
                    rotL(node.prnt.prnt);
                }
            }
            if (node == root) break;
        }
        root.isRd = false;
    }

    public boolean search(int key) {
        return srchTr(root, key) != TNULL;
    }

    private Node srchTr(Node node, int key) {
        if (node == TNULL || key == node.key) {
            return node;
        }
        if (key < node.key) {
            return srchTr(node.left, key);
        }
        return srchTr(node.right, key);
    }

    public void delete(int key) {
        delNd(root, key);
    }

    private void delNd(Node node, int key) {
        Node trgt = srchTr(node, key);
        if (trgt == TNULL) return;

        Node y = trgt;
        Node x;
        boolean yOrigClr = y.isRd;

        if (trgt.left == TNULL) {
            x = trgt.right;
            transplnt(trgt, trgt.right);
        } else if (trgt.right == TNULL) {
            x = trgt.left;
            transplnt(trgt, trgt.left);
        } else {
            y = min(trgt.right);
            yOrigClr = y.isRd;
            x = y.right;
            if (y.prnt == trgt) {
                x.prnt = y;
            } else {
                transplnt(y, y.right);
                y.right = trgt.right;
                y.right.prnt = y;
            }
            transplnt(trgt, y);
            y.left = trgt.left;
            y.left.prnt = y;
            y.isRd = trgt.isRd;
        }

        if (!yOrigClr) {
            balDelete(x);
        }
    }

    private void balDelete(Node x) {
        Node sib;
        while (x != root && !x.isRd) {
            if (x == x.prnt.left) {
                sib = x.prnt.right;
                if (sib.isRd) {
                    sib.isRd = false;
                    x.prnt.isRd = true;
                    rotL(x.prnt);
                    sib = x.prnt.right;
                }
                if (!sib.left.isRd && !sib.right.isRd) {
                    sib.isRd = true;
                    x = x.prnt;
                } else {
                    if (!sib.right.isRd) {
                        sib.left.isRd = false;
                        sib.isRd = true;
                        rotR(sib);
                        sib = x.prnt.right;
                    }
                    sib.isRd = x.prnt.isRd;
                    x.prnt.isRd = false;
                    sib.right.isRd = false;
                    rotL(x.prnt);
                    x = root;
                }
            } else {
                sib = x.prnt.left;
                if (sib.isRd) {
                    sib.isRd = false;
                    x.prnt.isRd = true;
                    rotR(x.prnt);
                    sib = x.prnt.left;
                }
                if (!sib.left.isRd && !sib.right.isRd) {
                    sib.isRd = true;
                    x = x.prnt;
                } else {
                    if (!sib.left.isRd) {
                        sib.right.isRd = false;
                        sib.isRd = true;
                        rotL(sib);
                        sib = x.prnt.left;
                    }
                    sib.isRd = x.prnt.isRd;
                    x.prnt.isRd = false;
                    sib.left.isRd = false;
                    rotR(x.prnt);
                    x = root;
                }
            }
        }
        x.isRd = false;
    }

    private void rotL(Node node) {
        Node y = node.right;
        node.right = y.left;
        if (y.left != TNULL) y.left.prnt = node;
        y.prnt = node.prnt;
        if (node.prnt == null) root = y;
        else if (node == node.prnt.left) node.prnt.left = y;
        else node.prnt.right = y;
        y.left = node;
        node.prnt = y;
    }

    private void rotR(Node node) {
        Node y = node.left;
        node.left = y.right;
        if (y.right != TNULL) y.right.prnt = node;
        y.prnt = node.prnt;
        if (node.prnt == null) root = y;
        else if (node == node.prnt.right) node.prnt.right = y;
        else node.prnt.left = y;
        y.right = node;
        node.prnt = y;
    }

    private void transplnt(Node u, Node v) {
        if (u.prnt == null) root = v;
        else if (u == u.prnt.left) u.prnt.left = v;
        else u.prnt.right = v;
        v.prnt = u.prnt;
    }

    private Node min(Node node) {
        while (node.left != TNULL) {
            node = node.left;
        }
        return node;
    }
}

class SegmentTree implements Tree {
    private int[] segTr;
    private int[] inp;

    public SegmentTree(int[] inp) {
        this.inp = inp;
        this.segTr = new int[inp.length * 4];
        build(0, 0, inp.length - 1);
    }

    private void build(int node, int start, int end) {
        if (start == end) {
            segTr[node] = inp[start];
        } else {
            int mid = (start + end) / 2;
            build(2 * node + 1, start, mid);
            build(2 * node + 2, mid + 1, end);
            segTr[node] = segTr[2 * node + 1] + segTr[2 * node + 2];
        }
    }

    @Override
    public void insert(int key) {
    }

    @Override
    public boolean search(int key) {
        return false;
    }

    @Override
    public void delete(int key) {
        update(key, 0);
    }

    public void update(int idx, int val) {
        updTr(0, 0, inp.length - 1, idx, val);
    }

    private void updTr(int node, int start, int end, int idx, int val) {
        if (start == end) {
            inp[start] = val;
            segTr[node] = val;
        } else {
            int mid = (start + end) / 2;
            if (idx <= mid) {
                updTr(2 * node + 1, start, mid, idx, val);
            } else {
                updTr(2 * node + 2, mid + 1, end, idx, val);
            }
            segTr[node] = segTr[2 * node + 1] + segTr[2 * node + 2];
        }
    }
}

class TernaryTree implements Tree {
    static class Node {
        int key;
        Node left, mid, right;

        public Node(int key) {
            this.key = key;
            left = mid = right = null;
        }
    }

    private Node root;

    public TernaryTree() {
        root = null;
    }

    public void insert(int key) {
        root = insRec(root, key);
    }

    private Node insRec(Node node, int key) {
        if (node == null) {
            node = new Node(key);
            return node;
        }

        if (key < node.key) {
            node.left = insRec(node.left, key);
        } else if (key > node.key) {
            node.right = insRec(node.right, key);
        } else {
            if (node.mid == null) {
                node.mid = new Node(key);
            } else {
                node.mid = insRec(node.mid, key);
            }
        }
        return node;
    }

    public boolean search(int key) {
        return srchRec(root, key);
    }

    private boolean srchRec(Node node, int key) {
        if (node == null) {
            return false;
        }
        if (key == node.key) {
            return true;
        }
        if (key < node.key) {
            return srchRec(node.left, key);
        } else {
            return srchRec(node.right, key);
        }
    }

    public void delete(int key) {
        root = delRec(root, key);
    }

    private Node delRec(Node node, int key) {
        if (node == null) {
            return node;
        }

        if (key < node.key) {
            node.left = delRec(node.left, key);
        } else if (key > node.key) {
            node.right = delRec(node.right, key);
        } else {
            if (node.mid != null) {
                node.mid = delRec(node.mid, key);
            }
        }
        return node;
    }
}


class NaryTree implements Tree {
    static class Node {
        int key;
        List<Node> chldrn;

        public Node(int key) {
            this.key = key;
            this.chldrn = new ArrayList<>();
        }
    }

    private Node root;
    private int maxDeg;

    public NaryTree(int maxDeg) {
        this.maxDeg = maxDeg;
        this.root = null;
    }

    public void insert(int key) {
        root = insRec(root, key);
    }

    private Node insRec(Node node, int key) {
        if (node == null) {
            return new Node(key);
        }

        if (node.chldrn.size() < maxDeg) {
            node.chldrn.add(new Node(key));
        } else {
            for (Node chld : node.chldrn) {
                Node res = insRec(chld, key);
                if (res != null) {
                    return node;
                }
            }
        }
        return node;
    }


    public boolean search(int key) {
        return srchRec(root, key);
    }

    private boolean srchRec(Node node, int key) {
        if (node == null) {
            return false;
        }
        if (node.key == key) {
            return true;
        }
        for (Node chld : node.chldrn) {
            if (srchRec(chld, key)) {
                return true;
            }
        }
        return false;
    }

    public void delete(int key) {
        root = delRec(root, key);
    }

    private Node delRec(Node root, int key) {
        if (root == null) {
            return null;
        }

        Deque<Node> stk = new ArrayDeque<>();
        stk.push(root);
    
        while (!stk.isEmpty()) {
            Node node = stk.pop();
        
            if (node.key == key) {
                return null;
            }

            for (Node chld : node.chldrn) {
                stk.push(chld);
            }
        }

        return root;
    }
}
