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
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public class OpenIDM_DeleteAdmin_Test extends TestCaseBehaviorSetup implements
		TestCase {

	@Inject
	private OperatorRegistry<UserMgmtOperator> userMgmtOperator;

	@BeforeSuite
	public void suiteSetUp() {
	}

	@BeforeTest
	public void testSetup() {
		logger.info("###### OpenIDM_Delete_Admin::deleteAdminUser ######");
	}

	@AfterTest
	public void testTearDown() {
		logger.info("###### END TEST OpenIDM_Delete_Admin::deleteAdminUser ######");
	}

	@AfterSuite
	public void suiteTearDown() {
	}

	@TestId(id = "delete_admin_role_func", title = "Delete Admin")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@DataDriven(name = "OpenIDM_Delete_Admin")
	public void deleteAdminUser(@Input("userName") String userName,
			@Input("appRole") String roleName) {

		UserMgmtOperator userMgmtOperator = getUserMgmtOperator();

		logger.debug("Login Admin:");
		userMgmtOperator.connect();

		logger.debug("Create user and assing him role:");
		ENMUser user = UserManagementStub.createDummyUser(userName);
		userMgmtOperator.createUser(user);
		userMgmtOperator.assignUserToRole(roleName, userName);

		assertTrue(userMgmtOperator.getUser(userName).contains(roleName));
		logger.debug("User exists, and has selected role.");
		userMgmtOperator.deleteUser(userName);

		String errorNotFound = "{\"error\":404,\"reason\":\"Not Found\",\"message\":\"Object managed/user/"
				+ userName + " not found in managed/user\"}";
		assertEquals(userMgmtOperator.getUser(userName), errorNotFound);

		userMgmtOperator.disconnect();
	}

	private UserMgmtOperator getUserMgmtOperator() {
		return userMgmtOperator.provide(UserMgmtOperator.class);
	}
}