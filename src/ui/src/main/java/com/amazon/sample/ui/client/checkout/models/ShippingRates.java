package com.amazon.sample.ui.client.checkout.models;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@jakarta.annotation.Generated("com.microsoft.kiota")
public class ShippingRates implements AdditionalDataHolder, Parsable {

  /**
   * Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
   */
  private Map<String, Object> additionalData;
  /**
   * The rates property
   */
  private java.util.List<ShippingOption> rates;
  /**
   * The shipmentId property
   */
  private String shipmentId;

  /**
   * Instantiates a new {@link ShippingRates} and sets the default values.
   */
  public ShippingRates() {
    this.setAdditionalData(new HashMap<>());
  }

  /**
   * Creates a new instance of the appropriate class based on discriminator value
   * @param parseNode The parse node to use to read the discriminator value and create the object
   * @return a {@link ShippingRates}
   */
  @jakarta.annotation.Nonnull
  public static ShippingRates createFromDiscriminatorValue(
    @jakarta.annotation.Nonnull final ParseNode parseNode
  ) {
    Objects.requireNonNull(parseNode);
    return new ShippingRates();
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
    deserializerMap.put("rates", n -> {
      this.setRates(
          n.getCollectionOfObjectValues(
            ShippingOption::createFromDiscriminatorValue
          )
        );
    });
    deserializerMap.put("shipmentId", n -> {
      this.setShipmentId(n.getStringValue());
    });
    return deserializerMap;
  }

  /**
   * Gets the rates property value. The rates property
   * @return a {@link java.util.List<ShippingOption>}
   */
  @jakarta.annotation.Nullable
  public java.util.List<ShippingOption> getRates() {
    return this.rates;
  }

  /**
   * Gets the shipmentId property value. The shipmentId property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getShipmentId() {
    return this.shipmentId;
  }

  /**
   * Serializes information the current object
   * @param writer Serialization writer to use to serialize this model
   */
  public void serialize(
    @jakarta.annotation.Nonnull final SerializationWriter writer
  ) {
    Objects.requireNonNull(writer);
    writer.writeCollectionOfObjectValues("rates", this.getRates());
    writer.writeStringValue("shipmentId", this.getShipmentId());
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
   * Sets the rates property value. The rates property
   * @param value Value to set for the rates property.
   */
  public void setRates(
    @jakarta.annotation.Nullable final java.util.List<ShippingOption> value
  ) {
    this.rates = value;
  }

  /**
   * Sets the shipmentId property value. The shipmentId property
   * @param value Value to set for the shipmentId property.
   */
  public void setShipmentId(@jakarta.annotation.Nullable final String value) {
    this.shipmentId = value;
  }
}
