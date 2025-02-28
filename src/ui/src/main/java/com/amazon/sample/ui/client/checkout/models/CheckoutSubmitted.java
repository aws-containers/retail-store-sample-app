package com.amazon.sample.ui.client.checkout.models;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@jakarta.annotation.Generated("com.microsoft.kiota")
public class CheckoutSubmitted implements AdditionalDataHolder, Parsable {

  /**
   * Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
   */
  private Map<String, Object> additionalData;
  /**
   * The email property
   */
  private String email;
  /**
   * The items property
   */
  private java.util.List<Item> items;
  /**
   * The orderId property
   */
  private String orderId;
  /**
   * The shipping property
   */
  private Integer shipping;
  /**
   * The subtotal property
   */
  private Integer subtotal;
  /**
   * The tax property
   */
  private Integer tax;
  /**
   * The total property
   */
  private Integer total;

  /**
   * Instantiates a new {@link CheckoutSubmitted} and sets the default values.
   */
  public CheckoutSubmitted() {
    this.setAdditionalData(new HashMap<>());
  }

  /**
   * Creates a new instance of the appropriate class based on discriminator value
   * @param parseNode The parse node to use to read the discriminator value and create the object
   * @return a {@link CheckoutSubmitted}
   */
  @jakarta.annotation.Nonnull
  public static CheckoutSubmitted createFromDiscriminatorValue(
    @jakarta.annotation.Nonnull final ParseNode parseNode
  ) {
    Objects.requireNonNull(parseNode);
    return new CheckoutSubmitted();
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
   * Gets the email property value. The email property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getEmail() {
    return this.email;
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
    >(7);
    deserializerMap.put("email", n -> {
      this.setEmail(n.getStringValue());
    });
    deserializerMap.put("items", n -> {
      this.setItems(
          n.getCollectionOfObjectValues(Item::createFromDiscriminatorValue)
        );
    });
    deserializerMap.put("orderId", n -> {
      this.setOrderId(n.getStringValue());
    });
    deserializerMap.put("shipping", n -> {
      this.setShipping(n.getIntegerValue());
    });
    deserializerMap.put("subtotal", n -> {
      this.setSubtotal(n.getIntegerValue());
    });
    deserializerMap.put("tax", n -> {
      this.setTax(n.getIntegerValue());
    });
    deserializerMap.put("total", n -> {
      this.setTotal(n.getIntegerValue());
    });
    return deserializerMap;
  }

  /**
   * Gets the items property value. The items property
   * @return a {@link java.util.List<Item>}
   */
  @jakarta.annotation.Nullable
  public java.util.List<Item> getItems() {
    return this.items;
  }

  /**
   * Gets the orderId property value. The orderId property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getOrderId() {
    return this.orderId;
  }

  /**
   * Gets the shipping property value. The shipping property
   * @return a {@link Integer}
   */
  @jakarta.annotation.Nullable
  public Integer getShipping() {
    return this.shipping;
  }

  /**
   * Gets the subtotal property value. The subtotal property
   * @return a {@link Integer}
   */
  @jakarta.annotation.Nullable
  public Integer getSubtotal() {
    return this.subtotal;
  }

  /**
   * Gets the tax property value. The tax property
   * @return a {@link Integer}
   */
  @jakarta.annotation.Nullable
  public Integer getTax() {
    return this.tax;
  }

  /**
   * Gets the total property value. The total property
   * @return a {@link Integer}
   */
  @jakarta.annotation.Nullable
  public Integer getTotal() {
    return this.total;
  }

  /**
   * Serializes information the current object
   * @param writer Serialization writer to use to serialize this model
   */
  public void serialize(
    @jakarta.annotation.Nonnull final SerializationWriter writer
  ) {
    Objects.requireNonNull(writer);
    writer.writeStringValue("email", this.getEmail());
    writer.writeCollectionOfObjectValues("items", this.getItems());
    writer.writeStringValue("orderId", this.getOrderId());
    writer.writeIntegerValue("shipping", this.getShipping());
    writer.writeIntegerValue("subtotal", this.getSubtotal());
    writer.writeIntegerValue("tax", this.getTax());
    writer.writeIntegerValue("total", this.getTotal());
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
   * Sets the email property value. The email property
   * @param value Value to set for the email property.
   */
  public void setEmail(@jakarta.annotation.Nullable final String value) {
    this.email = value;
  }

  /**
   * Sets the items property value. The items property
   * @param value Value to set for the items property.
   */
  public void setItems(
    @jakarta.annotation.Nullable final java.util.List<Item> value
  ) {
    this.items = value;
  }

  /**
   * Sets the orderId property value. The orderId property
   * @param value Value to set for the orderId property.
   */
  public void setOrderId(@jakarta.annotation.Nullable final String value) {
    this.orderId = value;
  }

  /**
   * Sets the shipping property value. The shipping property
   * @param value Value to set for the shipping property.
   */
  public void setShipping(@jakarta.annotation.Nullable final Integer value) {
    this.shipping = value;
  }

  /**
   * Sets the subtotal property value. The subtotal property
   * @param value Value to set for the subtotal property.
   */
  public void setSubtotal(@jakarta.annotation.Nullable final Integer value) {
    this.subtotal = value;
  }

  /**
   * Sets the tax property value. The tax property
   * @param value Value to set for the tax property.
   */
  public void setTax(@jakarta.annotation.Nullable final Integer value) {
    this.tax = value;
  }

  /**
   * Sets the total property value. The total property
   * @param value Value to set for the total property.
   */
  public void setTotal(@jakarta.annotation.Nullable final Integer value) {
    this.total = value;
  }
}
