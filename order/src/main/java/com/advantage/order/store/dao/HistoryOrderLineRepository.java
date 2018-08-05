package com.advantage.order.store.dao;

import com.advantage.order.store.model.OrderLines;

import java.util.List;

/**
 * @author Moti Ostrovski on 30/05/2016.
 */
//public interface HistoryOrderLineRepository extends DefaultCRUDOperations<OrderLines>{
public interface HistoryOrderLineRepository {

    List<OrderLines> getAll();

    List<OrderLines> getHistoryOrdersLinesByOrderId(long orderId);

    List<OrderLines> getHistoryOrdersLinesByUserId(long userId);

    List<OrderLines> getHistoryOrdersLinesByOrderIdAndUserId(long orderId,long userId);

    List<OrderLines> removeOrder(long userId, long orderId);

    boolean removeAllOrdersLinesForUser (long userId);


}
