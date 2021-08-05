package com.custodix.insite.local.ehr2edc.query.fhir

import com.custodix.insite.local.ehr2edc.vocabulary.PatientSearchCriteria

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FhirJson {

    abstract static class Resource {
        String resourceType
        String id
        Object meta
    }

    static class Bundle extends Resource {
        String type
        int total
        List link
        List entry

        static Bundle empty(String queryUrl) {
            return many(queryUrl, [])
        }

        static Bundle single(String queryUrl, Resource resource) {
            return many(queryUrl, [ resource ])
        }

        static Bundle many(String queryUrl, List<Resource> resources) {
            String bundleId = UUID.randomUUID().toString()
            return [
                    resourceType: 'Bundle',
                    id: bundleId,
                    meta: BundleMeta.aDefault(),
                    type: 'searchset',
                    total: resources.size(),
                    link: [ [ relation: 'self', url: queryUrl ] ],
                    entry: resources.collect { Entry.of(it) }
            ]
        }
    }

    static class Entry extends Resource {
        String fullUrl
        Resource resource
        Object search

        static Entry of(Resource resource) {
            return [ fullUrl:  resource.id, resource: resource, search: [ mode: 'match' ] ]
        }
    }

    static class Identity {
        private static final String DEFAULT_PATIENT_DOMAIN = "https://custodix.tech/ehr"
        static String aDefaultSystem() {
            return DEFAULT_PATIENT_DOMAIN
        }
    }

    static class Patient extends Resource {
        List<Identifier> identifier
        List name
        String gender
        String birthDate

        private static String GENDER_MALE = "male"
        private static String GENDER_FEMALE = "female"
        private static List<String> GENDERS = [ GENDER_MALE, GENDER_FEMALE ]
        private static List<LocalDate> BIRTH_DATES = [
            LocalDate.of(2009, 9, 9),
            LocalDate.of(2008, 8, 8),
            LocalDate.of(2007, 7, 7),
            LocalDate.of(2007, 6, 6),
            LocalDate.of(2005, 5, 5)]
        private static List<String> NAMES_FAMILY = [ "Goodman", "Figueroa", "Robinson", "Jacobson", "Clay", "Combs", "Wiggins", "Austin", "Horton", "Sosa", "Brandt", "Wu" ]
        private static Map<String, List<String>> NAMES_GIVEN = [
                (GENDER_MALE): [ "Camron", "Jeremy", "Oliver", "Cordell", "Hayden", "Bradyn", "Rhett", "Jaydin", "Jonah", "Jayvon", "Kenyon", "Marques" ],
                (GENDER_FEMALE): [ "Lacey", "Crystal", "Bethany", "Andrea", "Jade", "Carlie", "Cora", "Evelyn", "Natalia", "Alexus", "Amaya", "Miya" ]
        ]

        static Patient of(String fhirId, String source, String id, String familyName, String givenName, String gender, LocalDate birthDate) {
            return [
                    resourceType: 'Patient',
                    id: fhirId,
                    meta: ResourceMeta.aDefault(),
                    identifier: [ [ system: source, value: id ] ],
                    name: [ [ family: [ familyName ], given: [ givenName ] ] ],
                    gender: gender,
                    birthDate: birthDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            ]
        }

        static Patient of(String source, String id, String familyName, String givenName, String gender, LocalDate birthDate) {
            def fhirId = UUID.randomUUID().toString()
            return of(fhirId, source, id, familyName, givenName, gender, birthDate)
        }

        static Patient aRandomPatientWithSystemAndId(String system, String id) {
            def random = new Random()
            def gender = Randomly.select(random, GENDERS)
            def family = Randomly.select(random, NAMES_FAMILY)
            def given = Randomly.select(random, NAMES_GIVEN[gender])
            def birthDate = Randomly.select(random, BIRTH_DATES)
            return of(system, id, family, given, gender, birthDate)
        }

        static Patient aRandomPatientWithSystemAndId(String fhirId, String system, String id) {
            def random = new Random()
            def gender = Randomly.select(random, GENDERS)
            def family = Randomly.select(random, NAMES_FAMILY)
            def given = Randomly.select(random, NAMES_GIVEN[gender])
            def birthDate = Randomly.select(random, BIRTH_DATES)
            return of(fhirId, system, id, family, given, gender, birthDate)
        }

        static Patient aRandomPatientWithSystem(String system) {
            def id = UUID.randomUUID().toString()
            return aRandomPatientWithSystemAndId(system, id)
        }

        static Patient aRandomPatientWithDefaultSystem() {
            return aRandomPatientWithSystem(Identity.aDefaultSystem())
        }

        static Patient aRandomPatientWithPatientSearchCriteria(PatientSearchCriteria patientSearchCriteria) {
            def random = new Random()
            return of(patientSearchCriteria.patientCDWReference.source,
                    patientSearchCriteria.patientCDWReference.id,
                    patientSearchCriteria.lastName,
                    patientSearchCriteria.firstName,
                    Randomly.select(random, GENDERS),
                    patientSearchCriteria.birthDate)
        }

        static class Identifier {
            String system
            String value
        }
    }

    static class ResourceMeta {

        String versionId
        String lastUpdated

        static ResourceMeta aDefault() {
            return [
                    versionId: '2',
                    lastUpdated: '2018-12-12T10:06:55.663+00:00'
            ]
        }
    }

    static class BundleMeta {

        String lastUpdated
        List tag

        static BundleMeta aDefault() {
            return [
                    lastUpdated: '2020-01-22T14:48:11.818+00:00',
                    tag: []
            ]
        }
    }

    static class CodeableConcept {
        List<Coding> coding
        String text

        static CodeableConcept of(String system, String code, String text) {
            Coding coding = Coding.of(system, code, text)
            return [ coding: [ coding ], text: text ]
        }

        static CodeableConcept snomed(String code, String text) {
            return of("http://snomed.info/sct", code, text)
        }

        static CodeableConcept rxnorm(String code, String text) {
            return of("http://www.nlm.nih.gov/research/umls/rxnorm", code, text)
        }

        static CodeableConcept amoxicillin() {
            return rxnorm("308189", "Amoxicillin 80 MG/ML Oral Suspension")
        }

        static CodeableConcept routeOral() {
            return snomed("386359008","Administration of drug or medicament via oral route")
        }

        static CodeableConcept siteOral() {
            return snomed("181220002", "Entire oral cavity")
        }

        static class Coding {
            String system
            String code
            String display

            static Coding of(String system, String code, String text) {
                return [ system: system, code: code, display: text ]
            }
        }
    }

    static class Medication extends Resource {
        Product product

        static Medication amoxicillin() {
            return [
                    resourceType: 'Medication',
                    id: UUID.randomUUID().toString(),
                    meta: ResourceMeta.aDefault(),
                    product: Product.amoxicillin()
            ]
        }

        static class Product {
            CodeableConcept form

            static Product of(String code, String text) {
                return [ form: CodeableConcept.snomed(code, text) ]
            }

            static Product amoxicillin() {
                return of("27658006", "Amoxicillin 80 MG/ML Oral Suspension")
            }
        }
    }

    static class MedicationOrder extends Resource {
        String dateWritten
        String status
        Reference patient
        Reference encounter
        Reference reasonReference
        List<DosageInstruction> dosageInstruction

        private static MedicationOrder of(final String fhirPatientId,
                                  final String dateWritten,
                                  final List<DosageInstruction> dosageInstructions) {
            return [
                    resourceType: 'MedicationOrder',
                    id: UUID.randomUUID().toString(),
                    meta: ResourceMeta.aDefault(),
                    dateWritten: dateWritten,
                    status: "active",
                    patient: Reference.of("Patient/${fhirPatientId}"),
                    encounter: Reference.of("Encounter/" + UUID.randomUUID().toString()),
                    reasonReference: Reference.of("Condition/" + UUID.randomUUID().toString()),
                    dosageInstruction: dosageInstructions
            ]
        }

        private static MedicationOrder amoxicillin(String fhirPatientId) {
            def timing = DosageInstruction.Timing.repeat("2013-08-04", 1, 1, "d")
            def dosageInstruction = DosageInstruction.amoxicillinWithTiming(timing)
            def dateWritten = "2004-07-13"
            return of(fhirPatientId, dateWritten, [ dosageInstruction ])
        }

        static MedicationOrderWithConcept amoxicillinWithConcept(String fhirPatientId) {
            def order = amoxicillin(fhirPatientId)
            def props = order.properties - [class: order.class]
            return [ *: props, medicationCodeableConcept: CodeableConcept.amoxicillin() ]
        }

        static MedicationOrderWithMedication amoxicillinWithMedication(String fhirPatientId, String fhirMedicationId) {
            def medicationReference = Reference.of("Medication/${fhirMedicationId}")
            def order = amoxicillin(fhirPatientId)
            def props = order.properties - [class: order.class]
            return [ *: props, medicationReference: medicationReference ]
        }

        static class MedicationOrderWithConcept extends MedicationOrder {
            CodeableConcept medicationCodeableConcept
        }

        static class MedicationOrderWithMedication extends MedicationOrder {
            Reference medicationReference
        }

        static class Reference {
            String reference
//            String display

            static Reference of(String reference) {
                return [ reference: reference ]
            }
        }

        static class DosageInstruction {
            Timing timing
            Object siteCodeableConcept
            CodeableConcept route
            DoseQuantity doseQuantity

            static DosageInstruction of(Timing timing, Object siteCodeableConcept, CodeableConcept route, DoseQuantity doseQuantity) {
                return [
                        timing: timing,
                        siteCodeableConcept: siteCodeableConcept,
                        route: route,
                        doseQuantity: doseQuantity
                ]
            }

            static DosageInstruction amoxicillinWithTiming(Timing timing) {
                def site = CodeableConcept.siteOral()
                def route = CodeableConcept.routeOral()
                def doseQuantity = DoseQuantity.ml(50)
                return of(timing, site, route, doseQuantity)
            }

            static class Timing {
                List<String> event
                Repeat repeat

                static Timing repeat(String start, String end, int frequency, int period, String periodUnit) {
                    return [ event: [], repeat: Repeat.of(Repeat.BoundsPeriod.of(start, end), frequency, period, periodUnit) ]
                }

                static Timing repeat(String start, int frequency, int period, String periodUnit) {
                    return [ event: [], repeat: Repeat.of(Repeat.BoundsPeriod.of(start), frequency, period, periodUnit) ]
                }

                static Timing events(String ...event) {
                    return [ event: event ]
                }

                static class Repeat {
                    BoundsPeriod boundsPeriod
                    int frequency
                    int period
                    String periodUnits

                    static Repeat of(BoundsPeriod bounds, int frequency, int period, String periodUnit) {
                        return [
                                boundsPeriod: bounds,
                                frequency: frequency,
                                period: period,
                                periodUnits: periodUnit
                        ]
                    }

                    static class BoundsPeriod {
                        String start
                        String end

                        static BoundsPeriod of(String start, String end) {
                            return [ start: start, end: end ]
                        }

                        static BoundsPeriod of(String start) {
                            return [ start: start ]
                        }
                    }
                }
            }

            static class DoseQuantity {
                int value
                String unit
                String system
                String code

                static DoseQuantity mg(int value) {
                    return of(value, "mg", "mg")
                }

                static DoseQuantity ml(int value) {
                    return of(value, "ml", "ml")
                }

                static DoseQuantity of(int value, String unit, String code) {
                    return [
                            value: value,
                            unit: unit,
                            system: "http://unitsofmeasure.org",
                            code: code
                    ]
                }
            }
        }
    }

    private static class Randomly {
        static <T> T select(Random random, List<T> list) {
            def index = random.nextInt(list.size())
            return list.get(index)
        }
    }
}
