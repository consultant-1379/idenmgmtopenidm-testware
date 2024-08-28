package com.ericsson.nms.security.taf.test.operators;

public interface CliOperator {
    String loadAndExecuteCommandWithHelper(String commandRef);

    void initializeShell(String hostname);

    void closeShell();
}