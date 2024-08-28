package com.ericsson.nms.security.taf.test.operators;

import com.ericsson.cifwk.taf.tools.cli.Shell;
import com.ericsson.cifwk.taf.tools.cli.TimeoutException;

import java.io.FileNotFoundException;

public interface GenericOperator {
    final String cliCommandPropertyPrefix = "clicommand.";

    /**
     * Retrieves the command specified in cliCommands.properties file
     * @param command
     * @return CLI command
     */
    String getCommand(String command);

    /**
     * Creates CLI Shell instance using hostname provided
     * @param hostname
     */
    void initializeShell(String hostname);

    /**
     * Reads from the standard output
     */
    String getStdOut();

    /**
     * Execute a command with args on the host
     * @param command
     * @param args
     * @return
     */
    void writeln(String command, String args);

    /**
     * Execute a command with no args on the host
     * @param command
     * @return
     */
    void writeln(String command);

    /**
     * Converts a null error to a blank string if no standard error found
     * @param error
     */
    String checkForNullError(String error);

    /**
     * Checks open/close status of shell
     * @return
     * @throws TimeoutException
     */
    boolean isClosed() throws TimeoutException;

    /**
     * Wait for the spawned process to finish.
     * @param timeout
     * @throws TimeoutException
     */
    void expectClose(int timeout) throws TimeoutException;

    /**
     * Method that waits for pattern to appear on standard out
     * @param expectedText
     * @return read standard out
     * @throws TimeoutException
     */
    String expect(String expectedText) throws TimeoutException;

    /**
     * Method that waits for pattern to appear on standard out
     * @param expectedText
     * @param timeout
     * @return read standard out
     * @throws TimeoutException
     */
    String expect(String expectedText, int timeout) throws TimeoutException;


    /**
     * Get the exitCode of the most recent command
     * @return Integer representing exit value
     */
    int getExitValue();

    /**
     * Closes and invalidates SSH shell
     */
    void disconnect();

    /**
     * Send a file to a remote server
     * @param hostname
     */
    void sendFileRemotely(String hostname, String fileName, String remoteFileLocation) throws FileNotFoundException;

    /**
     * Delete a file from remote server
     * @param hostname
     */
    void deleteRemoteFile(String hostname, String fileName, String remoteFileLocation) throws FileNotFoundException;

    /**
     * String response to input prompt from script
     * @param message
     */
    void scriptInput(String message);

    /**
     * Create {@link Shell} and execute the command on it<br />
     * Command will be like a single command, or a list of commands that can be executed one after the other
     *
     * @param commands executed commands
     * @return new shell object, representing the shell result of the executed command
     */
    Shell executeCommand(String... commands);

    /**
     * Copy a file from a remote server to local
     * @param hostname
     * @param localFileLocation
     * @param remoteFileLocation
     * @throws FileNotFoundException
     */
    public void copyRemoteFileToLocal(String hostname, String localFileLocation, String remoteFileLocation) throws FileNotFoundException;
}


