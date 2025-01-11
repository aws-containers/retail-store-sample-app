package com.amazon.sample.ui.client.cart.carts.item.items.item;

import com.amazon.sample.ui.client.cart.models.Item;
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
 * Builds and executes requests for operations under /carts/{customerId}/items/{itemId}
 */
@jakarta.annotation.Generated("com.microsoft.kiota")
public class WithItemItemRequestBuilder extends BaseRequestBuilder {

  /**
   * Instantiates a new {@link WithItemItemRequestBuilder} and sets the default values.
   * @param pathParameters Path parameters for the request
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public WithItemItemRequestBuilder(
    @jakarta.annotation.Nonnull final HashMap<String, Object> pathParameters,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(
      requestAdapter,
      "{+baseurl}/carts/{customerId}/items/{itemId}",
      pathParameters
    );
  }

  /**
   * Instantiates a new {@link WithItemItemRequestBuilder} and sets the default values.
   * @param rawUrl The raw URL to use for the request builder.
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public WithItemItemRequestBuilder(
    @jakarta.annotation.Nonnull final String rawUrl,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(
      requestAdapter,
      "{+baseurl}/carts/{customerId}/items/{itemId}",
      rawUrl
    );
  }

  /**
   * Delete an item from a cart
   */
  public void delete() {
    delete(null);
  }

  /**
   * Delete an item from a cart
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   */
  public void delete(
    @jakarta.annotation.Nullable final java.util.function.Consumer<
      DeleteRequestConfiguration
    > requestConfiguration
  ) {
    final RequestInformation requestInfo = toDeleteRequestInformation(
      requestConfiguration
    );
    this.requestAdapter.sendPrimitive(requestInfo, null, Void.class);
  }

  /**
   * Retrieve an item from a cart
   * @return a {@link Item}
   */
  @jakarta.annotation.Nullable
  public Item get() {
    return get(null);
  }

  /**
   * Retrieve an item from a cart
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link Item}
   */
  @jakarta.annotation.Nullable
  public Item get(
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
        Item::createFromDiscriminatorValue
      );
  }

  /**
   * Delete an item from a cart
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toDeleteRequestInformation() {
    return toDeleteRequestInformation(null);
  }

  /**
   * Delete an item from a cart
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
    return requestInfo;
  }

  /**
   * Retrieve an item from a cart
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toGetRequestInformation() {
    return toGetRequestInformation(null);
  }

  /**
   * Retrieve an item from a cart
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
   * @return a {@link WithItemItemRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public WithItemItemRequestBuilder withUrl(
    @jakarta.annotation.Nonnull final String rawUrl
  ) {
    Objects.requireNonNull(rawUrl);
    return new WithItemItemRequestBuilder(rawUrl, requestAdapter);
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
