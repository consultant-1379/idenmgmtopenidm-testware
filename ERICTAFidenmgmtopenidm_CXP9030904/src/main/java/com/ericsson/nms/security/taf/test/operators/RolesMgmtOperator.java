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

public interface RolesMgmtOperator {

	void connect();

	void connect(String userName, String userPassword, UserType userType);

	public void disconnect();

	String getAllRoles();

	public String queryRole(String roleName);

	public String updateRoleProperty(String roleName, String propertyName,
			String newPropertyValue);

	public String deleteRole(String roleName);
}
