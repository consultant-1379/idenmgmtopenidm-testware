/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.nms.security.taf.test.cases;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.DataDriven;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.Output;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.guice.OperatorRegistry;
import com.ericsson.nms.security.taf.test.operators.GenericOperator;
import com.google.inject.Inject;

public class OpenIDM_Security_Test extends TorTestCaseHelper implements
		TestCase {

	Logger logger = Logger.getLogger(OpenIDM_Functional_Test.class);
	@Inject
	private OperatorRegistry<GenericOperator> operatorRegistry;

	/**
	 * @DESCRIPTION OpenIDM run As non root user
	 * @PRE SC nodes have been installed with LITP and TOR security OpenIDM
	 *      Package
	 * @PRIORITY HIGH
	 */
	@TestId(id = "TORF-9567_Secu_3", title = "Verify OpenIDM runs as non root")
	@Context(context = { Context.CLI })
	@Test(groups = { "Acceptance" })
	@DataDriven(name = "OpenIDM_Non_Root_User")
	public void verifyOpenIDMRunAsNonRoot(@Input("step") String step,
			@Input("host") String hostname, @Input("command") String command,
			@Input("timeout") int timeout, @Input("args") String args,
			@Output("expectedOut") String expectedOut,
			@Output("expectedExit") int expectedExitCode) {
		System.out.println("Test Step: " + step);
		GenericOperator cliOperator = operatorRegistry
				.provide(GenericOperator.class);
		cliOperator.initializeShell(hostname);

		cliOperator.writeln(command, args);

		if (expectedOut != null && !expectedOut.isEmpty()) {
			String output = cliOperator.getStdOut();
			assertTrue(output.contains(expectedOut));
		}

		cliOperator.writeln("exit");
		cliOperator.expectClose(timeout);
		assertTrue(cliOperator.isClosed());
		assert (cliOperator.getExitValue() == expectedExitCode);
		cliOperator.disconnect();
	}
}
