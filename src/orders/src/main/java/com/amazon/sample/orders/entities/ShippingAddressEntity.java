/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.amazon.sample.orders.entities;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "shipping_addresses")
public class ShippingAddressEntity {

  @Column(value = "first_name")
  private String firstName;

  @Column(value = "last_name")
  private String lastName;

  @Column(value = "email")
  private String email;

  @Column(value = "address1")
  private String address1;

  @Column(value = "address2")
  private String address2;

  @Column(value = "city")
  private String city;

  @Column(value = "zip_code")
  private String zipCode;

  @Column(value = "state_id")
  private String state;

  public ShippingAddressEntity() {}

  @SuppressWarnings("checkstyle:ParameterNumber")
  public ShippingAddressEntity(
    String firstName,
    String lastName,
    String email,
    String address1,
    String address2,
    String city,
    String zipCode,
    String state
  ) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.address1 = address1;
    this.address2 = address2;
    this.city = city;
    this.zipCode = zipCode;
    this.state = state;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAddress1() {
    return address1;
  }

  public void setAddress1(String address1) {
    this.address1 = address1;
  }

  public String getAddress2() {
    return address2;
  }

  public void setAddress2(String address2) {
    this.address2 = address2;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return (
      "ShippingAddressEntity [firstName=" +
      firstName +
      ", lastName=" +
      lastName +
      ", email=" +
      email +
      ", address1=" +
      address1 +
      ", address2=" +
      address2 +
      ", city=" +
      city +
      ", zipCode=" +
      zipCode +
      ", state=" +
      state +
      "]"
    );
  }
}
