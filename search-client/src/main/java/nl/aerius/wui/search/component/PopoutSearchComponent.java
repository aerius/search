package nl.aerius.wui.search.component;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Emit;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;

import nl.aerius.wui.search.domain.SearchSuggestion;
import nl.aerius.wui.search.resources.SearchImageResources;
import nl.aerius.wui.search.resources.SearchResources;
import nl.aerius.wui.vue.directives.VectorDirective;
import nl.aerius.wui.vue.transition.HorizontalCollapse;

@Component(components = {
    MapSearchComponent.class,
    HorizontalCollapse.class
}, directives = {
    VectorDirective.class
})
public class PopoutSearchComponent implements IsVueComponent {
  @Prop boolean auto;

  @Prop EventBus eventBus;

  @Data SearchImageResources img = SearchResources.images();

  @Data boolean searchShowing = false;

  @JsMethod
  @Emit
  public void selectSuggestion(final SearchSuggestion value) {}
}
