package com.amazon.sample.ui.client.orders.models;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@jakarta.annotation.Generated("com.microsoft.kiota")
public class Order implements AdditionalDataHolder, Parsable {

  /**
   * Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
   */
  private Map<String, Object> additionalData;
  /**
   * The items property
   */
  private java.util.List<OrderItem> items;
  /**
   * The shippingAddress property
   */
  private ShippingAddress shippingAddress;

  /**
   * Instantiates a new {@link Order} and sets the default values.
   */
  public Order() {
    this.setAdditionalData(new HashMap<>());
  }

  /**
   * Creates a new instance of the appropriate class based on discriminator value
   * @param parseNode The parse node to use to read the discriminator value and create the object
   * @return a {@link Order}
   */
  @jakarta.annotation.Nonnull
  public static Order createFromDiscriminatorValue(
    @jakarta.annotation.Nonnull final ParseNode parseNode
  ) {
    Objects.requireNonNull(parseNode);
    return new Order();
  }

  /**
   * Gets the AdditionalData property value. Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
   * @return a {@link Map<String, Object>}
   */
  @jakarta.annotation.Nonnull
  public Map<String, Object> getAdditionalData() {
    return this.additionalData;
  }

  /**
   * The deserialization information for the current model
   * @return a {@link Map<String, java.util.function.Consumer<ParseNode>>}
   */
  @jakarta.annotation.Nonnull
  public Map<
    String,
    java.util.function.Consumer<ParseNode>
  > getFieldDeserializers() {
    final HashMap<
      String,
      java.util.function.Consumer<ParseNode>
    > deserializerMap = new HashMap<
      String,
      java.util.function.Consumer<ParseNode>
    >(2);
    deserializerMap.put("items", n -> {
      this.setItems(
          n.getCollectionOfObjectValues(OrderItem::createFromDiscriminatorValue)
        );
    });
    deserializerMap.put("shippingAddress", n -> {
      this.setShippingAddress(
          n.getObjectValue(ShippingAddress::createFromDiscriminatorValue)
        );
    });
    return deserializerMap;
  }

  /**
   * Gets the items property value. The items property
   * @return a {@link java.util.List<OrderItem>}
   */
  @jakarta.annotation.Nullable
  public java.util.List<OrderItem> getItems() {
    return this.items;
  }

  /**
   * Gets the shippingAddress property value. The shippingAddress property
   * @return a {@link ShippingAddress}
   */
  @jakarta.annotation.Nullable
  public ShippingAddress getShippingAddress() {
    return this.shippingAddress;
  }

  /**
   * Serializes information the current object
   * @param writer Serialization writer to use to serialize this model
   */
  public void serialize(
    @jakarta.annotation.Nonnull final SerializationWriter writer
  ) {
    Objects.requireNonNull(writer);
    writer.writeCollectionOfObjectValues("items", this.getItems());
    writer.writeObjectValue("shippingAddress", this.getShippingAddress());
    writer.writeAdditionalData(this.getAdditionalData());
  }

  /**
   * Sets the AdditionalData property value. Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
   * @param value Value to set for the AdditionalData property.
   */
  public void setAdditionalData(
    @jakarta.annotation.Nullable final Map<String, Object> value
  ) {
    this.additionalData = value;
  }

  /**
   * Sets the items property value. The items property
   * @param value Value to set for the items property.
   */
  public void setItems(
    @jakarta.annotation.Nullable final java.util.List<OrderItem> value
  ) {
    this.items = value;
  }

  /**
   * Sets the shippingAddress property value. The shippingAddress property
   * @param value Value to set for the shippingAddress property.
   */
  public void setShippingAddress(
    @jakarta.annotation.Nullable final ShippingAddress value
  ) {
    this.shippingAddress = value;
  }
}
