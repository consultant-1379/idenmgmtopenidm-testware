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
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.ericsson.nms.security.taf.test.operators.GenericOperator;
import com.google.inject.Inject;

public class OpenIDM_Robustness_Test extends TorTestCaseHelper implements
		TestCase {
	Logger logger = Logger.getLogger(OpenIDM_Functional_Test.class);
	@Inject
	private OperatorRegistry<GenericOperator> operatorRegistry;

	/**
	  * @DESCRIPTION OpenIDM process could be restarted after killed
	  * @PRE SC nodes have been installed with LITP and TOR security OpenIDM Package
	  * @PRIORITY HIGH
	  */
 	@TestId(id = "TORF-9567_Robu_3", title = "Verify OpenIDM process could be restarted after killed")
	@Context(context = {Context.CLI})
	@Test(groups={"Acceptance"})
	@DataDriven(name = "OpenIDM_Process_Restart")
	public void verifyOpenIDMProcessRestart(@Input("step") String step,
			@Input("host") String hostname, @Input("command") String command,
			@Input("timeout") int timeout, @Input("args") String args,
			@Output("expectedOut") String expectedOut,
			@Output("expectedOutCode") int expectedOutCode,
			@Output("expectedExit") int expectedExitCode)
			throws InterruptedException, TimeoutException {

 	    if (command.equals("service")) {
 	        runCommands(step, hostname, command, timeout, args, expectedOut, expectedExitCode);
 	    } else if (command.equals("kill")) {
 	       runCommandsWithExitCode(step, hostname, command, timeout, args, expectedOutCode, expectedExitCode);
 	       Thread.sleep(5000);
 	    }
	}
	
	/**
	 * The private method to execute the command. It is common for most of the test cases.
	 * However, for some test cases that needs additional information, this method cannot
	 * be reused.
	 */
	private void runCommands(String step, String hostname, String command, int timeout,
	                         String args, String expectedOut, int expectedExitCode) {
            System.out.println("Test Step: " + step);
            GenericOperator cliOperator = operatorRegistry.provide(GenericOperator.class);
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
	
        /**
         * The private method to execute the command. It is common for most of the test cases.
         * However, for some test cases that needs additional information, this method cannot
         * be reused.
         */
        private void runCommandsWithExitCode(String step, String hostname, String command, int timeout,
                                 String args, int expectedOutCode, int expectedExitCode) {
            System.out.println("Test Step: " + step);
            GenericOperator cliOperator = operatorRegistry.provide(GenericOperator.class);
            cliOperator.initializeShell(hostname);
            
            cliOperator.writeln(command, args);

            assert(cliOperator.getExitValue() == expectedOutCode);
            
            cliOperator.writeln("exit");
            cliOperator.expectClose(timeout);
            assertTrue(cliOperator.isClosed());
            assert (cliOperator.getExitValue() == expectedExitCode);
            cliOperator.disconnect();
        }
}
