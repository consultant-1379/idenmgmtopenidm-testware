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
package com.ericsson.nms.security.taf.test.teststeps;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.ENMUser;
import com.ericsson.nms.security.taf.test.helpers.TestCaseBehaviorSetup;
import com.ericsson.nms.security.taf.test.helpers.UserManagementStub;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public class OpenIDM_PasswordChange_TestSteps extends TestCaseBehaviorSetup {

	@Inject
	private OperatorRegistry<UserMgmtOperator> openIDMLogin;

	private static Logger logger = Logger
			.getLogger(OpenIDM_PasswordChange_TestSteps.class);

	@TestStep(id = "forceUserPasswordChange")
	public void forceUserPasswordChange(@Input("userName") String userName,
			@Input("password") String password) {

		UserMgmtOperator operator = getOpenIDMLoginOperator();
		ENMUser user = getDummyUser(userName);

		try {
			createTestUserWhenTestIsRunOutsideScenario(operator, user);

			String userPassword = getUserPasswordDependingOnTheRunType(
					password, user);

			logger.debug("Verify user can login");
			assertTrue("User login failed before forcing password change",
					operator.checkIfCanLogin(user.getUsername(), userPassword,
							UserType.WEB));

			logger.debug("Force change password by user");
			setForcePasswordFlag(userName, operator, true);

			logger.debug("Verify user cannot login with old password");
			assertFalse("User could login after forcing password change",
					operator.checkIfCanLogin(user.getUsername(), userPassword,
							UserType.WEB));
			
			logger.debug("Reset force password change flag for user");
			setForcePasswordFlag(userName, operator, false);
		} finally {
			cleanup(operator, userName);
		}
	}

	@TestStep(id = "userChangePassword")
	public void userChangePassword(@Input("userName") String userName,
			@Input("password") String password,
			@Input("newPassword") String newPassword) {

		UserMgmtOperator operator = getOpenIDMLoginOperator();
		ENMUser user = getDummyUser(userName);

		try {
			createTestUserWhenTestIsRunOutsideScenario(operator, user);

			String userPassword = getUserPasswordDependingOnTheRunType(
					password, user);

			logger.debug("User: " + userName + " changes password");
			operator.changePassword(user.getUsername(), userPassword,
					newPassword);

			logger.debug("Verify user cannot login with old password");
			assertFalse("User could login with old password",
					operator.checkIfCanLogin(user.getUsername(), userPassword,
							UserType.WEB));

			logger.debug("Verify user can login with new password");
			assertTrue("User could not login with new password",
					operator.checkIfCanLogin(user.getUsername(), newPassword,
							UserType.WEB));

		} finally {
			cleanup(operator, userName);
		}
	}

	private void createTestUserWhenTestIsRunOutsideScenario(
			UserMgmtOperator operator, ENMUser user) {

		if (!isThisScenarioRun) {
			logger.debug("Login Admin");
			operator.connect();

			logger.debug("Create user " + user.getUsername());
			operator.createUser(user);
			operator.setForceUserChangePassword(user.getUsername(), false);
			operator.disconnect();
		}
	}

	private void setForcePasswordFlag(String userName,
			UserMgmtOperator operator, boolean isForced) {
		operator.connect();
		operator.setForceUserChangePassword(userName, isForced);
		operator.disconnect();
	}

	private String getUserPasswordDependingOnTheRunType(String password,
			ENMUser user) {
		if (!isThisScenarioRun) {
			return user.getPassword();
		} else {
			return password;
		}
	}

	private UserMgmtOperator getOpenIDMLoginOperator() {
		return openIDMLogin.provide(UserMgmtOperator.class);
	}

	private ENMUser getDummyUser(String userName) {
		return UserManagementStub.createDummyUser(userName);
	}
}
