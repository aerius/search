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

import java.util.Objects;

import nl.aerius.search.domain.SearchCapability;
import nl.aerius.search.domain.SearchRegion;

public final class CapabilityKey {
  private SearchCapability capability;
  private SearchRegion region;

  private CapabilityKey(final SearchCapability capability, final SearchRegion region) {
    this.capability = capability;
    this.region = region;
  }

  public static CapabilityKey of(final SearchCapability value, final SearchRegion region) {
    return new CapabilityKey(value, region);
  }

  public SearchCapability getCapability() {
    return capability;
  }

  public void setCapability(final SearchCapability capability) {
    this.capability = capability;
  }

  public SearchRegion getRegion() {
    return region;
  }

  public void setRegion(final SearchRegion region) {
    this.region = region;
  }

  @Override
  public int hashCode() {
    return Objects.hash(capability, region);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CapabilityKey other = (CapabilityKey) obj;
    return capability == other.capability && region == other.region;
  }

  @Override
  public String toString() {
    return String.format("CapabilityKey [capability=%s, region=%s]", capability, region);
  }
}
