package com.advantage.order.store.dao;

import com.advantage.order.store.dto.OrderPaymentInformation;
import com.advantage.order.store.dto.OrderPurchasedProductInformation;
import com.advantage.order.store.dto.OrderShippingInformation;
import com.advantage.order.store.dto.RestoredefaultDBsettingsResponse;

import java.util.List;

/**
 * @author Binyamin Regev on 06/01/2016.
 */
public interface OrderManagementRepository {

    void addUserOrder(long userId, long orderNumber, long orderTimestamp, double totalAmount,
                      OrderShippingInformation orderShippingInformation,
                      OrderPaymentInformation orderPaymentInformation,
                      List<OrderPurchasedProductInformation> purchasedProducts);

    void updateUserOrderTrackingNumber(long userId, long orderNumber, long shippingTrackingNumber);

    RestoredefaultDBsettingsResponse restoreToDefaultDb ();

}
