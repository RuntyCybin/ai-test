package com.cybindev.aidemo.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Herramientas sobre un "sistema de pedidos" simulado en memoria.
 * Muestra que las tools pueden leer y también modificar estado de tu aplicación.
 */
@Component
public class OrderTools {

    public record Order(String id, String customer, String product, String status) {
    }

    private final Map<String, Order> orders = new ConcurrentHashMap<>(Map.of(
            "A-100", new Order("A-100", "Ana", "Teclado mecánico", "ENVIADO"),
            "A-101", new Order("A-101", "Luis", "Monitor 27\"", "PREPARANDO"),
            "A-102", new Order("A-102", "Ana", "Ratón inalámbrico", "ENTREGADO")));

    @Tool(description = "Consulta un pedido por su identificador. Devuelve null si no existe.")
    public Order getOrder(@ToolParam(description = "Identificador del pedido, p. ej. A-100") String orderId) {
        return orders.get(orderId);
    }

    @Tool(description = "Cancela un pedido. Solo se pueden cancelar pedidos en estado PREPARANDO.")
    public String cancelOrder(@ToolParam(description = "Identificador del pedido") String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            return "El pedido " + orderId + " no existe";
        }
        if (!"PREPARANDO".equals(order.status())) {
            return "No se puede cancelar: el pedido está " + order.status();
        }
        orders.put(orderId, new Order(order.id(), order.customer(), order.product(), "CANCELADO"));
        return "Pedido " + orderId + " cancelado";
    }
}
