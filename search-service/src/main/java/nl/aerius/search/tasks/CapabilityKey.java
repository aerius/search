package nl.aerius.search.tasks;

import java.util.Objects;

import nl.aerius.search.domain.SearchCapability;

public class CapabilityKey {
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
