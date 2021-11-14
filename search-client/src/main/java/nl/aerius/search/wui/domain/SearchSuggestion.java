package nl.aerius.search.wui.domain;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class SearchSuggestion {
  @JsProperty public String id;

  @JsProperty public String type;
  @JsProperty public String description;

  @JsProperty public String centroid;

  @JsProperty public double score;
}
