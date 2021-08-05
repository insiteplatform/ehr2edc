@TypeDefs({ @TypeDef(name = "userIdentifier",
					 typeClass = UserIdentifierType.class,
					 defaultForType = UserIdentifier.class),
			@TypeDef(name = "cohortIdentifier",
					 typeClass = CohortIdentifierType.class,
					 defaultForType = CohortIdentifier.class),
			@TypeDef(name = "studyIdentifier",
					 typeClass = StudyIdentifierType.class,
					 defaultForType = StudyIdentifier.class),
			@TypeDef(name = "filterIdentifier",
					 typeClass = FilterIdentifierType.class,
					 defaultForType = FilterIdentifier.class),
			@TypeDef(name = "protocolDocumentIdentifier",
					 typeClass = ProtocolDocumentIdentifierType.class,
					 defaultForType = ProtocolDocumentIdentifier.class) })
package eu.ehr4cr.workbench.local.dao.types;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import eu.ehr4cr.workbench.local.vocabulary.CohortIdentifier;
import eu.ehr4cr.workbench.local.vocabulary.UserIdentifier;
import eu.ehr4cr.workbench.local.vocabulary.clinical.FilterIdentifier;
import eu.ehr4cr.workbench.local.vocabulary.clinical.ProtocolDocumentIdentifier;
import eu.ehr4cr.workbench.local.vocabulary.clinical.StudyIdentifier;