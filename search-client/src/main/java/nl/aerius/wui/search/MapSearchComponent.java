package nl.aerius.wui.search;

import javax.inject.Inject;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.Watch;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;

import nl.aerius.wui.vue.transition.VerticalCollapse;
import nl.aerius.wui.vue.transition.VerticalCollapseGroup;

@Component(components = {
    VerticalCollapse.class,
    VerticalCollapseGroup.class
})
public class MapSearchComponent implements IsVueComponent {
  @Prop EventBus eventBus;

  @Data String search;

  @Inject @Data SearchContext context;

  @Computed
  public boolean isSearching() {
    return context.isSearching();
  }

  @JsMethod
  public boolean hasResults() {
    return !context.getResults().isEmpty();
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
