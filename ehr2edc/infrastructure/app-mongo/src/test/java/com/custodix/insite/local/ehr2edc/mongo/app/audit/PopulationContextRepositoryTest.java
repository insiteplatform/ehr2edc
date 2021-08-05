package com.custodix.insite.local.ehr2edc.mongo.app.audit;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.custodix.insite.local.ehr2edc.audit.PopulationContext;
import com.custodix.insite.local.ehr2edc.audit.PopulationContextRepository;
import com.custodix.insite.local.ehr2edc.mongo.app.AppMongoTestContext;
import com.custodix.insite.local.ehr2edc.vocabulary.EventId;
import com.custodix.insite.local.ehr2edc.vocabulary.PopulationContextId;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AppMongoTestContext.class)
public class PopulationContextRepositoryTest {

	@Autowired
	private PopulationContextRepository populationContextRepository;

	@Autowired
	private PopulationContextMongoRepository.PopulationContextSnapshotRepository populationContextSnapshotRepository;

	@Test
	public void testPersistPopulationContext() {
		PopulationContextId populationContextId = PopulationContextId.of("PopulationId");
		EventId eventId = EventId.of("EventId");
		PopulationContext context = PopulationContext.newBuilder()
				.withId(populationContextId)
				.withEventId(eventId)
				.withItemQueryMappingsJson("{ \"item\" : \"mappings\", \"fieldName.with.dot\" : \"mappings\" }")
				.withEventDefinitionJson("{ \"id\" : \"eventOID\", \"fieldName.with.dot\" : \"value\" }")
				.withDatapointsJsons(Arrays.asList("{ \"id\" : \"eventOID\", \"fieldName.with.dot\" : \"value\" }",
						"{ \"id\" : \"eventOID\", \"fieldName.with.dot\" : \"value\" }"))
				.build();

		populationContextRepository.save(context);

		PopulationContextDocument snapshot = populationContextSnapshotRepository.findById(context.getId()
				.getId())
				.orElseThrow(AssertionError::new);

		assertPopulationContextMongoSnapshot(context, snapshot);
	}

	private void assertPopulationContextMongoSnapshot(PopulationContext context, PopulationContextDocument snapshot) {
		PopulationContext fromSnapshot = snapshot.restore();
		assertEquals(context.getId(), fromSnapshot.getId());
		assertEquals(context.getEventId(), fromSnapshot.getEventId());
		assertEquals(context.getItemQueryMappingsJson(), fromSnapshot.getItemQueryMappingsJson());
		assertEquals(context.getEventDefinitionJson(), fromSnapshot.getEventDefinitionJson());
		assertEquals(context.getDatapointsJsons(), fromSnapshot.getDatapointsJsons());
	}

}
