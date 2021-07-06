# AERIUS Search

This project unifies common AERIUS search capabilities in a single API and accompanying client libraries.

## Search service

The search service accepts search requests and delegates these to one or multiple different service based on the capabilities requested. It aggregates the results of these services and returns them.

Components implementing `SearchTaskService` will be scanned from the classpath, and indexed based on the search capability they implement.

Search queries can be sent to `/api/query`

### Running

Navigate to `/scripts/` and execute `search-service.sh`, or execute `mvn spring-boot:run -pl :search-service` on the command line.

A simple interface will be available on `localhost:8090`.

## Service extensions

The search service can be extended with independent implementations of a search task that fulfill a set capability.

An extension project must include the following dependency in order to create search task components:

```xml
    <dependency>
      <groupId>nl.aerius</groupId>
      <artifactId>search-service-extension</artifactId>
      <version>${project.version}</version>
    </dependency>
```

Aswell as a Spring dependency:

```xml
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
```

In this project, a component can be created that will fulfill a SearchCapability, for example:

```java
@Component
@ImplementsCapability(SearchCapability.RECEPTORS_28992)
public class RDNewReceptorSearchService implements SearchTaskService {
  @Autowired ReceptorUtil util;

  @Override
  public SearchTaskResult retrieveSearchResults(final String query) {
    return Optional.ofNullable(ReceptorUtils.tryParse(util, query))
        .orElse(SearchResultBuilder.empty());
  }
}
```

When an extension project is included (i.e. depended on) in the main project's classpath, the additional search task components will be automatically found and added.

## Client library

The client library contains client code and UI components that allow projects to easily tap into the search service.

**WIP**
