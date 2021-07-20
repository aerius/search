package nl.aerius.wui.search.component;

import java.util.Collection;

import javax.inject.Inject;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Emit;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.PropDefault;
import com.axellience.vuegwt.core.annotations.component.Watch;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;

import nl.aerius.wui.search.command.SearchTextCommand;
import nl.aerius.wui.search.context.SearchContext;
import nl.aerius.wui.search.domain.SearchSuggestion;
import nl.aerius.wui.search.event.SearchSuggestionSelectionEvent;
import nl.aerius.wui.vue.transition.HorizontalCollapse;
import nl.aerius.wui.vue.transition.VerticalCollapse;
import nl.aerius.wui.vue.transition.VerticalCollapseGroup;

@Component(components = {
    VerticalCollapse.class,
    VerticalCollapseGroup.class,
    HorizontalCollapse.class
})
public class MapSearchComponent implements IsVueComponent {
  @Prop boolean auto;

  @Prop EventBus eventBus;
  @Prop boolean visible;

  @Data String search;

  @Inject @Data SearchContext context;

  @PropDefault("auto")
  boolean autoDefault() {
    return true;
  }

  @PropDefault("visible")
  boolean visibleDefault() {
    return true;
  }

  @JsMethod
  @SuppressWarnings("rawtypes")
  public boolean isEmpty(final Object val) {
    return ((Collection) val).isEmpty();
  }

  @Computed
  public boolean isSearching() {
    return context.isSearching();
  }

  @JsMethod
  public boolean hasResults() {
    return !context.getResults().isEmpty();
  }

  @JsMethod
  @Emit
  public void selectSuggestion(final SearchSuggestion value) {
    if (auto) {
      eventBus.fireEvent(new SearchSuggestionSelectionEvent(value));
    }
  }

  @Computed
  public boolean isShowing() {
    return context.isSearching()
        || hasResults();
  }

  @Watch("search")
  public void onSearchChange(final String neww) {
    eventBus.fireEvent(new SearchTextCommand(neww));
  }
}
