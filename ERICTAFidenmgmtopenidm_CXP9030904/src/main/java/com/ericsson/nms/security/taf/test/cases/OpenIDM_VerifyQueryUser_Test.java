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
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.ENMUser;
import com.ericsson.nms.security.taf.test.helpers.TestCaseBehaviorSetup;
import com.ericsson.nms.security.taf.test.helpers.OperatorParams;
import com.ericsson.nms.security.taf.test.helpers.UserManagementStub;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public class OpenIDM_VerifyQueryUser_Test extends TestCaseBehaviorSetup
		implements TestCase {

	@Inject
	private OperatorRegistry<UserMgmtOperator> UserMgmtOperator;

	private String result;
	private String errorAccessDenied = "Access denied";

	@BeforeTest
	public void suiteSetup() {
		logger.info("###### OpenIDM_VerifyQueryUser_Test::verifyQueryUser_Test ######");
	}

	@AfterTest
	public void suiteTearDown() {
		logger.info("###### END TEST OpenIDM_VerifyQueryUser_Test::verifyQueryUser_Test ######");
	}

	@TestId(id = "query_user_func", title = "Verify query user")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@DataDriven(name = "OpenIDM_Verify_Query_User")
	public void verifyQueryUser_Test(@Input("userName") String userName,
			@Input("userNameAdmin") String userNameAdmin,
			@Input("roleAdmin") String roleAdmin,
			@Input("roleSecAdmin") String roleSecAdmin,
			@Input("flagAdmin") boolean flagAdmin,
			@Input("flagSecAdmin") boolean flagSecAdmin) {
		UserMgmtOperator userMgmtOperator = getUserMgmtOperator();

		logger.info("Querying user " + userName
				+ " using administrator role using administrator with"
				+ roleAdmin + " role. Assert should Fail.");
		queryUser(userMgmtOperator, userName, userNameAdmin, roleAdmin,
				flagAdmin);

		logger.info("Querying user " + userName
				+ " using administrator role using administrator with"
				+ roleAdmin + " role. Assert should Pass.");
		queryUser(userMgmtOperator, userName, userNameAdmin, roleSecAdmin,
				flagSecAdmin);
	}

	private void queryUser(UserMgmtOperator userMgmtOperator, String userName,
			String userNameAdmin, String role, boolean flag) {
		logger.debug("Method: queryUser(" + userMgmtOperator + ", " + userName
				+ ", " + userNameAdmin + ", " + role + ", " + flag + ")");

		logger.debug("Login Administrator");
		userMgmtOperator.connect();

		logger.debug("Method: getDummyUser(" + userName + ")");
		ENMUser user = getDummyUser(userName);

		logger.debug("Method: createUser(" + userName + ")");
		userMgmtOperator.createUser(user);

		logger.debug("Method: getDummyUser(" + userNameAdmin + ")");
		ENMUser adminUser = getDummyUser(userNameAdmin);

		logger.debug("Method: createUser(" + userNameAdmin + ")");
		userMgmtOperator.createUser(adminUser);

		logger.debug("Method: setForceUserChangePassword(" + userNameAdmin
				+ ", false)");
		userMgmtOperator.setForceUserChangePassword(userNameAdmin, false);

		logger.debug("Method: assignUserToRole(" + role + ", " + userNameAdmin
				+ ")");
		userMgmtOperator.assignUserToRole(role, userNameAdmin);

		logger.debug("Connect " + userNameAdmin);
		userMgmtOperator.disconnect();
		userMgmtOperator.connect(userNameAdmin,
				OperatorParams.DUMMY_USER_PASSWORD, UserType.ADMIN);

		logger.debug("Method: getUser(" + userName + ")");
		result = userMgmtOperator.getUser(userName);

		assertEquals("Fail occurred getting user " + userName,
				result.contains(errorAccessDenied), flag);

		logger.debug("Tear down - deleting created user");
		userMgmtOperator.disconnect();
		userMgmtOperator.connect();
		userMgmtOperator.deleteUser(userName);
		userMgmtOperator.deleteUser(userNameAdmin);
		userMgmtOperator.disconnect();
	}

	private UserMgmtOperator getUserMgmtOperator() {
		return UserMgmtOperator.provide(UserMgmtOperator.class);
	}

	private ENMUser getDummyUser(String userName) {
		return UserManagementStub.createDummyUser(userName);
	}
}