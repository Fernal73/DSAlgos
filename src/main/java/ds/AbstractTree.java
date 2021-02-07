package ds;

import static ds.MathUtils.requireInRangeInclusive;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import ds.Tree.TraversalOrder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

public abstract class AbstractTree<T extends Comparable<T>> implements Tree<T> {
  /** The root node. */
  ITreeNode<T> root;
  
  @Override
  public abstract void removeMin();
  
  @Override
  public abstract void removeMax();


  @Override
  public ITreeNode<T> root() {
    return root;
  }

  /**
   * Checks if the binary tree is empty.
   *
   * @return {@code true} if the binary tree is empty.
   */
  @Override
  public boolean isEmpty() {
    return isNull(root);
  }

  @Override
  public Iterator<T> iterator(TraversalOrder order) {
    return new TreeIterator<>(root, order);
  }

  /**
   * Returns the number value-value pairs in the binary tree.
   *
   * @return the number value-value pairs in the binary tree
   */
  public int size() {
    return size(root);
  }

  /**
   * Returns the number of nodes in the subtree.
   *
   * @param x the subtree
   * @return the number of nodes in the subtree
   */
  private int size(ITreeNode<T> x) {
    return isNull(x) ? 0 : x.size();
  }

  /**
   * Returns the number of values in the binary tree in the given range.
   *
   * @param lo minimum endpoint
   * @param hi maximum endpoint
   * @return the number of values in the binary tree between {@code lo} (inclusive) and {@code hi}
   *     (exclusive)
   * @throws NullPointerException if either {@code lo} or {@code hi} is {@code null}
   */
  public int size(T lo, T hi) {
    requireNonNull(lo);
    requireNonNull(hi);
    if (lo.compareTo(hi) > 0) return 0;
    if (contains(hi)) return rank(hi) - rank(lo) + 1;
    else return rank(hi) - rank(lo);
  }

  /**
   * Returns the height of the internal AVL tree. It is assumed that the height of an empty tree is
   * -1 and the height of a tree with just one node is 0.
   *
   * @return the height of the internal AVL tree
   */
  public int height() {
    return height(root);
  }

  /**
   * Returns the height of the subtree.
   *
   * @param x the subtree
   * @return the height of the subtree.
   */
  private int height(ITreeNode<T> x) {
    return isNull(x) ? -1 : x.height();
  }

  /**
   * Returns the smallest value in the binary tree.
   *
   * @return the smallest value in the binary tree
   * @throws NoSuchElementException if the binary tree is empty
   */
  @SuppressWarnings("PMD.LawOfDemeter")
  public T min() {
    if (isEmpty()) throw new NoSuchElementException("called min() with empty binary tree");
    return min(root).value();
  }

  /**
   * Returns the node with the smallest value in the subtree.
   *
   * @param x the subtree
   * @return the node with the smallest value in the subtree
   */
  private ITreeNode<T> min(ITreeNode<T> node) {
    ITreeNode<T> x = node;
    while (nonNull(x.left())) x = x.left();
    return x;
  }

  /**
   * Returns the largest value in the binary tree.
   *
   * @return the largest value in the binary tree
   * @throws NoSuchElementException if the binary tree is empty
   */
  @SuppressWarnings("PMD.LawOfDemeter")
  public T max() {
    if (isEmpty()) throw new NoSuchElementException("called max() with empty binary tree");
    return max(root).value();
  }

  /**
   * Returns the node with the largest value in the subtree.
   *
   * @param x the subtree
   * @return the node with the largest value in the subtree
   */
  private ITreeNode<T> max(ITreeNode<T> node) {
    ITreeNode<T> x = node;
    while (nonNull(x.right())) x = x.right();
    return x;
  }
  
  /**
   * Returns the largest value in the binary tree less than or equal to {@code value}.
   *
   * @param val the value
   * @return the largest value in the binary tree less than or equal to {@code value}
   * @throws NoSuchElementException if the binary tree is empty
   * @throws IllegalArgumentException if {@code value} is {@code null}
   */
  public T floor(T val) {
    requireNonNull(val);
    if (isEmpty()) throw new NoSuchElementException("called floor() with empty binary tree");
    ITreeNode<T> x = floor(root, val);
    if (isNull(x)) return null;
    else return x.value();
  }

  /**
   * Returns the node in the subtree with the largest value less than or equal to the given value.
   *
   * @param x the subtree
   * @param value the value
   * @return the node in the subtree with the largest value less than or equal to the given value
   */
  @SuppressWarnings("checkstyle:ReturnCount")
  private ITreeNode<T> floor(ITreeNode<T> x, T val) {
    if (isNull(x)) return null;
    int cmp = val.compareTo(x.value());
    if (cmp == 0) return x;
    if (cmp < 0) return floor(x.left(), val);
    ITreeNode<T> y = floor(x.right(), val);
    if (isNull(y)) return x;
    else return y;
  }

  /**
   * Returns the smallest value in the binary tree greater than or equal to {@code value}.
   *
   * @param val the value
   * @return the smallest value in the binary tree greater than or equal to {@code value}
   * @throws NoSuchElementException if the binary tree is empty
   * @throws IllegalArgumentException if {@code value} is {@code null}
   */
  public T ceiling(T val) {
    requireNonNull(val);
    if (isEmpty()) throw new NoSuchElementException("called ceiling() with empty binary tree");
    ITreeNode<T> x = ceiling(root, val);
    if (isNull(x)) return null;
    else return x.value();
  }

  /**
   * Returns the node in the subtree with the smallest value greater than or equal to the given
   * value.
   *
   * @param x the subtree
   * @param val the value
   * @return the node in the subtree with the smallest value greater than or equal to the given
   *     value
   */
  @SuppressWarnings("checkstyle:ReturnCount")
  private ITreeNode<T> ceiling(ITreeNode<T> x, T val) {
    if (isNull(x)) return null;
    int cmp = val.compareTo(x.value());
    if (cmp == 0) return x;
    if (cmp > 0) return ceiling(x.right(), val);
    ITreeNode<T> y = ceiling(x.left(), val);
    if (isNull(y)) return x;
    else return y;
  }
  
  /**
   * Returns the kth smallest value in the binary tree.
   *
   * @param k the order statistic
   * @return the kth smallest value in the binary tree
   * @throws IllegalArgumentException unless {@code k} is between 0 and {@code size() -1 }
   */
  @SuppressWarnings("PMD.LawOfDemeter")
  public T select(int k) {
    requireInRangeInclusive(0, size(), k);
    Iterable<T> iterable = values();
    List<T> result = new ArrayList<>();
    iterable.forEach(result::add);
    return result.get(k);
  }

  /**
   * Returns the number of values in the binary tree strictly less than {@code value}.
   *
   * @param val the value
   * @return the number of values in the binary tree strictly less than {@code value}
   * @throws NullPointerException if {@code value} is {@code null}
   */
  public int rankFromValues(T val) {
    requireNonNull(val);
    Iterable<T> iterable = values();
    int rank = 0;
    for (T value : iterable) if (value.compareTo(val) < 0) ++rank;
    return rank;
  }

  /**
   * Returns the number of values in the binary tree strictly less than {@code value}.
   *
   * @param val the value
   * @return the number of values in the binary tree strictly less than {@code value}
   * @throws NullPointerException if {@code value} is {@code null}
   */
  public int rank(T val) {
    requireNonNull(val);
    return rank(val, root);
  }

  /**
   * Returns the number of values in the subtree less than value.
   *
   * @param val the value
   * @param x the subtree
   * @return the number of values in the subtree less than value
   */
  @SuppressWarnings("checkstyle:ReturnValue")
  private int rank(T val, ITreeNode<T> x) {
    if (isNull(x)) return 0;
    ITreeNode<T> left = x.left();
    ITreeNode<T> right = x.right();
    int cmp = val.compareTo(x.value());
    if (cmp < 0) return rank(val, left);
    else if (cmp > 0) return x.refCount() + size(left) + rank(val, right);
    else return size(left);
  }

  /**
   * Returns all values in the binary tree.
   *
   * @return all values in the binary tree
   */
  public Iterable<T> values() {
    return valuesInOrder();
  }

  /**
   * Adds the values between {@code lo} and {@code hi} in the subtree to the {@code queue}.
   *
   * @param x the subtree
   * @param queue the queue
   * @param lo the lowest value
   * @param hi the highest value
   */
  private void values(ITreeNode<T> x, Queue<T> queue, T lo, T hi) {
    if (isNull(x)) return;
    int cmplo = lo.compareTo(x.value());
    int cmphi = hi.compareTo(x.value());
    if (cmplo < 0) values(x.left(), queue, lo, hi);
    if (cmplo <= 0 && cmphi >= 0) queue.offer(x.value());
    if (cmphi > 0) values(x.right(), queue, lo, hi);
  }

  /**
   * Returns all values in the binary tree in the given range.
   *
   * @param lo the lowest value
   * @param hi the highest value
   * @return all values in the binary tree between {@code lo} (inclusive) and {@code hi} (exclusive)
   * @throws NullPointerException if either {@code lo} or {@code hi} is {@code null}
   */
  public Iterable<T> values(T lo, T hi) {
    requireNonNull(lo);
    requireNonNull(hi);
    Queue<T> queue = new ArrayDeque<>();
    values(root, queue, lo, hi);
    return queue;
  }

  /**
   * Returns all values in the binary tree following an in-order traversal.
   *
   * @return all values in the binary tree following an in-order traversal
   */
  public Iterable<T> valuesInOrder() {
    Queue<T> queue = new ArrayDeque<>();
    valuesInOrder(root, queue);
    return queue;
  }

  /**
   * Adds the values in the subtree to queue following an in-order traversal.
   *
   * @param x the subtree
   * @param queue the queue
   */
  private void valuesInOrder(ITreeNode<T> x, Queue<T> queue) {
    if (isNull(x)) return;
    valuesInOrder(x.left(), queue);
    for (int i = 0; i < x.refCount(); i++) queue.offer(x.value());
    valuesInOrder(x.right(), queue);
  }

  /**
   * Returns all values in the binary tree following an pre-order traversal.
   *
   * @return all values in the binary tree following an pre-order traversal
   */
  public Iterable<T> valuesPreOrder() {
    Queue<T> queue = new ArrayDeque<>();
    valuesPreOrder(root, queue);
    return queue;
  }

  /**
   * Adds the values in the subtree to queue following an pre-order traversal.
   *
   * @param x the subtree
   * @param queue the queue
   */
  private void valuesPreOrder(ITreeNode<T> x, Queue<T> queue) {
    if (isNull(x)) return;
    for (int i = 0; i < x.refCount(); i++) queue.offer(x.value());
    valuesPreOrder(x.left(), queue);
    valuesPreOrder(x.right(), queue);
  }

  /**
   * Returns all values in the binary tree following an post-order traversal.
   *
   * @return all values in the binary tree following an post-order traversal
   */
  public Iterable<T> valuesPostOrder() {
    Queue<T> queue = new ArrayDeque<>();
    valuesPostOrder(root, queue);
    return queue;
  }

  /**
   * Adds the values in the subtree to queue following an post-order traversal.
   *
   * @param x the subtree
   * @param queue the queue
   */
  private void valuesPostOrder(ITreeNode<T> x, Queue<T> queue) {
    if (isNull(x)) return;
    valuesPostOrder(x.left(), queue);
    valuesPostOrder(x.right(), queue);
    for (int i = 0; i < x.refCount(); i++) queue.offer(x.value());
  }

  /**
   * Returns all values in the binary tree following a level-order traversal.
   *
   * @return all values in the binary tree following a level-order traversal.
   */
  public Iterable<T> valuesLevelOrder() {
    Queue<T> queue = new ArrayDeque<>();
    if (!isEmpty()) {
      Queue<ITreeNode<T>> queue2 = new ArrayDeque<>();
      queue2.offer(root);
      while (!queue2.isEmpty()) {
        ITreeNode<T> x = queue2.poll();
        for (int i = 0; i < x.refCount(); i++) queue.offer(x.value());
        if (nonNull(x.left())) queue2.offer(x.left());
        if (nonNull(x.right())) queue2.offer(x.right());
      }
    }
    return queue;
  }
}
