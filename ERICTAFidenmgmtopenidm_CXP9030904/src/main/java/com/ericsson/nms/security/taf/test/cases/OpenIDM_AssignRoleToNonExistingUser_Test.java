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
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.taf.test.helpers.TestCaseBehaviorSetup;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public class OpenIDM_AssignRoleToNonExistingUser_Test extends
		TestCaseBehaviorSetup implements TestCase {

	@Inject
	private OperatorRegistry<UserMgmtOperator> userMgmtOperator;

	@BeforeSuite
	public void suiteSetUp() {

	}

	@BeforeTest
	public void testSetup() {
		logger.info("###### OpenIDM_AssignRoleToNonExistingUser::assignRoleToNonExistingUser ######");
	}

	@AfterTest
	public void testTearDown() {
		logger.info("###### END TEST OpenIDM_AssignRoleToNonExistingUse::assignRoleToNonExistingUser ######");
	}

	@AfterSuite
	public void suiteTearDown() {

	}

	@TestId(id = "assign_role_negative", title = "Assign Role to non-existing user")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@DataDriven(name = "OpenIDM_AssignRoleToNonExistingUser")
	public void assignRoleToNonExistingUser(@Input("userName") String userName,
			@Input("appRole") String roleName) {

		UserMgmtOperator userMgmtOperator = getUserMgmtOperator();
		userMgmtOperator.connect();

		assertFalse(userMgmtOperator.getAllUsers().contains(userName));
		logger.debug("User does not exists. This is ok!");

		String errorActionForbidden = "{\"error\":403,\"reason\":\"Forbidden\",\"message\":\"User "
				+ userName + " does not exist\"}";
		assertEquals(userMgmtOperator.assignUserToRole(roleName, userName),
				errorActionForbidden);
		logger.debug("User does not exists, and assigning role has failed as expected.");
	}

	private UserMgmtOperator getUserMgmtOperator() {
		return userMgmtOperator.provide(UserMgmtOperator.class);
	}
}
