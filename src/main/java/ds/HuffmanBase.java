package ds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.PriorityQueue;

/**
 * Compress or expand a binary input stream using the Huffman algorithm.
 *
 * <p>****************************************************************************
 */

/**
 * The {@code Huffman} class provides static methods for compressing and expanding a binary input
 * using Huffman codes over the 8-bit extended ASCII alphabet.
 *
 * <p>For additional documentation, see <a
 * href="https://algs4.cs.princeton.edu/55compression">Section 5.5</a> of <i>Algorithms, 4th
 * Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public abstract class HuffmanBase {

  // alphabet size of extended ASCII
  static final int R = 256;

  static final char NULL_CHARACTER = '\0';
  static final char ZERO_CHARACTER = '0';
  static final char ONE_CHARACTER = '1';

  protected final String input;
  protected final BinaryInputStream bis;
  protected final BinaryOutputStream bos;

  public HuffmanBase(String input) {
    this.input = input;
    this.bis = new BinaryInputStream(new ByteArrayInputStream("".getBytes()));
    this.bos = new BinaryOutputStream(new ByteArrayOutputStream());
  }

  /**
   * Reads a sequence of 8-bit bytes from standard input; compresses them using Huffman codes with
   * an 8-bit alphabet; and writes the results to standard output.
   */
  @SuppressWarnings("PMD.LawOfDemeter")
  public void compress() {
    // read the input
    String s = bis.readString();
    char[] input = s.toCharArray();

    // tabulate frequency counts
    int[] freq = new int[R];
    for (int i : input) freq[i]++;

    // build Huffman trie
    Node root = buildTrie(freq);

    // build code table
    String[] st = new String[R];
    buildCode(st, root, "");

    // print trie for decoder
    writeTrie(root);

    // print number of bytes in original uncompressed message
    bos.write(input.length);

    // use Huffman code to encode input
    for (int i : input) {
      String code = st[i];
      for (int j = 0; j < code.length(); j++) {
        if (code.charAt(j) == ZERO_CHARACTER) bos.write(false);
        else if (code.charAt(j) == ONE_CHARACTER) bos.write(true);
        else throw new IllegalStateException("Illegal state");
      }
    }

    // close output stream
    bos.close();
  }

  // build the Huffman trie given frequencies
  protected Node buildTrie(int... freq) {

    // initialize priority queue with singleton trees
    PriorityQueue<Node> pq = new PriorityQueue<>();
    for (char c = 0; c < R; c++) if (freq[c] > 0) pq.offer(new Node(c, freq[c], null, null));

    // merge two smallest trees
    while (pq.size() > 1) {
      Node left = pq.poll();
      Node right = pq.poll();
      Node parent = new Node(NULL_CHARACTER, left.freq + right.freq, left, right);
      pq.offer(parent);
    }
    return pq.poll();
  }

  // write bitstring-encoded trie to standard output
  protected void writeTrie(Node x) {
    if (x.isLeaf()) {
      bos.write(true);
      bos.write(x.ch, 8);
      return;
    }
    bos.write(false);
    writeTrie(x.left);
    writeTrie(x.right);
  }

  // make a lookup table from symbols and their encodings
  protected void buildCode(String[] st, Node x, String s) {
    if (x.isLeaf()) st[x.ch] = s;
    else {
      buildCode(st, x.left, s + '0');
      buildCode(st, x.right, s + '1');
    }
  }

  /**
   * Reads a sequence of bits that represents a Huffman-compressed message from standard input;
   * expands them; and writes the results to standard output.
   */
  @SuppressWarnings("PMD.LawOfDemeter")
  public void expand() {

    // read in Huffman trie from input stream
    Node root = readTrie();

    // number of bytes to write
    int length = bis.readInt();

    // decode using the Huffman trie
    for (int i = 0; i < length; i++) {
      Node x = root;
      while (!x.isLeaf()) {
        boolean bit = bis.readBoolean();
        if (bit) x = x.right;
        else x = x.left;
      }
      bos.write(x.ch, 8);
    }
    bos.close();
  }

  protected Node readTrie() {
    boolean isLeaf = bis.readBoolean();
    if (isLeaf) return new Node(bis.readChar(), -1, null, null);
    else return new Node(NULL_CHARACTER, -1, readTrie(), readTrie());
  }

  // Huffman trie node
  @SuppressWarnings("nullness")
  static class Node implements Comparable<Node> {
    private final char ch;
    private final int freq;
    private final Node left;
    private final Node right;

    Node(char ch, int freq, Node left, Node right) {
      this.ch = ch;
      this.freq = freq;
      this.left = left;
      this.right = right;
    }

    // is the node a leaf node?
    private boolean isLeaf() {
      assert (left == null && right == null || left != null && right != null);
      return left == null && right == null;
    }

    // compare, based on frequency
    @Override
    public int compareTo(Node that) {
      return this.freq - that.freq;
    }
  }
}