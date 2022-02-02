package nl.aerius.search.tasks;

public class InterpretationRuntimeException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public InterpretationRuntimeException(final Exception e) {
    super(e);
  }

  public InterpretationRuntimeException(final String message, final Exception e) {
    super(message, e);
  }
}
