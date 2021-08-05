package com.custodix.insite.local.ehr2edc.infra.odm.parser


import com.custodix.insite.local.ehr2edc.shared.exceptions.SystemException
import com.custodix.insite.local.ehr2edc.shared.exceptions.UserException
import org.springframework.core.io.ClassPathResource
import org.springframework.util.FileCopyUtils
import spock.lang.Specification

class OdmXmlMetaDataParserSpec extends Specification {
    def "A Study element can be parsed"() {
        given: "A valid ODM XML"
        def odmXml = getValidODMXml()

        when: "The ODM XML is parsed"
        def odm = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)

        then: "Study is parsed correctly"
        def study = odm.study
        study != null
        study.oid == "EHR2EDC"
    }

    def "A GlobalVariables element can be parsed"() {
        given: "A valid ODM XML"
        def odmXml = getValidODMXml()

        when: "The ODM XML is parsed"
        def odm = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)

        then: "GlobalVariables is parsed correctly"
        def globalVariables = odm.study.globalVariables
        globalVariables != null
        globalVariables.name == "EHR2EDC Study"
        globalVariables.description == "EHR2EDC Description"
    }

    def "A MetaDataVersion element can be parsed"() {
        given: "A valid ODM XML"
        def odmXml = getValidODMXml()

        when: "The ODM XML is parsed"
        def odm = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)

        then: "MetaDataVersion is parsed correctly"
        def metaDataVersion = odm.study.metaDataVersion
        metaDataVersion != null
        metaDataVersion.oid == "2788"
        metaDataVersion.name == "D1.0_DEV_117MAR2019"
        metaDataVersion.studyEventDefs != null
        metaDataVersion.formDefs != null
        metaDataVersion.itemDefs != null
        metaDataVersion.itemGroupDefs != null
        metaDataVersion.codeLists != null
    }

    def "A BasicDefinitions element can be parsed"() {
        given: "A valid ODM XML"
        def odmXml = getValidODMXml()

        when: "The ODM XML is parsed"
        def odm = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)

        then: "BasicDefinitions is parsed correctly"
        def basicDefinitions = odm.study.basicDefinitions
        basicDefinitions != null
        basicDefinitions.measurementUnits.size() == 11
    }

    def "A StudyEventDef elements can be parsed"() {
        given: "A valid ODM XML"
        def odmXml = getValidODMXml()

        when: "The ODM XML is parsed"
        def odm = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)

        then: "StudyEventDef is parsed correctly"
        def studyEventDefs = odm.study.metaDataVersion.studyEventDefs
        studyEventDefs.size() == 15
        and: "Optional ParentId is parsed"
        studyEventDefs.any {
            it.oid == "00"
            it.parentId == "PID"
        }
    }

    def "A FormDef elements can be parsed"() {
        given: "A valid ODM XML"
        def odmXml = getValidODMXml()

        when: "The ODM XML is parsed"
        def odm = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)

        then: "FormDef is parsed correctly"
        def formDefs = odm.study.metaDataVersion.formDefs
        formDefs.size() == 143
        formDefs.each {
            assert it.oid != null
            assert it.itemGroupRefs.size() > 0
        }
    }

    def "A ItemGroupDef elements can be parsed"() {
        given: "A valid ODM XML"
        def odmXml = getValidODMXml()

        when: "The ODM XML is parsed"
        def odm = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)

        then: "ItemGroupDef is parsed correctly"
        def itemGroupDefs = odm.study.metaDataVersion.itemGroupDefs
        itemGroupDefs.size() == 210
    }

    def "A ItemDef elements can be parsed"() {
        given: "A valid ODM XML"
        def odmXml = getValidODMXml()

        when: "The ODM XML is parsed"
        def odm = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)

        then: "ItemDef is parsed correctly"
        def itemDefs = odm.study.metaDataVersion.itemDefs
        itemDefs.size() == 1338
        itemDefs.each {
            assert it.oid != null
        }
    }

    def "A CodeList elements can be parsed"() {
        given: "A valid ODM XML"
        def odmXml = getValidODMXml()

        when: "The ODM XML is parsed"
        def odm = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)

        then: "CodeList is parsed correctly"
        def codeLists = odm.study.metaDataVersion.codeLists
        codeLists.size() == 122
        codeLists.each {
            assert it.oid != null
        }
    }

    def "A FormDef element can be parsed"() {
        given: "A valid ODM XML"
        def odmXml = getValidODMXml()

        when: "The ODM XML is parsed"
        def odm = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)

        then: "FormDef is parsed correctly"
        def formDef = odm.study.metaDataVersion.getFormDef("SUBJSTAT")
        formDef.isPresent()
        formDef.get().name == "Subject Status Technical Form"
        formDef.get().itemGroupRefs.size() == 1
    }

    def "A MeasurementUnit element can be parsed"() {
        given: "A valid ODM XML"
        def odmXml = getValidODMXml()

        when: "The ODM XML is parsed"
        def odm = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)

        then: "MeasurementUnit is parsed correctly"
        def measurementUnit = odm.study.basicDefinitions.getMeasurementUnit("cm")
        measurementUnit.isPresent()
        measurementUnit.get().name == "cm"
    }

    def "A ItemGroupDef element can be parsed"() {
        given: "A valid ODM XML"
        def odmXml = getValidODMXml()

        when: "The ODM XML is parsed"
        def odm = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)

        then: "ItemGroupDef is parsed correctly"
        def itemGroupDef = odm.study.metaDataVersion.getItemGroupDef("SUBJSTAT_LOG_LINE")
        itemGroupDef.isPresent()
        itemGroupDef.get().name == "SUBJSTAT_LOG_LINE"
        itemGroupDef.get().itemRefs.size() == 5
    }

    def "An ItemDef with CodeList can be parsed"() {
        given: "A valid ODM XML"
        def odmXml = getValidODMXml()

        when: "The ODM XML is parsed"
        def odm = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)

        then: "ItemDef is parsed correctly"
        def itemDef = odm.study.metaDataVersion.getItemDef("SV_01_01.VISYN")
        itemDef.isPresent()
        itemDef.get().name == "VISYN"
        itemDef.get().dataType == "text"
        itemDef.get().length == 1
        itemDef.get().codeListRef != null
        itemDef.get().codeListRef.codeListOid == "NY_YN"
    }

    def "An ItemDef with MeasurementUnit can be parsed"() {
        given: "A valid ODM XML"
        def odmXml = getValidODMXml()

        when: "The ODM XML is parsed"
        def odm = new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)

        then: "ItemDef is parsed correctly"
        def itemDef = odm.study.metaDataVersion.getItemDef("VS_NON_NORM_02_03.VSWGHT")
        itemDef.isPresent()
        itemDef.get().name == "VSWGHT"
        itemDef.get().dataType == "float"
        itemDef.get().length == 5
        itemDef.get().measurementUnitRefs != null
        itemDef.get().measurementUnitRefs.size() == 1
    }

    def "An invalid ODM results in a UserException"() {
        given: "An invalid ODM XML"
        def odmXml = getInvalidODMXml()

        when: "The ODM XML is parsed"
        new OdmXmlMetaDataParserImpl().parseToXmlModel(odmXml)

        then: "A UserException is thrown"
        def exception = thrown UserException
        and: "The exception message gives information about the error"
        def prefix = "ODM XML is invalid: "
        exception.message.startsWith(prefix)
        exception.message.size() > prefix.size()
    }

    def "An unexpected error results in a SystemException"() {
        when: "A null ODM XML is parsed"
        new OdmXmlMetaDataParserImpl().parseToXmlModel(null)

        then: "A SystemException is thrown"
        def exception = thrown SystemException
        exception.message == "Could not parse ODM XML"
    }

    private String getValidODMXml() {
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("/ehr2edc-odm-sample.xml").getFile()))
    }

    private String getInvalidODMXml() {
        return new String(FileCopyUtils.copyToByteArray(new ClassPathResource("/odm-invalid.xml").getFile()))
    }
}


