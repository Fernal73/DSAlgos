package ds.tests;

import ds.IArray;
import ds.OrdArray;
import java.util.Optional;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

class OrdArrayArgumentsAggregator implements ArgumentsAggregator {

  public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context)
      throws ArgumentsAggregationException {
    Integer size = Optional.ofNullable(accessor.getInteger(0)).orElse(100);
    Boolean strict = Optional.ofNullable(accessor.getBoolean(1)).orElse(false);
    Object[] args = accessor.toArray();
    IArray array = new OrdArray(size, strict);
    for (int i = 2; i < args.length; i++) array.insert((long) args[i]);
    return array;
  }
}
