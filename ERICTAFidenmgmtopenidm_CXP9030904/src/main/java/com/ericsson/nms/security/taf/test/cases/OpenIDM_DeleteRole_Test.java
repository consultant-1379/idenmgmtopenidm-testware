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
import com.ericsson.cifwk.taf.annotations.Output;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.taf.test.helpers.RolesManagementStub;
import com.ericsson.nms.security.taf.test.helpers.TestCaseBehaviorSetup;
import com.ericsson.nms.security.taf.test.operators.RolesMgmtOperator;

public class OpenIDM_DeleteRole_Test extends TestCaseBehaviorSetup implements
		TestCase {
	
	@Inject
	private OperatorRegistry<RolesMgmtOperator> rolesMgmtOperator;

	@BeforeTest
	public void suiteSetup() {
		logger.info("###### OpenIDM_Delete_Role::deleteRole ######");
	}

	@AfterTest
	public void suiteTearDown() {
		logger.info("###### END TEST OpenIDM_Delete_Role::deleteRole ######");
	}

	@TestId(id = "delete_role_negative", title = "Verify delete a role")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@DataDriven(name = "OpenIDM_DeleteRole")
	public void deleteRole(@Input("roleName") String roleName,
			@Output("expectedOutput") String expectedOutput) {

		logger.debug("Login Admin");
		RolesMgmtOperator rolesMgmtOperator = getRolesMgmtOperator();
		rolesMgmtOperator.connect();

		logger.debug("Check if role " + roleName + " exist.");
		assertTrue(RolesManagementStub.doesRoleExist(roleName,
				rolesMgmtOperator));

		logger.info("Delete role " + roleName);
		String result = rolesMgmtOperator.deleteRole(roleName);
		logger.debug("deleteRole result: " + result);
		assertTrue(result.contains(expectedOutput));

		rolesMgmtOperator.disconnect();
	}

	private RolesMgmtOperator getRolesMgmtOperator() {
		return rolesMgmtOperator.provide(RolesMgmtOperator.class);
	}
}
