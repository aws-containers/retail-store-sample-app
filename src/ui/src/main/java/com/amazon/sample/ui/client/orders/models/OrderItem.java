package com.amazon.sample.ui.client.orders.models;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@jakarta.annotation.Generated("com.microsoft.kiota")
public class OrderItem implements AdditionalDataHolder, Parsable {

  /**
   * Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
   */
  private Map<String, Object> additionalData;
  /**
   * The productId property
   */
  private String productId;
  /**
   * The quantity property
   */
  private Integer quantity;
  /**
   * The totalCost property
   */
  private Integer totalCost;
  /**
   * The unitCost property
   */
  private Integer unitCost;

  /**
   * Instantiates a new {@link OrderItem} and sets the default values.
   */
  public OrderItem() {
    this.setAdditionalData(new HashMap<>());
  }

  /**
   * Creates a new instance of the appropriate class based on discriminator value
   * @param parseNode The parse node to use to read the discriminator value and create the object
   * @return a {@link OrderItem}
   */
  @jakarta.annotation.Nonnull
  public static OrderItem createFromDiscriminatorValue(
    @jakarta.annotation.Nonnull final ParseNode parseNode
  ) {
    Objects.requireNonNull(parseNode);
    return new OrderItem();
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
    >(4);
    deserializerMap.put("productId", n -> {
      this.setProductId(n.getStringValue());
    });
    deserializerMap.put("quantity", n -> {
      this.setQuantity(n.getIntegerValue());
    });
    deserializerMap.put("totalCost", n -> {
      this.setTotalCost(n.getIntegerValue());
    });
    deserializerMap.put("unitCost", n -> {
      this.setUnitCost(n.getIntegerValue());
    });
    return deserializerMap;
  }

  /**
   * Gets the productId property value. The productId property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getProductId() {
    return this.productId;
  }

  /**
   * Gets the quantity property value. The quantity property
   * @return a {@link Integer}
   */
  @jakarta.annotation.Nullable
  public Integer getQuantity() {
    return this.quantity;
  }

  /**
   * Gets the totalCost property value. The totalCost property
   * @return a {@link Integer}
   */
  @jakarta.annotation.Nullable
  public Integer getTotalCost() {
    return this.totalCost;
  }

  /**
   * Gets the unitCost property value. The unitCost property
   * @return a {@link Integer}
   */
  @jakarta.annotation.Nullable
  public Integer getUnitCost() {
    return this.unitCost;
  }

  /**
   * Serializes information the current object
   * @param writer Serialization writer to use to serialize this model
   */
  public void serialize(
    @jakarta.annotation.Nonnull final SerializationWriter writer
  ) {
    Objects.requireNonNull(writer);
    writer.writeStringValue("productId", this.getProductId());
    writer.writeIntegerValue("quantity", this.getQuantity());
    writer.writeIntegerValue("totalCost", this.getTotalCost());
    writer.writeIntegerValue("unitCost", this.getUnitCost());
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
   * Sets the productId property value. The productId property
   * @param value Value to set for the productId property.
   */
  public void setProductId(@jakarta.annotation.Nullable final String value) {
    this.productId = value;
  }

  /**
   * Sets the quantity property value. The quantity property
   * @param value Value to set for the quantity property.
   */
  public void setQuantity(@jakarta.annotation.Nullable final Integer value) {
    this.quantity = value;
  }

  /**
   * Sets the totalCost property value. The totalCost property
   * @param value Value to set for the totalCost property.
   */
  public void setTotalCost(@jakarta.annotation.Nullable final Integer value) {
    this.totalCost = value;
  }

  /**
   * Sets the unitCost property value. The unitCost property
   * @param value Value to set for the unitCost property.
   */
  public void setUnitCost(@jakarta.annotation.Nullable final Integer value) {
    this.unitCost = value;
  }
}
