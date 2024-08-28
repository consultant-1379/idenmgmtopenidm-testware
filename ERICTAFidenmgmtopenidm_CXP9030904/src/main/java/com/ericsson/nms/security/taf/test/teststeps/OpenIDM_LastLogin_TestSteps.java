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

import static com.ericsson.cifwk.taf.assertions.TafAsserts.assertFalse;
import static com.ericsson.cifwk.taf.assertions.TafAsserts.assertTrue;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.ericsson.nms.security.ENMUser;
import com.ericsson.nms.security.taf.test.helpers.UserManagementStub;
import com.ericsson.nms.security.taf.test.operators.UserMgmtOperator;

public class OpenIDM_LastLogin_TestSteps {

    @Inject
    private OperatorRegistry<UserMgmtOperator> openIDMLogin;

    private static Logger logger = Logger
            .getLogger(OpenIDM_LastLogin_TestSteps.class);

    private static final String LAST_LOGIN_FIELD = "lastLogin";
    private static final long LAST_LOGIN_SYNCHRONIZATION_TIME = 60000;

    @TestStep(id = "verifyLastLoginTime")
    public void verifyUserLastLoginTime(@Input("userName") String userName) throws InterruptedException,
            TimeoutException {

        UserMgmtOperator operator = getOpenIDMLoginOperator();
        ENMUser user = getDummyUser(userName);
        
        try {
            operator.connect();

            logger.debug("Create user " + user.getUsername());
            operator.createUser(user);
            operator.setForceUserChangePassword(user.getUsername(), false);

            logger.debug("Verify lastLogin field doesn't exist");
            String userData = operator.getUser(user.getUsername());
            operator.disconnect();

            logger.debug("Login user");
            assertFalse("Field lastLogin should not exist for new user",
                    userData.contains("\"" + LAST_LOGIN_FIELD + "\""));
            assertTrue("User could not login", operator.checkIfCanLogin(
                    userName, user.getPassword(), UserType.WEB));

            Thread.sleep(LAST_LOGIN_SYNCHRONIZATION_TIME);

            logger.debug("Verify lastLogin field exists");
            operator.connect();
            userData = operator.getUser(user.getUsername());
            assertTrue("Field lastLogin should exist for user",
                    userData.contains("\"" + LAST_LOGIN_FIELD + "\""));

        } finally {
            cleanup(operator, userName);
        }
    }

    private void cleanup(UserMgmtOperator operator, String userName) {
        logger.debug("Cleanup - delete user");
        if(operator != null) {
            operator.connect();
            operator.deleteUser(userName);
            operator.disconnect();
        }
    }

    private UserMgmtOperator getOpenIDMLoginOperator() {
        return openIDMLogin.provide(UserMgmtOperator.class);
    }

    private ENMUser getDummyUser(String userName) {
        return UserManagementStub.createDummyUser(userName);
    }
}
