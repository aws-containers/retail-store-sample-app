package com.amazon.sample.ui.client.cart.models;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@jakarta.annotation.Generated("com.microsoft.kiota")
public class Item implements AdditionalDataHolder, Parsable {

  /**
   * Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
   */
  private Map<String, Object> additionalData;
  /**
   * The itemId property
   */
  private String itemId;
  /**
   * The quantity property
   */
  private Integer quantity;
  /**
   * The unitPrice property
   */
  private Integer unitPrice;

  /**
   * Instantiates a new {@link Item} and sets the default values.
   */
  public Item() {
    this.setAdditionalData(new HashMap<>());
  }

  /**
   * Creates a new instance of the appropriate class based on discriminator value
   * @param parseNode The parse node to use to read the discriminator value and create the object
   * @return a {@link Item}
   */
  @jakarta.annotation.Nonnull
  public static Item createFromDiscriminatorValue(
    @jakarta.annotation.Nonnull final ParseNode parseNode
  ) {
    Objects.requireNonNull(parseNode);
    return new Item();
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
    >(3);
    deserializerMap.put("itemId", n -> {
      this.setItemId(n.getStringValue());
    });
    deserializerMap.put("quantity", n -> {
      this.setQuantity(n.getIntegerValue());
    });
    deserializerMap.put("unitPrice", n -> {
      this.setUnitPrice(n.getIntegerValue());
    });
    return deserializerMap;
  }

  /**
   * Gets the itemId property value. The itemId property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getItemId() {
    return this.itemId;
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
   * Gets the unitPrice property value. The unitPrice property
   * @return a {@link Integer}
   */
  @jakarta.annotation.Nullable
  public Integer getUnitPrice() {
    return this.unitPrice;
  }

  /**
   * Serializes information the current object
   * @param writer Serialization writer to use to serialize this model
   */
  public void serialize(
    @jakarta.annotation.Nonnull final SerializationWriter writer
  ) {
    Objects.requireNonNull(writer);
    writer.writeStringValue("itemId", this.getItemId());
    writer.writeIntegerValue("quantity", this.getQuantity());
    writer.writeIntegerValue("unitPrice", this.getUnitPrice());
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
   * Sets the itemId property value. The itemId property
   * @param value Value to set for the itemId property.
   */
  public void setItemId(@jakarta.annotation.Nullable final String value) {
    this.itemId = value;
  }

  /**
   * Sets the quantity property value. The quantity property
   * @param value Value to set for the quantity property.
   */
  public void setQuantity(@jakarta.annotation.Nullable final Integer value) {
    this.quantity = value;
  }

  /**
   * Sets the unitPrice property value. The unitPrice property
   * @param value Value to set for the unitPrice property.
   */
  public void setUnitPrice(@jakarta.annotation.Nullable final Integer value) {
    this.unitPrice = value;
  }
}
