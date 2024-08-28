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

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.taf.test.helpers.TestCaseBehaviorSetup;
import com.ericsson.nms.security.taf.test.helpers.UserManagementStub;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public class OpenIDM_QueryUser_Test extends TestCaseBehaviorSetup implements
		TestCase {

	@Inject
	private OperatorRegistry<UserMgmtOperator> userMgmtOperator;

	@BeforeTest
	public void suiteSetup() {
		logger.info("###### OpenIDM_Query_User::queryUser ######");
	}

	@AfterTest
	public void suiteTearDown() {
		logger.info("###### OpenIDM_Query_User::queryUser ######");
	}

	@TestId(id = "query_user_func", title = "Verify query a user")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@TestStep(id = "queryUser")
	@DataDriven(name = "OpenIDM_QueryUser")
	public void queryUser(@Input("userName") String userName,
			@Input("shouldUserBeCreated") boolean shouldUserBeCreated,
			@Input("expectedOutput") String expectedOutput) {

		logger.debug("Login Admin");
		UserMgmtOperator userMgmtOperator = getUserMgmtOperator();
		userMgmtOperator.connect();

		logger.debug("Check if user "
				+ userName
				+ " should be created. \nUser "
				+ userName
				+ (shouldUserBeCreated ? " should be created."
						: " should not be created."));

		if (shouldUserBeCreated) {
			logger.debug("Check if user " + userName + " exist.");
			assertFalse(UserManagementStub.doesUserExist(userName,
					userMgmtOperator));
			logger.debug("Creating user " + userName);
			String user = userMgmtOperator.createUser(UserManagementStub
					.createDummyUser(userName));
			logger.debug("createUser output: " + user);
		} else {
			logger.debug("User "
					+ userName
					+ (UserManagementStub.doesUserExist(userName,
							userMgmtOperator) ? " exist." : " do not exist."));
		}

		logger.info("Query user " + userName);
		String result = UserManagementStub
				.queryUser(userName, userMgmtOperator);
		logger.debug("result: " + result + "\nexpectedOutput: "
				+ expectedOutput);
		assertTrue(result.contains(expectedOutput));

		if (shouldUserBeCreated && !isThisScenarioRun) {
			logger.debug("Deleting user " + userName);
			userMgmtOperator.deleteUser(userName);
			logger.debug("Check if user " + userName + " exist after deleting.");
			assertFalse(UserManagementStub.doesUserExist(userName,
					userMgmtOperator));
		}
		userMgmtOperator.disconnect();
	}

	private UserMgmtOperator getUserMgmtOperator() {
		return userMgmtOperator.provide(UserMgmtOperator.class);
	}
}
