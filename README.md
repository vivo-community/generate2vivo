[![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](https://www.repostatus.org/badges/latest/active.svg)](https://www.repostatus.org/#active)

## datacitecommons2vivo
datacitecommons2vivo is a Data Ingest Tool for the Open-Source-Software VIVO. 
It queries metadata from the Datacite Commons PID-Graph and the ROR API, 
maps it to the VIVO ontology using [sparql-generate](https://ci.mines-stetienne.fr/sparql-generate/index.html) and optionally imports it into a VIVO instance.

- [Installation](#installation)
- [Usage](#usage)
    + [Datacite Commons](#datacite-commons)
    + [ROR](#ror)
- [Run in Command Line](#run-in-command-line)
- [Extensible](#extensible)

### Installation
1. Clone the repository to a local folder using `git clone https://github.com/vivo-community/datacitecommons2vivo.git`
2. Change into the folder where the repository has been cloned. 
3. Open `src/main/resources/application.properties` and change your VIVO details accordingly. 
   If you don't provide a vivo.url, vivo.email or vivo.password, the application will not import the mapped data to VIVO but return the triples in format JSON-LD.
3. Run the application:
  * If you have maven and a JDK for Java 11 installed, you can run the application directly via `mvn spring-boot:run`. 

  * Alternatively you can compile & run the application in Docker (with or without Java setup):
    ```dockerfile
    # with Java setup:
    mvn package
    docker build -t dc .
    docker run -p 9000:9000 -t dc
    
    # without Java setup
    docker build -f DockerfileBuild -t dc .
    docker run -p 9000:9000 -t dc

5. A minimal swagger-ui will be available at `http://localhost:9000/swagger-ui/`.

### Usage
Go to `http://localhost:9000/swagger-ui/` in your browser and choose Datacite Commons or ROR as a data source. 
  
##### Datacite Commons
For Datacite Commons there are 2 queries available:
* `getOrganizationPlusPersons`: Queries Datacite Commons for the organization and its affiliated people.
* `getPersonPlusPublications`: Queries Datacite Commons for the person and its affiliated publications. 

##### ROR
For ROR there are 2 queries available:
* `getOrganization`: Queries ROR for the organization.
* `getOrganizationPlusChildren`: Queries ROR for the organization and all of its sub-organizations recursively.

The program will return a 200 Status, if the data was imported to VIVO or if you chose not to provide your VIVO details,
it will return the RDF-data as a result in format JSON-LD.

### Run in Command Line
Alternatively you can run the queries from the command line using the sparql-generate executable JAR-file.
All queries are placed in folder `src/main/resources/sparqlg` and come with a `sparql-generate-conf.json`. 
Its structure and use are explained in detail on the [sparql-generate website](https://ci.mines-stetienne.fr/sparql-generate/language-cli.html).

### Extensible
The software is easily extensible, meaning you can add and remove datasources without touching the code.

For example, if you are not interested in using Datacite Commons, just remove the folder from `src/main/resources/sparqlg`
and the respective controller in the package `eu.tib.controller` and it's gone.

On the other hand, if you would like to add a datasource:
* add a folder with your queries under `src/main/resources/sparqlg` and include a `sparql-generate-conf.json` 
  (its structure is described on the [sparql-generate website](https://ci.mines-stetienne.fr/sparql-generate/language-cli.html)).
* add a controller in `eu.tib.controller` that retrieves your input and calls your query like `responseService.buildResponse(queryid, input)`
    * the connection between controller and the according query is made by the queryid. You need to supply the path within the resources folder to your `sparql-generate-conf.json`.
    * put your input into a Map and every key-value-pair will be available in your query as a binding, where ?key will be replaced with value.

