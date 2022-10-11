
package com.amazon.sample.events.orders;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Order Created Event
 * <p>
 * Order Created Event payload
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "order"
})
public class OrderCreatedEvent {

    /**
     * Order
     * <p>
     * An order within the store
     * (Required)
     * 
     */
    @JsonProperty("order")
    @JsonPropertyDescription("An order within the store")
    private Order order;

    /**
     * Order
     * <p>
     * An order within the store
     * (Required)
     * 
     */
    @JsonProperty("order")
    public Order getOrder() {
        return order;
    }

    /**
     * Order
     * <p>
     * An order within the store
     * (Required)
     * 
     */
    @JsonProperty("order")
    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(OrderCreatedEvent.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("order");
        sb.append('=');
        sb.append(((this.order == null)?"<null>":this.order));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.order == null)? 0 :this.order.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof OrderCreatedEvent) == false) {
            return false;
        }
        OrderCreatedEvent rhs = ((OrderCreatedEvent) other);
        return ((this.order == rhs.order)||((this.order!= null)&&this.order.equals(rhs.order)));
    }

}
