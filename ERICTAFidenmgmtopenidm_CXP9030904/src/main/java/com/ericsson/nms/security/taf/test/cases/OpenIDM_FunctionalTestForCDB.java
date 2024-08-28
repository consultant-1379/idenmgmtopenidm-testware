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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

import org.apache.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.*;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.ericsson.cifwk.taf.utils.FileFinder;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.security.taf.test.helpers.OpenIDMTestwareSettings;
import com.ericsson.nms.security.taf.test.operators.GenericOperator;
import com.ericsson.nms.security.taf.test.operators.OpenIDMOperator;
import com.google.inject.Inject;


public class OpenIDM_FunctionalTestForCDB extends TorTestCaseHelper implements TestCase {
    private static String httpdHostName;
    private static String cookieFileName = "/tmp/cookieForIdmTaf.txt";

    private static Logger logger = Logger.getLogger(OpenIDM_Functional_Test.class);
    String rootSuffixValue;

    @Inject
    private OpenIDMOperator cliOperator;

    @BeforeSuite
    @TestStep(id = "beforeSuiteCreateUser")
    public void suiteSetup() throws TimeoutException, FileNotFoundException, IOException {
        logger.info("Before suite: Cli operators setup and password decryption");

        cliOperator.initializeShell("sc1");

        uploadFile(OpenIDMTestwareSettings.DECRYPT_MYSQL_PASSWORD_FILE, "sc1", "/tmp", cliOperator);
        uploadFile(OpenIDMTestwareSettings.DECRYPT_OPENDJ_PASSWORD_FILE, "sc1", "/tmp", cliOperator);
        uploadFile(OpenIDMTestwareSettings.DECRYPT_OPENIDM_PASSWORD_FILE, "sc1", "/tmp", cliOperator);

        DataHandler.setAttribute("mysql.openidm.passowrd", decryptPassword("decryptMYSQLOpenIDMPwd", cliOperator));
        DataHandler.setAttribute("opendj.DM.passowrd", decryptPassword("decryptOpenDJAdminPwd", cliOperator));
        DataHandler.setAttribute("openidm.administrator.passowrd", decryptPassword("decryptOpenIDMSecAdminPwd", cliOperator));

        rootSuffixValue = readGlobalProperties(cliOperator, OpenIDMTestwareSettings.ROOT_SUFFIX_VARIABLE, null);
        assertTrue(rootSuffixValue.length() > 0);
        logger.debug("rootSuffixValue: " + rootSuffixValue);

        logger.debug("identifying httpd host name");
        httpdHostName = identifyHttpdHostName( cliOperator );
        logger.debug("suiteSetup: httpd hostname for OpenIDM Rest Interface tests = " + httpdHostName );
        assertNotNull( "suiteSetup: httpd host for OpenIDM Rest Interface ", httpdHostName );

        DataHandler.setAttribute(OpenIDMTestwareSettings.MOD_CLUSTER_HTTPD_HOSTNAME_VARIABLE, httpdHostName);

        DataHandler.setAttribute(OpenIDMTestwareSettings.COOKIE_FILE_VARIABLE, cookieFileName);
    }

    @AfterSuite
    @TestStep(id = "afterSuiteCreateUser")
    public void suiteTearDown() {
        logger.info("After suite: Cli operators teardown and file cleaning");

        cliOperator.initializeShell("sc1");

        deleteFile(OpenIDMTestwareSettings.DECRYPT_MYSQL_PASSWORD_FILE, "/tmp", cliOperator);
        deleteFile(OpenIDMTestwareSettings.DECRYPT_OPENDJ_PASSWORD_FILE, "/tmp", cliOperator);
        deleteFile(OpenIDMTestwareSettings.DECRYPT_OPENIDM_PASSWORD_FILE, "/tmp", cliOperator);

        cliOperator.writeln("exit");
        cliOperator.expectClose(10000);
        assertTrue(cliOperator.isClosed());
        assert (cliOperator.getExitValue() == 0);
        cliOperator.disconnect();
    }

    /**
     * @DESCRIPTION Upload the file needed to host connected with the cli operator
     */
    private void uploadFile(String fileName, String hostName, String directory, GenericOperator cliOperator)
            throws TimeoutException, FileNotFoundException, IOException {
        logger.info("Setup: Uploading " + fileName + " to " + hostName + "...");

        String filePath = FileFinder.findFile(fileName+".repo").get(0);
        String fileContent = FileUtils.readFileToString(new File(filePath));
        String tempFilePath = filePath.replace(".repo","");
        File tempFile = new File(tempFilePath);
        FileUtils.writeStringToFile(tempFile, fileContent);

        cliOperator.sendFileRemotely(hostName, fileName, directory);
        cliOperator.writeln("change.dir", directory);
        cliOperator.writeln("list.dir");
        String stdout = cliOperator.getStdOut();
        assertTrue(stdout.contains(fileName));
        logger.debug(stdout);
        tempFile.delete();

        logger.debug("done");
    }

    /**
     * @DESCRIPTION decrypt the password
     */
    private String decryptPassword(String command, GenericOperator cliOperator) {
        logger.info("Setup: " +  command + "...");

        cliOperator.writeln(command);
        String stdout = cliOperator.getStdOut();
        String password = stdout.split(System.getProperty("line.separator"))[1].trim();
        assertTrue(password.length() > 0);

        logger.debug("done");
        return password;
    }

    /**
     * @DESCRIPTION delete file through cli operator
     */
    private void deleteFile(String fileName, String directory, GenericOperator cliOperator) {
        logger.info("Setup: Cleaning " + fileName + "...");

        cliOperator.writeln("change.dir", directory);
        cliOperator.writeln("delete", fileName);

        logger.debug("done");
    }

    /**
     * @DESCRIPTION Check OpenIDM Status
     * @PRE SC nodes have been installed with LITP and TOR security OpenIDM Package
     * @VUsers 1
     * @PRIORITY HIGH
     * @throws TimeoutException
     */
    @TestId(id = "TORF-9567_Func_25", title = "OpenIDM status check")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_ST_Test_Data_CDB")
    public void verifyOpenIDMStatus(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
                                    @Input("timeout") int timeout, @Input("args") String args, @Output("expectedOut") String expectedOut,
                                    @Output("expectedExit") int expectedExitCode) throws InterruptedException, TimeoutException {

        runCommands(step, hostname, command, timeout, args, expectedOut, expectedExitCode);
    }

    /*
     * This is a prep test case which is executed to create a user which is added to OpenIDM and later sync into openDJ.
     * Command and its data is defined in IDMServiceRest_Login.csv file.
     * curl command is used to add the user.
     */
    @TestId(id = "OpenIDM_rest_prep_login", title = "login to ENM to obtain cookie")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_Login")
    public void loginToENM(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
            @Input("timeout") int timeout, @Input("args") String args )
            throws InterruptedException, TimeoutException
    {

        logger.info("Test Step: " + step );
        cliOperator.initializeShell(hostname);

        args = updateTestVariables(cliOperator, args);

        //run curl command with username and password to ENM login page
        cliOperator.writeln(command, args);

        //verify that cookie file gets created
        cliOperator.writeln( "list.dir", cookieFileName );
        String output = cliOperator.getStdOut();
        assertTrue(output.contains( cookieFileName ));

        logger.debug("Actual output:" + output);
    }

    /**
     * @DESCRIPTION Query All Users
     * @PRE SC node has been installed with LITP and TOR security OpenIDM Package
     * @PRIORITY HIGH
     */
    @TestId(id = "TORF-9567_Func_26", title = "Verify query all users")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_Query_All_User_Data")
    public void verifyQueryAllUsers(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
                                    @Input("timeout") int timeout, @Input("args") String args, @Output("expectedOut") String expectedOut,
                                    @Output("expectedExit") int expectedExitCode, @Output("errorOut") String errorOut) throws InterruptedException,
            TimeoutException {

        runCommands(step, hostname, command, timeout, args, expectedOut, expectedExitCode);
    }

    /**
     * @DESCRIPTION Create a new user In openIDM
     * @PRE SC node has been installed with LITP and TOR security OpenIDM Package
     * @PRIORITY HIGH
     */
    @TestStep(id = "createUser")
    @TestId(id = "TORF-9567_Func_21", title = "Verify create a new user")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_Create_New_User_CDB")
    public void verifyCreateANewUser(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
                                     @Input("timeout") int timeout, @Input("args") String args, @Output("expectedOut") String expectedOut,
                                     @Output("expectedExit") int expectedExitCode) throws InterruptedException, TimeoutException {

        logger.info("Test Step: " + step);
        cliOperator.initializeShell(hostname);

        args = updateTestVariables(cliOperator, args);
        expectedOut = updateTestVariables(cliOperator, expectedOut);

        cliOperator.writeln(command, args);

        if (command.equals("service")) {
            // This is the pre-setup for the test case.
            assertTrue(true);
        } else {
            if (expectedOut != null && !expectedOut.isEmpty()) {
                String output = cliOperator.getStdOut();
                logger.debug("Expected output: " + expectedOut);
                logger.debug("Actual output: " + output);
                assertTrue(output.contains(expectedOut));
            }
        }
    }

    /**
     * @DESCRIPTION Query an existing user
     * @PRE SC node has been installed with LITP and TOR security OpenIDM Package
     * @PRIORITY HIGH
     */
    @TestId(id = "TORF-9567_Func_22", title = "Verify query an existing user")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_Query_Existing_User_CDB")
    public void verifyQueryAnExistingUser(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
                                          @Input("timeout") int timeout, @Input("args") String args, @Output("expectedOut") String expectedOut,
                                          @Output("expectedExit") int expectedExitCode) throws InterruptedException, TimeoutException {
        runCommands(step, hostname, command, timeout, args, expectedOut, expectedExitCode);
    }

    /**
     * @DESCRIPTION Edit user information
     * @PRE SC node has been installed with LITP and TOR security OpenIDM Package
     * @PRIORITY HIGH
     */
    @TestId(id = "TORF-9567_Func_23", title = "Verify edit user information")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_Edit_Existing_User_CDB")
    public void verifyEditUserInformation(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
                                          @Input("timeout") int timeout, @Input("args") String args, @Output("expectedOut") String expectedOut,
                                          @Output("expectedOutCode") int expectedOutCode,
                                          @Output("expectedExit") int expectedExitCode) throws InterruptedException, TimeoutException {
        runCommandsWithExitCode(step, hostname, command, timeout, args, expectedOut, expectedOutCode, expectedExitCode);
    }

    /**
     * @DESCRIPTION Delete a user
     * @PRE SC node has been installed with LITP and TOR security OpenIDM Package
     * @PRIORITY HIGH
     */
    @TestId(id = "TORF-9567_Func_24", title = "Verify delete a user")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_Delete_Existing_User_CDB")
    public void verifyDeleteAUser(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
                                  @Input("timeout") int timeout, @Input("args") String args, @Output("expectedOut") String expectedOut,
                                  @Output("expectedOutCode") int expectedOutCode, @Output("expectedExit") int expectedExitCode,
                                  @Output("unexpectedOut") String unexpectedOut) throws InterruptedException, TimeoutException {
        if (args.contains("DELETE")) {
            if ( (expectedOut != null) && (! expectedOut.contains("not used")) ) {
                runCommands(step, hostname, command, timeout, args, expectedOut, expectedExitCode);
            }
            else {
                runCommandsWithExitCode(step, hostname, command, timeout, args, expectedOutCode, expectedExitCode);
            }
        } else if (args.contains("GET")) {
            if ( (expectedOut != null) && (! expectedOut.contains("not used")) ) {
            	runCommands(step, hostname, command, timeout, args, expectedOut, expectedExitCode);
	    } else {
                logger.info("Test Step: " + step);
                cliOperator.initializeShell(hostname);

                args = updateTestVariables(cliOperator, args);
                expectedOut = updateTestVariables(cliOperator, expectedOut);

                cliOperator.writeln(command, args);

                if (unexpectedOut != null && !unexpectedOut.isEmpty()) {
                    String output = cliOperator.getStdOut();
                    assertTrue(!output.contains(unexpectedOut));
                }
	    }
        } else if (args.contains("POST")) {
            runCommandsWithExitCode(step, hostname, command, timeout, args, expectedOutCode, expectedExitCode);
        } else if (command.contains("ldap")) {
            logger.info("Test Step: " + step);
            cliOperator.initializeShell(hostname);

            args = updateTestVariables(cliOperator, args);
            expectedOut = updateTestVariables(cliOperator, expectedOut);

            cliOperator.writeln(command, args);

            if (unexpectedOut != null && !unexpectedOut.isEmpty()) {
                String output = cliOperator.getStdOut();
                assertTrue(!output.contains(unexpectedOut));
            }
        } else {
            runCommands(step, hostname, command, timeout, args, expectedOut, expectedExitCode);
        }
    }

    /**
     * @DESCRIPTION Create a new user In openIDM
     * @PRE SC node has been installed with LITP and TOR security OpenIDM Package
     * @PRIORITY HIGH
     */
    @TestId(id = "TORF-16119_Func_1", title = "Verify create a new role")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_Create_New_Role_CDB")
    public void verifyCreateANewRole(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
                                     @Input("timeout") int timeout, @Input("args") String args, @Output("expectedOut") String expectedOut,
                                     @Output("expectedExit") int expectedExitCode) throws InterruptedException, TimeoutException {

        logger.info("Test Step: " + step);
        cliOperator.initializeShell(hostname);

        args = updateTestVariables(cliOperator, args);
        expectedOut = updateTestVariables(cliOperator, expectedOut);
        cliOperator.writeln(command, args);

        if (expectedOut != null && !expectedOut.isEmpty()) {
            String output = cliOperator.getStdOut();
            assertTrue(output.contains(expectedOut));
        }
    }

    /**
     * @DESCRIPTION Query an existing role
     * @PRE SC node has been installed with LITP and TOR security OpenIDM Package
     * @PRIORITY HIGH
     */
    @TestId(id = "TORF-16119_Func_2", title = "Verify querying an existing role")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_Query_Existing_Role_CDB")
    public void verifyQueryAnExistingRole(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
                                          @Input("timeout") int timeout, @Input("args") String args, @Output("expectedOut") String expectedOut,
                                          @Output("expectedExit") int expectedExitCode) throws InterruptedException, TimeoutException {
        runCommands(step, hostname, command, timeout, args, expectedOut, expectedExitCode);
    }

    /**
     * @DESCRIPTION Query All Roles
     * @PRE SC node has been installed with LITP and TOR security OpenIDM Package
     * @PRIORITY HIGH
     */
    @TestId(id = "TORF-16119_Func_5", title = "Verify querying all roles")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_Query_All_Role_Data_CDB")
    public void verifyQueryAllRoles(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
                                    @Input("timeout") int timeout, @Input("args") String args, @Output("expectedOut") String expectedOut,
                                    @Output("expectedExit") int expectedExitCode, @Output("errorOut") String errorOut) throws InterruptedException,
            TimeoutException {

        runCommands(step, hostname, command, timeout, args, expectedOut, expectedExitCode);
    }

    /**
     * @DESCRIPTION Query default ENM roles and administrator
     * @PRE SC node has been installed with LITP and TOR security OpenDJ and OpenIDM Packages
     * @PRIORITY HIGH
     */
    @TestId(id = "TORF-20463_Func_1-9", title = "Verify querying default ENM roles and administrator")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_Query_Default_ENM_Role_User_CDB")
    public void verifyQueryDefaultENMRolesUsers(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
                                    @Input("timeout") int timeout, @Input("args") String args, @Output("expectedOut") String expectedOut,
                                    @Output("expectedExit") int expectedExitCode) throws InterruptedException,
            TimeoutException {

        runCommands(step, hostname, command, timeout, args, expectedOut, expectedExitCode);
    }

    /**
     * @DESCRIPTION Verify the last login time of a user
     * @PRE SC node has been installed with LITP and TOR security OpenDJ and OpenIDM Packages
     * @PRIORITY HIGH
     */
    @TestId(id = "TORF-20463_Func_10", title = "Verify last login time")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_Last_Login_Time_CDB")
    public void verifyUserLastLoginTime(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
                                    @Input("timeout") int timeout, @Input("args") String args, @Output("expectedOut") String expectedOut,
                                    @Output("expectedExit") int expectedExitCode) throws InterruptedException,
            TimeoutException {
        if (command.equals("sleep")) {
            logger.info("Test Step: " + step);
            int milisecond = Integer.parseInt(args);
            Thread.sleep(milisecond);
        }
        else {
            runCommands(step, hostname, command, timeout, args, expectedOut, expectedExitCode);
            cliOperator.writeln("exit");
            cliOperator.expectClose( timeout);
            cliOperator.disconnect();
        }
    }

    /**
     * @DESCRIPTION Assign and unassign user from FIELD_TECHNICIAN role
     * @PRE SC node has been installed with LITP and TOR security OpenDJ and OpenIDM Packages
     * @PRIORITY HIGH
     */
    @TestId(id = "TORF-20459_Func_1", title = "Assign and unassign user from FIELD_TECHNICIAN role")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_FIELD_TECHNICIAN_Role_CDB")
    public void verifyFieldTechnicianRoleAssignment(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
                                                  @Input("timeout") int timeout, @Input("args") String args, @Output("expectedOuts") String expectedOuts,
                                                  @Output("unexpectedOuts") String unexpectedOuts,
                                                  @Output("expectedExit") int expectedExitCode) throws InterruptedException, TimeoutException {
        logger.info("Test Step: " + step);
        cliOperator.initializeShell(hostname);

        args = updateTestVariables(cliOperator, args);

        cliOperator.writeln(command, args);

        //Need a pause to prevent 502 Proxy error from httpd due to a timeout without getting response back from OpenIDM
        logger.debug("Put a sleep....... " );
        int milisecond = Integer.parseInt("5");
        Thread.sleep(milisecond);

        String output = cliOperator.getStdOut();
        logger.debug("Expected output: " + expectedOuts);
        logger.debug("Actual output : " + output);
        if (expectedOuts != null && !expectedOuts.isEmpty()) {
            String expectedOutput[] = expectedOuts.split(";");
            for (int i = 0; i < expectedOutput.length; i++) {
                assertTrue(output.contains(expectedOutput[i]));
            }
        }
        if (unexpectedOuts != null && !unexpectedOuts.isEmpty()) {
            String unexpectedOutput[] = unexpectedOuts.split(";");
            for (int i = 0; i < unexpectedOutput.length; i++) {
                assertFalse(output.contains(unexpectedOutput[i]));
            }
        }
    }

    /**
     * @DESCRIPTION Non-admin user self password change
     * @PRE SC node has been installed with LITP and TOR security OpenIDM Package
     * @PRIORITY HIGH
     */
    @TestId(id = "TORF-20845_Func_1", title = "Verify user self password change")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_User_Password_Change_CDB")
    public void verifyUserSelfPasswordChange(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
                                          @Input("timeout") int timeout, @Input("args") String args, @Output("expectedOut") String expectedOut,
                                          @Output("expectedExit") int expectedExitCode) throws InterruptedException, TimeoutException {
        runCommands(step, hostname, command, timeout, args, expectedOut, expectedExitCode);
    }

    /**
     * The private method to execute the command. It is common for most of the test cases. However, for some test cases that needs additional
     * information, this method cannot be reused.
     */
    private void runCommands(String step, String hostname, String command, int timeout, String args, String expectedOut, int expectedExitCode) throws InterruptedException {
        logger.info("Test Step: " + step);
        cliOperator.initializeShell(hostname);

        args = updateTestVariables(cliOperator, args);
        expectedOut = updateTestVariables(cliOperator, expectedOut);

        cliOperator.writeln(command, args);

        //Need a pause to prevent 502 Proxy error from httpd due to a timeout without getting response back from OpenIDM
        logger.debug("Put a sleep....... " );
        int milisecond = Integer.parseInt("5");
        Thread.sleep(milisecond);

        String output = cliOperator.getStdOut();
        logger.debug("Expected output: " + expectedOut);
        logger.debug("Actual output: " + output);
        if (expectedOut != null && !expectedOut.isEmpty()) {
            assertTrue(output.contains(expectedOut));
        } else if ((expectedOut == null) || expectedOut.isEmpty()) {
            //if the expectedOut is an empty string, it must means that there is no error
            assertFalse(output.contains("\"error\""));
            //this checking is also used for ldapsearch when it is expected to return no value
            assertFalse(output.contains("dn: "));
        }
    }

    /**
     * @DESCRIPTION Test cases to verify that all the application roles defined
     *              in application policyset files have been created successfully
     * @PRE Connection to SUT
     * @PRIORITY HIGH
     * @VUsers 1
     * @throws TimeoutException
     * @throws
     */
    @TestId(id = "TORF-19699_Func_1", title = "Verify Creating Application Roles")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "Create_App_Roles_Test_Data_CDB")
    public void verifyApplicationRoles (@Input("step") String step, @Input("host") String hostname, @Input("parseRoleCmd") String parseRoleCmd,
                                       @Input("timeout") int timeout, @Input("parseRoleArgs") String parseRoleArgs, @Input("queryRoleCmd") String queryRoleCmd,
                                       @Input("queryRoleArgs") String queryRoleArgs,@Input("exitCmd") String exitCmd,
                                       @Output("expectedExitCode") int expectedExitCode) throws InterruptedException, TimeoutException {

        String spliter = "([ \\t\\r]*\\n[ \\t\\r]*)+";

        cliOperator.initializeShell(hostname);
        queryRoleArgs = updateTestVariables(cliOperator, queryRoleArgs);
        cliOperator.writeln("list.dir","/ericsson/tor/data/access_control/policies/default/*.json");
        String stdout=cliOperator.getStdOut();

        if (!stdout.contains("No such file or directory")){
            cliOperator.writeln(parseRoleCmd,parseRoleArgs);
            String parseOut=cliOperator.getStdOut();
            String expectRoleInfo[]= parseOut.split(spliter);
            cliOperator.writeln(queryRoleCmd,queryRoleArgs);
            String queryStdOut=cliOperator.getStdOut();
            logger.debug("verifyApplicationRoles: queryRoleResult= " + queryStdOut);
            for (int i = 1;i < expectRoleInfo.length-1; i++){
                if (expectRoleInfo[i] != null && !expectRoleInfo[i].isEmpty()) {
                    logger.info("Test:" + step +" <" + expectRoleInfo[i] + ">");
                    assertTrue(queryStdOut.contains(expectRoleInfo[i]));
                }
            }
        }
    }

    /**
     * @DESCRIPTION Verify OpenIDM and OpenDJ Replication
     * @PRE SC node has been installed with LITP and TOR security OpenIDM Package
     * @PRIORITY HIGH
     */
    @TestId(id = "OpenIDM_Replication", title = "Verify openidm and opendj replication")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_Replication_CDB")
    public void verifyOpenIDMReplication(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
                                    @Input("timeout") int timeout, @Input("args") String args, @Output("expectedOut") String expectedOut,
                                    @Output("expectedExit") int expectedExitCode) throws InterruptedException,
            TimeoutException {

        runCommands(step, hostname, command, timeout, args, expectedOut, expectedExitCode);
    }

    /**
     * @DESCRIPTION Retrieve password policy
     * @PRE SC node has been installed with LITP and TOR security OpenIDM Package
     * @PRIORITY HIGH
     */
    @TestId(id = "TORF-9567_Func_22", title = "Verify retrieve password policy")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_Retrieve_Password_policy_CDB")
    public void verifyRetrievePasswordPolicy(@Input("step") String step, @Input("host") String hostname, @Input("command") String command,
                                          @Input("timeout") int timeout, @Input("args") String args, @Output("expectedOut") String expectedOut,
                                          @Output("expectedExit") int expectedExitCode) throws InterruptedException, TimeoutException {
        runCommands(step, hostname, command, timeout, args, expectedOut, expectedExitCode);
    }

    /**
     * The private method to execute the command. It is common for most of the test cases. However, for some test cases that needs additional
     * information, this method cannot be reused.
     */
    private void runCommandsWithExitCode(String step, String hostname, String command, int timeout, String args, String expectedOut,
                                         int expectedOutCode, int expectedExitCode) throws InterruptedException {
        logger.info("Test Step: " + step);
        cliOperator.initializeShell(hostname);

        args = updateTestVariables(cliOperator, args);

        cliOperator.writeln(command, args);

        //Need a pause to prevent 502 Proxy error from httpd due to a timeout without getting response back from OpenIDM
        logger.debug("Put a sleep....... " );
        int milisecond = Integer.parseInt("5");
        Thread.sleep(milisecond);

        String output = cliOperator.getStdOut();
        logger.debug("Expected output: " + expectedOut);
        logger.debug("Actual output: " + output);
        if (expectedOut != null && !expectedOut.isEmpty()) {
            assertTrue(output.contains(expectedOut));
        } else if ( ( expectedOut == null ) || expectedOut.isEmpty()) {
            //if the expectedOut is an empty string, if must means that there is no error
            assertFalse(output.contains("\"error\""));
        }
    }

    private String readGlobalProperties(GenericOperator cliOperator, String property, String args) {
        cliOperator.writeln("source", OpenIDMTestwareSettings.GLOBAL_PROPERTY_FILE);
        if (args == null) {
            cliOperator.writeln("echo", "$" + property);
        } else {
            cliOperator.writeln("echo", "$" + property + " " + args);
        }
        String result = cliOperator.getStdOut().split(System.getProperty("line.separator"))[2].trim();
        return result;
    }

    private String updateTestVariables(GenericOperator cliOperator, String data) {
        if (data==null) {
            return null;
        }
        String result = data;
        if (data.contains(OpenIDMTestwareSettings.ROOT_SUFFIX_VARIABLE)) {
            result = result.replace(OpenIDMTestwareSettings.ROOT_SUFFIX_VARIABLE, rootSuffixValue);
        }
        if (data.contains(OpenIDMTestwareSettings.MYSQLOPENIDM_PASSWORD_VARIABLE)) {
            result = result.replace(OpenIDMTestwareSettings.MYSQLOPENIDM_PASSWORD_VARIABLE, DataHandler.getAttribute("mysql.openidm.passowrd").toString());
        }
        if (data.contains(OpenIDMTestwareSettings.OPENIDM_ADMIN_PASSWORD_VARIABLE)) {
            result = result.replace(OpenIDMTestwareSettings.OPENIDM_ADMIN_PASSWORD_VARIABLE, DataHandler.getAttribute("openidm.administrator.passowrd").toString());
        }
        if (data.contains(OpenIDMTestwareSettings.OPENDJ_ADMIN_PASSWORD_VARIABLE)) {
            result = result.replace(OpenIDMTestwareSettings.OPENDJ_ADMIN_PASSWORD_VARIABLE, DataHandler.getAttribute("opendj.DM.passowrd").toString());
        }
        if (data.contains(OpenIDMTestwareSettings.MOD_CLUSTER_HTTPD_HOSTNAME_VARIABLE)) {
            result = result.replace(OpenIDMTestwareSettings.MOD_CLUSTER_HTTPD_HOSTNAME_VARIABLE, DataHandler.getAttribute(OpenIDMTestwareSettings.MOD_CLUSTER_HTTPD_HOSTNAME_VARIABLE).toString());
        }
        if (data.contains(OpenIDMTestwareSettings.COOKIE_FILE_VARIABLE)) {
            result = result.replace(OpenIDMTestwareSettings.COOKIE_FILE_VARIABLE, DataHandler.getAttribute(OpenIDMTestwareSettings.COOKIE_FILE_VARIABLE).toString());
        }
        return result;
    }
    /**
     * The private method to execute the command. It is common for most of the test cases. However, for some test cases that needs additional
     * information, this method cannot be reused.
     */
    private void runCommandsWithExitCode(String step, String hostname, String command, int timeout, String args, int expectedOutCode,
                                         int expectedExitCode) throws InterruptedException {
        runCommandsWithExitCode(step, hostname, command, timeout, args, null, expectedOutCode, expectedExitCode);
    }

    private String identifyHttpdHostName(GenericOperator cliOperator )
    {
        logger.debug("Identifying HTTPD HOST to access OpenIDM to add/remove user :");
        String testCommand = "getent";
        String testArgs = "hosts httpd | awk '{print $4}'";
        cliOperator.writeln( testCommand, testArgs );
        String result = cliOperator.getStdOut();
        logger.debug("Command: " + testCommand + " executed with args: " + testArgs);
        logger.debug("Command completed stdOut:" + result);
        if ( ( result != null ) && ( result.contains("httpd" )) )
        {
            logger.info("getent hosts httpd:" + result);
            //Example result string
            //getent hosts httpd\n
            //atrcxb2666-3.athtem.eei.ericsson.se\n
            //[root@atrcxb3211 tmp]#
            String hostName = result.split("\n")[1];
            //hostName = hostName.replace("\t", " ");
            //String[] splits = hostName.split(" ");
            //hostName = splits[ splits.length -1 ];
            logger.info("HttpdHostName to use: " + hostName.trim() );
            return hostName.trim();
        }
        return null;
    }

}
