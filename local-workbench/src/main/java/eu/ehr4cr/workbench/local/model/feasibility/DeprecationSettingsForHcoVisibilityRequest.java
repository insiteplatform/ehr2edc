package eu.ehr4cr.workbench.local.model.feasibility;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DeprecationSettingsForHcoVisibilityRequest {

    private final Integer deprecationPeriodInDays;

    public DeprecationSettingsForHcoVisibilityRequest(
            @Value("${feasibility.study.with.hco.visibility.request.deprecation.period.for.events.in.days:14}")
            final Integer deprecationPeriodInDays) {
        this.deprecationPeriodInDays = deprecationPeriodInDays;
    }

    public Date getDate() {
        return DateUtils.addDays(new Date(), deprecationPeriodInDays * -1);
    }
}
