package ds.tests;

import static ds.tests.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import ds.INode;
import ds.SinglyLinkedList;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@DisplayName("SinglyLinkedListTest")
@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.SAME_THREAD)
class SinglyLinkedListTest {
  private static final String SIZE_ZERO = "Size must be zero.";
  private static final String SIZE_ONE = "Size must be one.";

  @Test
  @DisplayName("SinglyLinkedListTest.testConstructor")
  void testConstructor() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    assertEquals(0, list.size(), SIZE_ZERO);
    assertNull(list.getHead(), "List head must be null.");
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  @Test
  @DisplayName("SinglyLinkedListTest.testAdd")
  void testAdd() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    list.add(SCORE);
    INode<Integer> head = list.getHead();
    assertEquals(1, list.size(), SIZE_ONE);
    assertNotNull(head, "List head must not be null.");
    assertEquals(String.valueOf(SCORE), head.toString(), "Values must be equal.");
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  @Test
  @DisplayName("SinglyLinkedListTest.testAddNull")
  void testAddNull() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    assertThrows(
        NullPointerException.class, () -> list.add(null), "NullPointerException expected.");
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  @Test
  @DisplayName("SinglyLinkedListTest.testAddIndex")
  void testAddIndex() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    list.add(SCORE, 0);
    INode<Integer> head = list.getHead();
    assertEquals(1, list.size(), SIZE_ONE);
    assertNotNull(head, "List head must not be null.");
    assertEquals(String.valueOf(SCORE), head.toString(), "Values must be equal.");
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  @Test
  @DisplayName("SinglyLinkedListTest.testAddIndexNull")
  void testAddIndexNull() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    assertThrows(
        NullPointerException.class, () -> list.add(null, 0), "NullPointerException expected.");
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  @Test
  @DisplayName("SinglyLinkedListTest.testAddIndexException")
  void testAddIndexException() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    assertThrows(IndexOutOfBoundsException.class, () -> list.add(SCORE, -1), "Exception expected.");
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  @Test
  @DisplayName("SinglyLinkedListTest.testAddIndexExcessException")
  void testAddIndexExcessException() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    assertThrows(
        IndexOutOfBoundsException.class, () -> list.add(SCORE, TEN), "Exception expected.");
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  @Test
  @DisplayName("SinglyLinkedListTest.testFind")
  void testFind() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    list.add(SCORE, 0);
    INode<Integer> node = list.find(Integer.valueOf(SCORE));
    assertEquals(1, list.size(), SIZE_ONE);
    assertNotNull(node, "Node must not be null.");
    assertEquals(String.valueOf(SCORE), node.toString(), "Values must be equal.");
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  @Test
  @DisplayName("SinglyLinkedListTest.testFindNull")
  void testFindNull() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    list.add(SCORE, 0);
    assertThrows(
        NullPointerException.class, () -> list.find(null), "NullPointerException expected.");
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  @Test
  @DisplayName("SinglyLinkedListTest.testDeleteNull")
  void testDeleteNull() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    assertThrows(
        NullPointerException.class, () -> list.delete(null), "NullPointerException expected.");
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  @Test
  @DisplayName("SinglyLinkedListTest.testEmptyToString")
  void testEmptyToString() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    assertEquals("[]", list.toString(), "Empty array string expected.");
  }

  @Test
  @DisplayName("SinglyLinkedListTest.testGetMultiple")
  void testGetMultiple() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    LongStream.rangeClosed(1, TEN).forEach(i -> list.add(i));
    LongStream.rangeClosed(0, TEN - 1)
        .forEach(i -> assertEquals(list.get(i).getData(), i, "Values must equal index."));
  }
}
