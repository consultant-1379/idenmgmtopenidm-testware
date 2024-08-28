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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.*;
import com.ericsson.cifwk.taf.tools.http.*;
import com.ericsson.cifwk.taf.tools.http.constants.ContentType;
import com.ericsson.nms.launcher.LauncherOperator;
import com.ericsson.nms.security.ENMUser;
import com.ericsson.nms.security.taf.test.cases.OpenIDM_Functional_Test;

@Operator(context = Context.REST)
@Singleton
public class OpenIDM_LoginImpl implements OpenIDM_Login {

	private static Logger logger = Logger
			.getLogger(OpenIDM_Functional_Test.class);

	// TODO move this to global properies
	private static final String USER_URI = "/openidm/managed/user/%s";
	private static final String CREATE_USER_COMMAND = "{\"userName\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\","
			+ "\"email\":\"%s\",\"password\":\"%s\",\"userType\":\"enmUser\",\"status\":\"%s\"}";
	final String LIST_ALL_USER = "/openidm/managed/user/?_queryId=query-all-ids";

	// TODO - MOVE TO THE GLOBAL CONSTANT
	private static final int TIMEOUT = 10;
	private HttpTool httpTool;

	@Inject
	private LauncherOperator launcherOperator;

	// TODO move constant variables to Global properties class
	@Override
	public void connect() {
		connect("administrator", "TestPassw0rd", UserType.ADMIN);
	}

	@Override
	public void connect(String userName, String userPassword, UserType userType) {
		httpTool = launcherOperator.login(new User(userName, userPassword,
				userType));
	}

	@Override
	public String createUser(ENMUser user) {
		final String status = (user.getEnabled()) ? "enabled" : "disabled";
		final String uri = String.format(USER_URI, user.getUsername());
		final String command = String.format(CREATE_USER_COMMAND,
				user.getUsername(), user.getFirstName(), user.getLastName(),
				user.getEmail(), user.getPassword(), status);
		logger.info("CreateUser URI: " + httpTool.getBaseUri() + uri + command);

		final HttpResponse response = executePutRestCall(uri, command);

		return response.getBody();
	}

	@Override
	public void assignUserToRole(String role, String username) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteUser(String userName) {
		final String uri = String.format(USER_URI, userName);
		executeDeleteRestCall(uri);
	}

	@Override
	public String getAllUsers() {
		final HttpResponse response = executeGetRestCall(LIST_ALL_USER);
		return response.getBody();
	}

	@Override
	public String getUser(String userName) {
		final HttpResponse response = executeGetRestCall(String.format(
				USER_URI, userName));
		return response.getBody();
	}

	private HttpResponse executeGetRestCall(String uri) {
		final HttpResponse response = httpTool.request()
				.header("Accept", "application/json")
				.header("X-Requested-With", "XMLHttpRequest").timeout(TIMEOUT)
				.get(uri);

		return response;
	}

	/**
	 * @param uri
	 * @return
	 */
	private HttpResponse executeDeleteRestCall(String uri) {

		logger.info("DeleteRestCall URI : " + httpTool.getBaseUri() + uri);

		final HttpResponse response = httpTool.request()
				.contentType(ContentType.APPLICATION_JSON)
				.header("Accept", "application/json")
				.header("X-Requested-With", "XMLHttpRequest")
				.header("If-Match", "\"*\"").timeout(TIMEOUT).delete(uri);

		return response;
	}

	private HttpResponse executePutRestCall(final String uri,
			final String jsonString) {

		final RequestBuilder request = httpTool.request();

		// TODO ADD TIMEOUT
		final HttpResponse response = request
				.contentType(ContentType.APPLICATION_JSON)
				.header("Accept", "application/json")
				.header("X-Requested-With", "XMLHttpRequest").body(jsonString)
				.timeout(TIMEOUT).put(uri);

		return response;
	}
}
