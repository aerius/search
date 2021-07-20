package nl.aerius.wui.search.domain;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class SearchResult {
  @JsProperty public boolean complete;

  @JsProperty public SearchSuggestion[] results;

  @JsProperty public String uuid;
}
