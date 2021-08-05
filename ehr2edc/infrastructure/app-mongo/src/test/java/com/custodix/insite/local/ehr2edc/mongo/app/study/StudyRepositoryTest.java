package com.custodix.insite.local.ehr2edc.mongo.app.study;

import static com.custodix.insite.local.ehr2edc.UserObjectMother.createUser;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.custodix.insite.local.ehr2edc.Study;
import com.custodix.insite.local.ehr2edc.StudyRepository;
import com.custodix.insite.local.ehr2edc.Subject;
import com.custodix.insite.local.ehr2edc.metadata.model.EventDefinition;
import com.custodix.insite.local.ehr2edc.metadata.model.FormDefinition;
import com.custodix.insite.local.ehr2edc.mongo.app.AppMongoTestContext;
import com.custodix.insite.local.ehr2edc.mongo.app.document.StudyDocument;
import com.custodix.insite.local.ehr2edc.query.InvestigatorProjection;
import com.custodix.insite.local.ehr2edc.shared.exceptions.DomainException;
import com.custodix.insite.local.ehr2edc.snapshots.*;
import com.custodix.insite.local.ehr2edc.user.UserRepository;
import com.custodix.insite.local.ehr2edc.vocabulary.*;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AppMongoTestContext.class)
public class StudyRepositoryTest {
	private static final StudyId STUDY_ID = StudyId.of("studyId");
	private static final StudyId STUDY_ID2 = StudyId.of("studyId2");
	private static final UserIdentifier NOT_AN_INVESTIGATOR = UserIdentifier.of("notAnInvestigator");
	private static final int SEED = 1235;
	private static final Random RANDOM = new Random(SEED);
	private static final String STUDY_NAME = "studyname";
	private static final String STUDY_DESCRIPTION = "test";
	private static final UserIdentifier INVESTIGATOR_ID = UserIdentifier.of("investigatorId");
	private static final String INVESTIGATOR_NAME = "Name investigatorId";
	private static final EventDefinitionId EVENT_DEFINITION_ID = EventDefinitionId.of("eventDefinitionId");
	private static final String EVENT_NAME = "Event Name";
	private static final FormDefinitionId FORM_DEFINITION_ID = FormDefinitionId.of("formDefinitionId");
	private static final String FORM_NAME = "Form Name";
	private static final LabName LOCAL_LAB = LabName.of("LAB_1");

	@Autowired
	private StudyRepository studyRepository;

	@Autowired
	private StudyMongoRepository.StudyMongoSnapshotRepository studyMongoSnapshotRepository;
	@MockBean
	private UserRepository userRepository;
	private StudySnapshot studySnapshot;
	private StudySnapshot studySnapshot2;
	private static final PatientCDWReference PATIENT_GERT = createPatientId("patientId", "patientSource");

	@Before
	public void setUp() {
		studySnapshot = createStudySnapshot(STUDY_ID);
		studySnapshot2 = createStudySnapshot(STUDY_ID2);
		studyMongoSnapshotRepository.saveAll(asList(StudyDocument.fromSnapshot(studySnapshot), StudyDocument.fromSnapshot(studySnapshot2)));
		mockUserRepository();
	}

	@After
	public void tearDown() {
		studyMongoSnapshotRepository.deleteAll();
	}

	@Test(expected = DomainException.class)
	public void testRetrieveSubjectUnknownSubject() {
		UserIdentifier userId = UserIdentifier.of("userId");
		SubjectId subjectIdGert = SubjectId.of("SUBJECT_ID_GERT");
		StudySnapshot study = createStudySnapshot(
				Collections.singletonList(createSubject(subjectIdGert, PATIENT_GERT, "user")),
				Collections.singletonList(createInvestigator(userId)));
		save(study);

		studyRepository.findSubjectByIdAndStudyIdAndInvestigator(SubjectId.of("UnkownsubjectId"), study.getStudyId(),
				userId);

	}

	private void save(StudySnapshot study) {
		studyMongoSnapshotRepository.save(StudyDocument.fromSnapshot(study));
	}

	@Test
	public void testRetrieveSubjectKnownSubject() {
		UserIdentifier userId = UserIdentifier.of("userId");
		SubjectId subjectIdGert = SubjectId.of("SUBJECT_ID_GERT");
		StudySnapshot study = createStudySnapshot(
				Collections.singletonList(createSubject(subjectIdGert, PATIENT_GERT, "SUBJECT_ID_GERT")),
				Collections.singletonList(createInvestigator(userId)));
		save(study);

		Subject subject = studyRepository.findSubjectByIdAndStudyIdAndInvestigator(subjectIdGert, study.getStudyId(),
				userId);
		assertThat(subject.getSubjectId()).isEqualTo(subjectIdGert);

	}

	@Test
	public void testSave() {
		StudyId studyId = StudyId.of("10");
		StudySnapshot studySnapshot = createStudySnapshot(studyId);

		studyRepository.save(Study.restoreSnapshot(studySnapshot));

		Study savedStudy = studyRepository.getStudyById(studyId);
		validateStudy(savedStudy, studyId);
	}

	@Test
	public void findAvailableStudiesForPatientNotASubject() {
		UserIdentifier userId = UserIdentifier.of("userId");
		StudySnapshot study = createStudyWithInvestigators(Collections.singletonList(createInvestigator(userId)));
		save(study);

		List<Study> availableStudiesForPatient = studyRepository.findAvailableStudiesForPatient(PATIENT_GERT, userId);
		assertThat(availableStudiesForPatient).isNotEmpty();
		assertThat(availableStudiesForPatient.get(0)
				.getStudyId()).isEqualTo(study.getStudyId());
	}

	@Test
	public void findAvailableStudiesForPatientASubjectUnknownInvestigator() {
		UserIdentifier userId = UserIdentifier.of("userId");
		StudySnapshot study = createStudyWithInvestigators(Collections.singletonList(createInvestigator(userId)));
		save(study);

		List<Study> availableStudiesForPatient = studyRepository.findAvailableStudiesForPatient(PATIENT_GERT,
				NOT_AN_INVESTIGATOR);
		assertThat(availableStudiesForPatient).isEmpty();
	}

	@Test
	public void findAvailableStudiesForPatientASubject() {
		UserIdentifier userId = UserIdentifier.of("userId");
		StudySnapshot study = createStudySnapshot(
				Collections.singletonList(createSubject(SubjectId.of("blablabla"), PATIENT_GERT, "blablabla")),
				Collections.singletonList(createInvestigator(userId)));
		save(study);

		List<Study> availableStudiesForPatient = studyRepository.findAvailableStudiesForPatient(PATIENT_GERT, userId);
		assertThat(availableStudiesForPatient).isEmpty();
	}

	@Test
	public void findRegisteredStudiesForPatientPatientKnownAsSubject() {
		UserIdentifier userId = UserIdentifier.of("userId");
		StudySnapshot study = createStudySnapshot(
				Collections.singletonList(createSubject(SubjectId.of("blablabla"), PATIENT_GERT, "blablabla")),
				Collections.singletonList(createInvestigator(userId)));
		save(study);

		List<Study> availableStudiesForPatient = studyRepository.findRegisteredStudiesForPatient(PATIENT_GERT, userId);
		assertThat(availableStudiesForPatient).isNotEmpty();
		assertThat(availableStudiesForPatient.get(0)
				.getStudyId()).isEqualTo(study.getStudyId());
	}

	@Test
	public void testSaveTwoTimes() {
		studyMongoSnapshotRepository.deleteAll();

		StudyId of = StudyId.of("10");
		studyRepository.save(Study.restoreSnapshot(createStudySnapshot(of)));

		Study byStudyId = studyRepository.getStudyById(of);
		assertThat(byStudyId).isNotNull();

		byStudyId.assignInvestigator(UserIdentifier.of("New"), userRepository);
		byStudyId.assignInvestigator(UserIdentifier.of("New2"), userRepository);

		studyRepository.save(byStudyId);

		List<StudyDocument> all = studyMongoSnapshotRepository.findAll();
		assertThat(all).hasSize(1);
		assertThat(all.get(0)
				.getInvestigators()).hasSize(3);
	}

	@Test
	public void findRegisteredStudiesForPatientPatientKnownAsSubjectNotAnInvestigator() {
		UserIdentifier userId = UserIdentifier.of("userId");
		StudySnapshot study = createStudySnapshot(
				Collections.singletonList(createSubject(SubjectId.of("blablabla"), PATIENT_GERT, "blablabla")),
				Collections.singletonList(createInvestigator(userId)));
		save(study);

		List<Study> availableStudiesForPatient = studyRepository.findRegisteredStudiesForPatient(PATIENT_GERT,
				NOT_AN_INVESTIGATOR);
		assertThat(availableStudiesForPatient).isEmpty();
	}

	@Test
	public void findRegisteredStudiesForPatientPatientUnknownAsSubject() {
		PatientCDWReference patient = createPatientId("patientId", "patientSource");
		UserIdentifier userId = UserIdentifier.of("userId");
		StudySnapshot study = createStudySnapshot(
				Collections.singletonList(createSubject(SubjectId.of("blablabla"), patient, "blablabla")),
				Collections.singletonList(createInvestigator(userId)));
		save(study);

		List<Study> availableStudiesForPatient = studyRepository.findRegisteredStudiesForPatient(
				createPatientId("patientIdUnkown", "patientSource"), userId);
		assertThat(availableStudiesForPatient).isEmpty();
	}

	@Test
	public void testExists() {
		StudyId of = StudyId.of("10");
		save(createStudySnapshot(of));
		assertThat(studyRepository.exists(of)).isTrue();

	}

	@Test
	public void testGetAllStudies() {
		List<Study> all = studyRepository.findAll();
		assertThat(all).hasSize(2);
	}

	@Test
	public void findStudyByIdAndInvestigator() {
		StudyId studyId = StudyId.of("findStudyByIdAndInvestigator");
		UserIdentifier investigatorId = UserIdentifier.of("investigatorId2");
		StudySnapshot study = createStudyWithInvestigators(studyId, generateInvestigators(investigatorId));
		save(study);

		Study studyByIdAndInvestigator = studyRepository.findStudyByIdAndInvestigator(studyId, investigatorId);
		assertThat(studyByIdAndInvestigator.getStudyId()).isEqualTo(study.getStudyId());
		Assertions.assertThat(studyByIdAndInvestigator)
				.isEqualToComparingFieldByFieldRecursively(Study.restoreSnapshot(study));
	}

	@Test(expected = DomainException.class)
	public void findStudyByIdAndInvestigatorUnknown() {
		StudyId studyId = StudyId.of("findStudyByIdAndInvestigator");
		UserIdentifier investigatorId = UserIdentifier.of("investigatorId");
		StudySnapshot study = createStudyWithInvestigators(studyId, generateInvestigators(investigatorId));
		save(study);

		studyRepository.findStudyByIdAndInvestigator(studyId, UserIdentifier.of("investigatorId2"));
	}

	@Test
	public void getStudyById() {
		Study study = studyRepository.getStudyById(STUDY_ID);

		validateStudy(study, STUDY_ID);
		Assertions.assertThat(StudyDocument.fromSnapshot(study.toSnapShot()))
				.isEqualToComparingFieldByFieldRecursively(StudyDocument.fromSnapshot(studySnapshot));
	}

	private void mockUserRepository() {
		when(userRepository.getUsers()).thenReturn(Arrays.asList(createUser("New"), createUser("New2")));
		when(userRepository.getUser(any(UserIdentifier.class))).thenAnswer(invocation -> {
			UserIdentifier userIdentifier = invocation.getArgument(0);
			return createUser(userIdentifier.getId());
		});
	}

	private void validateStudy(Study study, StudyId expectedStudyId) {
		assertThat(study.getStudyId()).isEqualTo(expectedStudyId);
		assertThat(study.getName()).isEqualTo(STUDY_NAME + expectedStudyId.getId());
		assertThat(study.getDescription()).isEqualTo(STUDY_DESCRIPTION);
		assertThat(study.getInvestigators()
				.getAssigned()).extracting(InvestigatorProjection::getInvestigatorId)
				.contains(INVESTIGATOR_ID);
		assertThat(study.getInvestigators()
				.getAssigned()).extracting(InvestigatorProjection::getName)
				.contains(INVESTIGATOR_NAME);
		validateEventDefinition(getFindEventDefinition(study).get());
	}

	private Optional<EventDefinition> getFindEventDefinition(Study study) {
		return ReflectionTestUtils.invokeMethod(study, "findEventDefinition", EVENT_DEFINITION_ID);
	}

	private void validateEventDefinition(EventDefinition eventDefinition) {
		assertThat(eventDefinition.getId()).isEqualTo(EVENT_DEFINITION_ID);
		assertThat(eventDefinition.getName()).isEqualTo(EVENT_NAME);
		eventDefinition.getFormDefinitions().forEach(this::validateFormDefinition);
	}

	private void validateFormDefinition(FormDefinition formDefinition) {
		assertThat(formDefinition.getId()).isEqualTo(FORM_DEFINITION_ID);
		assertThat(formDefinition.getName()).isEqualTo(FORM_NAME);
		assertThat(formDefinition.getItemGroupDefinitions()).isNotEmpty();
		assertThat(formDefinition.getLocalLab()).isEqualTo(LOCAL_LAB);
	}

	private SubjectSnapshot createSubject(SubjectId subjectIdGert, PatientCDWReference patientGert,
			String subject_id_gert) {
		return SubjectSnapshot.newBuilder()
				.withPatientCDWReference(patientGert)
				.withSubjectId(subjectIdGert)
				.withEdcSubjectReference(EDCSubjectReference.of(subject_id_gert))
				.withDateOfConsent(LocalDate.now())
				.build();
	}

	private InvestigatorSnapshot createInvestigator(UserIdentifier userId) {
		return InvestigatorSnapshot.newBuilder()
				.withName("investigatorId")
				.withUserId(userId)
				.build();
	}

	private static PatientCDWReference createPatientId(String patientId, String patientSource) {
		return PatientCDWReference.newBuilder()
				.withId(patientId)
				.withSource(patientSource)
				.build();
	}

	private StudySnapshot createStudySnapshot(Collection<SubjectSnapshot> subjects,
			Collection<InvestigatorSnapshot> investigatorSnapshots) {
		return createStudySnapshot(StudyId.of(UUID.randomUUID()
				.toString()), investigatorSnapshots, subjects);
	}

	private StudySnapshot createStudySnapshot(StudyId studyId, Collection<InvestigatorSnapshot> investigators,
			Collection<SubjectSnapshot> subjects) {
		return StudySnapshot.newBuilder()
				.withName(STUDY_NAME + studyId.getId())
				.withDescription(STUDY_DESCRIPTION)
				.withStudyId(studyId)
				.withSubjects(subjects)
				.withInvestigators(investigators)
				.withMetadata(generateMetadata())
				.build();
	}

	private StudySnapshot createStudySnapshot(StudyId studyId) {
		return createStudyWithInvestigators(studyId, generateInvestigators(INVESTIGATOR_ID));
	}

	private StudySnapshot createStudyWithInvestigators(Collection<InvestigatorSnapshot> investigators) {
		return createStudyWithInvestigators(StudyId.of(UUID.randomUUID()
				.toString()), investigators);
	}

	private StudySnapshot createStudyWithInvestigators(StudyId studyId, Collection<InvestigatorSnapshot> investigators) {
		return createStudySnapshot(studyId, investigators, createSubjects());
	}

	private HashSet<SubjectSnapshot> createSubjects() {
		return Sets.newHashSet(SubjectSnapshot.newBuilder()
				.withEdcSubjectReference(EDCSubjectReference.of("subjectId"))
				.withPatientCDWReference(createPatientId("oatientId", "patientSource"))
				.withSubjectId(SubjectId.of("subjectId"))
				.build());
	}

	public static Set<InvestigatorSnapshot> generateInvestigators(UserIdentifier investigatorId) {
		return new HashSet<>(singleton(generateInvestigator(investigatorId)));
	}

	private static MetaDataDefinitionSnapshot generateMetadata() {
		return MetaDataDefinitionSnapshot.newBuilder()
				.withId("MetaDataDefinitionSnapshotId")
				.withEventDefinitions(singletonList(generateEventDefinition()))
				.build();
	}

	private static InvestigatorSnapshot generateInvestigator(UserIdentifier investigatorId) {
		return InvestigatorSnapshot.newBuilder()
				.withUserId(investigatorId)
				.withName("Name " + investigatorId.getId())
				.build();
	}

	private static EventDefinitionSnapshot generateEventDefinition() {
		return EventDefinitionSnapshot.newBuilder()
				.withId(EVENT_DEFINITION_ID)
				.withName(EVENT_NAME)
				.withFormDefinitions(IntStream.range(0, 5)
						.mapToObj(i -> generateFormDefinition())
						.collect(toList()))
				.build();
	}

	private static FormDefinitionSnapshot generateFormDefinition() {
		return FormDefinitionSnapshot.newBuilder()
				.withId(FORM_DEFINITION_ID)
				.withName(FORM_NAME)
				.withItemGroupDefinitions(singletonList(generateItemGroup()))
				.withLocalLab(LOCAL_LAB)
				.build();
	}

	private static ItemGroupDefinitionSnapshot generateItemGroup() {
		return ItemGroupDefinitionSnapshot.newBuilder()
				.withId(ItemGroupDefinitionId.of("itemGroupSnapshotId"))
				.withItemDefinitions(IntStream.range(0, 3)
						.mapToObj(StudyRepositoryTest::generateItem)
						.collect(toList()))
				.withRepeating(RANDOM.nextBoolean())
				.build();
	}

	private static ItemDefinitionSnapshot generateItem(int i) {
		return ItemDefinitionSnapshot.newBuilder()
				.withId(ItemDefinitionId.of("itemId" + i))
				.withDataType("dataType")
				.withCodeList(generateCodeList())
				.withLength(i)
				.withMeasurementUnits(generateMeasurementUnits())
				.withLabel(new ItemLabelSnapshot("label" + i))
				.build();
	}

	private static CodeListSnapshot generateCodeList() {
		return CodeListSnapshot.newBuilder()
				.withId("codeListSnapshot")
				.build();
	}

	private static List<MeasurementUnitSnapshot> generateMeasurementUnits() {
		return Collections.singletonList(MeasurementUnitSnapshot.newBuilder()
				.withName("measureMentUnit")
				.withId("measureMentUnitDB")
				.build());
	}
}