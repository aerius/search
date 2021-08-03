package nl.aerius.search.wui.command;

import nl.aerius.wui.event.SimpleGenericEvent;

public class SearchTextCommand extends SimpleGenericEvent<String> {
  public SearchTextCommand(final String value) {
    super(value);
  }
}
