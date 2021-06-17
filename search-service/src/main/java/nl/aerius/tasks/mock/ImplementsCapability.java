package nl.aerius.tasks.mock;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import nl.aerius.domain.SearchCapability;
import nl.aerius.tasks.TaskFactory;

/**
 * TODO Add a priority? Duplicate implementors of a capability are currently overridded.
 * See {@link TaskFactory}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ImplementsCapability {
  SearchCapability value();
}
