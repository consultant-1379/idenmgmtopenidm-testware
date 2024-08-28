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

public interface OpenIDM_Login {
	
	public void connect();
	public void connect(String userName, String userPassword, UserType userType );
	public String createUser(final ENMUser user);
	public void assignUserToRole(final String role, final String username);
	public void deleteUser(final String userName);
	public String getAllUsers();
	public String getUser(String userName);
}
