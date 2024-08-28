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
import com.ericsson.nms.security.ENMUser;
import com.ericsson.nms.security.taf.test.helpers.TestCaseBehaviorSetup;
import com.ericsson.nms.security.taf.test.helpers.UserManagementStub;
import com.ericsson.nms.security.taf.test.operators.RolesMgmtOperator;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public class OpenIDM_ChangeRole_Test extends TestCaseBehaviorSetup implements
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
		logger.info("###### OpenIDM_Roles_Test::changeRoleAssignmentToUser ######");
	}

	@AfterTest
	public void testTearDown() {
		logger.info("###### END TEST OpenIDM_Roles_Test::changeRoleAssignmentToUser ######");
	}

	@AfterSuite
	public void suiteTearDown() {

	}

	@TestId(id = "change_role_assignment_func", title = "Change role assignment")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@DataDriven(name = "OpenIDM_Change_RoleAssignment")
	public void changeRoleAssignment(@Input("userName") String userName,
			@Input("role") String role, @Input("newRole") String newRole) {

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

			logger.debug("Assert that role has changed:");
			assertRoleChanged(userName, usersForRoleOld, usersForRoleNew);

			logger.debug("Method: assignUserToRole(" + role + ", " + userName
					+ ")");
			usersForRoleOld = rolesMgmtOperator.queryRole(role);
			userMgmtOperator.assignUserToRole(newRole, userName);
			usersForRoleNew = rolesMgmtOperator.queryRole(newRole);

			logger.debug("Assert that role has changed:");
			assertTrue("Assuring that role has been added correctly",
					usersForRoleNew.contains(userName));

			logger.debug("Method: unassignUserToRole(" + newRole + ", "
					+ userName + ")");
			logger.debug(userMgmtOperator.updateUser(userName, newRole));
			usersForRoleNew = rolesMgmtOperator.queryRole(newRole);
			assertFalse("Assuring that role has been unassigned",
					usersForRoleNew.contains(userName));

			userMgmtOperator.disconnect();
			rolesMgmtOperator.disconnect();

		} finally {
			logger.debug("Tear down - deleting created user");
			cleanup(userMgmtOperator, userName);
		}
	}

	private void createTestUserWhenTestIsRunOutsideScenario(String userName,
			UserMgmtOperator userMgmtOperator) {
		logger.debug("Method: getDummyUser(" + userName + ")");
		ENMUser user = UserManagementStub.createDummyUser(userName);

		logger.debug("Method: createUser(" + userName + ")");
		userMgmtOperator.createUser(user);
	}

	private void assertRoleChanged(String userName, String rolesOld,
			String rolesNew) {
		assertTrue("Assuring that role has been added correctly",
				rolesNew.contains(userName));
		assertFalse("Assuring that role has not been assigned previously",
				rolesOld.contains(userName));
	}

	private UserMgmtOperator getUserMgmtOperator() {
		return userMgmtOperator.provide(UserMgmtOperator.class);
	}

	private RolesMgmtOperator getRolesMgmtOperator() {
		return rolesMgmtOperator.provide(RolesMgmtOperator.class);
	}
}
