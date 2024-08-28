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

import java.io.IOException;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.nms.security.taf.test.helpers.OperatorParams;
import com.ericsson.nms.security.taf.test.helpers.PasswordDecoder;

@Operator(context = Context.REST)
public class RolesMgmtOperatorImpl extends RestExecutor implements
		RolesMgmtOperator {

	@Override
	public void connect() {
		try {
			connect("administrator", PasswordDecoder.getPassword(),
					UserType.ADMIN);
		} catch (IOException e) {
			logger.debug(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void connect(String userName, String userPassword, UserType userType) {
		User user = new User(userName, userPassword, userType);
		httpTool = launcherOperator.login(user);
	}

	public void disconnect() {
		httpTool = launcherOperator.logout();
	}

	@Override
	public String getAllRoles() {
		final HttpResponse response = executeGetRestCall(OperatorParams.LIST_ALL_ROLES);
		return response.getBody();
	}

	@Override
	public String queryRole(String roleName) {

		final String uri = String.format(OperatorParams.ROLE_URI, roleName);

		logger.debug("QueryRole URI: " + httpTool.getBaseUri() + uri);

		final HttpResponse response = executeGetRestCall(uri);

		return response.getBody();
	}

	@Override
	public String updateRoleProperty(String roleName, String propertyName,
			String newPropertyValue) {

		final String uri = String.format(OperatorParams.ROLE_URI, roleName);
		final String command = String.format(OperatorParams.UPDATE_ROLE,
				propertyName, newPropertyValue);

		logger.debug("UpdateRole URI: " + httpTool.getBaseUri() + uri + command);

		final HttpResponse response = executePostRestCall(uri, command);

		return response.getBody();

	}

	@Override
	public String deleteRole(String roleName) {

		final String uri = String.format(OperatorParams.ROLE_URI, roleName);

		logger.debug("DeleteRole URI: " + httpTool.getBaseUri() + uri);

		final HttpResponse response = executeDeleteRestCall(uri);

		return response.getBody();
	}

}
