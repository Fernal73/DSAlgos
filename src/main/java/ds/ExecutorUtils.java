package ds;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("PMD.LawOfDemeter")
public final class ExecutorUtils {

  private ExecutorUtils() throws InstantiationException {
    throw new InstantiationException("Private constructor for: " + ExecutorUtils.class.getName());
  }

  public static void terminateExecutor(ExecutorService service, long timeUnits, TimeUnit unit) {
    service.shutdown();
    try {
      if (!service.awaitTermination(timeUnits, unit)) service.shutdownNow();
      if (!service.awaitTermination((long) (timeUnits * 0.2), unit)) service.shutdownNow();
    } catch (InterruptedException ie) {
      service.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  @Generated
  public static void assertServiceTerminated(ExecutorService service) {
    if (!service.isShutdown()) throw new AssertionError("ExecutorService must be shutdown.");
    if (!Thread.currentThread().isInterrupted() && !service.isTerminated())
      throw new AssertionError("ExecutorService must terminate cleanly.");
  }
}
