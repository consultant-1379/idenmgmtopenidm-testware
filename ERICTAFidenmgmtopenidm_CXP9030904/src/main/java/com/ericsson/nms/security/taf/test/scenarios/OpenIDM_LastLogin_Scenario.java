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
import com.ericsson.nms.security.taf.test.teststeps.OpenIDM_LastLogin_TestSteps;

public class OpenIDM_LastLogin_Scenario extends TorTestCaseHelper {

    @Inject
    private OpenIDM_LastLogin_TestSteps lastLogin_testSteps;

    private static Logger logger = Logger
            .getLogger(OpenIDM_LastLogin_Scenario.class);

    @BeforeTest
    public void suiteSetup() {
        logger.info("###### OpenIDM_LastLogin::verifyLastLogin ######");
    }

    @AfterTest
    public void suiteTearDown() {
        logger.info("###### OpenIDM_LastLogin::verifyLastLogin ######");
    }

    @TestId(id = "verify_last_login_func", title = "Verify last login time")
    @Context(context = { Context.REST })
    @Test(groups = { "Acceptance" })
    public void verifyLastLogin() {
        TestStepFlow verifyLastLoginFlow = flow("verifyLastLoginTime")
                .addTestStep(
                        annotatedMethod(lastLogin_testSteps,
                                "verifyLastLoginTime"))
                .withDataSources(dataSource("OpenIDM_LastLogin")).build();

        TestScenario scenario = scenario().addFlow(verifyLastLoginFlow).build();

        TestScenarioRunner runner = runner().build();
        runner.start(scenario);
    }
}
