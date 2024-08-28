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

package com.ericsson.nms.security.taf.test.scenarios;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.nms.security.taf.test.cases.OpenIDM_CreateUser_Test;
import com.ericsson.nms.security.taf.test.cases.OpenIDM_Roles_Test;
import com.ericsson.nms.security.taf.test.teststeps.OpenIDM_PasswordChange_TestSteps;
import com.ericsson.nms.security.taf.test.teststeps.UserCleanup;

public class OpenIDM_UserFlow_Scenario extends TorTestCaseHelper {

	private static final Logger logger = Logger
			.getLogger(OpenIDM_UserFlow_Scenario.class);

	@Inject
	private OpenIDM_CreateUser_Test createUserTest;

	@Inject
	private OpenIDM_Roles_Test rolesTest;

	@Inject
	private OpenIDM_PasswordChange_TestSteps passwordChangeTest;

	@Inject
	private UserCleanup userCleanup;

	@BeforeTest
	public void suiteSetup() {
		logger.info("###### STARTING SCENARIO: OpenIDM_UserFlow::CommonUserActivities ######");

		logger.debug("Setting test cases for the scenario run");
		createUserTest.setThisScenarioRun(true);
		rolesTest.setThisScenarioRun(true);
		passwordChangeTest.setThisScenarioRun(true);
	}

	@AfterTest
	public void suiteTearDown() {
		logger.info("###### ENDING SCENARIO: OpenIDM_UserFlow::CommonUserActivities ######");
	}

	@TestId(id = "verify_user_flow", title = "Verify that user can perform common activities")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance", "RFA" })
	public void verifyThatEndToEndUserFlowIsPossible() {
		TestStepFlow userActionsFlow = defineScenario();
		TestStepFlow cleanupFlow = defineCleanupStep();

		TestScenario scenario = scenario().addFlow(userActionsFlow).build();
		TestScenario cleanup = scenario().addFlow(cleanupFlow).build();

		TestScenarioRunner runner = runner().build();
		try {
			runner.start(scenario);
		} finally {
			runner.start(cleanup);
		}

	}

	private TestStepFlow defineScenario() {
		TestStepFlow userActionsFlow = flow("userActionsFlow")
				.addTestStep(annotatedMethod(createUserTest, "createUser"))
				.addTestStep(annotatedMethod(rolesTest, "assignRole"))
				.addTestStep(
						annotatedMethod(passwordChangeTest,
								"userChangePassword"))
				.withDataSources(dataSource("OpenIDM_UserFlow_Scenario"))
				.build();
		return userActionsFlow;
	}

	private TestStepFlow defineCleanupStep() {
		TestStepFlow cleanupFlow = flow("cleanupScenario")
				.addTestStep(annotatedMethod(userCleanup, "cleanupUser"))
				.withDataSources(dataSource("OpenIDM_UserFlow_Scenario")).build();
		return cleanupFlow;
	}
}
