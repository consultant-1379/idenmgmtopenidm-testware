/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.nms.security.taf.test.operators;

import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.nms.security.ENMUser;

public interface UserMgmtOperator {

	public void connect();

	public void connect(String userName, String userPassword, UserType userType);

	public void disconnect();

	public boolean checkIfCanLogin(String userName, String userPassword,
			UserType userType);

	public String createUser(final ENMUser user);

	public String assignUserToRole(final String role, final String username);

	public String unassignUserFromRole(final String role, final String username);

	public void deleteUser(final String userName);

	public String getRoleForUser(String userName);

	public String getAllUsers();

	public String getUser(String userName);

	public ENMUser getENMUser(String userName);

	public String setForceUserChangePassword(String userName, boolean value);

	public String changePassword(String userName, String oldPassword,
			String newPassword);

	public String updateUser(String userName, String role);

	public String changeUserProperty(String userName, String property,
			String propertyValue);

    public String showOpenIDMStatus();
}
