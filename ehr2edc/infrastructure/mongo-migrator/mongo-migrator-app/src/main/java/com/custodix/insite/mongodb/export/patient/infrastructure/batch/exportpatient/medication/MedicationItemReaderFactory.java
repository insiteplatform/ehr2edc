package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.medication;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemReader;

import com.custodix.insite.mongodb.export.patient.domain.model.medication.MedicationItem;
import com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.CollectingJdbcCursorItemReaderBuilder;
import com.custodix.insite.mongodb.export.patient.infrastructure.datawarehouse.ConceptCategorySettings;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

class MedicationItemReaderFactory {
	private static final String MEDICATION_QUERY = "select p.patient_ide, p.patient_ide_source, o.start_date, "
			+ "o.concept_cd, o.observation_id, o.modifier_cd, o.nval_num, o.units_cd,  o.start_date, o.end_date, ("
			+ "select name_char from concept_dimension where concept_cd = o.concept_cd limit 1) "
			+ "from observation_fact o inner join patient_mapping p on o.patient_num = p.patient_num "
			+ "where o.concept_cd in (select c.concept_cd from concept_dimension c where c.concept_path like ?) "
			+ "and p.patient_ide = ? and p.patient_ide_source = ? order by observation_id";
	private static final String CONCEPT_PATH_LIKE_FORMAT = "\\\\insite\\\\%s%%";

	private final DataSource dataSource;
	private final ConceptCategorySettings conceptCategorySettings;

	MedicationItemReaderFactory(DataSource dataSource, ConceptCategorySettings conceptCategorySettings) {
		this.dataSource = dataSource;
		this.conceptCategorySettings = conceptCategorySettings;
	}

	ItemReader<MedicationItem> buildItemReader(PatientIdentifier patientIdentifier) {
		return new CollectingJdbcCursorItemReaderBuilder<MedicationItem>().withName("medicationItemReader")
				.withDataSource(dataSource)
				.withSql(MEDICATION_QUERY)
				.withQueryArguments(new Object[] { getConceptPathLike(),
												   patientIdentifier.getPatientId().getId(),
												   patientIdentifier.getNamespace().getName() })
				.withFetchSize(10000)
				.withCollectingRowMapper(new MedicationRowMapper())
				.build();
	}

	private String getConceptPathLike() {
		return String.format(CONCEPT_PATH_LIKE_FORMAT, conceptCategorySettings.getMedication());
	}
}