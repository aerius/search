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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A location suggested by the Bing autosuggest endpoint, to be translated into an actual location.
 */
final class SuggestedLocation {
  final String name;
  private final String locality;
  private final String adminDistrict;
  private final String addressLine;
  private final String formattedAddress;

  SuggestedLocation(final String name, final String locality, final String adminDistrict, final String addressLine, final String formattedAddress) {
    this.name = name;
    this.locality = locality;
    this.adminDistrict = adminDistrict;
    this.addressLine = addressLine;
    this.formattedAddress = formattedAddress;
  }

  @Override
  public int hashCode() {
    return Objects.hash(addressLine, adminDistrict, formattedAddress, locality, name);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final SuggestedLocation other = (SuggestedLocation) obj;
    return Objects.equals(addressLine, other.addressLine) && Objects.equals(adminDistrict, other.adminDistrict)
        && Objects.equals(formattedAddress, other.formattedAddress) && Objects.equals(locality, other.locality)
        && Objects.equals(name, other.name);
  }

  String toAddressUrlParameters(final String countryRegion) {
    final Map<String, String> parameters = new HashMap<>();
    parameters.put("countryRegion", countryRegion);
    if (locality != null) {
      parameters.put("locality", locality);
    }
    if (adminDistrict != null) {
      parameters.put("adminDistrict", adminDistrict);
    }
    if (addressLine != null) {
      parameters.put("addressLine", addressLine);
    }
    if (formattedAddress != null) {
      parameters.put("query", formattedAddress);
    }
    return parameters.isEmpty()
        ? ""
        : ("&" + parameters.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&")));
  }
}
