# Mongo Migrator
## Development
### Mongo
* Start a local mongo container  
```
docker run -d -p 27017-27019:27017-27019 \
    -e MONGO_INITDB_ROOT_USERNAME=insite \
    -e MONGO_INITDB_ROOT_PASSWORD=password \
    --name ehr2edc-mongo \
    mongo:3.6.12
```
* Open local mongo shell  
```
docker exec -it ehr2edc-mongo \
    mongo -u insite -p password --authenticationDatabase admin
```
* Useful mongo commands
  * `use insite`  
  switches to insite db
  * `db.Demographic.find({})`  
  shows all documents in `Demographic`-collection
  * More info: [v3.6](https://docs.mongodb.com/v3.6/mongo/)
### Spring
* Start SpringBootApplication `SpringBootExport` with profile `dev`
* The migration can be triggered by running an HTTP Request against the ExportPatient endpoint, e.g.:  
```
POST http://localhost:8080/migrator/patient
Content-Type: application/json

{
 "patientIdentifier": {
   "patientId": {
     "id": "0001012001"
   },
   "namespace": {
     "name": "MASTER_PATIENT_INDEX"
   },
   "subjectId": "SUBJ"
 }
}
```