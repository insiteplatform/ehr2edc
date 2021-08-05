package com.custodix.insite.mongodb.export.patient.infrastructure.batch.exportpatient.laboratory;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;

import com.custodix.insite.mongodb.export.patient.domain.model.LaboratoryObservationFactRecord;
import com.custodix.insite.mongodb.export.patient.infrastructure.datawarehouse.ConceptCategorySettings;
import com.custodix.insite.mongodb.vocabulary.PatientIdentifier;

class LaboratoryItemReaderFactory {
	private static final String LABORATORY_QUERY =
			"select p.patient_ide, p.patient_ide_source, o.start_date, o.end_date, "
					+ "o.concept_cd, o.nval_num, o.units_cd, o.val_lln, o.val_uln, o.sourcesystem_cd, "
					+ "(select name_char from concept_dimension where concept_cd = o.concept_cd limit 1) "
					+ "from observation_fact o inner join patient_mapping p on o.patient_num = p.patient_num "
					+ "where o.concept_cd in (select c.concept_cd from concept_dimension c where c.concept_path like ?) "
					+ "and o.modifier_cd = '@' and p.patient_ide = ? and p.patient_ide_source = ?";
	private static final String CONCEPT_PATH_LIKE_FORMAT = "\\\\insite\\\\%s%%";

	private final DataSource dataSource;
	private final ConceptCategorySettings conceptCategorySettings;

	LaboratoryItemReaderFactory(DataSource dataSource, ConceptCategorySettings conceptCategorySettings) {
		this.dataSource = dataSource;
		this.conceptCategorySettings = conceptCategorySettings;
	}

	ItemReader<LaboratoryObservationFactRecord> buildItemReader(PatientIdentifier patientIdentifier) {
		return new JdbcCursorItemReaderBuilder<LaboratoryObservationFactRecord>().name("laboratoryReader")
				.dataSource(dataSource)
				.sql(LABORATORY_QUERY)
				.queryArguments(new Object[] { getConceptPathLike(),
											   patientIdentifier.getPatientId().getId(),
											   patientIdentifier.getNamespace().getName() })
				.fetchSize(10000)
				.rowMapper(new LaboratoryObservationFactRowMapper())
				.build();
	}

	private String getConceptPathLike() {
		return String.format(CONCEPT_PATH_LIKE_FORMAT, conceptCategorySettings.getLaboratory());
	}
}