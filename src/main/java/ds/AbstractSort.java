package ds;

public abstract class AbstractSort implements ISort {
  protected int comparisonCount;
  protected int swapCount;
  protected int copyCount;
  protected int innerLoopCount;
  protected int outerLoopCount;

  protected abstract void sort(long[] array, int length);

  @SuppressWarnings("PMD.LawOfDemeter")
  @Override
  public IArray sort(IArray array) {
    IArray copy = array.copy();
    sort(copy.get(), copy.count());
    return copy;
  }

  protected void swap(long[] a, int first, int second) {
    if (first == second) return;
    long temp = a[first];
    a[first] = a[second];
    a[second] = temp;
  }

  protected void resetCounts() {
    copyCount = swapCount = comparisonCount = innerLoopCount = outerLoopCount = 0;
  }

  @Override
  public int getCopyCount() {
    return copyCount;
  }

  @Override
  public int getSwapCount() {
    return swapCount;
  }

  @Override
  public int getComparisonCount() {
    return comparisonCount;
  }

  @Override
  public int getTimeComplexity() {
    System.out.printf("%d %d%n", innerLoopCount, outerLoopCount);
    return innerLoopCount == 0 ? outerLoopCount : innerLoopCount;
  }
}
