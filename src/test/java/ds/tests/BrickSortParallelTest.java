package ds.tests;

import static ds.ArrayUtils.*;
import static ds.tests.TestData.*;
import static org.junit.jupiter.api.Assertions.*;

import ds.AbstractSort;
import ds.BrickSortParallel;
import ds.HighArray;
import ds.IArray;
import ds.ISort;
import ds.OrdArray;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.CsvSource;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.SAME_THREAD)
@SuppressWarnings("PMD.LawOfDemeter")
class BrickSortParallelTest implements SortProvider {

  @ParameterizedTest
  @CsvSource(INIT_DATA)
  void testSort(@AggregateWith(HighArrayArgumentsAggregator.class) IArray arr) {
    long[] a = {00, 11, 22, 33, 44, 55, 66, 77, 88, 99};
    ISort sorter = new BrickSortParallel();
    IArray sorted = sorter.sort(arr);
    long[] extent = sorted.getExtentArray();
    assertArrayEquals(a, extent, "Elements must be sorted and equal.");
  }

  @ParameterizedTest
  @CsvSource(INIT_BRICK_SORT_DATA)
  void testSortSmallData(@AggregateWith(HighArrayArgumentsAggregator.class) IArray arr) {
    ISort sorter = new BrickSortParallel();
    IArray sorted = sorter.sort(arr);
    assertEquals(13, sorter.getSwapCount(), "Swap count will be five.");
  }

  @Test
  void testStreamUnSorted() {
    IArray high = new HighArray();
    IArray ord = new OrdArray();
    LongStream.rangeClosed(1, 20)
        .unordered()
        .forEach(
            i -> {
              high.insert(i);
              ord.insert(i);
            });
    ISort sorter = new BrickSortParallel();
    IArray sorted = sorter.sort(high);
    long[] extentSorted = sorted.getExtentArray();
    long[] extent = ord.getExtentArray();
    assertArrayEquals(extentSorted, extent, "Elements must be sorted and equal.");
  }

  @Test
  void testStreamSorted() {
    IArray high = new HighArray();
    IArray ord = new OrdArray();
    LongStream.rangeClosed(1, 20)
        .forEach(
            i -> {
              high.insert(i);
              ord.insert(i);
            });
    ISort sorter = new BrickSortParallel();
    IArray sorted = sorter.sort(high);
    long[] extentSorted = sorted.getExtentArray();
    long[] extent = ord.getExtentArray();
    assertArrayEquals(extentSorted, extent, "Elements must be sorted and equal.");
  }

  @Test
  void testComparisonCountSorted() {
    IArray high = new HighArray();
    LongStream.rangeClosed(1, 20).forEach(i -> high.insert(i));
    ISort sorter = new BrickSortParallel();
    sorter.sort(high);
    int compCount = sorter.getComparisonCount();
    assertEquals(19, compCount, "Comparison count must be 19.");
  }

  @Test
  void testComparisonCountUnsorted() {
    IArray high = new HighArray();
    LongStream.rangeClosed(1, 20).parallel().unordered().forEach(i -> high.insert(i));
    ISort sorter = new BrickSortParallel();
    sorter.sort(high);
    int compCount = sorter.getComparisonCount();
    assertTrue(
        19 <= compCount && compCount <= 400, "Comparison count must be in range 19 and 400.");
  }

  @Test
  void testReverseSorted() {
    IArray high = new HighArray();
    revRange(1, 20).forEach(i -> high.insert(i));
    ISort sorter = new BrickSortParallel();
    IArray sorted = sorter.sort(high);
    assertEquals(
        sorter.getSwapCount(),
        sorter.getComparisonCount(),
        "Comparison count must be same as swap count in reverse ordered array.");
    assertTrue(isSorted(sorted), "Array must be sorted");
  }

  @Test
  void testReverseSortedOdd() {
    IArray high = new HighArray();
    revRange(1, 21).forEach(i -> high.insert(i));
    ISort sorter = new BrickSortParallel();
    IArray sorted = sorter.sort(high);
    assertEquals(
        sorter.getSwapCount(),
        sorter.getComparisonCount(),
        "Comparison count must be same as swap count in reverse ordered array.");
    assertTrue(isSorted(sorted), "Array must be sorted");
  }

  @Test
  void testSwapCount() {
    IArray high = new HighArray();
    LongStream.rangeClosed(1, 20).forEach(i -> high.insert(i));
    ISort sorter = new BrickSortParallel();
    sorter.sort(high);
    assertEquals(0, sorter.getSwapCount(), "Swap count must be zero.");
  }

  @Test
  void testTimeComplexity() {
    IArray high = new HighArray();
    LongStream.rangeClosed(1, 20).forEach(i -> high.insert(i));
    ISort sorter = new BrickSortParallel();
    sorter.sort(high);
    assertEquals(19, sorter.getTimeComplexity(), "Time complexity must be twenty.");
  }

  @Test
  void testToStringClass() {
    AbstractSort sorter = new BrickSortParallel();
    String className = BrickSortParallel.class.getName();
    assertTrue(
        sorter.toString().startsWith(className), () -> "ToString must start with " + className);
  }

  @Test
  void testReverseSortedOddException() {
    IArray high = new HighArray();
    revRange(1, 21).forEach(i -> high.insert(i));
    ISort sorter = new BrickSortExceptionable();
    assertThrows(
        CompletionException.class, () -> sorter.sort(high), "CompletionException expected.");
  }

  @Test
  void testReverseSortedOddInterruption() throws InterruptedException, ExecutionException {
    IArray high = new HighArray();
    revRange(1, 21).forEach(i -> high.insert(i));
    BrickSortInterruptible sorter = new BrickSortInterruptible();
    assertThrows(
        CompletionException.class, () -> sorter.sort(high), "CompletionException expected.");
  }

  @Test
  void testZeroTimeComplexity() {
    BrickSortComplex bsc = new BrickSortComplex();
    bsc.sortZeroLengthArray();
    assertEquals(0, bsc.getTimeComplexity(), "Time Complexity must be zero.");
  }

  @Test
  void testOneTimeComplexity() {
    BrickSortComplex bsc = new BrickSortComplex();
    bsc.sortOneLengthArray();
    assertEquals(0, bsc.getTimeComplexity(), "Time Complexity must be zero.");
  }

  static class BrickSortExceptionable extends BrickSortParallel {
    @Override
    protected void bubble(long[] a, int i) {
      throw new IllegalStateException("Error in " + BrickSortExceptionable.class + ".bubble");
    }
  }

  static class BrickSortInterruptible extends BrickSortParallel {
    @Override
    protected void sortInterruptibly(long[] a, int length)
        throws InterruptedException, ExecutionException {
      throw new InterruptedException(
          "Error in " + BrickSortInterruptible.class + ".sortInterruptible");
    }
  }

  static class BrickSortComplex extends BrickSortParallel {
    void sortZeroLengthArray() {
      long[] a = {2, 5, 6, 8, 0};
      sort(a, 0);
    }

    void sortOneLengthArray() {
      long[] a = {2, 5, 6, 8, 0};
      sort(a, 1);
    }
  }
}
