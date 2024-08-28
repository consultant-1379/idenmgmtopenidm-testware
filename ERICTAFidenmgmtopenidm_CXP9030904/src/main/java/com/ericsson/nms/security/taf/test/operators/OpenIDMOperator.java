/*
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 */
package com.ericsson.nms.security.taf.test.operators;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.RemoteFileHandler;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.Shell;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;
import com.ericsson.cifwk.taf.utils.FileFinder;
import com.ericsson.nms.host.HostConfigurator;
import com.google.inject.Singleton;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.List;

@Operator(context = Context.CLI)
@Singleton
public class OpenIDMOperator implements GenericOperator {

    private CLI cli;
    private Shell shell;
    private Host host;
    private String currentHostName = "";

    Logger logger = Logger.getLogger(OpenIDMOperator.class);

    @Override
    public String getCommand(String command) {
        return DataHandler.getAttribute(cliCommandPropertyPrefix + command)
                .toString();
    }

    @Override
    public void initializeShell(String hostname) {
        if ((hostname.equalsIgnoreCase(currentHostName)) && (shell != null)) {
            // use the current shell instance
            return;
        }
        // create a new shell instance
        currentHostName = hostname;
        gettesthost(hostname);
        if (host == null) {
            logger.debug("Host is null from DataHandler, trying to get host again.");
            gettesthost(hostname);
            if (host == null) {
                logger.debug("Cannot get host by name: " + hostname);
            }
        }
        cli = new CLI(host);
        if (shell != null) {
            writeln("exit");
            expectClose(1000);
            disconnect();
        }
        logger.debug("Creating new shell instance");
        shell = cli.openShell();
    }

    @Override
    public void writeln(String command, String args) {
        String cmd = getCommand(command);
        logger.trace("Writing " + cmd + " " + args + " to standard input");
        logger.debug("Executing commmand " + cmd + " with args " + args);
        shell.writeln(cmd + " " + args);
    }

    @Override
    public void writeln(String command) {
        String cmd = getCommand(command);
        logger.trace("Writing " + cmd + " to standard input");
        logger.debug("Executing commmand " + cmd);
        shell.writeln(cmd);
    }

    @Override
    public int getExitValue() {
        int exitValue = shell.getExitValue();
        logger.debug("Getting exit value from shell, exit value is :"
                + exitValue);
        return exitValue;
    }

    @Override
    public String expect(String expectedText) throws TimeoutException {
        logger.debug("Expected return is " + expectedText);
        String found = shell.expect(expectedText);
        logger.debug("Found string <" + found + ">");
        return found;
    }

    @Override
    public String expect(String expectedText, int timeout)
            throws TimeoutException {
        logger.debug("Expected return is " + expectedText);
        String found = shell.expect(expectedText, timeout);
        logger.debug("Found string <" + found + ">");
        return found;
    }

    @Override
    public void expectClose(int timeout) throws TimeoutException {
        shell.expectClose(timeout);
    }

    @Override
    public boolean isClosed() throws TimeoutException {
        return shell.isClosed();
    }

    @Override
    public String checkForNullError(String error) {
        if (error == null) {
            error = "";
            return error;
        }
        return error;
    }

    @Override
    public String getStdOut() {
        String result = shell.read();
        logger.debug("Standard out: " + result);
        return result;
    }

    @Override
    public void disconnect() {
        logger.debug("Disconnecting from shell");
        shell.disconnect();
        shell = null;
    }

    @Override
    public void sendFileRemotely(String hostname, String fileName,
            String fileServerLocation) throws FileNotFoundException {
        gettesthost(hostname);
        RemoteFileHandler remote = new RemoteFileHandler(host);
        List<String> fileLocation = FileFinder.findFile(fileName);
        String remoteFileLocation = fileServerLocation;
        remote.copyLocalFileToRemote(fileLocation.get(0), remoteFileLocation);
        logger.debug("Copying " + fileName + " to " + remoteFileLocation
                + " on remote host " + hostname);

    }

    @Override
    public void deleteRemoteFile(String hostname, String fileName,
            String fileServerLocation) throws FileNotFoundException {
        gettesthost(hostname);
        RemoteFileHandler remoteFileHandler = new RemoteFileHandler(host);
        String remoteFileLocation = fileServerLocation;
        remoteFileHandler.deleteRemoteFile(remoteFileLocation + fileName);
        logger.debug("deleting " + fileName + " at location "
                + remoteFileLocation + " on remote host " + hostname);
    }

    @Override
    public void scriptInput(String message) {
        logger.debug("Writing " + message + " to standard in");
        shell.writeln(message);
    }

    @Override
    public Shell executeCommand(String... commands) {
        logger.debug("Executing command(s) " + commands);
        return cli.executeCommand(commands);

    }

    private void gettesthost(String hostname) {
        if (hostname.contains("sc1")) {
            host = HostConfigurator.getSC1();
        }
        if (hostname.contains("sc2")) {
            host = HostConfigurator.getSC2();
        }
    }

    public void copyRemoteFileToLocal(String hostname,
            String localFileLocation, String remoteFileLocation)
            throws FileNotFoundException {
        gettesthost(hostname);
        RemoteFileHandler remote = new RemoteFileHandler(host);
        logger.debug("Copying " + remoteFileLocation + " to "
                + localFileLocation + " on the local host " + hostname);
        remote.copyRemoteFileToLocal(remoteFileLocation, localFileLocation);
    }
}
