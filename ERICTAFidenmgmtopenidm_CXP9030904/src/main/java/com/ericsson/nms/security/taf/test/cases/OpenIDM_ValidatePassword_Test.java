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

import java.util.ArrayList;

import javax.inject.Inject;

import org.testng.annotations.AfterTest;
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
import com.ericsson.nms.security.taf.test.helpers.OperatorParams;
import com.ericsson.nms.security.taf.test.helpers.UserManagementStub;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public class OpenIDM_ValidatePassword_Test extends
		TestCaseBehaviorSetup implements TestCase {

	@Inject
	private OperatorRegistry<UserMgmtOperator> UserMgmtOperator;

	private String response;
	private ArrayList<String> passwordList = new ArrayList<>();
	private String errorPolicyValidation = "Failed policy validation";

	@BeforeTest
	public void suiteSetup() {
		logger.info("###### OpenIDM_Validate_Password::validatePassword_Test ######");
	}

	@AfterTest
	public void suiteTearDown() {
		logger.info("###### END TEST OpenIDM_Validate_Password::validatePassword_Test ######");
	}

	@TestId(id = "validate_password_policy_negative", title = "Validate password")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@DataDriven(name = "OpenIDM_Validate_Password")
	public void validatePassword_Test(@Input("userName") String userName,
			@Input("passwordNumber") String passwordNumber,
			@Input("passwordUppercaseLetter") String passwordUppercaseLetter,
			@Input("passwordLowercaseLetter") String passwordLowercaseLetter,
			@Input("passwordUserName") String passwordUserName,
			@Input("passwordFirstName") String passwordFirstName,
			@Input("passwordLastName") String passwordLastName,
			@Input("passwordSpecialCharacters") String passwordSpecialCharacters) {
		logger.debug("Adding passwords to Array List");
		passwordList.add(passwordNumber);
		passwordList.add(passwordUppercaseLetter);
		passwordList.add(passwordLowercaseLetter);
		passwordList.add(passwordUserName);
		passwordList.add(passwordFirstName);
		passwordList.add(passwordLastName);
		passwordList.add(passwordSpecialCharacters);

		UserMgmtOperator userMgmtOperator = getUserMgmtOperator();

		logger.debug("Login Administrator");
		userMgmtOperator.connect();

		logger.debug("Method: getDummyUser(" + userName + ")");
		ENMUser user = UserManagementStub.createDummyUser(userName);

		logger.debug("Method: createUser(" + userName + ")");
		userMgmtOperator.createUser(user);

		logger.info("Checking passwords ...");
		for (String newPassword : passwordList) {
			assertChangePassword(userMgmtOperator, userName,
					OperatorParams.DUMMY_USER_PASSWORD, newPassword);
		}
		logger.info("Finished checking passwords");

		logger.debug("Tear down - deleting created user");
		userMgmtOperator.deleteUser(userName);
		userMgmtOperator.disconnect();
	}

	private void assertChangePassword(UserMgmtOperator userMgmtOperator,
			String userName, String password, String newPassword) {
		logger.info("Method: changePassword for " + userName);
		response = userMgmtOperator.changePassword(userName, password,
				newPassword);
		assertTrue("Error for user " + userName + ", password did change",
				response.contains(errorPolicyValidation));
	}

	private UserMgmtOperator getUserMgmtOperator() {
		return UserMgmtOperator.provide(UserMgmtOperator.class);
	}
}