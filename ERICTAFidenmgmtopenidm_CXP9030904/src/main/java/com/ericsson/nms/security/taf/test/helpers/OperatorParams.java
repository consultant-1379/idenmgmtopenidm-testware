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
package com.ericsson.nms.security.taf.test.helpers;

public class OperatorParams {

	// Management endpoins
	public static final String USER_URI = "/openidm/managed/user/%s";
	public static final String ROLE_URI = "/openidm/managed/role/%s";
	public static final String PASSWORD_RESET_URI = "/openidm/endpoint/passwordReset?state=%b";
	public static final String MANAGE_PASSWORD_URI = "/openidm/endpoint/managePassword";
	public static final String CHANGE_PROPERTY_URI = "/openidm/managed/user?_action=patch&_queryId=for-userName&uid=%s";
	public static final String UPDATE_ROLE_URI = "/openidm/endpoint/manageRole?action=deleteuser&rName=%s";

	// Management commands
	public static final String CREATE_USER = "{\"userName\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\","
			+ "\"email\":\"%s\",\"password\":\"%s\",\"userType\":\"enmUser\",\"status\":\"%s\"}";
	public static final String ADD_USER_TO_ROLE = "/openidm/endpoint/manageRole?action=%s&rName=%s";
	public static final String LIST_ALL_USERS = "/openidm/managed/user/?_queryId=query-all-ids";
	public static final String LIST_ALL_ROLES = "/openidm/managed/role/?_queryId=query-all";
	public static final String PASSWORD_RESET = "[ \"%s\" ]";
	public static final String CHANGE_USER_FIELD = "[{\"replace\":\"%s\",\"value\":\"%s\"}]";
    public static final String SHOW_OPENIDM_STATUS = "/openidm/info/ping";

	public static final String UPDATE_ROLE = "{\"replace\":\"%s\", \"value\": \"%s\"}";

	// Headers
	public static final String NEW_PASSWORD_HEADER = "X-OpenIDM-New-Password";
	public static final String REAUTH_PASSWORD_HEADER = "X-OpenIDM-Reauth-Password";
	public static final String USERNAME_HEADER = "X-OpenIDM-Username";

	// Timeout settings
	public static final int TIMEOUT = 10;

	// Dummy User Password
	public static final String DUMMY_USER_PASSWORD = "T3stP4ssw0rd";
}
