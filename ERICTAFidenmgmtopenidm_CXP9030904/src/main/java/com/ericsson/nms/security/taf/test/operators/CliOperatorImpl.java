package com.ericsson.nms.security.taf.test.operators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.Operator;
import com.ericsson.cifwk.taf.configuration.TafConfiguration;
import com.ericsson.cifwk.taf.configuration.TafConfigurationProvider;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.CLICommandHelper;
import com.ericsson.cifwk.taf.tools.cli.Shell;
import com.ericsson.nms.host.HostConfigurator;
import com.google.inject.Singleton;

@Operator(context = Context.CLI)
@Singleton
public class CliOperatorImpl implements CliOperator {

    Logger logger = LoggerFactory.getLogger(CliOperatorImpl.class);
    private Shell shell;
    private CLICommandHelper commandHelper;
    private Host host;
    private CLI cli;

    @Override
    public String loadAndExecuteCommandWithHelper(String commandRef) {
        String command = getCommand(commandRef);
        String response = commandHelper.simpleExec(command);
        return response;
    }

    @Override
    public void initializeShell(String hostName) {
        logger.debug("Method: initializeShell (" + hostName + ")");
        if (shell != null) {
            logger.debug("Shell is already initialized. Disconnecting current shell connection.");
            disconnect();
        }

        setHost(hostName);

        logger.debug("Creating new shell instance");
        shell = cli.openShell();

        logger.debug("Creating command Helper");
        commandHelper = new CLICommandHelper(host);
    }

    @Override
    public void closeShell() {
        logger.info("Disconnect shell.");
        if (shell != null) {
            shell.disconnect();
            commandHelper.closeAndValidateShell();
            logger.debug("Shell is closed.");
        } else {
            logger.debug("You cannot close shell, because shell is not initialized.");
        }
    }

    private String getCommand(String commandRef) {
        logger.debug("getCommand (" + commandRef + ")");
        logger.debug("Using commandRef to extract the actual command from a properties file.");
        TafConfiguration configuration = TafConfigurationProvider.provide();

        logger.debug("property: " + configuration.getProperty(commandRef) + "\n" + configuration.getString(commandRef));
        String command = (String) configuration.getProperty(commandRef);

        logger.debug("Read command: " + command);
        return command;
    }

    private void getTestHost(String hostname) {
        if (hostname.contains("sc1")) {
            host = HostConfigurator.getSC1();
        }
        if (hostname.contains("sc2")) {
            host = HostConfigurator.getSC2();
        }
    }

    private void disconnect() {
        logger.debug("Disconnecting from shell");
        shell.disconnect();
        shell = null;
    }

    private void setHost(String hostName) {
        logger.debug("Method: setHost (" + hostName + ")");

        if (host == null) {
            getTestHost(hostName);
        }

        logger.debug("Initialize CLI");
        cli = new CLI(host);
    }
}
