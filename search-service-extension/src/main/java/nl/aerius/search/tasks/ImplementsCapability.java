package nl.aerius.search.tasks;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import nl.aerius.search.domain.SearchCapability;

/**
 * TODO Add a priority? Duplicate implementors of a capability are currently
 * overridden. See {@link TaskFactory}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ImplementsCapability {
  SearchCapability value();

  SearchRegion[] region() default {};
}
