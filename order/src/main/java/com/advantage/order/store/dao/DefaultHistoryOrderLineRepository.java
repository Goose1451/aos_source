package com.advantage.order.store.dao;

import com.advantage.order.store.model.OrderLines;
import com.advantage.root.util.ArgumentValidationHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Moti Ostrovski on 30/05/2016.
 */
@Component
@Qualifier("historyOrderLineRepository")
@Repository
public class DefaultHistoryOrderLineRepository extends AbstractRepository implements HistoryOrderLineRepository {

    @Override
    public List<OrderLines> getAll() {
        List<OrderLines> lines = new ArrayList<>();
        return null;
    }

    @Override
    public List<OrderLines> getHistoryOrdersLinesByOrderId(long orderId) {
        ArgumentValidationHelper.validateLongArgumentIsPositiveOrZero(orderId, "order id");
        List<OrderLines> lines = new ArrayList<>();

        try {
            List<OrderLines> orderLines = entityManager.createNamedQuery(OrderLines.QUERY_GET_ORDER_LINES_BY_ORDER, OrderLines.class)
                    .setParameter(OrderLines.PARAM_ORDER_NUMBER, orderId)
                    .getResultList();
            return orderLines;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Get history of orders lines by user-id. <br/>
     * This retrieve includes demonstration of <b><i>Memory Leak</b></i> in
     * {@code OrderManagementService} class.
     * @param userId Unique identifier of user account, or 0 for all orders of all users.
     * @return {@link List} or {@link OrderLines}
     */
    @Override
    public List<OrderLines> getHistoryOrdersLinesByUserId(long userId) {
        ArgumentValidationHelper.validateLongArgumentIsPositiveOrZero(userId, "user id");
        List<OrderLines> lines = new ArrayList<>();

        try {
            List<OrderLines> orderLines = entityManager.createNamedQuery(OrderLines.QUERY_GET_ORDERS_LINES_BY_USER_ID, OrderLines.class)
                    .setParameter(OrderLines.PARAM_USER_ID, userId)
                    .getResultList();
            return orderLines;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public List<OrderLines> getHistoryOrdersLinesByOrderIdAndUserId(long orderId, long userId) {
        ArgumentValidationHelper.validateLongArgumentIsPositiveOrZero(userId, "user id");
        ArgumentValidationHelper.validateLongArgumentIsPositiveOrZero(orderId, "order id");

        List<OrderLines> lines = new ArrayList<>();
        return null;
    }

    @Override
    @Transactional
    public List<OrderLines> removeOrder(long userId, long orderId){
        ArgumentValidationHelper.validateLongArgumentIsPositiveOrZero(userId, "user id");
        List<OrderLines> lines = new ArrayList<>();

        try {
            int index = entityManager.createNamedQuery(OrderLines.QUERY_DELETE_ORDERS_LINE_BY_ORDER_PK)
                    .setParameter(OrderLines.PARAM_ORDER_NUMBER, orderId)
                    .executeUpdate();
            List<OrderLines> orderLines = entityManager.createNamedQuery(OrderLines.QUERY_GET_ORDERS_LINES_BY_USER_ID, OrderLines.class)
                    .setParameter(OrderLines.PARAM_USER_ID, userId)
                    .getResultList();
            return orderLines;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public boolean removeAllOrdersLinesForUser(long userId){
        ArgumentValidationHelper.validateLongArgumentIsPositiveOrZero(userId, "user id");
        List<OrderLines> lines = new ArrayList<>();

        try {
            int index = entityManager.createNamedQuery(OrderLines.QUERY_DELETE_ORDERS_LINES_BY_USER_ID_PK)
                    .setParameter(OrderLines.PARAM_USER_ID, userId)
                    .executeUpdate();
            List<OrderLines> orderLines = entityManager.createNamedQuery(OrderLines.QUERY_GET_ORDERS_LINES_BY_USER_ID, OrderLines.class)
                    .setParameter(OrderLines.PARAM_USER_ID, userId)
                    .getResultList();
            if(orderLines.size() == 0 ){
                return true;
            }

            else {return false;}
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
