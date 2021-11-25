/*
 * Copyright the State of the Netherlands
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package nl.aerius.search.wui.component;

import javax.inject.Inject;

import com.axellience.vuegwt.core.annotations.component.Component;
import com.axellience.vuegwt.core.annotations.component.Computed;
import com.axellience.vuegwt.core.annotations.component.Data;
import com.axellience.vuegwt.core.annotations.component.Emit;
import com.axellience.vuegwt.core.annotations.component.Prop;
import com.axellience.vuegwt.core.annotations.component.PropDefault;
import com.axellience.vuegwt.core.annotations.component.Ref;
import com.axellience.vuegwt.core.annotations.component.Watch;
import com.axellience.vuegwt.core.client.component.IsVueComponent;
import com.google.web.bindery.event.shared.EventBus;

import jsinterop.annotations.JsMethod;

import nl.aerius.search.wui.context.SearchContext;
import nl.aerius.search.wui.domain.SearchSuggestion;
import nl.aerius.search.wui.i18n.SearchM;
import nl.aerius.search.wui.i18n.SearchMessages;
import nl.aerius.search.wui.resources.SearchImageResources;
import nl.aerius.search.wui.resources.SearchResources;
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

  @Data SearchMessages i18n = SearchM.messages();
  @Data SearchImageResources img = SearchResources.images();

  @Inject @Data SearchContext context;

  @Ref MapSearchComponent input;

  @Computed("isSearchShowing")
  public boolean isSearchShowing() {
    return context.isSearchShowing();
  }

  @JsMethod
  public void toggle() {
    context.toggleSearchShowing();
  }

  @PropDefault("auto")
  boolean autoDefault() {
    return true;
  }

  @Watch("isSearchShowing()")
  public void onSearchShowingChange(final boolean neww) {
    if (neww) {
      input.focus();
    }
  }

  @JsMethod
  @Emit
  public void selectSuggestion(final SearchSuggestion value) {}
}
