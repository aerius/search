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

The search service can be extended with independent implementations of a search task that fullfill a set capability.

**WIP**

## Client library

The client library contains client code and UI components that allow projects to easily tap into the search service.

**WIP**
