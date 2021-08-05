package eu.ehr4cr.workbench.local.model;

import java.util.ArrayList;

import eu.ehr4cr.workbench.local.model.clinicalStudy.TreatingPhysician;
import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.service.DomainTime;
import eu.ehr4cr.workbench.local.service.TestTimeService;

public class UserObjectMother {
	public static User createActiveUser() {
		User user = createUser();
		user.setEnabled(true);
		return user;
	}

	public static User createDisabledUser() {
		User user = createUser();
		user.setEnabled(false);
		return user;
	}

	public static User createDeletedUser() {
		User user = createUser();
		user.setEnabled(true);
		user.delete();
		return user;
	}

	public static TreatingPhysician createTreatingPhysician() {
		TreatingPhysician physician = new TreatingPhysician();
		physician.setUser(createActiveUser());
		physician.setId(1L);
		physician.setProviderId("Test provider");
		physician.setIsDefault(false);
		physician.setPatients(new ArrayList<>());
		return physician;
	}

	private static User createUser() {
		DomainTime.setTime(new TestTimeService());
		User user = new User("Test user", "testUser!", "testuser@devnull.com");
		user.setId(1L);
		user.updateSecurityQuestion("1", "Test answer");
		return user;
	}
}
