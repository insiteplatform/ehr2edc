package eu.ehr4cr.workbench.local.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import eu.ehr4cr.workbench.local.model.security.User;
import eu.ehr4cr.workbench.local.security.SecurityContextUser;
import eu.ehr4cr.workbench.local.service.IUserMgrService;

@Service
public class WorkbenchUserDetailServiceImpl implements UserDetailsService {

	@Autowired
	private IUserMgrService userManagementService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SecurityContextUser securityContextUser = null;
		User user = userManagementService.findUserByUsername(username);
		if (user != null) {
			securityContextUser = new SecurityContextUser(user);
		}
		return securityContextUser;
	}
}
