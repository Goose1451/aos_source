package com.advantage.common.utils;

import com.advantage.common.Url_resources;
import com.advantage.common.dto.AppUserDto;
import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;

import javax.xml.soap.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by dubilyer on 8/30/2016.
 */

public class SoapApiHelper {

    private static final Logger logger = Logger.getLogger(SoapApiHelper.class);

    private final static String REQUEST_NAME_SPACE = "com";
    private final static String RESPONSE_NAME_SPACE = "ns2";
    private final static String DESTINATION = "com.advantage.online.store.accountservice";
    //// TODO-Benny: Remove the line of code "public final static String URL..."
    //public final static String URL = "http://localhost:8080/accountservice/AccountLoginRequest";
    private static SOAPConnection soapConnection;

    /**
     * The method returns soap message ready to be sent.
     * @param requestName -                     e.g. "GetAccountByIdRequest"
     * @param data -                            Map of request parameters. Key - tag name,
     *                                          value - parameter value
     * @return SOAPMessage
     * @throws SOAPException
     */
    private static SOAPMessage createSOAPRequest(String requestName, HashMap<String, String> data) throws SOAPException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        if(requestName.contains("HealthCheck"))
            envelope.addNamespaceDeclaration(REQUEST_NAME_SPACE, "https://www.AdvantageOnlineBanking.com/ShipEx/");
        else
            envelope.addNamespaceDeclaration(REQUEST_NAME_SPACE, DESTINATION);
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement(requestName, REQUEST_NAME_SPACE);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            soapBodyElem
                    .addChildElement(entry.getKey(), REQUEST_NAME_SPACE)
                    .addTextNode(entry.getValue());
        }
        soapMessage.saveChanges();
        return soapMessage;
    }

    /**
     * The method is to send a previously generated SOAPMessage. Attention! You
     * should close soapConnection after using the method if not closed inside!
     * @param request                                               SOAPMessage
     * @return                                             SOAPMessage responce
     * @throws SOAPException
     */
    private static synchronized SOAPMessage sendSoapMessage(SOAPMessage request, String url) throws SOAPException {
        URL accountServiceUrl = null;
        URL accountServicePrefixUrl = null;
        try {
            accountServiceUrl = Url_resources.getUrlSoapAccount();
            logger.debug("accountServiceUrl=\"" + accountServiceUrl.toString() + "\"");
            //accountServiceUrl="http://localhost:8080/accountservice/accountservice.wsdl"
            System.out.println("accountServiceUrl=\"" + accountServiceUrl.toString() + "\"");

            String urlString = accountServiceUrl.toString().replace("/accountservice.wsdl", "/AccountLoginRequest");
            if(url.equals(""))
                accountServicePrefixUrl = new URL(urlString);
            else
                accountServicePrefixUrl = new URL(url);
            logger.debug("accountServicePrefixUrl=\"" + accountServicePrefixUrl.toString() + "\"");
            System.out.println("accountServicePrefixUrl=\"" + accountServicePrefixUrl + "\"");
        } catch (MalformedURLException e) {
            logger.error(accountServicePrefixUrl, e);
        }

        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        soapConnection = soapConnectionFactory.createConnection();
        //logger.warn("sendSoapMessage request: " + convertSoapMessageToString(request));
        SOAPMessage result = soapConnection.call(request, accountServicePrefixUrl);
        soapConnection.close();
        //logger.warn("sendSoapMessage response: " + convertSoapMessageToString(result));
        return result;
    }

    /**
     * Function converts SOAPMessage object to string. Goal to use for debugging
     *
     * @param soapMessage
     * @return
     */
    private static String convertSoapMessageToString(SOAPMessage soapMessage) {
        String soapMessageStr = "";
        ByteArrayOutputStream baos = null;
        try
        {
            baos = new ByteArrayOutputStream();
            soapMessage.writeTo(baos);
            soapMessageStr = baos.toString();
        } catch (Exception e) {
        } finally {
            if (baos != null)
            {
                try {
                    baos.close();
                } catch (IOException ioe) {
                }
            }
        }
        return soapMessageStr;
    }

    /**
     * Returns xml parent node of all response parameters
     * @param soapResponse                      SOAPMessage
     * @param responseName                 name of Response
     * @return                                     Nodelist
     * @throws SOAPException
     */
    private static NodeList getRoot(SOAPMessage soapResponse, String responseName) throws SOAPException {
        SOAPEnvelope env = soapResponse.getSOAPPart().getEnvelope();
        SOAPBody sb = env.getBody();
        return sb.getElementsByTagName(RESPONSE_NAME_SPACE+":"+responseName).item(0).getChildNodes();
    }

    /**
     * Returns parameter from response according to the given node name
     * @param nodeName                                     String value
     * @param root                                      Parent nodelist
     * @return                                String value of parameter
     */
    private static String getResponseValue(String nodeName, NodeList root){
        for (int i = 0; i < root.getLength(); i++) {
            if(root.item(i).getNodeName().equals(RESPONSE_NAME_SPACE+":"+nodeName)){
                return root.item(i).getTextContent();
            }
        }
        throw new NoSuchElementException("There's no parameter "+nodeName + "in response.");
    }

    public static AppUserDto getUserByLogin(String userName, String base64Token) throws SOAPException  {
        HashMap<String, String> data = new HashMap<>();
        data.put("userName", userName);
        data.put("base64Token", base64Token);
        SOAPMessage soapResponse = sendSoapMessage(createSOAPRequest("GetAccountByLoginRequest", data), "");
        NodeList root = getRoot(soapResponse, "AccountResponse");
        String userPassword = getResponseValue("loginPassword", root);
        int accountType = Integer.valueOf(getResponseValue("accountType", root));
        long userId = Long.valueOf(getResponseValue("id", root));
        return new AppUserDto(userName, userPassword, userId, accountType);
    }

    //no Auth token required
    public static AppUserDto getUserById(long userId) throws SOAPException  {
        HashMap<String, String> data = new HashMap<>();
        data.put("accountId", Long.toString(userId));
        SOAPMessage soapResponse = sendSoapMessage(createSOAPRequest("GetAccountByIdRequest", data), "");
        NodeList root = getRoot(soapResponse, "AccountResponse");
        String userPassword = getResponseValue("loginPassword", root);
        int accountType = Integer.valueOf(getResponseValue("accountType", root));
        String userName = getResponseValue("loginName", root);
        return new AppUserDto(userName, userPassword, userId, accountType);
    }

    public static NodeList doLogin(String userName, String password) throws SOAPException  {
        HashMap<String, String> data = new HashMap<>();
        data.put("loginUser", userName);
        data.put("email", "");
        data.put("loginPassword", password);
        SOAPMessage soapResponse = sendSoapMessage(createSOAPRequest("AccountLoginRequest", data), "");
        NodeList root = getRoot(soapResponse, "StatusMessage");

        String success = getResponseValue("success", root);
        if(success.equals("false"))
            return null;
        if(success.equals("true")){
            return root;
        }

//        return new AppUserDto(userName, userPassword, userId, accountType);
        return null;
    }

    public static AppUserDto changeUserName(String[] currentUser, String[] newUser) throws SOAPException{
        return null;
    }

    public static boolean changeUserPassword(String[] currentUserDetails, NodeList loginResponse, String newPassword) throws SOAPException{
        String basicToken = getResponseValue("t_authorization", loginResponse);
        AppUserDto user = getUserByLogin(currentUserDetails[0], basicToken);


        HashMap<String, String> data = new HashMap<>();
        data.put("oldPassword", currentUserDetails[1]);
        data.put("newPassword", newPassword);
        data.put("base64Token", "Basic " + basicToken);
        data.put("accountId", Long.toString(user.getUserId()));
        SOAPMessage soapResponse = sendSoapMessage(createSOAPRequest("ChangePasswordRequest", data), "");
        NodeList root = getRoot(soapResponse, "StatusMessage");

        return getResponseValue("success", root).equals("true") ? true : false;
    }

    public static String encodePassword(String userName, String password, long userId, String base64Token) throws SOAPException {
        HashMap<String, String> data = new HashMap<>();
        data.put("userName", userName);
        data.put("password", password);
        data.put("base64Token", base64Token);
        data.put("accountId", Long.toString(userId));
        SOAPMessage soapResponse = sendSoapMessage(createSOAPRequest("EncodePasswordRequest", data), "");
        NodeList root = getRoot(soapResponse, "EncodePasswordResponse");
        return getResponseValue("password", root);
    }

    public static Boolean deleteAccount(String userName, NodeList loginResponse) throws SOAPException{
        String basicToken = getResponseValue("t_authorization", loginResponse);
        AppUserDto user = getUserByLogin(userName, basicToken);

        HashMap<String, String> data = new HashMap<>();
        data.put("base64Token", basicToken);
        data.put("accountId", Long.toString(user.getUserId()));

        SOAPMessage soapResponse = sendSoapMessage(createSOAPRequest("AccountDeleteRequest", data), "");
        NodeList root = getRoot(soapResponse, "StatusMessage");
        return root != null && getResponseValue("success", root).equals("true") ? true : false;
    }

    public static boolean createUser(String[] newUserDetails) throws SOAPException{
        HashMap<String, String> data = getUserAccountData(newUserDetails);
        SOAPMessage soapResponse = sendSoapMessage(createSOAPRequest("AccountCreateRequest", data), "");
        NodeList root = getRoot(soapResponse, "StatusMessage");
        return true;
    }

    private static HashMap<String, String> getUserAccountData(String[] newUserDetails) {
        HashMap<String, String> data = new HashMap<>();
        data.put("lastName", "Brown");
        data.put("firstName", "John");
        data.put("loginName", newUserDetails[0]);
        data.put("accountType", "10");
        data.put("countryId", "40");
        data.put("stateProvince", "MA");
        data.put("cityName", "Newton");
        data.put("zipcode", "02458");
        data.put("address", "826 Morseland Ave.");
        data.put("phoneNumber", "617-527-5555");
        data.put("email", "AppPlusedemo@aos.ad");
        data.put("defaultPaymentMethodId", "10");
        data.put("allowOffersPromotion", "true");
        data.put("internalUnsuccessfulLoginAttempts", "");
        data.put("internalUserBlockedFromLoginUntil", "");
        data.put("internalLastSuccesssulLogin", "");
        data.put("password", newUserDetails[1]);
//        session.persist(new Account(AccountType.ADMIN.getAccountTypeCode(), "Brown", "John", "AppPulse", "AppPulse1", countryMap.get(40L), "617-527-5555", "MA", "Newton", "826 Morseland Ave.", "02458", "AppPlusedemo@aos.ad", true));
        return data;
    }

    public static boolean getHealthCheck() throws SOAPException {
        HashMap<String, String> data = new HashMap<>();

        URL shipexUrl = null;
        shipexUrl = Url_resources.getUrlSoapShipEx();
        //accountServiceUrl="http://localhost:8080/accountservice/accountservice.wsdl"


        String urlString = shipexUrl.toString().replace("/shipex.wsdl", "/GetHealthCheckRequest");

        SOAPMessage soapResponse = sendSoapMessage(createSOAPRequest("GetHealthCheckRequest", data), urlString);
        NodeList root = getRoot(soapResponse, "GetHealthCheckResponse");

        String status = getResponseValue("status", root);
        if (status.equals("success"))
            return true;
        return false;
    }
}

