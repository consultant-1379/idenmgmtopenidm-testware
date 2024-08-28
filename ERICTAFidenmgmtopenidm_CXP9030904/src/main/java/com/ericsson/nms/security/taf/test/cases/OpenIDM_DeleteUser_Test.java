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

public class OpenIDM_DeleteUser_Test extends TestCaseBehaviorSetup implements
		TestCase {

	@Inject
	private OperatorRegistry<UserMgmtOperator> userMgmtOperator;

	@BeforeTest
	public void suiteSetup() {
		logger.info("###### OpenIDM_Delete_User::deleteUser ######");
	}

	@AfterTest
	public void suiteTearDown() {
		logger.info("###### OpenIDM_Delete_User::deleteUser ######");
	}

	@TestId(id = "delete_user_func", title = "Verify delete a user")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@TestStep(id = "deleteUser")
	@DataDriven(name = "OpenIDM_DeleteUser")
	public void deleteUser(@Input("userName") String userName) {

		logger.debug("Login Admin");
		UserMgmtOperator userMgmtOperator = getUserMgmtOperator();
		userMgmtOperator.connect();

		logger.debug("Check if user " + userName
				+ " exist, if not - create it.");
		if (!UserManagementStub.doesUserExist(userName, userMgmtOperator)) {
			logger.debug("Creating user " + userName);
			String user = userMgmtOperator.createUser(UserManagementStub
					.createDummyUser(userName));
			logger.debug("createUser output: " + user);
			logger.debug("Check if user " + userName + " exist.");
			assertTrue(UserManagementStub.doesUserExist(userName,
					userMgmtOperator));
		}

		logger.info("Deleting user " + userName);
		userMgmtOperator.deleteUser(userName);
		assertFalse(UserManagementStub
				.doesUserExist(userName, userMgmtOperator));

		userMgmtOperator.disconnect();
	}

	private UserMgmtOperator getUserMgmtOperator() {
		return userMgmtOperator.provide(UserMgmtOperator.class);
	}
}
