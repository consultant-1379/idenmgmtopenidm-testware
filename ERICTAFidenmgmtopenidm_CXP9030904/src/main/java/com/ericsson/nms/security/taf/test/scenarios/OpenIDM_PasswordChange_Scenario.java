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

import static com.ericsson.cifwk.taf.scenario.TestScenarios.*;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.testng.annotations.*;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.scenario.*;
import com.ericsson.nms.security.taf.test.teststeps.OpenIDM_PasswordChange_TestSteps;

public class OpenIDM_PasswordChange_Scenario extends TorTestCaseHelper {

    @Inject
    private OpenIDM_PasswordChange_TestSteps passwordChange_testSteps;

    private static Logger logger = Logger
            .getLogger(OpenIDM_PasswordChange_Scenario.class);

    @BeforeTest
    public void suiteSetup() {
        logger.info("###### OpenIDM_PasswordChange::passwordChange ######");
    }

    @AfterTest
    public void suiteTearDown() {
        logger.info("###### OpenIDM_PasswordChange::passwordChange ######");
    }

    @TestId(id = "force_password_change_func", title = "Force user password change")
    @Context(context = { Context.REST })
    @Test(groups = { "Acceptance" })
    public void forceUserPasswordChange() {
        TestStepFlow forceUserPasswordChangeFlow = flow(
                "forceUserPasswordChange")
                .addTestStep(
                        annotatedMethod(passwordChange_testSteps,
                                "forceUserPasswordChange"))
                .withDataSources(dataSource("OpenIDM_PasswordChange")).build();

        TestScenario scenario = scenario().addFlow(forceUserPasswordChangeFlow)
                .build();

        TestScenarioRunner runner = runner().build();
        runner.start(scenario);
    }

    @TestId(id = "password_change_func", title = "Verify user self password change")
    @Context(context = { Context.REST })
    @Test(groups = { "Acceptance" })
    public void userPasswordChange() {
        TestStepFlow forceUserPasswordChangeFlow = flow("userPasswordChange")
                .addTestStep(
                        annotatedMethod(passwordChange_testSteps,
                                "userChangePassword"))
                .withDataSources(dataSource("OpenIDM_PasswordChange")).build();

        TestScenario scenario = scenario().addFlow(forceUserPasswordChangeFlow)
                .build();

        TestScenarioRunner runner = runner().build();
        runner.start(scenario);
    }
}
