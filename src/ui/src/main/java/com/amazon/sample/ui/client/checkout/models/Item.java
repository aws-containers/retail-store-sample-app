package com.amazon.sample.ui.client.checkout.models;

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
   * The id property
   */
  private String id;
  /**
   * The name property
   */
  private String name;
  /**
   * The price property
   */
  private Integer price;
  /**
   * The quantity property
   */
  private Integer quantity;
  /**
   * The totalCost property
   */
  private Integer totalCost;

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
    >(5);
    deserializerMap.put("id", n -> {
      this.setId(n.getStringValue());
    });
    deserializerMap.put("name", n -> {
      this.setName(n.getStringValue());
    });
    deserializerMap.put("price", n -> {
      this.setPrice(n.getIntegerValue());
    });
    deserializerMap.put("quantity", n -> {
      this.setQuantity(n.getIntegerValue());
    });
    deserializerMap.put("totalCost", n -> {
      this.setTotalCost(n.getIntegerValue());
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
   * Gets the name property value. The name property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getName() {
    return this.name;
  }

  /**
   * Gets the price property value. The price property
   * @return a {@link Integer}
   */
  @jakarta.annotation.Nullable
  public Integer getPrice() {
    return this.price;
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
   * Serializes information the current object
   * @param writer Serialization writer to use to serialize this model
   */
  public void serialize(
    @jakarta.annotation.Nonnull final SerializationWriter writer
  ) {
    Objects.requireNonNull(writer);
    writer.writeStringValue("id", this.getId());
    writer.writeStringValue("name", this.getName());
    writer.writeIntegerValue("price", this.getPrice());
    writer.writeIntegerValue("quantity", this.getQuantity());
    writer.writeIntegerValue("totalCost", this.getTotalCost());
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
   * Sets the id property value. The id property
   * @param value Value to set for the id property.
   */
  public void setId(@jakarta.annotation.Nullable final String value) {
    this.id = value;
  }

  /**
   * Sets the name property value. The name property
   * @param value Value to set for the name property.
   */
  public void setName(@jakarta.annotation.Nullable final String value) {
    this.name = value;
  }

  /**
   * Sets the price property value. The price property
   * @param value Value to set for the price property.
   */
  public void setPrice(@jakarta.annotation.Nullable final Integer value) {
    this.price = value;
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
}
