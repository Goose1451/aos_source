
package ShipExServiceClient;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PlaceOrderTransactionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PlaceOrderTransactionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PlaceShippingOrder"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PlaceOrderTransactionType", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/")
@XmlEnum
public enum PlaceOrderTransactionType {

    @XmlEnumValue("PlaceShippingOrder")
    PLACE_SHIPPING_ORDER("PlaceShippingOrder");
    private final String value;

    PlaceOrderTransactionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PlaceOrderTransactionType fromValue(String v) {
        for (PlaceOrderTransactionType c: PlaceOrderTransactionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
