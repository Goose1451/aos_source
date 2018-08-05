package com.advantage.order.store.dao;

import com.advantage.order.store.model.OrderHeader;
import com.advantage.order.store.model.OrderHeaderPk;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author Moti Ostrovski on 30/05/2016.
 * @author Binyamin Regev on 24/07/2016.
 */
@Component
@Qualifier("historyOrderHeaderRepository")
@Repository
public class DefaultHistoryOrderHeaderRepository extends AbstractRepository implements HistoryOrderHeaderRepository {

    public OrderHeader find(long userId, long orderId) {
        OrderHeaderPk orderHeaderPk = new OrderHeaderPk(userId, orderId);
        OrderHeader orderHeader = entityManager.find(OrderHeader.class, orderHeaderPk);

        return orderHeader;
    }

    @Override
    public List<OrderHeader> getAll() {
        List<OrderHeader> orderHeaders = entityManager.createNamedQuery(OrderHeader.QUERY_GET_All_ORDERS_HISTORY, OrderHeader.class)
                .getResultList();

        return orderHeaders;
    }

    @Override
    public List<OrderHeader> getOrdersHeaderByUserId(long userId) {
        List<OrderHeader> orderHeaders = entityManager.createNamedQuery(OrderHeader.QUERY_GET_ORDERS_BY_USER_ID, OrderHeader.class)
                .setParameter(OrderHeader.PARAM_USER_ID,userId)
                .getResultList();

        return orderHeaders;
    }

    @Override
    public List<OrderHeader> getOrdersHeaderByOrderId(long orderId) {
        List<OrderHeader> orderHeaders = entityManager.createNamedQuery(OrderHeader.QUERY_GET_ORDER_BY_ORDER, OrderHeader.class)
                .setParameter(OrderHeader.PARAM_ORDER_NUMBER,orderId)
                .getResultList();
        return orderHeaders;
    }

    @Override
    public List<OrderHeader> getOrdersHeaderByOrderIdAndUserId(long orderId, long userId) {
        List<OrderHeader> orderHeaders = entityManager.createNamedQuery(OrderHeader.QUERY_GET_ORDER_BY_PK_COLUMNS, OrderHeader.class)
                .setParameter(OrderHeader.PARAM_ORDER_NUMBER,orderId)
                .setParameter(OrderHeader.PARAM_USER_ID, userId)
                .getResultList();
        return orderHeaders;
    }

    @Override
    @Transactional
    public boolean removeOrder(long orderId,long userId){
        int index = entityManager.createNamedQuery(OrderHeader.QUERY_DELETE_ORDER_BY_ORDER_PK)
                .setParameter(OrderHeader.PARAM_ORDER_NUMBER,orderId)
                .executeUpdate();
        return index != 0;
    }

    @Override
    @Transactional
    public boolean removeAllOrdersHeaders(long userId){
        try {
            entityManager.createNamedQuery(OrderHeader.QUERY_DELETE_ORDER_BY_USER_PK)
                    .setParameter(OrderHeader.PARAM_USER_ID, userId)
                    .executeUpdate();
            List<OrderHeader> orderHeaders = entityManager.createNamedQuery(OrderHeader.QUERY_GET_ORDERS_BY_USER_ID, OrderHeader.class)
                    .setParameter(OrderHeader.PARAM_USER_ID, userId)
                    .getResultList();
            if (orderHeaders.size() == 0) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
