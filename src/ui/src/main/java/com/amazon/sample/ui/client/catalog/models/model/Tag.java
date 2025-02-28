package com.amazon.sample.ui.client.catalog.models.model;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@jakarta.annotation.Generated("com.microsoft.kiota")
public class Tag implements AdditionalDataHolder, Parsable {

  /**
   * Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
   */
  private Map<String, Object> additionalData;
  /**
   * The displayName property
   */
  private String displayName;
  /**
   * The name property
   */
  private String name;

  /**
   * Instantiates a new {@link Tag} and sets the default values.
   */
  public Tag() {
    this.setAdditionalData(new HashMap<>());
  }

  /**
   * Creates a new instance of the appropriate class based on discriminator value
   * @param parseNode The parse node to use to read the discriminator value and create the object
   * @return a {@link Tag}
   */
  @jakarta.annotation.Nonnull
  public static Tag createFromDiscriminatorValue(
    @jakarta.annotation.Nonnull final ParseNode parseNode
  ) {
    Objects.requireNonNull(parseNode);
    return new Tag();
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
   * Gets the displayName property value. The displayName property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getDisplayName() {
    return this.displayName;
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
    deserializerMap.put("displayName", n -> {
      this.setDisplayName(n.getStringValue());
    });
    deserializerMap.put("name", n -> {
      this.setName(n.getStringValue());
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
   * Serializes information the current object
   * @param writer Serialization writer to use to serialize this model
   */
  public void serialize(
    @jakarta.annotation.Nonnull final SerializationWriter writer
  ) {
    Objects.requireNonNull(writer);
    writer.writeStringValue("displayName", this.getDisplayName());
    writer.writeStringValue("name", this.getName());
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
   * Sets the displayName property value. The displayName property
   * @param value Value to set for the displayName property.
   */
  public void setDisplayName(@jakarta.annotation.Nullable final String value) {
    this.displayName = value;
  }

  /**
   * Sets the name property value. The name property
   * @param value Value to set for the name property.
   */
  public void setName(@jakarta.annotation.Nullable final String value) {
    this.name = value;
  }
}
