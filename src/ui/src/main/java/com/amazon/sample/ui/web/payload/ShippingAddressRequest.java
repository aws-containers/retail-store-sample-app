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

package com.amazon.sample.ui.web.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ShippingAddressRequest {

  // Shipping
  @NotEmpty(message = "First name is required")
  private String firstName = "John";

  @NotEmpty(message = "Last name is required")
  private String lastName = "Doe";

  @Email(message = "Email invalid")
  @NotEmpty(message = "Email is required")
  private String email = "john_doe@example.com";

  @NotEmpty(message = "Address is required")
  private String address1 = "100 Main Street";

  private String address2;

  @NotEmpty(message = "City is required")
  private String city = "Anytown";

  @Pattern(regexp = "^[0-9]{5}$", message = "Invalid zip code")
  private String zipCode = "11111";

  @Pattern(regexp = "^[A-Z]{2}$", message = "Invalid state")
  private String state = "CA";
}
