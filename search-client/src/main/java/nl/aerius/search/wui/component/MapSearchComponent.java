package nl.aerius.search.wui.component;

import java.util.Collection;
import java.util.Map.Entry;

import javax.inject.Inject;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Emit;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.PropDefault;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.annotations.component.Watch;
import com.axellience.vuegwt.core.client.Vue;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.axellience.vuegwt.core.client.component.hooks.HasCreated;
import com.axellience.vuegwt.core.client.component.hooks.HasDestroyed;
import com.axellience.vuegwt.core.client.component.hooks.HasMounted;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;

import elemental2.dom.ClientRect;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;

import nl.aerius.search.wui.command.SearchTextCommand;
import nl.aerius.search.wui.context.SearchContext;
import nl.aerius.search.wui.domain.SearchSuggestion;
import nl.aerius.search.wui.event.SearchSuggestionSelectionEvent;
import nl.aerius.wui.vue.transition.HorizontalCollapse;
import nl.aerius.wui.vue.transition.VerticalCollapse;
import nl.aerius.wui.vue.transition.VerticalCollapseGroup;

@Component(components = {
    VerticalCollapse.class,
    VerticalCollapseGroup.class,
    HorizontalCollapse.class
})
public class MapSearchComponent implements IsVueComponent, HasCreated, HasMounted, HasDestroyed {
  @Prop boolean auto;

  @Prop EventBus eventBus;
  @Prop boolean visible;

  @Data String maxHeight;
  @Data boolean scrolling;

  @Ref HTMLElement resultsContainer;
  @Ref HTMLElement input;

  @Data String search;

  @Inject @Data SearchContext context;

  private HandlerRegistration resizeHandler;

  @JsMethod
  public String getAriaLabel(final SearchSuggestion sug, final String group, final int idx) {
    return group + " result " + (idx + 1) + ":" + sug.description;
  }

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

  @JsMethod
  @SuppressWarnings("rawtypes")
  public String getEntryGroup(final Entry entry) {
    return entry.getKey() == null ? "-" : String.valueOf(entry.getKey());
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

  @Override
  public void created() {
    DomGlobal.window.addEventListener("resize", null);

    resizeHandler = Window.addResizeHandler(r -> {
      resize();
    });
  }

  @Override
  public void mounted() {
    resize();
  }

  private void resize() {
    final ClientRect rect = resultsContainer.getBoundingClientRect();
    maxHeight = "calc(100vh - " + rect.top + "px - 10px)";
    scrolling = Window.getClientHeight() - rect.top - rect.height - 20 < 0;
  }

  @Override
  public void destroyed() {
    resizeHandler.removeHandler();
  }

  @Watch("search")
  public void onSearchChange(final String neww) {
    eventBus.fireEvent(new SearchTextCommand(neww));
  }

  public void focus() {
    // Delay setting focus by a tick because we are (probably) still in a click
    // handler (which takes focus)
    Vue.nextTick(() -> input.focus());
  }
}
