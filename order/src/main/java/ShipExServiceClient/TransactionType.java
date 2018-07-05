
package ShipExServiceClient;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransactionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransactionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ShippingCost"/>
 *     &lt;enumeration value="PlaceShippingOrder"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TransactionType", namespace = "https://www.AdvantageOnlineBanking.com/ShipEx/")
@XmlEnum
public enum TransactionType {

    @XmlEnumValue("ShippingCost")
    SHIPPING_COST("ShippingCost"),
    @XmlEnumValue("PlaceShippingOrder")
    PLACE_SHIPPING_ORDER("PlaceShippingOrder");
    private final String value;

    TransactionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TransactionType fromValue(String v) {
        for (TransactionType c: TransactionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
