package com.advantage.common;

/**
 * @author Evgeney Fiskin on 31-12-2015.
 */

import org.apache.log4j.Logger;
import org.junit.Before;
import org.mockito.Mock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import java.net.URL;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@PropertySources(value = {@PropertySource(Constants.FILE_PROPERTIES_EXTERNAL)})
public class Url_resources {

    private static URL urlPrefixCatalog;
    private static URL urlPrefixMasterCredit;
    private static URL urlPrefixOrder;
    private static URL urlPrefixSafePay;

    private static URL urlPrefixSoapAccount;
    private static URL urlPrefixSoapShipEx;

    private static final Logger logger = Logger.getLogger(Url_resources.class);
    @Inject
    @Mock
    private Environment environment;



    public void initEnvForTests(){
        environment = mock(Environment.class);
        when(environment.getProperty(Constants.SINGLE_MACHINE_DEPLOYMENT))
                .thenReturn("true");
        when(environment.getProperty("AOS.Domain.url.host"))
                .thenReturn("No");
        when(environment.getProperty("catalog.service.url.port"))
                .thenReturn("8080");
        when(environment.getProperty("catalog.service.url.suffix"))
                .thenReturn("catalog/api/v1");

    }

    public void setConfigForTest(){
        urlPrefixCatalog = generateUrlPrefix("Catalog");
    }

    @Bean
    public int setConfiguration() {

        urlPrefixCatalog = generateUrlPrefix("Catalog");
        urlPrefixMasterCredit = generateUrlPrefix("MasterCredit");
        urlPrefixOrder = generateUrlPrefix("Order");
        urlPrefixSafePay = generateUrlPrefix("SafePay");
        //urlPrefixService = generateUrlPrefix("Service");

        urlPrefixSoapAccount = generateUrlSoapPrefix("Account");
        urlPrefixSoapShipEx = generateUrlSoapPrefix("ShipEx");


        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("Url_resources: ").append(System.lineSeparator());
            sb.append("   Catalog=\'" + getUrlCatalog() + "\'").append(System.lineSeparator());
            sb.append("   MasterCredit=\'" + getUrlMasterCredit() + "\'").append(System.lineSeparator());
            sb.append("   Order=\'" + getUrlOrder() + "\'").append(System.lineSeparator());
            sb.append("   SafePay=\'" + getUrlSafePay() + "\'").append(System.lineSeparator());
            sb.append("   Account (SOAP)=\'" + getUrlSoapAccount() + "\'").append(System.lineSeparator());
            sb.append("   ShipEx (SOAP)=\'" + getUrlSoapShipEx() + "\'");
            logger.info(sb.toString());
        }
        return 1;
    }

    public URL generateUrlPrefix(String serviceName) {
        URL url = null;

        try {
            String schema = Constants.URI_SCHEMA;
            String host = "";

            if(environment.getProperty(Constants.SINGLE_MACHINE_DEPLOYMENT).equals("true")){
                host = "localhost";
            }
            else{
                host = environment.getProperty(serviceName.toLowerCase() + ".service.url.host");
            }

            String isAOSDomain = environment.getProperty("AOS.Domain.url.host");
            int port = isAOSDomain != null && isAOSDomain.equalsIgnoreCase("Yes")
                    ? 80
                    : Integer.parseInt(environment.getProperty(serviceName.toLowerCase() + ".service.url.port"));

            String suffix = '/' + environment.getProperty(serviceName.toLowerCase() + ".service.url.suffix") + "/";

            url = new URL(schema, host, port, suffix);

        } catch (Throwable e) {
            logger.fatal("Wrong properties file", e);
        }
        logger.debug("URL = " + url.toString());
        return url;
    }

    public URL generateUrlSoapPrefix(String serviceName) {

        URL urlWithWsdl = null;

        try {
            String schema = Constants.URI_SCHEMA;
            String host = "";

            if(environment.getProperty(Constants.SINGLE_MACHINE_DEPLOYMENT).equals("true")){
                host = "localhost";
            }
            else{
                host = environment.getProperty(serviceName.toLowerCase() + ".soapservice.url.host");
            }

            int port = Integer.parseInt(environment.getProperty(serviceName.toLowerCase() + ".soapservice.url.port"));
            String suffix = environment.getProperty(serviceName.toLowerCase() + ".soapservice.url.suffix");
            String wsdl = environment.getProperty(serviceName.toLowerCase() + ".soapservice.url.wsdl");
            if (! wsdl.contains("/")) { suffix += '/'; }
            urlWithWsdl = new URL(new URL(schema, host, port, suffix), suffix + wsdl);

        } catch (Throwable e) {
            logger.fatal("Wrong properties file", e);
        }
        logger.debug("URL = " + urlWithWsdl.toString());
        return urlWithWsdl;

    }

    public static URL getUrlSoapAccount() { return urlPrefixSoapAccount; }

    public static URL getUrlCatalog() {
        return urlPrefixCatalog;
    }

    public static URL getUrlMasterCredit() {
        return urlPrefixMasterCredit;
    }

    public static URL getUrlOrder() {
        return urlPrefixOrder;
    }

    public static URL getUrlSafePay() { return urlPrefixSafePay; }

    public static URL getUrlSoapShipEx() { return urlPrefixSoapShipEx; }


//    @Ignore
//    @Test
//    public void ttt() throws URISyntaxException, MalformedURLException {
//        String scheme = "http";
//        String userInfo = null;
//        String host = "www.advantageonlineshopping.com";
//        int port = 8080;
//        String service_path = "/account/";
//        String query = null;
//        String fragment = null;
//
//        URI uri = new URI(scheme, userInfo, host, port, service_path, query, fragment);
//
//
//        URL url = new URL("http", host, port, service_path);
//        URL url2 = new URL(url, "kuku2");
//        URL url3 = new URL(url2, "kuku3/kuku3_1");
//        URL url4 = new URL(url2, "kuku4/kuku4_1/");
//        URL url5 = new URL(url4, "file.wsdl");
//        URL url6 = new URL(url3, "file.wsdl");
//
//        environment.getProperty("evg.fisk.test.property.catalog");
//    }
}
