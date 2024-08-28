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

package com.ericsson.nms.security.taf.test.teststeps;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public class UserCleanup {

	@Inject
	private OperatorRegistry<UserMgmtOperator> userMgmtOperator;

	private static Logger logger = Logger.getLogger(UserCleanup.class);

	@TestStep(id = "cleanupUser")
	public void cleanupUser(@Input("userName") String userName) {
		logger.info("===[ START OF SCENARIO TEARDOWN: CLEANUP USER ]===");
		logger.debug("Deleting user: " + userName);

		UserMgmtOperator operator = getUserMgmtOperator();
		try {
			operator.connect();
			operator.deleteUser(userName);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("Failed to delete user: " + userName);
		} finally {
			operator.disconnect();
		}
		
		logger.info("===[ END OF SCENARIO TEARDOWN: CLEANUP USER ]===");
	}

	private UserMgmtOperator getUserMgmtOperator() {
		return userMgmtOperator.provide(UserMgmtOperator.class);
	}
}
