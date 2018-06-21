package com.advantage.common_test.online.store.util;

import com.advantage.common.Constants;
import com.advantage.common_test.cfg.AdvantageTestContextConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AdvantageTestContextConfiguration.class})
public class PropertiesFilesTests {
    @Autowired
    private Environment environment;

    @Test
    public void testExternalFilePropertyName() throws Exception {
        String[] properties = {
                "account.soapservice.url.host",
                "account.soapservice.url.port",
                "account.soapservice.url.suffix",
                "account.soapservice.url.wsdl",

                "catalog.service.url.host",
                "catalog.service.url.port",
                "catalog.service.url.suffix",

                "order.service.url.host",
                "order.service.url.port",
                "order.service.url.suffix",

                "mastercredit.service.url.host",
                "mastercredit.service.url.port",
                "mastercredit.service.url.suffix",

                "shipex.soapservice.url.host",
                "shipex.soapservice.url.port",
                "shipex.soapservice.url.suffix",
                "shipex.soapservice.url.wsdl",

                "safepay.service.url.host",
                "safepay.service.url.port",
                "safepay.service.url.suffix"
        };
        validatePropertiesName(properties, Constants.FILE_PROPERTIES_EXTERNAL);
    }

    @Test
    public void testInternalFilePropertyName() throws Exception {
        String[] properties = {
                "account.hibernate.db.url.host",
                "account.hibernate.db.url.port",
                "account.hibernate.db.name",
                "account.hibernate.db.login",
                "account.hibernate.db.password",

                "catalog.hibernate.db.url.host",
                "catalog.hibernate.db.url.port",
                "catalog.hibernate.db.name",
                "catalog.hibernate.db.login",
                "catalog.hibernate.db.password",

                "order.hibernate.db.url.host",
                "order.hibernate.db.url.port",
                "order.hibernate.db.name",
                "order.hibernate.db.login",
                "order.hibernate.db.password"
        };
        validatePropertiesName(properties, Constants.FILE_PROPERTIES_INTERNAL);
    }

//    @Test
//    public void testPropertiesPorts() throws Exception {
//        String[] properties = {
//                "account.hibernate.db.url.port",
//                "catalog.hibernate.db.url.port",
//                "order.hibernate.db.url.port",
//                "account.soapservice.url.port",
//                "catalog.service.url.port",
//                "order.service.url.port",
//                "mastercredit.service.url.port",
//                "shipex.soapservice.url.port",
//                "safepay.service.url.port"
//        };
//        validatePropertiesAreNaturalNumber(properties);
//        validatePropertiesArePorts(properties);
//    }

    @Test
    public void testGlobalProperties() throws Exception {
        String[] properties = {
//                "hibernate.format_sql",
//                "hibernate.show_sql",
//                "hibernate.hbm2ddl.auto",
                Constants.ENV_HIBERNATE_DIALECT_PARAMNAME,
                Constants.ENV_HIBERNATE_DB_DRIVER_CLASSNAME_PARAMNAME,

                "db.url.prefix",
                "db.url.query"
        };
        validatePropertiesName(properties, Constants.FILE_PROPERTIES_GLOBAL);
    }

    private void validatePropertiesName(String[] properties, String fileProperties) throws Exception {
        for (String propertyName : properties) {
            String propertyValue = environment.getProperty(propertyName);
            if (!environment.containsProperty(propertyName))
                throw new Exception("Property file " + fileProperties + " doesn't contains property \"" + propertyName + "\"");
            if (propertyValue.isEmpty()) {
                throw new Exception("In property file " + fileProperties + "the property \"" + propertyName + "\" has empty value");
            }
        }
    }

    private void validatePropertiesAreNaturalNumber(String[] properties) throws Exception {
        for (String propertyName : properties) {
            String propertyValue = environment.getProperty(propertyName);
            if (!propertyValue.matches("^\\d+$")) {
                throw new Exception("The property \"" + propertyName + "\" have value \"" + propertyValue + "\" and isn't natural number");
            }
        }
    }

    private void validatePropertiesArePorts(String[] properties) throws Exception {
        for (String propertyName : properties) {
            String propertyValue = environment.getProperty(propertyName);
            int tmp = Integer.parseInt(propertyValue);
            if (tmp < 1 || tmp > 65535) {
                throw new Exception("The property \"" + propertyName + "\" have value \"" + propertyValue + "\" and isn't correct port number");
            }
        }
    }
}
