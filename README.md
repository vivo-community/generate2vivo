## datacitecommons2vivo
datacitecommons2vivo is a Data Ingest Tool from Datacite Commons to VIVO.
It queries metadata from datacite commons' PID graph via its [GraphQL API](https://api.datacite.org/graphql), maps it to the VIVO ontology and optionally imports it into a VIVO instance.


### Status
This is the first prototype of a Data Ingest Tool from Datacite Commons to VIVO.
It only contains one query for importing an organization and its people.


### Installation
1. Clone the repository to a local folder using `git clone https://github.com/vivo-community/datacitecommons2vivo.git`
2. Change into the folder where the repository has been cloned. 
3. Open `src/main/resources/application.properties` and change your VIVO details accordingly. 
   If you don't provide a vivo.url, vivo.email or vivo.password, the application will not import the mapped data to VIVO but return the triples in format json-ld.
3. Run the application:
  * If you have maven and a JDK for Java 11 installed, you can run the application directly via `mvn spring-boot:run`. 

  * Alternatively you can compile & run the application in Docker (no Java setup needed):
    ```dockerfile
    docker build -t dc .
    docker run -p 9000:9000 -t dc

5. A minimal swagger-ui will be available at `http://localhost:9000/swagger-ui/`.

### Usage
* Go to `http://localhost:9000/swagger-ui/` in your browser. In the category "organization" there is one query for adding an organization and its people. 
* Enter a valid ROR-URL and click on Execute. 
* The program will return a 200 Status, if the data was imported to VIVO or if you chose not to provide your VIVO details, it will return the RDF-data as a result in format json-ld.