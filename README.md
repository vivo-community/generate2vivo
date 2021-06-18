[![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](https://www.repostatus.org/badges/latest/active.svg)](https://www.repostatus.org/#active)

## generate2vivo
generate2vivo is an extensible Data Ingest Tool for the Open-Source-Software VIVO. 
It currently queries metadata from Datacite Commons, ROR and ORCID
and maps them to the VIVO ontology using [sparql-generate](https://ci.mines-stetienne.fr/sparql-generate/index.html).
The resulting RDF data can be exported to a VIVO instance directly or returned in a HTTP response.

- [Available queries](#available-queries)
  + [Datacite Commons](#datacite-commons)
  + [ROR](#ror)
  + [ORCID](#orcid)
- [Installation](#installation)
- [Run in Command Line](#run-in-command-line)
- [Extensible](#extensible)

### Available queries
The datasources and queries that are currently available are listed below.

##### Datacite Commons
For Datacite Commons the following queries are available:
* `organization` : This method gets data about an organization by passing a ROR id.
* `organizationPlusPeople`: This method gets data about an organization and its affiliated people by passing a ROR id.
* `organizationPlusPeoplePlusWorks`:This method gets data about an organization and its affiliated people and their respective works by passing a ROR id.
* `person`: This method gets data about a person by passing an ORCID id.
* `personPlusWorks`: This method gets data about a person and their works by passing an ORCID id.
* `work`: This method gets data about a work by passing an DOI.

##### ROR
For ROR there are 2 queries available:
* `organization`: This method gets data about an organization by passing a ROR id.
* `organizationPlusChildren`: This method gets data about an organization and all their sub-organizations by passing a ROR id.

##### ORCID
For ORCID the following queries are available:
* `personPlusWorks`: This method gets data about a person and their works by passing an ORCID id.
* `currentEmployeesPlusWorks`: This method gets data about an organization's current employees and their works by passing a ROR id.



### Installation
1. Clone the repository to a local folder using `git clone https://github.com/vivo-community/generate2vivo.git`
2. Change into the folder where the repository has been cloned. 
3. Open `src/main/resources/application.properties` and change your VIVO details accordingly. 
   If you don't provide a vivo.url, vivo.email or vivo.password, the application will not import the mapped data to VIVO but return the triples in format JSON-LD.
3. Run the application:
  * If you have maven and a JDK for Java 11 installed, you can run the application directly via `mvn spring-boot:run`. 

  * Alternatively you can compile & run the application in Docker (with or without Java setup):
    ```dockerfile
    # with Java setup:
    mvn package
    docker build -t g2v .
    docker run -p 9000:9000 -t g2v
    
    # without Java setup
    docker build -f DockerfileBuild -t g2v .
    docker run -p 9000:9000 -t g2v

5. A minimal swagger-ui will be available at `http://localhost:9000/swagger-ui/`.

### Run in Command Line
Alternatively you can run the queries from the command line using the sparql-generate executable JAR-file.
All queries are placed in folder `src/main/resources/sparqlg` and come with a `sparql-generate-conf.json`. 
Its structure and use are explained in detail on the [sparql-generate website](https://ci.mines-stetienne.fr/sparql-generate/language-cli.html).

### Extensible
The software is easily extensible, meaning you can add and remove datasources.

For example, if you are not interested in using Datacite Commons, just remove the folder from `src/main/resources/sparqlg`
and the respective controller in the package `eu.tib.controller`.

On the other hand, if you would like to add a datasource:
* add a folder with your queries under `src/main/resources/sparqlg` and include a `sparql-generate-conf.json` 
  (its structure is described on the [sparql-generate website](https://ci.mines-stetienne.fr/sparql-generate/language-cli.html)).
* add a controller in `eu.tib.controller` that retrieves your input and calls your query like `responseService.buildResponse(queryid, input)`
    * the connection between controller and the according query is made by the queryid. You need to supply the path within the resources folder to your `sparql-generate-conf.json`.
    * put your input into a Map and every key-value-pair will be available in your query as a binding, where ?key will be replaced with value.

