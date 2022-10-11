/*
 * Checkout API
 * Checkout API
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package com.amazon.sample.ui.clients.checkout.model;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * ShippingAddress
 */
@JsonPropertyOrder({
  ShippingAddress.JSON_PROPERTY_FIRST_NAME,
  ShippingAddress.JSON_PROPERTY_LAST_NAME,
  ShippingAddress.JSON_PROPERTY_ADDRESS1,
  ShippingAddress.JSON_PROPERTY_ADDRESS2,
  ShippingAddress.JSON_PROPERTY_CITY,
  ShippingAddress.JSON_PROPERTY_STATE,
  ShippingAddress.JSON_PROPERTY_ZIP
})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2021-01-19T15:03:37.310542-08:00[America/Los_Angeles]")
public class ShippingAddress {
  public static final String JSON_PROPERTY_FIRST_NAME = "firstName";
  private String firstName;

  public static final String JSON_PROPERTY_LAST_NAME = "lastName";
  private String lastName;

  public static final String JSON_PROPERTY_ADDRESS1 = "address1";
  private String address1;

  public static final String JSON_PROPERTY_ADDRESS2 = "address2";
  private String address2;

  public static final String JSON_PROPERTY_CITY = "city";
  private String city;

  public static final String JSON_PROPERTY_STATE = "state";
  private String state;

  public static final String JSON_PROPERTY_ZIP = "zip";
  private String zip;


  public ShippingAddress firstName(String firstName) {
    
    this.firstName = firstName;
    return this;
  }

   /**
   * Get firstName
   * @return firstName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_FIRST_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getFirstName() {
    return firstName;
  }


  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }


  public ShippingAddress lastName(String lastName) {
    
    this.lastName = lastName;
    return this;
  }

   /**
   * Get lastName
   * @return lastName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_LAST_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getLastName() {
    return lastName;
  }


  public void setLastName(String lastName) {
    this.lastName = lastName;
  }


  public ShippingAddress address1(String address1) {
    
    this.address1 = address1;
    return this;
  }

   /**
   * Get address1
   * @return address1
  **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_ADDRESS1)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getAddress1() {
    return address1;
  }


  public void setAddress1(String address1) {
    this.address1 = address1;
  }


  public ShippingAddress address2(String address2) {
    
    this.address2 = address2;
    return this;
  }

   /**
   * Get address2
   * @return address2
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")
  @JsonProperty(JSON_PROPERTY_ADDRESS2)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getAddress2() {
    return address2;
  }


  public void setAddress2(String address2) {
    this.address2 = address2;
  }


  public ShippingAddress city(String city) {
    
    this.city = city;
    return this;
  }

   /**
   * Get city
   * @return city
  **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_CITY)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getCity() {
    return city;
  }


  public void setCity(String city) {
    this.city = city;
  }


  public ShippingAddress state(String state) {
    
    this.state = state;
    return this;
  }

   /**
   * Get state
   * @return state
  **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_STATE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getState() {
    return state;
  }


  public void setState(String state) {
    this.state = state;
  }


  public ShippingAddress zip(String zip) {
    
    this.zip = zip;
    return this;
  }

   /**
   * Get zip
   * @return zip
  **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty(JSON_PROPERTY_ZIP)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getZip() {
    return zip;
  }


  public void setZip(String zip) {
    this.zip = zip;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ShippingAddress shippingAddress = (ShippingAddress) o;
    return Objects.equals(this.firstName, shippingAddress.firstName) &&
        Objects.equals(this.lastName, shippingAddress.lastName) &&
        Objects.equals(this.address1, shippingAddress.address1) &&
        Objects.equals(this.address2, shippingAddress.address2) &&
        Objects.equals(this.city, shippingAddress.city) &&
        Objects.equals(this.state, shippingAddress.state) &&
        Objects.equals(this.zip, shippingAddress.zip);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstName, lastName, address1, address2, city, state, zip);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShippingAddress {\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    address1: ").append(toIndentedString(address1)).append("\n");
    sb.append("    address2: ").append(toIndentedString(address2)).append("\n");
    sb.append("    city: ").append(toIndentedString(city)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    zip: ").append(toIndentedString(zip)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

