package com.amazon.sample.ui.client.checkout.models;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@jakarta.annotation.Generated("com.microsoft.kiota")
public class ShippingOption implements AdditionalDataHolder, Parsable {

  /**
   * Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
   */
  private Map<String, Object> additionalData;
  /**
   * The amount property
   */
  private Integer amount;
  /**
   * The estimatedDays property
   */
  private Integer estimatedDays;
  /**
   * The name property
   */
  private String name;
  /**
   * The token property
   */
  private String token;

  /**
   * Instantiates a new {@link ShippingOption} and sets the default values.
   */
  public ShippingOption() {
    this.setAdditionalData(new HashMap<>());
  }

  /**
   * Creates a new instance of the appropriate class based on discriminator value
   * @param parseNode The parse node to use to read the discriminator value and create the object
   * @return a {@link ShippingOption}
   */
  @jakarta.annotation.Nonnull
  public static ShippingOption createFromDiscriminatorValue(
    @jakarta.annotation.Nonnull final ParseNode parseNode
  ) {
    Objects.requireNonNull(parseNode);
    return new ShippingOption();
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
   * Gets the amount property value. The amount property
   * @return a {@link Integer}
   */
  @jakarta.annotation.Nullable
  public Integer getAmount() {
    return this.amount;
  }

  /**
   * Gets the estimatedDays property value. The estimatedDays property
   * @return a {@link Integer}
   */
  @jakarta.annotation.Nullable
  public Integer getEstimatedDays() {
    return this.estimatedDays;
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
    deserializerMap.put("amount", n -> {
      this.setAmount(n.getIntegerValue());
    });
    deserializerMap.put("estimatedDays", n -> {
      this.setEstimatedDays(n.getIntegerValue());
    });
    deserializerMap.put("name", n -> {
      this.setName(n.getStringValue());
    });
    deserializerMap.put("token", n -> {
      this.setToken(n.getStringValue());
    });
    return deserializerMap;
  }

  /**
   * Gets the name property value. The name property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getName() {
    return this.name;
  }

  /**
   * Gets the token property value. The token property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getToken() {
    return this.token;
  }

  /**
   * Serializes information the current object
   * @param writer Serialization writer to use to serialize this model
   */
  public void serialize(
    @jakarta.annotation.Nonnull final SerializationWriter writer
  ) {
    Objects.requireNonNull(writer);
    writer.writeIntegerValue("amount", this.getAmount());
    writer.writeIntegerValue("estimatedDays", this.getEstimatedDays());
    writer.writeStringValue("name", this.getName());
    writer.writeStringValue("token", this.getToken());
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
   * Sets the amount property value. The amount property
   * @param value Value to set for the amount property.
   */
  public void setAmount(@jakarta.annotation.Nullable final Integer value) {
    this.amount = value;
  }

  /**
   * Sets the estimatedDays property value. The estimatedDays property
   * @param value Value to set for the estimatedDays property.
   */
  public void setEstimatedDays(
    @jakarta.annotation.Nullable final Integer value
  ) {
    this.estimatedDays = value;
  }

  /**
   * Sets the name property value. The name property
   * @param value Value to set for the name property.
   */
  public void setName(@jakarta.annotation.Nullable final String value) {
    this.name = value;
  }

  /**
   * Sets the token property value. The token property
   * @param value Value to set for the token property.
   */
  public void setToken(@jakarta.annotation.Nullable final String value) {
    this.token = value;
  }
}
