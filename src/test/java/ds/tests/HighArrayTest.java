package ds.tests;

import static org.junit.jupiter.api.Assertions.*;

import ds.HighArray;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HighArrayTest {
  private static final Logger LOGGER = Logger.getLogger(HighArrayTest.class.getName());
  HighArray arr;

  @BeforeAll
  @SuppressWarnings("checkstyle:magicnumber")
  public void initialise() {
    // create the array
    arr = new HighArray(100);
  }

  @Test
  @Order(1)
  @SuppressWarnings("checkstyle:magicnumber")
  public void testInsert() {
    // insert 10 items
    arr.insert(77);
    arr.insert(99);
    arr.insert(44);
    arr.insert(55);
    arr.insert(22);
    arr.insert(88);
    arr.insert(11);
    arr.insert(00);
    arr.insert(66);
    arr.insert(33);
    assertEquals(arr.count(), 10, "10 elements inserted.");
  }

  @Test
  @SuppressWarnings("checkstyle:magicnumber")
  public void testFindIndexFalse() {
    int searchKey = 35;
    // search for item
    assertEquals(arr.findIndex(searchKey), -1, () -> "Key " + searchKey + " not available");
  }

  @Test
  @SuppressWarnings("checkstyle:magicnumber")
  public void testFindIndexTrue() {
    int searchKey = 11;
    // search for item
    assertTrue(arr.findIndex(searchKey) >= 0, () -> "Key " + searchKey + " available");
  }

  @Test
  @SuppressWarnings("checkstyle:magicnumber")
  public void testFindTrue() {
    int searchKey = 11;
    // search for item
    assertTrue(arr.find(searchKey), () -> "Key " + searchKey + " available");
  }

  @Test
  @Order(2)
  @SuppressWarnings("checkstyle:magicnumber")
  public void testDelete() {
    LOGGER.info(() -> arr.toString());
    assertTrue(
        () -> arr.delete(00) && arr.delete(55) && arr.delete(99),
        () -> "Elements 00, 55, 99 deleted");
  }
}
