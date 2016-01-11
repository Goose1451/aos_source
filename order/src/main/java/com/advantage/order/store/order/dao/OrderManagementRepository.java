package com.advantage.order.store.order.dao;

import com.advantage.order.store.order.dto.OrderPaymentInformation;
import com.advantage.order.store.order.dto.OrderPurchasedProductInformation;
import com.advantage.order.store.order.dto.OrderShippingInformation;

import java.util.List;

/**
 * @author Binyamin Regev on 06/01/2016.
 */
public interface OrderManagementRepository {

    void addUserOrder(long userId, long orderNumber, long orderTimestamp,
                      OrderShippingInformation orderShippingInformation,
                      OrderPaymentInformation orderPaymentInformation,
                      List<OrderPurchasedProductInformation> purchasedProducts);

    void updateUserOrderTrackingNumber(long userId, long orderNumber, long shippingTrackingNumber);

}
