package ds;

public interface INode<T> {

  T getData();

  void setData(T data);

  boolean distinctCompare(INode<T> node);
}