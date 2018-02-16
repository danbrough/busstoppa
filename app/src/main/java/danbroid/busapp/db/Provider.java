package danbroid.busapp.db;

import org.slf4j.LoggerFactory;

/**
 * Interface for things that process an X into a Y and then pass that to something else.
 */


@FunctionalInterface
public interface Provider<X, Y> {

  void process(X arg, Provider<Y, ?> next);

  default void onError(String message, Throwable t) {
    LoggerFactory.getLogger(Provider.class).error(message, t);
  }
}
