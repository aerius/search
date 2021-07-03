package nl.aerius.search.tasks;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import nl.aerius.geo.epsg.EPSGRDNew;
import nl.aerius.geo.epsg.RDNewReceptorGridSettings;
import nl.aerius.geo.util.ReceptorUtil;

@Component
public class ReceptorUtilFactory {
  @Bean
  public ReceptorUtil getReceptorUtil() {
    return new ReceptorUtil(new RDNewReceptorGridSettings(new EPSGRDNew()));
  }
}
