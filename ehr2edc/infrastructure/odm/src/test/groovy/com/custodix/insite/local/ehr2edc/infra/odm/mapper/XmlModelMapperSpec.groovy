package com.custodix.insite.local.ehr2edc.infra.odm.mapper

import com.custodix.insite.local.ehr2edc.infra.odm.parser.OdmXmlMetaDataParserImpl
import com.custodix.insite.local.ehr2edc.infra.odm.parser.model.XmlOdm
import com.custodix.insite.local.ehr2edc.vocabulary.StudyId
import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils
import spock.lang.Shared
import spock.lang.Specification

class XmlModelMapperSpec extends Specification {

    @Shared
    XmlOdm parsedModel
    @Shared
    XmlOdm parsedModelWithFormName
    @Shared
    XmlOdm parsedModelWithFormLabReference
    @Shared
    XmlModelMapper mapper

    def setupSpec() {
        def odmXml = new String(FileCopyUtils.copyToByteArray(new ClassPathResource("/ehr2edc-odm-sample.xml").getFile()))
        parsedModel = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)
        odmXml = new String(FileCopyUtils.copyToByteArray(new ClassPathResource("/ehr2edc-odm-sample-without-form-name.xml").getFile()))
        parsedModelWithFormName = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)
        odmXml = new String(FileCopyUtils.copyToByteArray(new ClassPathResource("/odm-with-form-lab-reference.xml").getFile()))
        parsedModelWithFormLabReference = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)
        mapper = new XmlModelMapper()
    }

    def "can map to StudyMetaData"() {
        given:
        assert mapper != null

        when:
        def studyMetaData = mapper.toStudyMetaData(parsedModel)

        then:
        studyMetaData != null
        studyMetaData.id == StudyId.of("EHR2EDC")
        studyMetaData.name == "EHR2EDC Study"
        studyMetaData.description == "EHR2EDC Description"
        studyMetaData.metaDataDefinition != null
    }

    def "can map the metadata"() {
        given:
        def studyMetaData = mapper.toStudyMetaData(parsedModel)

        when:
        def metaDataDefinition = studyMetaData.metaDataDefinition

        then:
        metaDataDefinition != null
        metaDataDefinition.eventDefinitions != null
    }

    def "can map the event definitions"() {
        when:
        def studyMetaData = mapper.toStudyMetaData(parsedModel)
        def metaDataDefinition = studyMetaData.metaDataDefinition

        then:
        metaDataDefinition.eventDefinitions != null
        metaDataDefinition.eventDefinitions.size() == 14
        and:
        metaDataDefinition.eventDefinitions.any {
            it.id.id == "00"
            it.parentId == "PID"
        }
    }

    def "only maps event definitions in protocol section"() {
        given:
        def studyMetaData = mapper.toStudyMetaData(parsedModel)
        def metaDataDefinition = studyMetaData.metaDataDefinition

        when:
        def eventDefinitionIds = metaDataDefinition.eventDefinitions.collect { it.getId().id }

        then:
        !("NOT_IN_PROTOCOL" in eventDefinitionIds)
        eventDefinitionIds.size() == 14
    }

    def "can map the form definitions contained in event"() {
        given:
        def studyMetaData = mapper.toStudyMetaData(parsedModel)
        def metaDataDefinition = studyMetaData.metaDataDefinition
        def eventDefinition = metaDataDefinition.eventDefinitions.find { it.getId().id == "SUBJECT" }

        when:
        def formDefinitions = eventDefinition.formDefinitions

        then:
        formDefinitions != null
        formDefinitions.size() == 1
        with(formDefinitions) {
            id.id == ["SUBJSTAT"]
            name == ["Subject Status Technical Form"]
        }
    }

    def "can map the form definitions without name contained in event"() {
        given:
        def studyMetaData = mapper.toStudyMetaData(parsedModelWithFormName)
        def metaDataDefinition = studyMetaData.metaDataDefinition
        def eventDefinition = metaDataDefinition.eventDefinitions.find { it.getId().id == "SUBJECT" }

        when:
        def formDefinitions = eventDefinition.formDefinitions

        then:
        formDefinitions != null
        formDefinitions.size() == 1
        with(formDefinitions) {
            id.id == ["SUBJSTAT"]
            name == ["SUBJSTAT"]
        }
    }

    def "can map form definitions with local lab reference"() {
        given:
        def studyMetaData = mapper.toStudyMetaData(parsedModelWithFormLabReference)
        def metaDataDefinition = studyMetaData.metaDataDefinition
        def eventDefinition = metaDataDefinition.eventDefinitions.find { it.getId().id == "LB_EVENT" }

        when:
        def formDefinition = eventDefinition.formDefinitions.find { it.getId().id == "LB_FORM" }

        then:
        with(formDefinition) {
            id.id == "LB_FORM"
            name == "Local Laboratory"
            !itemGroupDefinitions.empty
            localLab.name == "LAB_1"
        }
    }

    def "can map the itemgroups contained in a form definition"() {
        given:
        def studyMetaData = mapper.toStudyMetaData(parsedModel)
        def metaDataDefinition = studyMetaData.metaDataDefinition
        def eventDefinition = metaDataDefinition.eventDefinitions.find { it.getId().id == "SUBJECT" }
        def formDefinition = eventDefinition.formDefinitions.find { it.getId().id == "SUBJSTAT" }

        when:
        def itemgroups = formDefinition.itemGroupDefinitions

        then:
        itemgroups != null
        itemgroups.size() == 1
    }

    def "can map item groups"() {
        given:
        def studyMetaData = mapper.toStudyMetaData(parsedModel)
        def metaDataDefinition = studyMetaData.metaDataDefinition
        def eventDefinition = metaDataDefinition.eventDefinitions.find { it.getId().id == "SUBJECT" }
        def formDefinition = eventDefinition.formDefinitions.find { it.getId().id == "SUBJSTAT" }

        when:
        def itemGroup = formDefinition.itemGroupDefinitions.find { it.id.id == "SUBJSTAT_LOG_LINE" }

        then:
        itemGroup != null
        itemGroup.name == "SUBJSTAT_LOG_LINE"
        itemGroup.itemDefinitions.size() == 5
    }

    def "can map items"() {
        given:
        def studyMetaData = mapper.toStudyMetaData(parsedModel)
        def metaDataDefinition = studyMetaData.metaDataDefinition
        def eventDefinition = metaDataDefinition.eventDefinitions.find { it.getId().id == "SUBJECT" }
        def formDefinition = eventDefinition.formDefinitions.find { it.getId().id == "SUBJSTAT" }
        def itemGroup = formDefinition.itemGroupDefinitions.find { it.id.id == "SUBJSTAT_LOG_LINE" }

        when:
        def item = itemGroup.itemDefinitions.find { it.id.id == "SUBJSTAT.SUBJECT_STATUS_CODE" }

        then:
        item != null
        item.dataType == "text"
        item.getDisplayLabel(Locale.forLanguageTag("")) == "SUBJECT_STATUS_CODE"
        item.length == 200
    }

    def "can map items with codelists"() {
        given:
        def studyMetaData = mapper.toStudyMetaData(parsedModel)
        def metaDataDefinition = studyMetaData.metaDataDefinition
        def eventDefinition = metaDataDefinition.eventDefinitions.find { it.getId().id == "00" }
        def formDefinition = eventDefinition.formDefinitions.find { it.getId().id == "SV_01_01" }
        def itemGroup = formDefinition.itemGroupDefinitions.find { it.id.id == "SV_01_01" }

        when:
        def item = itemGroup.itemDefinitions.find { it.id.id == "SV_01_01.VISYN" }

        then:
        item != null
        item.dataType == "text"
        item.length == 1
        item.codeList.id == "NY_YN"
    }

    def "can map codelists"() {
        given:
        def studyMetaData = mapper.toStudyMetaData(parsedModel)
        def metaDataDefinition = studyMetaData.metaDataDefinition
        def eventDefinition = metaDataDefinition.eventDefinitions.find { it.getId().id == "00" }
        def formDefinition = eventDefinition.formDefinitions.find { it.getId().id == "SV_01_01" }
        def itemGroup = formDefinition.itemGroupDefinitions.find { it.id.id == "SV_01_01" }
        def item = itemGroup.itemDefinitions.find { it.id.id == "SV_01_01.VISYN" }

        when:
        def codelist = item.codeList

        then:
        codelist != null
        codelist.id == "NY_YN"
    }

    def "can map items with measurement units"() {
        given:
        def studyMetaData = mapper.toStudyMetaData(parsedModel)
        def metaDataDefinition = studyMetaData.metaDataDefinition
        def eventDefinition = metaDataDefinition.eventDefinitions.find { it.getId().id == "SUBJECT" }
        def formDefinition = eventDefinition.formDefinitions.find { it.getId().id == "SUBJSTAT" }
        def itemGroup = formDefinition.itemGroupDefinitions.find { it.id.id == "SUBJSTAT_LOG_LINE" }

        when:
        def item = itemGroup.itemDefinitions.find { it.id.id == "VS_NON_NORM_02_03.VSWGHT" }

        then:
        item != null
        item.dataType == "float"
        item.measurementUnits.size() == 1
    }

    def "can map measurement units"() {
        given:
        def studyMetaData = mapper.toStudyMetaData(parsedModel)
        def metaDataDefinition = studyMetaData.metaDataDefinition
        def eventDefinition = metaDataDefinition.eventDefinitions.find { it.getId().id == "SUBJECT" }
        def formDefinition = eventDefinition.formDefinitions.find { it.getId().id == "SUBJSTAT" }
        def itemGroup = formDefinition.itemGroupDefinitions.find { it.id.id == "SUBJSTAT_LOG_LINE" }
        def item = itemGroup.itemDefinitions.find { it.id.id == "VS_NON_NORM_02_03.VSWGHT" }

        when:
        def measurementUnit = item.measurementUnits.find { it.id == "kg" }

        then:
        measurementUnit != null
        measurementUnit.name == "kg"
    }
}
