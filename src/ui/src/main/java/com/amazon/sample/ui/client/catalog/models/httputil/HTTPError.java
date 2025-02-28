package com.amazon.sample.ui.client.catalog.models.httputil;

import com.microsoft.kiota.ApiException;
import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@jakarta.annotation.Generated("com.microsoft.kiota")
public class HTTPError
  extends ApiException
  implements AdditionalDataHolder, Parsable {

  /**
   * Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
   */
  private Map<String, Object> additionalData;
  /**
   * The code property
   */
  private Integer code;
  /**
   * The message property
   */
  private String message;

  /**
   * Instantiates a new {@link HTTPError} and sets the default values.
   */
  public HTTPError() {
    this.setAdditionalData(new HashMap<>());
  }

  /**
   * Creates a new instance of the appropriate class based on discriminator value
   * @param parseNode The parse node to use to read the discriminator value and create the object
   * @return a {@link HTTPError}
   */
  @jakarta.annotation.Nonnull
  public static HTTPError createFromDiscriminatorValue(
    @jakarta.annotation.Nonnull final ParseNode parseNode
  ) {
    Objects.requireNonNull(parseNode);
    return new HTTPError();
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
   * Gets the code property value. The code property
   * @return a {@link Integer}
   */
  @jakarta.annotation.Nullable
  public Integer getCode() {
    return this.code;
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
    deserializerMap.put("code", n -> {
      this.setCode(n.getIntegerValue());
    });
    deserializerMap.put("message", n -> {
      this.setMessage(n.getStringValue());
    });
    return deserializerMap;
  }

  /**
   * The primary error message.
   * @return a {@link String}
   */
  @jakarta.annotation.Nonnull
  @Override
  public String getMessage() {
    return super.getMessage();
  }

  /**
   * Gets the message property value. The message property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getMessageEscaped() {
    return this.message;
  }

  /**
   * Serializes information the current object
   * @param writer Serialization writer to use to serialize this model
   */
  public void serialize(
    @jakarta.annotation.Nonnull final SerializationWriter writer
  ) {
    Objects.requireNonNull(writer);
    writer.writeIntegerValue("code", this.getCode());
    writer.writeStringValue("message", this.getMessageEscaped());
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
   * Sets the code property value. The code property
   * @param value Value to set for the code property.
   */
  public void setCode(@jakarta.annotation.Nullable final Integer value) {
    this.code = value;
  }

  /**
   * Sets the message property value. The message property
   * @param value Value to set for the message property.
   */
  public void setMessage(@jakarta.annotation.Nullable final String value) {
    this.message = value;
  }
}
