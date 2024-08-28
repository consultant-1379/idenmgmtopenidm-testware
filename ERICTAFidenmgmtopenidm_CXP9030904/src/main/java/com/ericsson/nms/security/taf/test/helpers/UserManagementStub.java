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
package com.ericsson.nms.security.taf.test.helpers;

import org.apache.log4j.Logger;

import com.ericsson.nms.security.ENMUser;
import com.ericsson.nms.security.taf.test.cases.OpenIDM_Functional_Test;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public class UserManagementStub {

	private static Logger logger = Logger
			.getLogger(OpenIDM_Functional_Test.class);

	public static ENMUser createDummyUser(String userName) {
		ENMUser enmUser = new ENMUser();
		enmUser.setUsername(userName);
		enmUser.setFirstName("DummyFirstName");
		enmUser.setLastName("DummyLastName");
		enmUser.setEmail("email@dummy.com");
		enmUser.setPassword(OperatorParams.DUMMY_USER_PASSWORD);
		enmUser.setEnabled(true);

		logger.debug("Create user : " + userName);

		return enmUser;
	}

	public static ENMUser createUser(String userName, String firstName,
			String lastName, String email, String password, Boolean isEnabled) {
		ENMUser enmUser = new ENMUser();
		enmUser.setUsername(userName);
		enmUser.setFirstName(firstName);
		enmUser.setLastName(lastName);
		enmUser.setEmail(email);
		enmUser.setPassword(password);
		enmUser.setEnabled(isEnabled);

		logger.debug("Create user : " + userName);

		return enmUser;
	}

	public static boolean doesUserExist(String userName,
			UserMgmtOperator userMgmtOperator) {
		// TODO improve this method - because can be user which login can
		// contains other login
		return listAllUsers(userMgmtOperator).contains(userName);
	}

	public static String listAllUsers(UserMgmtOperator userMgmtOperator) {
		String results = userMgmtOperator.getAllUsers();
		logger.debug("listAllUsers: " + results);
		return results;
	}

	public static String queryUser(String userName,
			UserMgmtOperator userMgmtOperator) {
		String result = userMgmtOperator.getUser(userName);
		logger.debug("queryUser result: " + result);
		return result;
	}

	public static String updateUserProperty(String userName, String property,
			String value, UserMgmtOperator userMgmtOperator) {
		String result = userMgmtOperator.changeUserProperty(userName, property,
				value);
		logger.debug("updateUserProperty result: " + result);
		return result;
	}
}
