package com.custodix.insite.mongodb.export.patient.infrastructure.embedded;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.core.io.ClassPathResource;

import com.custodix.insite.mongodb.export.patient.domain.model.FastingStatus;
import com.custodix.insite.mongodb.export.patient.domain.model.labvalue.LaboratoryConceptInfo;

class LaboratoryConceptInfoMappingReaderFactory {
	private static final String SUBSET_FILE_PATH = "loinc/loinc-lab.csv";
	private static final String CODE_FIELD = "LOINC_NUM";
	private static final String COMPONENT_FIELD = "COMPONENT";
	private static final String PROPERTY_FIELD = "PROPERTY";
	private static final String TIME_ASPECT_FIELD = "TIME_ASPECT";
	private static final String SPECIMEN_FIELD = "SYSTEM";
	private static final String SCALE_FIELD = "SCALE_TYP";
	private static final String METHOD_FIELD = "METHOD_TYP";
	private static final String CLASS_FIELD = "CLASS";
	private static final String CLASS_TYPE_FIELD = "CLASSTYPE";
	private static final String LABEL_FIELD = "LONG_COMMON_NAME";
	private static final String LABEL_SHORT_FIELD = "SHORTNAME";
	private static final String COPYRIGHT_FIELD = "EXTERNAL_COPYRIGHT_NOTICE";
	private static final String STATUS_FIELD = "STATUS";
	private static final String FASTING_STATUS_FIELD = "FASTING_STATUS";

	private static final String NON_FASTING_VALUE = "0";
	private static final String FASTING_VALUE = "1";

	ItemReader<LaboratoryConceptInfoMapping> createReader() {
		FlatFileItemReader<LaboratoryConceptInfoMapping> reader = new FlatFileItemReader<>();
		reader.setResource(new ClassPathResource(SUBSET_FILE_PATH));
		reader.setLinesToSkip(1);
		reader.setLineMapper(getLineMapper());
		return reader;
	}

	private LineMapper<LaboratoryConceptInfoMapping> getLineMapper() {
		DefaultLineMapper<LaboratoryConceptInfoMapping> lineMapper = new DefaultLineMapper<>();
		lineMapper.setLineTokenizer(getLineTokenizer());
		lineMapper.setFieldSetMapper(this::mapLaboratoryInfoRow);
		return lineMapper;
	}

	private LineTokenizer getLineTokenizer() {
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setNames(CODE_FIELD, COMPONENT_FIELD, PROPERTY_FIELD, TIME_ASPECT_FIELD, SPECIMEN_FIELD,
				SCALE_FIELD, METHOD_FIELD, CLASS_FIELD, CLASS_TYPE_FIELD, LABEL_FIELD, LABEL_SHORT_FIELD,
				COPYRIGHT_FIELD, STATUS_FIELD, FASTING_STATUS_FIELD);
		lineTokenizer.setDelimiter(";");
		return lineTokenizer;
	}

	private LaboratoryConceptInfoMapping mapLaboratoryInfoRow(FieldSet fieldSet) {
		LaboratoryConceptInfo info = LaboratoryConceptInfo.newBuilder()
				.withMethodValue(fieldSet.readString(METHOD_FIELD))
				.withMethodText(fieldSet.readString(METHOD_FIELD))
				.withSpecimenValue(fieldSet.readString(SPECIMEN_FIELD))
				.withSpecimenText(fieldSet.readString(SPECIMEN_FIELD))
				.withFastingStatus(mapFastingStatus(fieldSet.readString(FASTING_STATUS_FIELD)))
				.withComponent(fieldSet.readString(COMPONENT_FIELD))
				.build();
		return new LaboratoryConceptInfoMapping(fieldSet.readString(CODE_FIELD), info);
	}

	private FastingStatus mapFastingStatus(String fastingStatusValue) {
		if (fastingStatusValue.equals(NON_FASTING_VALUE)) {
			return FastingStatus.NOT_FASTING;
		} else if (fastingStatusValue.equals(FASTING_VALUE)) {
			return FastingStatus.FASTING;
		} else {
			return FastingStatus.UNDEFINED;
		}
	}
}