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
package com.ericsson.nms.security.taf.test.helpers;

import org.apache.log4j.Logger;

import com.ericsson.nms.security.taf.test.operators.RolesMgmtOperator;

public class RolesManagementStub {

	private static Logger logger = Logger.getLogger(RolesManagementStub.class);

	public static boolean doesRoleExist(String roleName,
			RolesMgmtOperator rolesMgmtOperator) {
		String results = rolesMgmtOperator.getAllRoles();
		logger.debug("doesRoleExist result: " + results.replace("}", "}\n"));
		return results.contains(roleName);
	}

	public static String queryRole(String roleName,
			RolesMgmtOperator rolesMgmtOperator) {
		String result = rolesMgmtOperator.queryRole(roleName);
		logger.debug("queryRole result: " + result);
		return result;
	}

	public static String updateRoleDescription(String roleName,
			String newDescription, RolesMgmtOperator rolesMgmtOperator) {
		String result = rolesMgmtOperator.updateRoleProperty(roleName,
				"description", newDescription);
		logger.debug("updateRoleDescription result: " + result);
		return result;
	}
}
