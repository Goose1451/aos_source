package com.advantage.common;

public class Constants {
    public static final char AT_SIGN    = '@';
    public static final char COMMA      = ',';
    public static final char DOT        = '.';
    public static final char MODULU     = '%';
    public static final char POWER      = '^';
    public static final char SPACE      = ' ';
    public static final char TAB        = '\t';
    public static final char UNDERSCORE = '_';

    public static final String DOUBLE_SPACES    = "  ";
    public static final String EMPTY_STRING     = "";
    public static final String TRIPLE_SPACES    = "   ";
    public static final String NEW_LINE         = "\n\r";
    public static final String NOT_FOUND        = "NOT FOUND";

    public static final String URI_API = "/api";

    public static final String FILE_PROPERTIES_INTERNAL = "classpath:properties/internal_config_for_env.properties";
    public static final String FILE_PROPERTIES_EXTERNAL = "classpath:properties/services.properties";
    //public static final String FILE_PROPERTIES_EXTERNAL = "file://Z:/Tomcat 8.0 latest/webapps/ROOT/services.properties";
    public static final String FILE_PROPERTIES_GLOBAL = "classpath:properties/global.properties";
    public static final String FILE_PROPERTIES_DEMO_APP = "classpath:/DemoApp.properties";
    public static final String FILE_PROPERTIES_APP = "classpath:/app.properties";
    public static final String FILE_DEMO_APP_CONFIG_XML = "classpath:/DemoAppConfig.xml";

    public final static String AOS_DEMO_APP_VERSION = "aos.demo.version";
    public final static String SINGLE_MACHINE_DEPLOYMENT = "single.machine.deployment";
    public final static String SECURED_DEPLOYMENT = "secured.deployment";

    public final static String ENV_USER_LOGIN_BLOCKING = "user.login.blocking";
    public final static String ENV_ADD_EMAIL_FIELD_TO_LOGIN = "email.address.in.login";
    public final static String ENV_NUMBER_OF_LOGIN_TRIES_BEFORE_BLOCKING = "number.of.login.tries.before.blocking";
    public final static String ENV_PRODUCT_INSTOCK_DEFAULT_VALUE = "product.inStock.default.value";

    public static final String TRANSACTION_TYPE_SHIPPING_COST = "ShippingCost";
    public static final String TRANSACTION_TYPE_PLACE_SHIPPING_ORDER = "PlaceShippingOrder";

    public static final String PROPERTY_IMAGE_MANAGEMENT_REPOSITORY = "advantage.imageManagement.repository";

    public static final String PART_DB_URL_HOST_PARAMNAME = "hibernate.db.url.host";
    public static final String PART_DB_URL_PORT_PARAMNAME = "hibernate.db.url.port";
    public static final String PART_DB_URL_NAME_PARAMNAME = "hibernate.db.name";

    public static final String ENV_DB_URL_PREFIX_PARAMNAME = "db.url.prefix";
    public static final String ENV_DB_URL_QUERY_PARAMNAME = "db.url.query";

    public static final String PART_HIBERNATE_DB_LOGIN_PARAMNAME = "hibernate.db.login";
    public static final String PART_HIBERNATE_DB_PASSWORD_PARAMNAME = "hibernate.db.password";

    public static final String ENV_HIBERNATE_DIALECT_PARAMNAME = "hibernate.dialect";
    public static final String ENV_HIBERNATE_HBM2DDL_AUTO_PARAMNAME = "hibernate.hbm2ddl.auto";
    public static final String ENV_HIBERNATE_SHOW_SQL_PARAMNAME = "hibernate.show_sql";
    public static final String ENV_HIBERNATE_FORMAT_SQL_PARAMNAME = "hibernate.format_sql";

    public static final String URI_SCHEMA = "http";
    public static final String FILE_PROPERTIES_VER_TXT = "classpath:/ver.txt";

    public static final String ENV_HIBERNATE_DB_DRIVER_CLASSNAME_PARAMNAME = "hibernate.db.driver_classname";
    public static final String ENV_LIQUIBASE_FILE_CHANGELOG_PARAMNAME = "liquibase.file.changelog";
    public static final String ENV_ALLOW_USER_CONFIGURATION = "allow.user.configuration";
    public final static String ENV_USER_LOGIN_TIMEOUT = "user.login.timeout";
    public final static String ENV_PRODUCTION_HOST_NAME = "production.host.name";
    public final static String ENV_PRODUCTION_HOST_IP = "production.host.ip";
    public final static String ENV_PRODUCTION_HOST_PORT_ACCOUNTSERVICE = "production.host.port.accountservice";

    public final static String USER_LOGIN_NAME = "admin";
    public final static String USER_LOGIN_PASSWORD = "adm1n";

    public final static String MASTER_CREDIT_IDENTICAL_CREDENTIALS_ERROR = "Expiration date has passed. Try another card.";
    public final static String NO_IMAGE_AVAILABLE_ID = "no_image_available";

    public static class UserSession {
        public static final String TOKEN = "token";
        public static final String IS_SUCCESS = "isSuccess";
        public static final String USER_ID = "userId";
    }

    public static String paramNameAsParam(String paramName) {
        StringBuilder sb = new StringBuilder("${");
        sb.append(paramName);
        sb.append("}");
        return sb.toString();
    }

}
