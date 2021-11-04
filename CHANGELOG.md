# Changelog
All notable changes to this project will be documented in this file.

## [Unreleased]
- add new parameter for ROR affiliation to ORCID query
- add queries for Crossref work and personPlusWorks
- improve README
- remove Dockerfiles and instead use Spring Docker capabilities
- add tests for all controllers


## [1.1.0] - 2021-06-22
### Added
- query ORCID for person and current employees at ROR organization
- refactor all queries to follow three steps: source, extraction, mapping to vivo-rdf
- check GraphQL queries for response code 200 and additionally if response body contains error messages (no data)
- make all controller use HTTP-Get requests
- centralize input validation
- export to VIVO in chunks
- simplify error handling
- document methods in swagger-UI and remove response section

## [Renamed Project] - 2021-05-25
As new datsources were integrated and the name datacitecommons2vivo was not reflecting
these additions, the repository was renamed generate2vivo and moved to https://github.com/vivo-community/generate2vivo. 
Development will continue there.

## [1.0.0] - 2021-05-25
### Added
- Queries for Datacite Commons (orga2person & person2publication).
- Queries for ROR (organization & organizationPlusChildren).
- Sparql-Generate Pipeline based on config & resources.
- Health Check via spring-boot-starter-actuator.
- Dockerfiles (one relying on local Java setup and one without any dependencies).
- Swagger-UI for all queries available.
- Optional export to VIVO.
- Optional output to JSON-LD.
- Error Handling.
- Log file.
