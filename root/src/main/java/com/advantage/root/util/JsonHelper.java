package com.advantage.root.util;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Binyamin Regev on 10/01/2016.
 */
public class JsonHelper {
    private static final Logger logger = Logger.getLogger(JsonHelper.class);

    public static Map<String, Object> jsonStringToMap(String jsonString) {
        Map<String, Object> jsonMap = new HashMap<>();

        /*
        //  =================================================
        //  Example: MasterCreditResponse for PAYMENT request
        //  =================================================
        String jsonString = "{\"MCTransactionType\":\"PAYMENT\"," +
                "\"MCResponse.Code\":\"Approved\"," +
                "\"MCResponse.Reason\":\"Approved\"," +
                "\"MCRefNumber\":2427754160," +
                "\"TransactionDate\":\"10012016\"}";
        */

        //DynamicDataSet data = new DynamicDataSet(jsonString);
        try {
            ObjectMapper mapper = new ObjectMapper();

            // convert JSON string to Map
            jsonMap = mapper.readValue(jsonString, new TypeReference<Map<String, Object>>(){});
            logger.info(jsonMap);

        } catch (JsonGenerationException e) {
            logger.fatal(e);
        } catch (JsonMappingException e) {
            logger.fatal(e);
        } catch (IOException e) {
            logger.fatal(e);
        }//

        /*
        //  =================================================
        //  Example: MasterCreditResponse for PAYMENT request
        //  =================================================
        System.out.println("MCResponse.Code=\'" + map.get("MCResponse.Code") + "\'");
        System.out.println("MCResponse.Reason=\'" + map.get("MCResponse.Reason") + "\'");
        System.out.println("MCRefNumber=\'" + map.get("MCRefNumber") + "\'");
        System.out.println("TransactionDate=\'" + map.get("TransactionDate") + "\'");

        String responseCode = (String) jsonMap.get("MCResponse.Code");
        String responseReason = (String) jsonMap.get("MCResponse.Reason");
        long refNumber = Long.valueOf(String.valueOf(jsonMap.get("MCRefNumber")));
        String transactionDate = (String) jsonMap.get("TransactionDate");
         */
        return jsonMap;
    }
}
