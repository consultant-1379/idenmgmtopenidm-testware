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

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.nms.security.ENMUser;
import com.ericsson.nms.security.taf.test.helpers.OperatorParams;
import com.ericsson.nms.security.taf.test.helpers.PasswordDecoder;
import com.ericsson.nms.security.taf.test.utils.JSONtoUserMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Operator(context = Context.REST)
public class UserMgmtOperatorImpl extends RestExecutor implements
		UserMgmtOperator {

	@Override
	public String assignUserToRole(String role, String userName) {
		logger.debug("Trying assign " + role + " to " + userName);

		final String uri = String.format(OperatorParams.ADD_USER_TO_ROLE,
				"adduser", role);
		final HttpResponse resp = executeGetRestCall(uri, userName);

		logger.debug("Result : " + resp.getBody());

		return resp.getBody();
	}

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
	public boolean checkIfCanLogin(String userName, String userPassword,
			UserType userType) {
		connect(userName, userPassword, userType);

		boolean loggedIn = launcherOperator.checkIfLoggedIn(httpTool);
		if (loggedIn) {
			disconnect();
		}

		return loggedIn;
	}

	@Override
	public void connect(String userName, String userPassword, UserType userType) {
		User user = new User(userName, userPassword, userType);
		httpTool = launcherOperator.login(user);
	}

	@Override
	public String createUser(ENMUser user) {

		final String status = (user.getEnabled()) ? "enabled" : "disabled";
		final String uri = String.format(OperatorParams.USER_URI,
				user.getUsername());
		final String command = String.format(OperatorParams.CREATE_USER,
				user.getUsername(), user.getFirstName(), user.getLastName(),
				user.getEmail(), user.getPassword(), status);

		logger.info("CreateUser URI: " + httpTool.getBaseUri() + uri);

		final HttpResponse response = executePutRestCall(uri, command);

		return response.getBody();
	}

	@Override
	public void deleteUser(String userName) {
		final String uri = String.format(OperatorParams.USER_URI, userName);
		executeDeleteRestCall(uri).getBody();
	}

	public void disconnect() {
		httpTool = launcherOperator.logout();
	}

	@Override
	public String getAllUsers() {
		final HttpResponse response = executeGetRestCall(OperatorParams.LIST_ALL_USERS);
		return response.getBody();
	}

	@Override
	public String getRoleForUser(String userName) {
		final String uri = String.format(OperatorParams.LIST_ALL_ROLES);
		final HttpResponse response = executeGetRestCall(uri, userName);
		return response.getBody();
	}

	@Override
	public String getUser(String userName) {
		final HttpResponse response = executeGetRestCall(String.format(
				OperatorParams.USER_URI, userName));
		return response.getBody();
	}

	@Override
	public ENMUser getENMUser(String userName) {
		String stringifiedUser = getUser(userName);
		ENMUser user = JSONtoUserMapper.mapStringToUser(stringifiedUser);

		return user;
	}

	@Override
	public String unassignUserFromRole(String role, String userName) {
		logger.debug("Trying unassign " + role + " from " + userName);
		final String uri = String.format(OperatorParams.ADD_USER_TO_ROLE,
				"deleteuser", role);
		final HttpResponse response = executeGetRestCall(uri, userName);
		logger.debug("Result: " + response.getBody());

		return response.getBody();

	}

	@Override
	public String setForceUserChangePassword(String userName, boolean value) {
		final HttpResponse response = executePostRestCall(
				String.format(OperatorParams.PASSWORD_RESET_URI, value),
				String.format(OperatorParams.PASSWORD_RESET, userName));
		return response.getBody();
	}

	@Override
	public String changePassword(String userName, String oldPassword,
			String newPassword) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(OperatorParams.USERNAME_HEADER, userName);
		headers.put(OperatorParams.REAUTH_PASSWORD_HEADER, oldPassword);
		headers.put(OperatorParams.NEW_PASSWORD_HEADER, newPassword);
		final HttpResponse response = executePostRestCall(
				OperatorParams.MANAGE_PASSWORD_URI, "", headers);
		return response.getBody();
	}

	@Override
	public String updateUser(String userName, String role) {
		final String uri = String.format(OperatorParams.UPDATE_ROLE_URI, role);
		final HttpResponse response = executeGetRestCall(uri, userName);
		return response.getBody();
	}

	@Override
	public String changeUserProperty(String userName, String property,
			String propertyValue) {
		final String uri = String.format(OperatorParams.CHANGE_PROPERTY_URI,
				userName);
		final String command = String.format(OperatorParams.CHANGE_USER_FIELD,
				property, propertyValue);
		
		logger.info("Change " + property + " URI: " + httpTool.getBaseUri()
				+ uri + command);

		final HttpResponse response = executePostRestCall(uri, command);

		return response.getBody();
	}

    @Override
    public String showOpenIDMStatus() {
        final HttpResponse response = executeGetRestCall(String.format(
                OperatorParams.SHOW_OPENIDM_STATUS));
        return response.getBody();
    }
}
