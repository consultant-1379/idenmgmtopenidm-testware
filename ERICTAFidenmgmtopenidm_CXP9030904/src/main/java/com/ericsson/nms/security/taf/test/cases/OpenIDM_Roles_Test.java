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
import com.ericsson.nms.security.taf.test.operators.RolesMgmtOperator;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public class OpenIDM_Roles_Test extends TestCaseBehaviorSetup implements
		TestCase {

	@Inject
	private OperatorRegistry<UserMgmtOperator> userMgmtOperator;

	@Inject
	private OperatorRegistry<RolesMgmtOperator> rolesMgmtOperator;

	@BeforeSuite
	public void suiteSetUp() {

	}

	@BeforeTest
	public void testSetup() {
		logger.info("###### OpenIDM_Roles_Test::assignRoleToUser ######");
	}

	@AfterTest
	public void testTearDown() {
		logger.info("###### END TEST OpenIDM_Roles_Test::assignRoleToUser ######");
	}

	@AfterSuite
	public void suiteTearDown() {

	}

	@TestId(id = "assign_role_func", title = "Assign Role")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@TestStep(id = "assignRole")
	@DataDriven(name = "OpenIDM_Change_RoleAssignment")
	public void assignRoleToUser(@Input("userName") String userName,
			@Input("role") String role) {

		UserMgmtOperator userMgmtOperator = getUserMgmtOperator();
		RolesMgmtOperator rolesMgmtOperator = getRolesMgmtOperator();

		try {
			logger.debug("Login Admin");
			userMgmtOperator.connect();
			rolesMgmtOperator.connect();

			createTestUserWhenTestIsRunOutsideScenario(userName,
					userMgmtOperator);

			logger.debug("Method: assignUserToRole(" + role + ", " + userName
					+ ")");
			String usersForRoleOld = rolesMgmtOperator.queryRole(role);
			userMgmtOperator.assignUserToRole(role, userName);
			String usersForRoleNew = rolesMgmtOperator.queryRole(role);
			assertRoleChanged(userName, usersForRoleOld, usersForRoleNew);

			userMgmtOperator.disconnect();
			rolesMgmtOperator.disconnect();
		} finally {
			cleanup(userMgmtOperator, userName);
		}
	}

	private void createTestUserWhenTestIsRunOutsideScenario(String userName,
			UserMgmtOperator userMgmtOperator) {
		if (!isThisScenarioRun) {
			logger.debug("Method: getDummyUser(" + userName + ")");
			ENMUser user = UserManagementStub.createDummyUser(userName);

			logger.debug("Method: createUser(" + userName + ")");
			userMgmtOperator.createUser(user);
		}
	}

	private void assertRoleChanged(String userName, String rolesOld,
			String rolesNew) {
		assertTrue("Assuring that role has been added correctly",
				rolesNew.contains(userName));
		assertFalse("Assuring that role has not been assignet previously",
				rolesOld.contains(userName));
	}

	private UserMgmtOperator getUserMgmtOperator() {
		return userMgmtOperator.provide(UserMgmtOperator.class);
	}

	private RolesMgmtOperator getRolesMgmtOperator() {
		return rolesMgmtOperator.provide(RolesMgmtOperator.class);
	}

}
