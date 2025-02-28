package com.amazon.sample.ui.client.cart.carts.item;

import com.amazon.sample.ui.client.cart.carts.item.items.ItemsRequestBuilder;
import com.amazon.sample.ui.client.cart.carts.item.merge.MergeRequestBuilder;
import com.amazon.sample.ui.client.cart.models.Cart;
import com.microsoft.kiota.BaseRequestBuilder;
import com.microsoft.kiota.BaseRequestConfiguration;
import com.microsoft.kiota.HttpMethod;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.RequestInformation;
import com.microsoft.kiota.RequestOption;
import com.microsoft.kiota.serialization.Parsable;
import com.microsoft.kiota.serialization.ParsableFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Builds and executes requests for operations under /carts/{customerId}
 */
@jakarta.annotation.Generated("com.microsoft.kiota")
public class WithCustomerItemRequestBuilder extends BaseRequestBuilder {

  /**
   * The items property
   * @return a {@link ItemsRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public ItemsRequestBuilder items() {
    return new ItemsRequestBuilder(pathParameters, requestAdapter);
  }

  /**
   * The merge property
   * @return a {@link MergeRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public MergeRequestBuilder merge() {
    return new MergeRequestBuilder(pathParameters, requestAdapter);
  }

  /**
   * Instantiates a new {@link WithCustomerItemRequestBuilder} and sets the default values.
   * @param pathParameters Path parameters for the request
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public WithCustomerItemRequestBuilder(
    @jakarta.annotation.Nonnull final HashMap<String, Object> pathParameters,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(requestAdapter, "{+baseurl}/carts/{customerId}", pathParameters);
  }

  /**
   * Instantiates a new {@link WithCustomerItemRequestBuilder} and sets the default values.
   * @param rawUrl The raw URL to use for the request builder.
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public WithCustomerItemRequestBuilder(
    @jakarta.annotation.Nonnull final String rawUrl,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(requestAdapter, "{+baseurl}/carts/{customerId}", rawUrl);
  }

  /**
   * Delete a cart
   * @return a {@link Cart}
   */
  @jakarta.annotation.Nullable
  public Cart delete() {
    return delete(null);
  }

  /**
   * Delete a cart
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link Cart}
   */
  @jakarta.annotation.Nullable
  public Cart delete(
    @jakarta.annotation.Nullable final java.util.function.Consumer<
      DeleteRequestConfiguration
    > requestConfiguration
  ) {
    final RequestInformation requestInfo = toDeleteRequestInformation(
      requestConfiguration
    );
    return this.requestAdapter.send(
        requestInfo,
        null,
        Cart::createFromDiscriminatorValue
      );
  }

  /**
   * Retrieve a cart
   * @return a {@link Cart}
   */
  @jakarta.annotation.Nullable
  public Cart get() {
    return get(null);
  }

  /**
   * Retrieve a cart
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link Cart}
   */
  @jakarta.annotation.Nullable
  public Cart get(
    @jakarta.annotation.Nullable final java.util.function.Consumer<
      GetRequestConfiguration
    > requestConfiguration
  ) {
    final RequestInformation requestInfo = toGetRequestInformation(
      requestConfiguration
    );
    return this.requestAdapter.send(
        requestInfo,
        null,
        Cart::createFromDiscriminatorValue
      );
  }

  /**
   * Delete a cart
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toDeleteRequestInformation() {
    return toDeleteRequestInformation(null);
  }

  /**
   * Delete a cart
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toDeleteRequestInformation(
    @jakarta.annotation.Nullable final java.util.function.Consumer<
      DeleteRequestConfiguration
    > requestConfiguration
  ) {
    final RequestInformation requestInfo = new RequestInformation(
      HttpMethod.DELETE,
      urlTemplate,
      pathParameters
    );
    requestInfo.configure(
      requestConfiguration,
      DeleteRequestConfiguration::new
    );
    requestInfo.headers.tryAdd("Accept", "application/json");
    return requestInfo;
  }

  /**
   * Retrieve a cart
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toGetRequestInformation() {
    return toGetRequestInformation(null);
  }

  /**
   * Retrieve a cart
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toGetRequestInformation(
    @jakarta.annotation.Nullable final java.util.function.Consumer<
      GetRequestConfiguration
    > requestConfiguration
  ) {
    final RequestInformation requestInfo = new RequestInformation(
      HttpMethod.GET,
      urlTemplate,
      pathParameters
    );
    requestInfo.configure(requestConfiguration, GetRequestConfiguration::new);
    requestInfo.headers.tryAdd("Accept", "application/json");
    return requestInfo;
  }

  /**
   * Returns a request builder with the provided arbitrary URL. Using this method means any other path or query parameters are ignored.
   * @param rawUrl The raw URL to use for the request builder.
   * @return a {@link WithCustomerItemRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public WithCustomerItemRequestBuilder withUrl(
    @jakarta.annotation.Nonnull final String rawUrl
  ) {
    Objects.requireNonNull(rawUrl);
    return new WithCustomerItemRequestBuilder(rawUrl, requestAdapter);
  }

  /**
   * Configuration for the request such as headers, query parameters, and middleware options.
   */
  @jakarta.annotation.Generated("com.microsoft.kiota")
  public class DeleteRequestConfiguration extends BaseRequestConfiguration {}

  /**
   * Configuration for the request such as headers, query parameters, and middleware options.
   */
  @jakarta.annotation.Generated("com.microsoft.kiota")
  public class GetRequestConfiguration extends BaseRequestConfiguration {}
}
