package ds.knapsack;

@SuppressWarnings("PMD.ShortClassName")
public class Item {

  final String name;
  final int value;
  final int weight;
  int bounding = 1;

  public Item(String name, int value, int weight) {
    this.name = name;
    this.value = value;
    this.weight = weight;
  }
  
  public Item(String name, int value, int weight, int bounding) {
    this(name, value, weight);
    this.bounding = bounding;
  }

  @Override
  public String toString() {
    return name + " [value = " + value + ", weight = " + weight + "]";
  }
}
