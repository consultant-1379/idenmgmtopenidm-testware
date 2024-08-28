package com.ericsson.nms.security.taf.test.cases;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.*;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.ericsson.cifwk.taf.utils.FileFinder;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.security.taf.test.operators.OpenIDMOperator;
import com.ericsson.nms.security.taf.test.utils.*;
import com.ericsson.nms.security.taf.test.helpers.OpenIDMTestwareSettings;
import com.google.inject.Inject;

public class OpenIDM_ChangeMysqlAdmPwdFunctionalTest extends TorTestCaseHelper implements TestCase {

    private Logger logger = Logger.getLogger(OpenIDM_ChangeMysqlAdmPwdFunctionalTest.class);
    private static String mysqlAdminPwd;
    private static String newMysqlAdminPwd;
    private Decoder decoderSc1;
    private Decoder decoderSc2;

    @Inject
    private OpenIDMOperator cliOperator;

    @BeforeSuite
    public void setUp() {
        logger.info("OpenIdm MYSQL admin password change test setup");

        cliOperator.initializeShell(OpenIDMTestwareSettings.FIRST_NODE);
        sendFileToRemoteServer(OpenIDMTestwareSettings.FIRST_NODE, OpenIDMTestwareSettings.SCRIPTS_DIR_REMOTE_TARGET, OpenIDMTestwareSettings.DECRYPT_PWD_SCRIPT_FILENAME);
        decoderSc1 = new Decoder();
        initializeDecoder(decoderSc1);

        cliOperator.writeln(OpenIDMTestwareSettings.DECRYPT_MYSQL_PWD_CMD);
        mysqlAdminPwd = cliOperator.getStdOut().split("\n")[1].trim();
        closeShell(OpenIDMTestwareSettings.CLOSE_SHELL_TIMEOUT);

        cliOperator.initializeShell(OpenIDMTestwareSettings.SECOND_NODE);
        sendFileToRemoteServer(OpenIDMTestwareSettings.SECOND_NODE, OpenIDMTestwareSettings.SCRIPTS_DIR_REMOTE_TARGET, OpenIDMTestwareSettings.DECRYPT_PWD_SCRIPT_FILENAME);
        decoderSc2 = new Decoder();
        initializeDecoder(decoderSc2);
        closeShell(OpenIDMTestwareSettings.CLOSE_SHELL_TIMEOUT);

        logger.debug("Setup: Checking if OpenIDM MYSQL admin password fulfills default policies as a must condition for all tests tu run");
        assertTrue(PasswordValidator.validate(mysqlAdminPwd, OpenIDMTestwareSettings.REGEXES));
        newMysqlAdminPwd = OpenIDMTestwareSettings.MYSQL_NEW_PWD;
    }

    @TestId(id = "TORF-35472", title = "Verify OpenIDM MYSQL admin password change")
    @Context(context = { Context.CLI })
    @Test(groups = { "Acceptance" })
    @DataDriven(name = "OpenIDM_MysqlAdminPasswordChangeData")
    public void verifyOpenDJPwdChange(@Input("step") String step,@Input("host") String hostname, @Input("command") String command,
    @Input("args") String args, @Output("expectedOut") String expectedOut) throws TimeoutException, InterruptedException {

        logger.info("Test step: " + step);
        cliOperator.initializeShell(hostname);

        args = updateTestVariables(args);
        expectedOut = updateTestVariables(expectedOut);
        expectedOut = expectedOut.replaceAll("\\s+", " ").trim();

        if (command.equals(OpenIDMTestwareSettings.CHANGE_MYSQL_ADMIN_PWD_CMD))
            changePassword(args, expectedOut);
        else if (command.equals(OpenIDMTestwareSettings.DECODE_JSON_CMD)){
            if (hostname.equals(OpenIDMTestwareSettings.FIRST_NODE))
                assertPwdChangeInRepoJdbcJsonFile(hostname, expectedOut, decoderSc1);
            else
                assertPwdChangeInRepoJdbcJsonFile(hostname, expectedOut, decoderSc2);
        }
        else {
            if (args != null && !args.isEmpty())
                cliOperator.writeln(command, args);
            else
                cliOperator.writeln(command);

            String output = cliOperator.getStdOut().replaceAll("\\s+", " ").trim();
            logger.debug("Expected output: " + expectedOut);
            logger.debug("Actual output: " + output);
            if (expectedOut != null && !expectedOut.isEmpty()) {
                assertTrue(output.contains(expectedOut));
            }
            if (expectedOut.contains(OpenIDMTestwareSettings.MYSQL_CL_PROMPT)){
                cliOperator.writeln("exit");
            }
        }
        closeShell(OpenIDMTestwareSettings.CLOSE_SHELL_TIMEOUT);
    }

    @AfterSuite
    public void TearDown() {
        logger.info("Restituting initial state");
        logger.info(OpenIDMTestwareSettings.DELETE_FILES_LOG_MSG);

        try {
            cliOperator.initializeShell(OpenIDMTestwareSettings.FIRST_NODE);
            cliOperator.deleteRemoteFile(OpenIDMTestwareSettings.FIRST_NODE, OpenIDMTestwareSettings.DECRYPT_PWD_SCRIPT_FILENAME, OpenIDMTestwareSettings.SCRIPTS_DIR_REMOTE_TARGET);
            closeShell(OpenIDMTestwareSettings.CLOSE_SHELL_TIMEOUT);
            cliOperator.initializeShell(OpenIDMTestwareSettings.SECOND_NODE);
            cliOperator.deleteRemoteFile(OpenIDMTestwareSettings.SECOND_NODE, OpenIDMTestwareSettings.DECRYPT_PWD_SCRIPT_FILENAME, OpenIDMTestwareSettings.SCRIPTS_DIR_REMOTE_TARGET);
            closeShell(OpenIDMTestwareSettings.CLOSE_SHELL_TIMEOUT);
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

    private String updateTestVariables(String args) {
        String result = null;
        if (args != null && !args.isEmpty()) {
            result = args;

            if (args.contains(OpenIDMTestwareSettings.OPENIDM_MYSQLADMIN_PASSWORD_VARIABLE)) {
                result = result.replace(OpenIDMTestwareSettings.OPENIDM_MYSQLADMIN_PASSWORD_VARIABLE, mysqlAdminPwd);
            }

            if (args.contains(OpenIDMTestwareSettings.OPENIDM_NEW_MYSQLADMIN_PASSWORD_VARIABLE)) {
                result = result.replace(OpenIDMTestwareSettings.OPENIDM_NEW_MYSQLADMIN_PASSWORD_VARIABLE, newMysqlAdminPwd);
            }

            if (args.contains(OpenIDMTestwareSettings.SQLPWD_PATTERN)) {
                String pwdStr = OpenIDMTestwareSettings.MYSQL_PWD_OPTION;
                result = result.replace(OpenIDMTestwareSettings.SQLPWD_PATTERN, pwdStr + newMysqlAdminPwd);
            }
        }
        return result;
    }

    private void sendFileToRemoteServer(String hostname, String targetDirPath, String fname) {
        logger.info(OpenIDMTestwareSettings.UPLOAD_FILES_LOG_MSG);

        String filepath = FileFinder.findFile(fname + ".repo").get(0);
        assertTrue(filepath.contains(fname + ".repo"));
        try {
            String content = FileUtils.readFileToString(new File(filepath));
            String tempFilePath = filepath.replace(".repo","");
            File tempFile = new File(tempFilePath);
            FileUtils.writeStringToFile(tempFile, content);

            cliOperator.sendFileRemotely(hostname, fname, targetDirPath);

            cliOperator.writeln(OpenIDMTestwareSettings.CHANGE_DIR_CMD, targetDirPath);
            cliOperator.writeln(OpenIDMTestwareSettings.LIST_DIR_CMD);
            String stdout = cliOperator.getStdOut();
            assertTrue(stdout.contains(fname));
            tempFile.delete();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void changePassword(String inputs, String outputs) throws InterruptedException {
        List<String> inputList = new ArrayList<String>(Arrays.asList(inputs.split(":")));
        List<String> outputList = new ArrayList<String>(Arrays.asList(outputs.split(":")));

        Iterator<String> inputIter = inputList.iterator();

        logger.info("Changing MYSQL administrator password");
        cliOperator.writeln(OpenIDMTestwareSettings.CHANGE_MYSQL_ADMIN_PWD_CMD);

        for (String expectedOut : outputList) {
            String stdout = cliOperator.expect(expectedOut, OpenIDMTestwareSettings.INTERACTIVE_SHELL_TIMEOUT);
            assertTrue(stdout.contains(expectedOut));

            if (inputIter.hasNext()) {
                String next = inputIter.next();
                DataHandler.setAttribute("clicommand." + next, next);
                cliOperator.writeln(next);
            }
        }
    }

    private void assertPwdChangeInRepoJdbcJsonFile(String hostname,String expectedOut,Decoder decoder) {
        try {
            String initialVector = retrieveJsonFromRepoJdbcJsonFile(OpenIDMTestwareSettings.VECTOR_PATTERN);
            String encryptedPwd = retrieveJsonFromRepoJdbcJsonFile(OpenIDMTestwareSettings.PWD_PATTERN);
            JsonRetriever jr = new JsonRetriever(initialVector, null);
            initialVector = jr.getValue(OpenIDMTestwareSettings.VECTOR_KEY);
            jr.setJsonString(encryptedPwd);
            encryptedPwd = jr.getValue(OpenIDMTestwareSettings.PWD_KEY);
            //loading </opt/openidm/security/keystore.jceks> file to /tmp folder before using decode method
            cliOperator.copyRemoteFileToLocal(hostname, OpenIDMTestwareSettings.LOCAL_FILE_LOCATION, OpenIDMTestwareSettings.REMOTE_FILE_LOCATION);
            assertTrue(decoder.decode(initialVector, encryptedPwd, OpenIDMTestwareSettings.LOCAL_FILE_LOCATION).contains(expectedOut));
            deleteFileFromLocalhost(OpenIDMTestwareSettings.LOCAL_FILE_LOCATION);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private String retrieveJsonFromRepoJdbcJsonFile(String pattern){
        cliOperator.writeln(OpenIDMTestwareSettings.GREP_CMD, pattern + " " + OpenIDMTestwareSettings.REPO_JDBC_JSON_FILE);
        String stdout = cliOperator.getStdOut();

        List<String> stdOutLines = new ArrayList<String>(Arrays.asList(stdout.split(System.getProperty("line.separator"))));
        String result = stdOutLines.get(1).trim();
        result = result.substring(0, result.length() - 1);
        result = "{" + " " + result + " " + "}";

        return result;
    }

    private void deleteFileFromLocalhost(String filepath){
        Path path = Paths.get(filepath);
        try {
            Files.delete(path);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private String readBootProperties(String property) {
        cliOperator.writeln(OpenIDMTestwareSettings.GREP_CMD, property + " " +  OpenIDMTestwareSettings.BOOT_PROPERTIES_FILE);
        String stdout = cliOperator.getStdOut();
        List<String> stdOutLines = new ArrayList<String>(Arrays.asList(stdout.split(System.getProperty("line.separator"))));
        String result = stdOutLines.get(1).trim();
        stdOutLines = new ArrayList<String>(Arrays.asList(result.split("=")));
        result = stdOutLines.get(1).trim();
        return result;
    }

    private void initializeDecoder(Decoder decoder) {
        String pwdVal = readBootProperties(OpenIDMTestwareSettings.PASSWORD_PROPERTY);
        String keystoreTypeVal = readBootProperties(OpenIDMTestwareSettings.KEYSTORE_TYPE_PROPERTY);
        String cryptoAliasVal = readBootProperties(OpenIDMTestwareSettings.CRYPTO_ALIAS_PROPERTY);

        decoder.setKeystoreType(keystoreTypeVal);
        decoder.setCryptoAlias(cryptoAliasVal);
        decoder.setKeystorePassword(pwdVal);
    }

    private void closeShell(int timeout) {
        cliOperator.writeln("exit");
        cliOperator.expectClose(timeout);
        assertTrue(cliOperator.isClosed());
        assert (cliOperator.getExitValue() == 0);
        cliOperator.disconnect();
    }
}
