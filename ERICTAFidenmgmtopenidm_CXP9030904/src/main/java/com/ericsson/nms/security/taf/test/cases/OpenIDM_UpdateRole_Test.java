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
import com.ericsson.nms.security.taf.test.helpers.TestCaseBehaviorSetup;
import com.ericsson.nms.security.taf.test.helpers.RolesManagementStub;
import com.ericsson.nms.security.taf.test.operators.RolesMgmtOperator;

public class OpenIDM_UpdateRole_Test extends TestCaseBehaviorSetup
		implements TestCase {

	@Inject
	private OperatorRegistry<RolesMgmtOperator> rolesMgmtOperator;

	@BeforeTest
	public void suiteSetup() {
		logger.info("###### OpenIDM_Update_Role::updateRole ######");
	}

	@AfterTest
	public void suiteTearDown() {
		logger.info("###### END TEST OpenIDM_Update_Role::updateRole ######");
	}

	@TestId(id = "update_role_func", title = "Verify update a role")
	@Context(context = { Context.REST })
	@Test(groups = { "Acceptance" })
	@DataDriven(name = "OpenIDM_UpdateRole")
	public void updateRole(@Input("roleName") String roleName,
			@Input("newDescription") String newDescription,
			@Output("outputBefore") String outputBefore,
			@Output("outputAfter") String outputAfter) {

		logger.debug("Login Admin");
		RolesMgmtOperator rolesMgmtOperator = getRolesMgmtOperator();
		rolesMgmtOperator.connect();

		logger.debug("Query role " + roleName + ".");
		String role = RolesManagementStub
				.queryRole(roleName, rolesMgmtOperator);
		logger.info("Check role " + roleName + " before update.");
		assertTrue(role.contains(outputBefore));
		logger.info("Update description of role " + roleName + ".");
		assertTrue(RolesManagementStub.updateRoleDescription(roleName,
				newDescription, rolesMgmtOperator).contains(outputAfter));

		logger.debug("Logout Admin");
		rolesMgmtOperator.disconnect();
	}

	private RolesMgmtOperator getRolesMgmtOperator() {
		return rolesMgmtOperator.provide(RolesMgmtOperator.class);
	}
}
