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
import com.ericsson.nms.security.taf.test.cases.OpenIDM_ChangeEmail_Test;
import com.ericsson.nms.security.taf.test.cases.OpenIDM_CreateUser_Test;
import com.ericsson.nms.security.taf.test.cases.OpenIDM_DeleteUser_Test;
import com.ericsson.nms.security.taf.test.cases.OpenIDM_QueryUser_Test;
import com.ericsson.nms.security.taf.test.cases.OpenIDM_Roles_Test;
import com.ericsson.nms.security.taf.test.teststeps.OpenIDM_PasswordChange_TestSteps;
import com.ericsson.nms.security.taf.test.teststeps.UserCleanup;

public class OpenIDM_AdminFlow_Scenario extends TorTestCaseHelper{

	private static final Logger logger = Logger
			.getLogger(OpenIDM_AdminFlow_Scenario.class);

	@Inject
	private OpenIDM_CreateUser_Test createUserTest;

	@Inject
	private OpenIDM_PasswordChange_TestSteps passwordChangeTest;

	@Inject
	private OpenIDM_ChangeEmail_Test changeEmailTest;

	@Inject
	private OpenIDM_Roles_Test roleTest;

	@Inject
	private OpenIDM_QueryUser_Test queryUserTest;

	@Inject
	private OpenIDM_DeleteUser_Test deleteUserTest;

	@Inject
	private UserCleanup userCleanup;

	@BeforeTest
	public void suiteSetup() {
		logger.info("###### STARTING SCENARIO: OpenIDM_AdministratorFlow::CommonAdministratorActivities ######");

		logger.debug("Setting test cases for the scenario run");
		createUserTest.setThisScenarioRun(true);
		passwordChangeTest.setThisScenarioRun(true);
		changeEmailTest.setThisScenarioRun(true);
		roleTest.setThisScenarioRun(true);
		queryUserTest.setThisScenarioRun(true);
		deleteUserTest.setThisScenarioRun(true);
	}

	@AfterTest
	public void suiteTearDown() {
		logger.info("###### ENDING SCENARIO: OpenIDM_AdministratorFlow::CommonAdministratorActivities ######");
	}

	@TestId(id = "verify_admin_flow", title = "Verify that admin can perform common activities")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance", "RFA" })
	public void verifyThatEndToEndAdminFlowIsPossible() {
		TestStepFlow adminActionsFlow = defineScenario();
		TestStepFlow cleanupFlow = defineCleanupStep();

		TestScenario scenario = scenario().addFlow(adminActionsFlow).build();
		TestScenario cleanup = scenario().addFlow(cleanupFlow).build();

		TestScenarioRunner runner = runner().build();
		try {
			runner.start(scenario);
		} finally {
			runner.start(cleanup);
		}

	}

	private TestStepFlow defineScenario() {
		TestStepFlow adminActionsFlow = flow("adminActionsFlow")
				.addTestStep(annotatedMethod(createUserTest, "createUser"))
				.addTestStep(
						annotatedMethod(passwordChangeTest,
								"forceUserPasswordChange"))
				.addTestStep(
						annotatedMethod(changeEmailTest, "changeEmailByAdmin"))
				.addTestStep(annotatedMethod(roleTest, "assignRole"))
				.addTestStep(annotatedMethod(queryUserTest, "queryUser"))
				.addTestStep(annotatedMethod(deleteUserTest, "deleteUser"))
				.withDataSources(dataSource("OpenIDM_AdminFlow_Scenario")).build();
		return adminActionsFlow;
	}

	private TestStepFlow defineCleanupStep() {
		TestStepFlow cleanupFlow = flow("cleanupScenario")
				.addTestStep(annotatedMethod(userCleanup, "cleanupUser"))
				.withDataSources(dataSource("OpenIDM_AdminFlow_Scenario")).build();
		return cleanupFlow;
	}
}
