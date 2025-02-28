package com.amazon.sample.ui.client.orders.models;

import com.microsoft.kiota.serialization.AdditionalDataHolder;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParseNode;
import com.microsoft.kiota.serialization.SerializationWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@jakarta.annotation.Generated("com.microsoft.kiota")
public class ShippingAddress implements AdditionalDataHolder, Parsable {

  /**
   * Stores additional data not described in the OpenAPI description found when deserializing. Can be used for serialization as well.
   */
  private Map<String, Object> additionalData;
  /**
   * The address1 property
   */
  private String address1;
  /**
   * The address2 property
   */
  private String address2;
  /**
   * The city property
   */
  private String city;
  /**
   * The email property
   */
  private String email;
  /**
   * The firstName property
   */
  private String firstName;
  /**
   * The lastName property
   */
  private String lastName;
  /**
   * The state property
   */
  private String state;
  /**
   * The zipCode property
   */
  private String zipCode;

  /**
   * Instantiates a new {@link ShippingAddress} and sets the default values.
   */
  public ShippingAddress() {
    this.setAdditionalData(new HashMap<>());
  }

  /**
   * Creates a new instance of the appropriate class based on discriminator value
   * @param parseNode The parse node to use to read the discriminator value and create the object
   * @return a {@link ShippingAddress}
   */
  @jakarta.annotation.Nonnull
  public static ShippingAddress createFromDiscriminatorValue(
    @jakarta.annotation.Nonnull final ParseNode parseNode
  ) {
    Objects.requireNonNull(parseNode);
    return new ShippingAddress();
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
   * Gets the address1 property value. The address1 property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getAddress1() {
    return this.address1;
  }

  /**
   * Gets the address2 property value. The address2 property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getAddress2() {
    return this.address2;
  }

  /**
   * Gets the city property value. The city property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getCity() {
    return this.city;
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
    >(8);
    deserializerMap.put("address1", n -> {
      this.setAddress1(n.getStringValue());
    });
    deserializerMap.put("address2", n -> {
      this.setAddress2(n.getStringValue());
    });
    deserializerMap.put("city", n -> {
      this.setCity(n.getStringValue());
    });
    deserializerMap.put("email", n -> {
      this.setEmail(n.getStringValue());
    });
    deserializerMap.put("firstName", n -> {
      this.setFirstName(n.getStringValue());
    });
    deserializerMap.put("lastName", n -> {
      this.setLastName(n.getStringValue());
    });
    deserializerMap.put("state", n -> {
      this.setState(n.getStringValue());
    });
    deserializerMap.put("zipCode", n -> {
      this.setZipCode(n.getStringValue());
    });
    return deserializerMap;
  }

  /**
   * Gets the firstName property value. The firstName property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getFirstName() {
    return this.firstName;
  }

  /**
   * Gets the lastName property value. The lastName property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getLastName() {
    return this.lastName;
  }

  /**
   * Gets the state property value. The state property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getState() {
    return this.state;
  }

  /**
   * Gets the zipCode property value. The zipCode property
   * @return a {@link String}
   */
  @jakarta.annotation.Nullable
  public String getZipCode() {
    return this.zipCode;
  }

  /**
   * Serializes information the current object
   * @param writer Serialization writer to use to serialize this model
   */
  public void serialize(
    @jakarta.annotation.Nonnull final SerializationWriter writer
  ) {
    Objects.requireNonNull(writer);
    writer.writeStringValue("address1", this.getAddress1());
    writer.writeStringValue("address2", this.getAddress2());
    writer.writeStringValue("city", this.getCity());
    writer.writeStringValue("email", this.getEmail());
    writer.writeStringValue("firstName", this.getFirstName());
    writer.writeStringValue("lastName", this.getLastName());
    writer.writeStringValue("state", this.getState());
    writer.writeStringValue("zipCode", this.getZipCode());
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
   * Sets the address1 property value. The address1 property
   * @param value Value to set for the address1 property.
   */
  public void setAddress1(@jakarta.annotation.Nullable final String value) {
    this.address1 = value;
  }

  /**
   * Sets the address2 property value. The address2 property
   * @param value Value to set for the address2 property.
   */
  public void setAddress2(@jakarta.annotation.Nullable final String value) {
    this.address2 = value;
  }

  /**
   * Sets the city property value. The city property
   * @param value Value to set for the city property.
   */
  public void setCity(@jakarta.annotation.Nullable final String value) {
    this.city = value;
  }

  /**
   * Sets the email property value. The email property
   * @param value Value to set for the email property.
   */
  public void setEmail(@jakarta.annotation.Nullable final String value) {
    this.email = value;
  }

  /**
   * Sets the firstName property value. The firstName property
   * @param value Value to set for the firstName property.
   */
  public void setFirstName(@jakarta.annotation.Nullable final String value) {
    this.firstName = value;
  }

  /**
   * Sets the lastName property value. The lastName property
   * @param value Value to set for the lastName property.
   */
  public void setLastName(@jakarta.annotation.Nullable final String value) {
    this.lastName = value;
  }

  /**
   * Sets the state property value. The state property
   * @param value Value to set for the state property.
   */
  public void setState(@jakarta.annotation.Nullable final String value) {
    this.state = value;
  }

  /**
   * Sets the zipCode property value. The zipCode property
   * @param value Value to set for the zipCode property.
   */
  public void setZipCode(@jakarta.annotation.Nullable final String value) {
    this.zipCode = value;
  }
}
