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
package nl.aerius.search.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchRegion;

class CapabilityKeyTest {

  @Test
  void shouldExposeCapabilityAndRegion() {
    final CapabilityKey key = CapabilityKey.of(SearchCapability.RECEPTOR, SearchRegion.NL);

    assertEquals(SearchCapability.RECEPTOR, key.getCapability(), "Capability should be the one provided");
    assertEquals(SearchRegion.NL, key.getRegion(), "Region should be the one provided");
  }

  @Test
  void shouldUpdateViaSetters() {
    final CapabilityKey key = CapabilityKey.of(SearchCapability.RECEPTOR, SearchRegion.NL);
    key.setCapability(SearchCapability.COORDINATE);
    key.setRegion(SearchRegion.UK);

    assertEquals(SearchCapability.COORDINATE, key.getCapability(), "Capability should reflect the setter");
    assertEquals(SearchRegion.UK, key.getRegion(), "Region should reflect the setter");
  }

  @Test
  void shouldBeEqualForSameCapabilityAndRegion() {
    final CapabilityKey a = CapabilityKey.of(SearchCapability.RECEPTOR, SearchRegion.NL);
    final CapabilityKey b = CapabilityKey.of(SearchCapability.RECEPTOR, SearchRegion.NL);

    assertEquals(a, a, "A key should equal itself");
    assertEquals(a, b, "Keys with the same capability and region should be equal");
    assertEquals(a.hashCode(), b.hashCode(), "Equal keys should share a hash code");
  }

  @Test
  void shouldNotBeEqualForDifferentValuesOrTypes() {
    final CapabilityKey key = CapabilityKey.of(SearchCapability.RECEPTOR, SearchRegion.NL);

    assertNotEquals(key, CapabilityKey.of(SearchCapability.COORDINATE, SearchRegion.NL), "Different capability should not be equal");
    assertNotEquals(key, CapabilityKey.of(SearchCapability.RECEPTOR, SearchRegion.UK), "Different region should not be equal");
    assertNotEquals(key, null, "A key should not equal null");
    assertNotEquals(key, "not a key", "A key should not equal an unrelated type");
  }

  @Test
  void shouldRenderToString() {
    final String rendered = CapabilityKey.of(SearchCapability.RECEPTOR, SearchRegion.NL).toString();

    assertTrue(rendered.contains("RECEPTOR") && rendered.contains("NL"), "toString should mention capability and region, but was: " + rendered);
  }
}
