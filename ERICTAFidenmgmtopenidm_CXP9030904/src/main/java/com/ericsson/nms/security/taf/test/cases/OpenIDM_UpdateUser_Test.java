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
import com.ericsson.cifwk.taf.annotations.Output;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.taf.test.helpers.TestCaseBehaviorSetup;
import com.ericsson.nms.security.taf.test.helpers.UserManagementStub;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public class OpenIDM_UpdateUser_Test extends TestCaseBehaviorSetup
		implements TestCase {
	
	@Inject
	private OperatorRegistry<UserMgmtOperator> userMgmtOperator;

	@BeforeTest
	public void suiteSetup() {
		logger.info("###### OpenIDM_update_User::updateUser ######");
	}

	@AfterTest
	public void suiteTearDown() {
		logger.info("###### OpenIDM_Update_User::updateUser ######");
	}

	@TestId(id = "update_user_func", title = "Verify update a user")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@DataDriven(name = "OpenIDM_UpdateUser")
	public void updateUser(@Input("userName") String userName,
			@Input("property") String property,
			@Input("propertyValue") String propertyValue,
			@Output("outputBefore") String outputBefore,
			@Output("outputAfter") String outputAfter) {

		logger.debug("Login Admin");
		UserMgmtOperator userMgmtOperator = getUserMgmtOperator();
		userMgmtOperator.connect();

		logger.debug("Check if user " + userName
				+ " exist, if exsit - delete it.");
		if (UserManagementStub.doesUserExist(userName, userMgmtOperator)) {
			logger.debug("User " + userName + " exist, deleting it.");
			userMgmtOperator.deleteUser(userName);
		}

		logger.debug("Create " + userName);
		userMgmtOperator.createUser(UserManagementStub
				.createDummyUser(userName));

		logger.debug("Query user " + userName);
		String user = UserManagementStub.queryUser(userName, userMgmtOperator);
		logger.debug("Checking " + property + " for user " + userName
				+ " before update, expected output: " + outputBefore);
		assertTrue(user.contains(outputBefore));

		logger.info("Updating property " + property + " with value: "
				+ propertyValue + " ,for user " + userName
				+ ", expected output: " + outputAfter);
		UserManagementStub.updateUserProperty(userName, property,
				propertyValue, userMgmtOperator);
		assertTrue(UserManagementStub.queryUser(userName, userMgmtOperator)
				.contains(outputAfter));

		logger.debug("Delete user " + userName);
		userMgmtOperator.deleteUser(userName);
		userMgmtOperator.disconnect();
	}

	private UserMgmtOperator getUserMgmtOperator() {
		return userMgmtOperator.provide(UserMgmtOperator.class);
	}
}
