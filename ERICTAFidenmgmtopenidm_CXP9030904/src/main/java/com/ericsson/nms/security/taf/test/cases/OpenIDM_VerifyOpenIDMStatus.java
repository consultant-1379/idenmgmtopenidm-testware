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

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Output;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.inject.Inject;

public class OpenIDM_VerifyOpenIDMStatus extends TorTestCaseHelper implements TestCase {
    private static Logger logger = Logger.getLogger(OpenIDM_Functional_Test.class);
    private static String rootSuffixValue;

    @Inject
    private OperatorRegistry<UserMgmtOperator> UserMgmtOperator;

    @BeforeSuite
    public void suiteSetup() {

    }

    @AfterSuite
    public void suiteTearDown() {

    }

    @BeforeTest
    public void testSetup() {
        logger.info("######## OpenIDM_VerifyOpenIDMStatus::OpenIDM_Verify_Status ######");
    }

    @AfterTest
    public void testTearDown() {
        logger.info("####### END TEST OpenIDM_VerifyOpenIDMStatus::OpenIDM_Verify_Status #########");
    }

    @TestId(id = "check_openidm_process_id", title = "Verify OpenIDM process is running")
    @Context(context = {Context.REST})
    @Test(groups = {"Acceptance"})
    @DataDriven(name = "OpenIDM_Verify_Status")
    public void verifyOpenIDMStatus(@Output("expectedOut") String expectedOut)
            throws InterruptedException, TimeoutException {
        UserMgmtOperator operator = getUserMgmtOperator();

        logger.debug("Login Administrator");
        operator.connect();

        String result = operator.showOpenIDMStatus();
        logger.debug("OpenIDM Status result: " + result);

        logger.info("Checking OpenIDM status ...");
        assertTrue("OpenIDM is not running", result.contains(expectedOut));

        logger.debug("Disconnect administrator");
        operator.disconnect();
    }

    private UserMgmtOperator getUserMgmtOperator() {
        return UserMgmtOperator.provide(UserMgmtOperator.class);
    }
}