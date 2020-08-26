package ds;

public class SelectionSort extends AbstractSort {

  @Override
  protected void sort(long[] a, int length) {
    if (!shouldSort(length)) return;
    int out;
    int in;
    int min;
    for (out = 0; out < length - 1; ++out) {
      min = out;
      ++outerLoopCount;
      for (in = out + 1; in < length; ++in) {
        ++comparisonCount;
        ++innerLoopCount;
        if (a[in] < a[min]) min = in;
      }
      if (min != out) {
        swap(a, out, min);
        ++swapCount;
      }
    }
  }
}
