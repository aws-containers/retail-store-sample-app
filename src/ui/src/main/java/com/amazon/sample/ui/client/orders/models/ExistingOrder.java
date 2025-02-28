package com.amazon.sample.ui.client.orders.models;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@jakarta.annotation.Generated("com.microsoft.kiota")
public class ExistingOrder implements AdditionalDataHolder, Parsable {

  /**
   * Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
   */
  private Map<String, Object> additionalData;
  /**
   * The createdDate property
   */
  private OffsetDateTime createdDate;
  /**
   * The id property
   */
  private String id;
  /**
   * The items property
   */
  private java.util.List<OrderItem> items;
  /**
   * The shippingAddress property
   */
  private ShippingAddress shippingAddress;

  /**
   * Instantiates a new {@link ExistingOrder} and sets the default values.
   */
  public ExistingOrder() {
    this.setAdditionalData(new HashMap<>());
  }

  /**
   * Creates a new instance of the appropriate class based on discriminator value
   * @param parseNode The parse node to use to read the discriminator value and create the object
   * @return a {@link ExistingOrder}
   */
  @jakarta.annotation.Nonnull
  public static ExistingOrder createFromDiscriminatorValue(
    @jakarta.annotation.Nonnull final ParseNode parseNode
  ) {
    Objects.requireNonNull(parseNode);
    return new ExistingOrder();
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
   * Gets the createdDate property value. The createdDate property
   * @return a {@link OffsetDateTime}
   */
  @jakarta.annotation.Nullable
  public OffsetDateTime getCreatedDate() {
    return this.createdDate;
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
    >(4);
    deserializerMap.put("createdDate", n -> {
      this.setCreatedDate(n.getOffsetDateTimeValue());
    });
    deserializerMap.put("id", n -> {
      this.setId(n.getStringValue());
    });
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
   * Gets the id property value. The id property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getId() {
    return this.id;
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
    writer.writeOffsetDateTimeValue("createdDate", this.getCreatedDate());
    writer.writeStringValue("id", this.getId());
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
   * Sets the createdDate property value. The createdDate property
   * @param value Value to set for the createdDate property.
   */
  public void setCreatedDate(
    @jakarta.annotation.Nullable final OffsetDateTime value
  ) {
    this.createdDate = value;
  }

  /**
   * Sets the id property value. The id property
   * @param value Value to set for the id property.
   */
  public void setId(@jakarta.annotation.Nullable final String value) {
    this.id = value;
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
