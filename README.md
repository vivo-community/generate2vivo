query metadata from datacite common's pid graph via graphql api

graphql api: https://api.datacite.org/graphql
api documentation: https://support.datacite.org/docs/datacite-graphql-api-guide

This is the first prototype of a Data Ingest Tool from Datacite Commons to VIVO.
It only contains one query for importing organization and its people.

Run it on your computer with "mvn spring-boot:run" or use the Dockerfile.
A minimal swagger-ui will be available at http://yourhost:9000/swagger-ui/
