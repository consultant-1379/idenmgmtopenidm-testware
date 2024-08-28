package com.ericsson.nms.security.taf.test.helpers;

import javax.inject.Singleton;

@Singleton
public class OpenIDMTestwareSettings {

    public static final String GLOBAL_PROPERTY_FILE = "/ericsson/tor/data/global.properties";
    public static final String SCRIPTS_DIR_REMOTE_TARGET = "/tmp/";
    public static final String FIRST_NODE = "sc1";
    public static final String SECOND_NODE = "sc2";
    public static final String MYSQL_NEW_PWD = "Admin123457";
    public static final String MYSQL_CL_PROMPT  = "mysql>";
    public static final String LOCAL_FILE_LOCATION  = "/tmp/keystore.jceks";
    public static final String REMOTE_FILE_LOCATION = "/opt/openidm/security/keystore.jceks";
    public static final String REPO_JDBC_JSON_FILE = "/opt/openidm/conf/repo.jdbc.json";
    public static final String BOOT_PROPERTIES_FILE = "/opt/openidm/conf/boot/boot.properties";

    public static final String DECRYPT_MYSQL_PWD_CMD = "decryptMYSQLOpenIDMPwd";
    public static final String CHANGE_MYSQL_ADMIN_PWD_CMD = "change.pwd";
    public static final String CHANGE_DIR_CMD = "change.dir";
    public static final String LIST_DIR_CMD = "list.dir";
    public static final String GREP_CMD = "grep";
    public static final String DELETE_FILE_CMD = "delete";
    public static final String MYSQL_PWD_OPTION = "-p";
    public static final String CMD_PREFIX = "clicommand.";
    public static final String DECODE_JSON_CMD = "decodeJson";

    public static final int CLOSE_SHELL_TIMEOUT = 5000;
    public static final int THREAD_SLEEP_TIME_MILISEC = 5;
    public static final int INTERACTIVE_SHELL_TIMEOUT = 120;

    public static final String OPENIDM_MYSQLADMIN_PASSWORD_VARIABLE = "OPENIDM_MYSQL_ADMIN_PWD";
    public static final String OPENIDM_NEW_MYSQLADMIN_PASSWORD_VARIABLE = "OPENIDM_NEW_MYSQL_ADMIN_PWD";
    public static final String OPENDJ_ADMIN_PASSWORD_VARIABLE = "OPENDJ_ADMIN_PASSWORD";
    public static final String OPENIDM_ADMIN_PASSWORD_VARIABLE = "OPENIDM_ADMIN_PASSWORD";
    public static final String MYSQLOPENIDM_PASSWORD_VARIABLE = "MYSQL_OPENIDM_PASSWORD";
    public static final String SQLPWD_PATTERN = "SQLPWD";
    public static final String ROOT_SUFFIX_VARIABLE = "COM_INF_LDAP_ROOT_SUFFIX";
    public static final String MOD_CLUSTER_HTTPD_HOSTNAME_VARIABLE = "HTTPD_HOST_NAME";
    public static final String COOKIE_FILE_VARIABLE = "COOKIE_FILE";

    public static final String UPLOAD_FILES_LOG_MSG = "Uploading test script file";
    public static final String DELETE_FILES_LOG_MSG = "Deleting test script file";
    public static final String PWD_CHANGE_SUCCESS_MSG = "Password successfully changed";

    public static final String DECRYPT_PWD_SCRIPT_FILENAME = "decryptMYSQLAdmOpenIDMPwd.sh";
    public static final String DECRYPT_OPENIDM_PASSWORD_FILE = "decryptOpenIDMSecAdminPwd.sh";
    public static final String DECRYPT_MYSQL_PASSWORD_FILE = "decryptMYSQLOpenIDMPwd.sh";
    public static final String DECRYPT_OPENDJ_PASSWORD_FILE = "decryptOpenDJAdminPwd.sh";

    public static final String PWD_KEY = "data";
    public static final String PWD_PATTERN = "\"\\\"data\\\" :\"";
    public static final String[] REGEXES = {"^([-._a-zA-Z0-9])*$", "^(?=.*[A-Z])(?=.*[0-9]).{8,}$"};
    public static final String VECTOR_KEY = "iv";
    public static final String VECTOR_PATTERN = "\"\\\"iv\\\" :\"";

    public static final String KEYSTORE_TYPE_PROPERTY = "openidm.keystore.type";
    public static final String CRYPTO_ALIAS_PROPERTY = "openidm.config.crypto.alias";
    public static final String PASSWORD_PROPERTY = "openidm.keystore.password";
}
