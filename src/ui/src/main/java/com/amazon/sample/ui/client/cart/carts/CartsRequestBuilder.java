package com.amazon.sample.ui.client.cart.carts;

import com.amazon.sample.ui.client.cart.carts.item.WithCustomerItemRequestBuilder;
import com.microsoft.kiota.BaseRequestBuilder;
import com.microsoft.kiota.RequestAdapter;
import java.util.HashMap;
import java.util.Objects;

/**
 * Builds and executes requests for operations under /carts
 */
@jakarta.annotation.Generated("com.microsoft.kiota")
public class CartsRequestBuilder extends BaseRequestBuilder {

  /**
   * Gets an item from the com.amazon.sample.ui.client.cart.carts.item collection
   * @param customerId Unique identifier of the item
   * @return a {@link WithCustomerItemRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public WithCustomerItemRequestBuilder byCustomerId(
    @jakarta.annotation.Nonnull final String customerId
  ) {
    Objects.requireNonNull(customerId);
    final HashMap<String, Object> urlTplParams = new HashMap<String, Object>(
      this.pathParameters
    );
    urlTplParams.put("customerId", customerId);
    return new WithCustomerItemRequestBuilder(urlTplParams, requestAdapter);
  }

  /**
   * Instantiates a new {@link CartsRequestBuilder} and sets the default values.
   * @param pathParameters Path parameters for the request
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public CartsRequestBuilder(
    @jakarta.annotation.Nonnull final HashMap<String, Object> pathParameters,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(requestAdapter, "{+baseurl}/carts", pathParameters);
  }

  /**
   * Instantiates a new {@link CartsRequestBuilder} and sets the default values.
   * @param rawUrl The raw URL to use for the request builder.
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public CartsRequestBuilder(
    @jakarta.annotation.Nonnull final String rawUrl,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(requestAdapter, "{+baseurl}/carts", rawUrl);
  }
}
