package com.amazon.sample.ui.client.checkout.models;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@jakarta.annotation.Generated("com.microsoft.kiota")
public class CheckoutRequest implements AdditionalDataHolder, Parsable {

  /**
   * Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
   */
  private Map<String, Object> additionalData;
  /**
   * The deliveryOptionToken property
   */
  private String deliveryOptionToken;
  /**
   * The items property
   */
  private java.util.List<ItemRequest> items;
  /**
   * The shippingAddress property
   */
  private ShippingAddress shippingAddress;

  /**
   * Instantiates a new {@link CheckoutRequest} and sets the default values.
   */
  public CheckoutRequest() {
    this.setAdditionalData(new HashMap<>());
  }

  /**
   * Creates a new instance of the appropriate class based on discriminator value
   * @param parseNode The parse node to use to read the discriminator value and create the object
   * @return a {@link CheckoutRequest}
   */
  @jakarta.annotation.Nonnull
  public static CheckoutRequest createFromDiscriminatorValue(
    @jakarta.annotation.Nonnull final ParseNode parseNode
  ) {
    Objects.requireNonNull(parseNode);
    return new CheckoutRequest();
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
   * Gets the deliveryOptionToken property value. The deliveryOptionToken property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getDeliveryOptionToken() {
    return this.deliveryOptionToken;
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
    >(3);
    deserializerMap.put("deliveryOptionToken", n -> {
      this.setDeliveryOptionToken(n.getStringValue());
    });
    deserializerMap.put("items", n -> {
      this.setItems(
          n.getCollectionOfObjectValues(
            ItemRequest::createFromDiscriminatorValue
          )
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
   * @return a {@link java.util.List<ItemRequest>}
   */
  @jakarta.annotation.Nullable
  public java.util.List<ItemRequest> getItems() {
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
    writer.writeStringValue(
      "deliveryOptionToken",
      this.getDeliveryOptionToken()
    );
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
   * Sets the deliveryOptionToken property value. The deliveryOptionToken property
   * @param value Value to set for the deliveryOptionToken property.
   */
  public void setDeliveryOptionToken(
    @jakarta.annotation.Nullable final String value
  ) {
    this.deliveryOptionToken = value;
  }

  /**
   * Sets the items property value. The items property
   * @param value Value to set for the items property.
   */
  public void setItems(
    @jakarta.annotation.Nullable final java.util.List<ItemRequest> value
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
