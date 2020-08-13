package ds;

import static ds.MathUtils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("PMD.DataClass")
public class BrickSortParallel extends AbstractBrickSort {
  private static final int NO_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();

  protected int oddTaskCount;
  protected int evenTaskCount;
  protected ExecutorService service;
  private final AtomicBoolean sorted;
  private final AtomicInteger swapCount;

  public BrickSortParallel() {
    service = Executors.newFixedThreadPool(NO_OF_PROCESSORS);
    sorted = new AtomicBoolean();
    swapCount = new AtomicInteger();
  }

  protected void reset(int length) {
    super.reset();
    sorted.getAndSet(false);
    swapCount.set(0);
    oddTaskCount = computeOddTaskCount(length);
    evenTaskCount = computeEvenTaskCount(length);
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  @Override
  protected void sort(long[] a, int length) {
    try {
      sortInterruptibly(a, length);
    } catch (ExecutionException | InterruptedException ee) {
      throw new CompletionException(ee);
    }
  }

  protected void sortInterruptibly(long[] a, int length)
      throws InterruptedException, ExecutionException {
    reset(length);
    if (length <= 1) {
      sorted.set(true);
      return;
    }
    final int maxComparisons = computeMaxComparisons(length);
    while (!sorted.get()) {
      ++outerLoopCount;
      sorted.set(true);
      oddSort(a, length);
      if (swapCount.intValue() == maxComparisons) {
        sorted.set(true);
        break;
      }
      evenSort(a, length);
      if (swapCount.intValue() == maxComparisons) sorted.set(true);
    }
  }

  @SuppressWarnings({"PMD.LawOfDemeter", "PMD.SystemPrintln"})
  @Generated
  @Override
  protected void oddSort(long[] a, int length) throws InterruptedException, ExecutionException {
    List<Future<Void>> futures = new ArrayList<>(oddTaskCount);
    for (int i = 1; i < length - 1; i += 2) {
      ++innerLoopCount;
      ++comparisonCount;
      futures.add(service.submit(new BubbleTask(this, a, i)));
    }
    assert futures.size() == oddTaskCount;
    for (Future future : futures) future.get();
  }

  @SuppressWarnings({"PMD.LawOfDemeter", "PMD.SystemPrintln"})
  @Generated
  @Override
  protected void evenSort(long[] a, int length) throws InterruptedException, ExecutionException {
    List<Future<Void>> futures = new ArrayList<>(evenTaskCount);
    for (int i = 0; i < length - 1; i += 2) {
      ++innerLoopCount;
      ++comparisonCount;
      futures.add(service.submit(new BubbleTask(this, a, i)));
    }
    assert futures.size() == evenTaskCount;
    for (Future future : futures) future.get();
  }

  @Override
  protected void bubble(long[] a, int i) {
    if (a[i] > a[i + 1]) {
      swap(a, i, i + 1);
      sorted.set(false);
      swapCount.incrementAndGet();
    }
  }

  @Override
  public int getSwapCount() {
    return swapCount.intValue();
  }

  @Override
  public boolean isSorted() {
    return sorted.get();
  }

  @SuppressWarnings("PMD.LawOfDemeter")
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    String lineSeparator = System.lineSeparator();
    sb.append(getClass().getName())
        .append(lineSeparator)
        .append("Comparison count: ")
        .append(comparisonCount)
        .append(lineSeparator)
        .append("Swap count: ")
        .append(swapCount)
        .append(lineSeparator)
        .append("Copy count: ")
        .append(copyCount)
        .append(lineSeparator)
        .append("inner loop count: ")
        .append(innerLoopCount)
        .append(lineSeparator)
        .append("outer loop count: ")
        .append(outerLoopCount)
        .append(lineSeparator)
        .append("odd task count: ")
        .append(oddTaskCount)
        .append(lineSeparator)
        .append("even task count: ")
        .append(evenTaskCount)
        .append(lineSeparator)
        .append("sorted: ")
        .append(sorted)
        .append(lineSeparator)
        .append("ExecutorService: ")
        .append(service);
    return sb.toString();
  }

  public static int computeOddTaskCount(int length) {
    if (length < 0) throw new IllegalArgumentException("Illegal argument value: " + length);
    return length < 2 ? 0 : isOdd(length) ? length >> 1 : (length - 1) >> 1;
  }

  public static int computeEvenTaskCount(int length) {
    if (length < 0) throw new IllegalArgumentException("Illegal argument value: " + length);
    return length > 0 ? length >> 1 : 0;
  }

  static final class BubbleTask implements Callable<Void> {
    long[] a;
    int i;
    BrickSortParallel sorter;

    private BubbleTask(BrickSortParallel sorter, long[] a, int i) {
      this.a = a;
      this.i = i;
      this.sorter = sorter;
    }

    @Override
    public Void call() throws InterruptedException {
      sorter.bubble(a, i);
      return null;
    }
  }
}
