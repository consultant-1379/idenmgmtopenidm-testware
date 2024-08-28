package com.ericsson.nms.security.taf.test.cases;

import javax.inject.Inject;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.Output;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.taf.test.helpers.TestCaseBehaviorSetup;
import com.ericsson.nms.security.taf.test.helpers.OperatorParams;
import com.ericsson.nms.security.taf.test.helpers.UserManagementStub;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public class OpenIDM_NoRightsParameterUpdate_Test extends
		TestCaseBehaviorSetup implements TestCase {

	@Inject
	private OperatorRegistry<UserMgmtOperator> userMgmtOperator;

	@BeforeTest
	public void suiteSetup() {
	}

	@BeforeTest
	public void testSetup() {
		logger.info("###### OpenIDM_NoRightsParameterUpdate_Test::editUserProperties ######");
	}

	@AfterTest
	public void testTearDown() {
		logger.info("###### END TEST OpenIDM_NoRightsParameterUpdate_Test::editUserProperties ######");
	}

	@AfterTest
	public void suiteTearDown() {

	}

	private final String unauthorizedError = "{\"error\":401,\"reason\":\"Unauthorized\",\"message\":\"Invalid user name or credentials\"}";
	private final String testPassword = "ShouldN0tPass";
	private final String newPassword = "Baj0Jajo";
	private final String userJohnOutput = "{\"_id\":\"John\",\"_rev\":\"6\"}";

	@TestId(id = "update_user_data_negative", title = "Verify non-admin user has no rights to update his profile")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@DataDriven(name = "OpenIDM_NoRightsParameterUpdate")
	public void editUserProperties(@Input("userName") String userName,
			@Input("firstName") String firstName,
			@Input("lastName") String lastName, @Input("email") String email,
			@Input("userType") String userType,
			@Input("enabled") Boolean enabled,
			@Output("expected") String expectedOutput) {
		logger.debug("Login administrator:");
		UserMgmtOperator userMgmtOperator = getUserMgmtOperator();
		userMgmtOperator.connect();

		logger.debug("Check if user " + userName + " exist.");
		if (!UserManagementStub.doesUserExist(userName, userMgmtOperator)) {
			logger.debug("Creating user " + userName);
			String user = userMgmtOperator.createUser(UserManagementStub
					.createDummyUser(userName));
			logger.debug("createUser output: " + user);
		}
		userMgmtOperator.setForceUserChangePassword(userName, false);
		userMgmtOperator.disconnect();

		logger.debug("Login as a user and trying to update account information::");
		userMgmtOperator.connect(userName, OperatorParams.DUMMY_USER_PASSWORD,
				UserType.OPER);
		assertValueChangeUserProperty(userMgmtOperator, expectedOutput,
				userName, "userName", "test");
		assertValueChangeUserProperty(userMgmtOperator, expectedOutput,
				userName, "firstName", "test");
		assertValueChangeUserProperty(userMgmtOperator, expectedOutput,
				userName, "lastName", "test");
		assertEquals(userJohnOutput, userMgmtOperator.changePassword(userName,
				OperatorParams.DUMMY_USER_PASSWORD, newPassword));
		userMgmtOperator.disconnect();

		userMgmtOperator.connect(userName, newPassword, UserType.OPER);
		assertValueChangeUserProperty(userMgmtOperator, expectedOutput,
				userName, "email", "em@il.com");
		assertValueChangeUserProperty(userMgmtOperator, expectedOutput,
				userName, "lastName", "Disabled");
		userMgmtOperator.disconnect();

		logger.debug(unauthorizedError
				+ " = "
				+ userMgmtOperator.changePassword(userName,
						OperatorParams.DUMMY_USER_PASSWORD, testPassword));
		assertEquals(unauthorizedError, userMgmtOperator.changePassword(
				userName, OperatorParams.DUMMY_USER_PASSWORD, testPassword));

		logger.debug("OpenIDM_NoRightsParameterUpdate_Test::Cleaning up");
		userMgmtOperator.connect();
		userMgmtOperator.deleteUser(userName);
		userMgmtOperator.disconnect();
	}

	private UserMgmtOperator getUserMgmtOperator() {
		return userMgmtOperator.provide(UserMgmtOperator.class);
	}

	private void assertValueChangeUserProperty(
			UserMgmtOperator userMgmtOperator, String expectedOutput,
			String userName, String parameter, String value) {
		logger.info(userMgmtOperator.changeUserProperty(userName, parameter,
				value));
		assertEquals(expectedOutput,
				userMgmtOperator.changeUserProperty(userName, parameter, value));
	}

}
