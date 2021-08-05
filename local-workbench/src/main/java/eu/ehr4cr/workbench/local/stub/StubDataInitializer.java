package eu.ehr4cr.workbench.local.stub;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "generateMockData",
					   havingValue = "true")
class StubDataInitializer implements ApplicationListener<ApplicationReadyEvent>, Ordered {

	private final List<StubDataCreator> stubDataCreators;

	public StubDataInitializer(final List<StubDataCreator> stubDataCreators) {
		this.stubDataCreators = stubDataCreators;
	}

	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		stubDataCreators.forEach(StubDataCreator::createStubData);
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}
