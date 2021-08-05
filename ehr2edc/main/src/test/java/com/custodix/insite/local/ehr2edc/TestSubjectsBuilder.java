package com.custodix.insite.local.ehr2edc;

import static java.util.stream.IntStream.range;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.custodix.insite.local.ehr2edc.snapshots.SubjectSnapshot;

public final class TestSubjectsBuilder {

	private final List<SubjectSnapshot.Builder> subjectsUnderConstruction;

	public static TestSubjectsBuilder amountOfSubjects(int amountOfSubjectsToCreate){
		return new TestSubjectsBuilder(amountOfSubjectsToCreate);
	}

	private TestSubjectsBuilder(int amountOfSubjectsToCreate) {
		this.subjectsUnderConstruction = range(0, amountOfSubjectsToCreate)
				.mapToObj(i -> SubjectSnapshot.newBuilder())
				.collect(Collectors.toList());
	}

	public void applyToAll(Consumer<SubjectSnapshot.Builder> modifySubject){
		subjectsUnderConstruction.forEach(modifySubject::accept);
	}

	public <T> void applyToEach(List<T> values, BiConsumer<T, SubjectSnapshot.Builder> modifySubject){
		range(0, subjectsUnderConstruction.size())
				.forEach(i -> {
					SubjectSnapshot.Builder builder = subjectsUnderConstruction.get(i);
					T value = values.get(i);
					modifySubject.accept(value, builder);
				});
	}

	public List<SubjectSnapshot> toSnapshots(){
		return subjectsUnderConstruction.stream()
				.map(SubjectSnapshot.Builder::build)
				.collect(Collectors.toList());
	}
}
