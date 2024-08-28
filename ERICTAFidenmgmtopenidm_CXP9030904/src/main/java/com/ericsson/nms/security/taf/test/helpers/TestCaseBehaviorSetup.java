package com.ericsson.nms.security.taf.test.helpers;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public abstract class TestCaseBehaviorSetup extends TorTestCaseHelper {

	protected static Logger logger = Logger
			.getLogger(TestCaseBehaviorSetup.class);

	protected boolean isThisScenarioRun = false;

	public boolean isThisScenarioRun() {
		return isThisScenarioRun;
	}

	public void setThisScenarioRun(boolean isThisScenarioRun) {
		this.isThisScenarioRun = isThisScenarioRun;
	}

	protected void announceScenario() {
		if (isThisScenarioRun) {
			logger.info("==[ THIS IS SCENARION RUN ]==");
		}
	}

	protected void cleanup(UserMgmtOperator operator, String userName) {
		if (!isThisScenarioRun) {
			logger.debug("Tear down - deleting created user");
			deleteUser(operator, userName);
		} else {
			logger.debug("This is scenario run. Cleanup is not executed until scenario concludes.");
		}
	}

	private void deleteUser(UserMgmtOperator operator, String userName) {
		logger.debug("Cleanup - delete user");
		if (operator != null) {
			operator.connect();
			operator.deleteUser(userName);
			operator.disconnect();
		} else {
			logger.error("Operator is null. Cannot perform cleanup");
		}
	}
}
