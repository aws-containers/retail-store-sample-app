package com.amazon.sample.ui.client.catalog.models.model;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@jakarta.annotation.Generated("com.microsoft.kiota")
public class Product implements AdditionalDataHolder, Parsable {

  /**
   * Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
   */
  private Map<String, Object> additionalData;
  /**
   * The description property
   */
  private String description;
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
   * The tags property
   */
  private java.util.List<Tag> tags;

  /**
   * Instantiates a new {@link Product} and sets the default values.
   */
  public Product() {
    this.setAdditionalData(new HashMap<>());
  }

  /**
   * Creates a new instance of the appropriate class based on discriminator value
   * @param parseNode The parse node to use to read the discriminator value and create the object
   * @return a {@link Product}
   */
  @jakarta.annotation.Nonnull
  public static Product createFromDiscriminatorValue(
    @jakarta.annotation.Nonnull final ParseNode parseNode
  ) {
    Objects.requireNonNull(parseNode);
    return new Product();
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
   * Gets the description property value. The description property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getDescription() {
    return this.description;
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
    deserializerMap.put("description", n -> {
      this.setDescription(n.getStringValue());
    });
    deserializerMap.put("id", n -> {
      this.setId(n.getStringValue());
    });
    deserializerMap.put("name", n -> {
      this.setName(n.getStringValue());
    });
    deserializerMap.put("price", n -> {
      this.setPrice(n.getIntegerValue());
    });
    deserializerMap.put("tags", n -> {
      this.setTags(
          n.getCollectionOfObjectValues(Tag::createFromDiscriminatorValue)
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
   * Gets the tags property value. The tags property
   * @return a {@link java.util.List<Tag>}
   */
  @jakarta.annotation.Nullable
  public java.util.List<Tag> getTags() {
    return this.tags;
  }

  /**
   * Serializes information the current object
   * @param writer Serialization writer to use to serialize this model
   */
  public void serialize(
    @jakarta.annotation.Nonnull final SerializationWriter writer
  ) {
    Objects.requireNonNull(writer);
    writer.writeStringValue("description", this.getDescription());
    writer.writeStringValue("id", this.getId());
    writer.writeStringValue("name", this.getName());
    writer.writeIntegerValue("price", this.getPrice());
    writer.writeCollectionOfObjectValues("tags", this.getTags());
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
   * Sets the description property value. The description property
   * @param value Value to set for the description property.
   */
  public void setDescription(@jakarta.annotation.Nullable final String value) {
    this.description = value;
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
   * Sets the tags property value. The tags property
   * @param value Value to set for the tags property.
   */
  public void setTags(
    @jakarta.annotation.Nullable final java.util.List<Tag> value
  ) {
    this.tags = value;
  }
}
