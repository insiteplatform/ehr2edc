package eu.ehr4cr.workbench.local.model.clinicalStudy;

import static java.lang.Math.max;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.DAYS;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import eu.ehr4cr.workbench.local.usecases.clinical.study.create.CreateClinicalStudyInfo;
import eu.ehr4cr.workbench.local.usecases.clinical.study.update.UpdateClinicalStudyInfo;
import eu.ehr4cr.workbench.local.vocabulary.clinical.StudyIdentifier;

@Entity
@Table(name = "clinical_study")
public class ClinicalStudy {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(length = 400)
	private String name;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String description;

	private Boolean archived;

	@Enumerated(EnumType.STRING)
	private ClinicalStudyState state;

	@ManyToOne
	private ExternalUser sponsor;

	private Date creationDate;

	private Date lastUpdated;

	@Column(unique = true)
	private String externalId;

	private Long goal;
	private Date deadline;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "clinical_study_investigators",
			   joinColumns = { @JoinColumn(name = "clinical_study_id") },
			   inverseJoinColumns = { @JoinColumn(name = "investigator_id") })
	private List<PrincipalInvestigator> investigators;

	@OneToOne
	private ProtocolVersionScreeningFilter currentScreeningFilters;

	public ClinicalStudy() {
		// JPA
	}

	public ClinicalStudy(CreateClinicalStudyInfo studyInfo, ExternalUser sponsor,
			List<PrincipalInvestigator> investigators) {
		this.archived = false;
		this.creationDate = studyInfo.getCreationDate();
		this.externalId = studyInfo.getPublicId();
		this.goal = studyInfo.getGoal();
		this.lastUpdated = studyInfo.getModificationDate();
		this.name = studyInfo.getName();
		this.state = ClinicalStudyState.REQUESTED;
		this.deadline = studyInfo.getDeadline();
		this.description = studyInfo.getDescription();
		this.sponsor = sponsor;
		this.investigators = investigators;
	}

	public void update(UpdateClinicalStudyInfo studyInfo, ExternalUser sponsor) {
		this.sponsor = sponsor;
		this.lastUpdated = studyInfo.getModificationDate();
		this.name = studyInfo.getName();
		this.goal = studyInfo.getGoal();
		this.description = studyInfo.getDescription();
	}

	public void accept() {
		this.state = ClinicalStudyState.ACCEPTED;
	}

	public void decline() {
		this.state = ClinicalStudyState.DECLINED;
	}

	public StudyIdentifier getIdentifier() {
		return StudyIdentifier.of(id);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getArchived() {
		return archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}

	public ClinicalStudyState getState() {
		return state;
	}

	public void setState(ClinicalStudyState state) {
		this.state = state;
	}

	public ExternalUser getSponsor() {
		return sponsor;
	}

	public void setSponsor(ExternalUser sponsor) {
		this.sponsor = sponsor;
	}

	public Long getGoal() {
		return goal;
	}

	public void setGoal(Long goal) {
		this.goal = goal;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public List<PrincipalInvestigator> getInvestigators() {
		return investigators;
	}

	public void setInvestigators(List<PrincipalInvestigator> investigators) {
		this.investigators = investigators;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public ProtocolVersionScreeningFilter getCurrentScreeningFilters() {
		return currentScreeningFilters;
	}

	public void setCurrentScreeningFilters(ProtocolVersionScreeningFilter currentScreeningFilters) {
		this.currentScreeningFilters = currentScreeningFilters;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Optional<Long> daysUntilDeadline() {
		return Optional.ofNullable(deadline).map(d -> {
			long daysBetween = DAYS.between(now().atZone(systemDefault()), d.toInstant().atZone(systemDefault()));
			return max(daysBetween, 0L);
		});
	}

	public boolean isInState(ClinicalStudyState... states) {
		return Arrays.stream(states).anyMatch(s -> s == state);
	}

	public boolean isFilterEditingAllowed() {
		return !isInState(ClinicalStudyState.REQUESTED, ClinicalStudyState.DECLINED, ClinicalStudyState.CLOSED);
	}

	public boolean isFiltering() {
		return isInState(ClinicalStudyState.RECRUITING, ClinicalStudyState.PAUSED_ACCRUAL, ClinicalStudyState.PAUSED_ACCRUAL_INTERVENTION);
	}
}