### Querying ORCID for current employees

This query reengineers the DataciteCommons organization -> people connection, 
described in https://www.pidforum.org/t/employment-field-always-empty-when-using-connection/1571/12 ,
skipping the first step ROR->Wikidata and adding a filter at the end to only return ORCIDs 
affiliated with the organization via Ringgold or Grid-id in their ORCID profile and with an empty end-date.

This query is very costly and takes quite some time as you need to call the ORCID API
x times (x being the number of affiliated employees).