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
package com.ericsson.nms.security.taf.test.cases;

import javax.inject.Inject;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.ENMUser;
import com.ericsson.nms.security.taf.test.helpers.TestCaseBehaviorSetup;
import com.ericsson.nms.security.taf.test.helpers.UserManagementStub;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public class OpenIDM_ChangeEmail_Test extends TestCaseBehaviorSetup implements
		TestCase {

	@Inject
	private OperatorRegistry<UserMgmtOperator> UserMgmtOperator;

	private String emailPropertyName = "email";

	@BeforeSuite
	public void suiteSetUp() {

	}

	@BeforeTest
	public void testSetup() {
		logger.info("###### OpenIDM_Change_Email::changeEmail ######");
		announceScenario();
	}

	@AfterTest
	public void testTearDown() {
		logger.info("###### END TEST OpenIDM_Create_New_User::changeEmail ######");
	}

	@AfterSuite
	public void suiteTearDown() {

	}

	@TestId(id = "change_email_func", title = "Change email")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@TestStep(id = "changeEmailByAdmin")
	@DataDriven(name = "OpenIDM_Change_Email_By_Admin")
	public void changeEmailByAdmin_Test(@Input("userName") String userName,
			@Input("newEmail") String newEmail, @Input("role") String role) {

		UserMgmtOperator operator = getUserMgmtOperator();

		createTestUserWhenTestIsRunOutsideScenario(userName, role, operator);

		operator.connect();
		logger.debug("Method: assertChangeEmail(userMgmtOperator, " + userName
				+ ", " + newEmail + ")");
		assertChangeEmail(operator, userName, newEmail);
		operator.disconnect();

		cleanup(operator, userName);
	}

	@TestId(id = "change_email_func", title = "Change email")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@TestStep(id = "changeEmailbyUser")
	@DataDriven(name = "OpenIDM_Change_Email_By_User")
	public void changeEmailByUser_Test(@Input("userName") String userName,
			@Input("password") String password,
			@Input("newEmail") String newEmail, @Input("role") String role) {

		UserMgmtOperator operator = getUserMgmtOperator();

		createTestUserWhenTestIsRunOutsideScenario(userName, role, operator);

		logger.debug("Login user: " + userName);
		operator.connect(userName, password, UserType.ADMIN);

		logger.debug("Method: assertChangeEmail(userMgmtOperator, " + userName
				+ ", " + newEmail + ")");
		assertChangeEmail(operator, userName, newEmail);
		operator.disconnect();

		cleanup(operator, userName);
	}

	private void createTestUserWhenTestIsRunOutsideScenario(String userName,
			String role, UserMgmtOperator operator) {
		if (!isThisScenarioRun) {
			logger.debug("Login Administrator");
			operator.connect();

			logger.debug("Method: getDummyUser(" + userName + ")");
			ENMUser user = getDummyUser(userName);

			logger.debug("Method: createUser(" + userName + ")");
			operator.createUser(user);

			logger.debug("Method: assignUserToRole(" + role + ", " + userName);
			operator.assignUserToRole(role, userName);

			logger.debug("Method: setForceUserChangePassword(" + userName
					+ ", false)");
			operator.setForceUserChangePassword(userName, false);
			operator.disconnect();
		}
	}

	private void assertChangeEmail(UserMgmtOperator userMgmtOperator,
			String userName, String newEmail) {

		logger.debug("Change Email");
		userMgmtOperator.changeUserProperty(userName, emailPropertyName,
				newEmail);

		logger.debug("Method: getUser(" + userName + ")");
		String result = userMgmtOperator.getUser(userName);

		assertTrue("User " + userName + " contains new email " + newEmail,
				result.contains(newEmail));
	}

	private UserMgmtOperator getUserMgmtOperator() {
		return UserMgmtOperator.provide(UserMgmtOperator.class);
	}

	private ENMUser getDummyUser(String userName) {
		return UserManagementStub.createDummyUser(userName);
	}
}
