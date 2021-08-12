![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)


[![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](https://www.repostatus.org/badges/latest/active.svg)](https://www.repostatus.org/#active)

## generate2vivo
generate2vivo is an extensible Data Ingest Tool for the open source software VIVO.
It currently queries metadata from [Datacite Commons](https://commons.datacite.org/), [ROR](https://ror.org/) and [ORCID](https://orcid.org/)
and maps them to the VIVO ontology using [sparql-generate](https://ci.mines-stetienne.fr/sparql-generate/index.html).
The resulting RDF data can be exported to a VIVO instance directly or it can be returned in JSON-LD.


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
Alternatively you can execute the queries in the command line using the sparql-generate executable JAR-file, since
each query is placed in a subfolder of `src/main/resources/sparqlg` and comes with its own `sparql-generate-conf.json`.
You can find the JAR-file and details about the configuration file and its use on the [sparql-generate website](https://ci.mines-stetienne.fr/sparql-generate/language-cli.html).

### Wiki resources
Additional resources are available in the wiki, e.g.

* _[data sources & queries](https://github.com/vivo-community/generate2vivo/wiki/data-sources-&-queries)_ :
A detailed overview of the data sources and their queries.

* _[features](https://github.com/vivo-community/generate2vivo/wiki)_: 
   An overview of the features and components that make up generate2vivo.

* _[using generate2vivo](https://github.com/vivo-community/generate2vivo/wiki/using-generate2vivo)_: A short user tutorial with screenshots on how to use the swagger-UI to execute a query.

* _[dev guide](https://github.com/vivo-community/generate2vivo/wiki/dev-guide)_ : resources specifically for developers, e.g. on how to add a data source or query, how to put variables from code into the query.