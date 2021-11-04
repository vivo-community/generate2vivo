![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

[![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](https://www.repostatus.org/badges/latest/active.svg)](https://www.repostatus.org/#active)
![Open Source Love](https://badges.frapsoft.com/os/v3/open-source.svg?v=102)

## generate2vivo
generate2vivo is an extensible Data Ingest and Transformation Tool for the open source software VIVO.
It currently contains queries for metadata from [Datacite Commons](https://commons.datacite.org/), [Crossref](https://www.crossref.org/), [ROR](https://ror.org/) and [ORCID](https://orcid.org/)
and maps them to the VIVO ontology using [sparql-generate](https://ci.mines-stetienne.fr/sparql-generate/index.html).
The resulting RDF data can be exported to a VIVO instance (or any SPARQL endpoint) directly or it can be returned in JSON-LD.

<hr style="border:1px solid gray"> </hr>

- [Features](#features)
- [Installation](#installation)
- [Wiki resources](#wiki-resources)


### Features
![generate2vivo features](https://raw.githubusercontent.com/wiki/vivo-community/generate2vivo/images/generate2vivo.png)

Starting point was the sparql-generate library that we use as an engine for our transformations, which are
defined in different GENERATE queries. \
Notice that code and queries are separate, this allows users
* to write or change queries without going into the code
* to reuse queries (meaning you can dump the code and only use the queries for example with the command line
  tool provided on the sparql-generate website)
* to reuse code (meaning you can dump the queries if the data sources are not interesting for you and use only the code with your own queries)


In addition we gave the application a REST API so other programs or services can communicate with the application using HTTP requests which allows generate2vivo to be integrated in an existing data ingest process.


On the other side we added output functionality that allows you to export the generated data either directly into a VIVO instance via its SPARQL API or alternatively if you want to check the data before importing or are using a messaging service like Apache Kafka you can return the generated data as
JSON-LD and do some post-processing with it.


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

### Wiki resources
Additional resources are available in the wiki, e.g.

* _[data sources & queries](https://github.com/vivo-community/generate2vivo/wiki/data-sources-&-queries)_ :
A detailed overview of the data sources and their queries.

* _[using generate2vivo](https://github.com/vivo-community/generate2vivo/wiki/using-generate2vivo)_: A short user tutorial with screenshots on how to use the swagger-UI to execute a query.

* _[run queries in cmd line](https://github.com/vivo-community/generate2vivo/wiki/install-&-run#run-in-command-line)_ :
  An alternative way of running the queries via cmd line with the provided Jar from sparql-generate.

* _[dev guide](https://github.com/vivo-community/generate2vivo/wiki/dev-guide)_ : resources specifically for developers, e.g. on how to add a data source or query, how to put variables from code into the query.