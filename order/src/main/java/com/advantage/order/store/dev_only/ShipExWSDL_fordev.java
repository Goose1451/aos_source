package com.advantage.order.store.dev_only;

import ShipExServiceClient.*;
import com.advantage.common.Constants;
import com.advantage.common.enums.ResponseEnum;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;

import javax.xml.ws.Service;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

/**
 * Created by regevb on 05/01/2016.
 */
public class ShipExWSDL_fordev {
    public static void main(String args[]) throws Exception {

        URL url = new URL("http://localhost:8080/ShipEx/shipex.wsdl");

        Definitions wsdl = new WSDLParser().parse(url.toString());
        StringWriter writer = new StringWriter();

//        String stringWsdlFile = WSDLHelper.getWsdlFile(url);
//        System.out.println("WSDL File=\'" + stringWsdlFile + "\'");

        QName serviceName = new QName("https://www.AdvantageOnlineBanking.com/ShipEx/", "ShipExPortService");
        QName portName = new QName("https://www.AdvantageOnlineBanking.com/ShipEx/", "ShipExPortSoap11");

        //  *************************************************
        SEAddress address = new SEAddress();
        ShippingCostRequest costRequest = new ShippingCostRequest();

        address.setAddressLine1("address");
        address.setCity("Jerusalem");
        address.setCountry("IL");
        address.setPostalCode("123123");
        address.setState("Israel");

        costRequest.setSEAddress(address);
        costRequest.setSETransactionType(ShippingCostTransactionType.SHIPPING_COST);
        costRequest.setSECustomerName("Customer Full Name");
        costRequest.setSECustomerPhone("+972777654321");
        costRequest.setSENumberOfProducts(1);

        URL urlWsdlLocation = new URL("http://localhost:8080/ShipEx/shipex.wsdl");

        //ShipExPortService shipExPortService = new ShipExPortService(urlWsdlLocation, serviceName);
        ShipExPortService shipExPortService = new ShipExPortService(urlWsdlLocation);

        ShipExPort shipExPort = shipExPortService.getShipExPortSoap11();

        ShippingCostResponse costResponse = shipExPort.shippingCost(costRequest);

        System.out.println("costResponse.getCode()=\'" + costResponse.getCode() + "\'");

        if (!costResponse.getCode().equalsIgnoreCase(ResponseEnum.OK.getStringCode())) {
            //  Failure - Response code IS NOT "OK"
            System.out.println("Response returned Failure. Reason: \'" + costResponse.getReason() + "\'");
        }
        else if (costResponse.getAmount().isEmpty()) {
            //  Failure - invalid amount (empty)
            System.out.println("Failure - invalid amount (empty)");
        }
        else if (costResponse.getCurrency().isEmpty()) {
            //  Failure - invalid currency (empty)
            System.out.println("Failure - invalid currency (empty)");
        }
        else if (costResponse.getSETransactionType().value().equalsIgnoreCase(costRequest.getSETransactionType().value())) {
            //  Failure - Transaction type mismatch
            System.out.println("Failure - Transaction type mismatch");
        }
        else {
            System.out.println("Successful");
        }

    }

}
