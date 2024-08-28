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
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.ENMUser;
import com.ericsson.nms.security.taf.test.helpers.TestCaseBehaviorSetup;
import com.ericsson.nms.security.taf.test.helpers.UserManagementStub;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public class OpenIDM_CreateUser_Test extends TestCaseBehaviorSetup implements
		TestCase {

	private final String errorNotFound = "\"error\":404,\"reason\":\"Not Found\"";

	@Inject
	private OperatorRegistry<UserMgmtOperator> userMgmtOperator;

	@BeforeSuite
	public void suiteSetUp() {

	}

	@BeforeTest
	public void testSetup() {
		logger.info("###### OpenIDM_CreateUser::createUser ######");
		announceScenario();
	}

	@AfterTest
	public void testTearDown() {
		logger.info("###### END TEST OpenIDM_CreateUser::createUser ######");
	}

	@AfterSuite
	public void suiteTearDown() {

	}

	@TestId(id = "create_user_func", title = "Verify create a new user")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@TestStep(id = "createUser")
	@DataDriven(name = "OpenIDM_Create_New_User")
	public void createUser(@Input("userName") String userName,
			@Input("firstName") String firstName,
			@Input("lastName") String lastName, @Input("email") String email,
			@Input("password") String password,
			@Input("userType") String userType,
			@Input("enabled") Boolean enabled,
			@Input("expected") String expectedOutput, @Input("role") String role) {

		UserMgmtOperator operator = getUserMgmtOperator();
		try {
			logger.debug("Login Admin");
			operator.connect();

			logger.debug("Check if user " + userName
					+ " exist, if so - delete him");
			if (doesUserExist(userName)) {
				logger.warn("User " + userName
						+ " exist but should not. This user will be deleted");
				assertDeleteUser(userName, operator);
			}

			ENMUser enmUser = UserManagementStub.createDummyUser(userName);

			logger.debug("Method: createUser(" + userName + ")");
			String result = operator.createUser(enmUser);
			assertEquals(
					"Return result is empty or is not match with expected output",
					result, expectedOutput);
		} finally {

			if (!isThisScenarioRun) {
				logger.debug("Tear down - deleting created user");
				assertDeleteUser(userName, operator);
			} else {
				logger.debug("This test case is part of scenario. User is not being deleted during test case tear down.");
				logger.debug("Setting force password flag to false for user: "
						+ userName);
				operator.setForceUserChangePassword(userName, false);
			}
			operator.disconnect();
		}

	}

	private void assertDeleteUser(String userName,
			UserMgmtOperator userMgmtOperator) {
		userMgmtOperator.deleteUser(userName);
		assertTrue("User " + userName + " can not be deleted", userMgmtOperator
				.getUser(userName).contains(errorNotFound));
	}

	private UserMgmtOperator getUserMgmtOperator() {
		return userMgmtOperator.provide(UserMgmtOperator.class);
	}

	public String listAllUsers() {
		UserMgmtOperator userMgmtOperator = getUserMgmtOperator();
		userMgmtOperator.connect();

		return userMgmtOperator.getAllUsers();
	}

	private boolean doesUserExist(String userName) {
		// TODO improve this method - because can be user which login can
		// contains other login
		return listAllUsers().contains(userName);
	}
}
