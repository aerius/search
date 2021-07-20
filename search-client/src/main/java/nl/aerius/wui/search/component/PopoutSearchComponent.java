package nl.aerius.wui.search.component;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.google.web.bindery.event.shared.EventBus;

import nl.aerius.wui.search.resources.SearchImageResources;
import nl.aerius.wui.search.resources.SearchResources;
import nl.aerius.wui.vue.directives.VectorDirective;

@Component(directives = {
    VectorDirective.class
})
public class PopoutSearchComponent implements IsVueComponent {
  @Prop EventBus eventBus;

  @Data SearchImageResources img = SearchResources.images();

  @Data boolean searchShowing = false;
}
