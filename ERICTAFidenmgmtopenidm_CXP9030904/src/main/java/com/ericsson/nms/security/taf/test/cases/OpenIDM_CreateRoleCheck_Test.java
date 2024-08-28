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
import com.ericsson.nms.security.taf.test.helpers.TestCaseBehaviorSetup;
import com.ericsson.nms.security.taf.test.helpers.RolesManagementStub;
import com.ericsson.nms.security.taf.test.operators.RolesMgmtOperator;

public class OpenIDM_CreateRoleCheck_Test extends TestCaseBehaviorSetup implements
		TestCase {
	
	@Inject
	private OperatorRegistry<RolesMgmtOperator> rolesMgmtOperator;

	@BeforeTest
	public void suiteSetup() {
		logger.info("###### OpenIDM_Create_Role_Check::createRoleCheck ######");
	}

	@AfterTest
	public void suiteTearDown() {
		logger.info("###### END TEST OpenIDM_Create_Role_Check::createRoleCheck ######");
	}

	@TestId(id = "role_exist_check_conf", title = "Verify create a role")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@DataDriven(name = "OpenIDM_CreateRoleCheck")
	public void createRoleCheck(@Input("roleName") String roleName) {

		logger.debug("Login Admin");
		RolesMgmtOperator rolesMgmtOperator = getRolesMgmtOperator();
		rolesMgmtOperator.connect();

		logger.info("Check if role " + roleName + " exist.");
		assertTrue(RolesManagementStub.doesRoleExist(roleName,
				rolesMgmtOperator));

		rolesMgmtOperator.disconnect();
	}

	private RolesMgmtOperator getRolesMgmtOperator() {
		return rolesMgmtOperator.provide(RolesMgmtOperator.class);
	}
}
