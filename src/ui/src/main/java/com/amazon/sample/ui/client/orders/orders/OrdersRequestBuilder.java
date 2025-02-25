package com.amazon.sample.ui.client.orders.orders;

import com.amazon.sample.ui.client.orders.models.ExistingOrder;
import com.amazon.sample.ui.client.orders.models.Order;
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
 * Builds and executes requests for operations under /orders
 */
@jakarta.annotation.Generated("com.microsoft.kiota")
public class OrdersRequestBuilder extends BaseRequestBuilder {

  /**
   * Instantiates a new {@link OrdersRequestBuilder} and sets the default values.
   * @param pathParameters Path parameters for the request
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public OrdersRequestBuilder(
    @jakarta.annotation.Nonnull final HashMap<String, Object> pathParameters,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(requestAdapter, "{+baseurl}/orders", pathParameters);
  }

  /**
   * Instantiates a new {@link OrdersRequestBuilder} and sets the default values.
   * @param rawUrl The raw URL to use for the request builder.
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public OrdersRequestBuilder(
    @jakarta.annotation.Nonnull final String rawUrl,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(requestAdapter, "{+baseurl}/orders", rawUrl);
  }

  /**
   * List orders
   * @return a {@link java.util.List<ExistingOrder>}
   */
  @jakarta.annotation.Nullable
  public java.util.List<ExistingOrder> get() {
    return get(null);
  }

  /**
   * List orders
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link java.util.List<ExistingOrder>}
   */
  @jakarta.annotation.Nullable
  public java.util.List<ExistingOrder> get(
    @jakarta.annotation.Nullable final java.util.function.Consumer<
      GetRequestConfiguration
    > requestConfiguration
  ) {
    final RequestInformation requestInfo = toGetRequestInformation(
      requestConfiguration
    );
    return this.requestAdapter.sendCollection(
        requestInfo,
        null,
        ExistingOrder::createFromDiscriminatorValue
      );
  }

  /**
   * Create an order
   * @param body The request body
   * @return a {@link ExistingOrder}
   */
  @jakarta.annotation.Nullable
  public ExistingOrder post(@jakarta.annotation.Nonnull final Order body) {
    return post(body, null);
  }

  /**
   * Create an order
   * @param body The request body
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link ExistingOrder}
   */
  @jakarta.annotation.Nullable
  public ExistingOrder post(
    @jakarta.annotation.Nonnull final Order body,
    @jakarta.annotation.Nullable final java.util.function.Consumer<
      PostRequestConfiguration
    > requestConfiguration
  ) {
    Objects.requireNonNull(body);
    final RequestInformation requestInfo = toPostRequestInformation(
      body,
      requestConfiguration
    );
    return this.requestAdapter.send(
        requestInfo,
        null,
        ExistingOrder::createFromDiscriminatorValue
      );
  }

  /**
   * List orders
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toGetRequestInformation() {
    return toGetRequestInformation(null);
  }

  /**
   * List orders
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
   * Create an order
   * @param body The request body
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toPostRequestInformation(
    @jakarta.annotation.Nonnull final Order body
  ) {
    return toPostRequestInformation(body, null);
  }

  /**
   * Create an order
   * @param body The request body
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toPostRequestInformation(
    @jakarta.annotation.Nonnull final Order body,
    @jakarta.annotation.Nullable final java.util.function.Consumer<
      PostRequestConfiguration
    > requestConfiguration
  ) {
    Objects.requireNonNull(body);
    final RequestInformation requestInfo = new RequestInformation(
      HttpMethod.POST,
      urlTemplate,
      pathParameters
    );
    requestInfo.configure(requestConfiguration, PostRequestConfiguration::new);
    requestInfo.headers.tryAdd("Accept", "application/json");
    requestInfo.setContentFromParsable(
      requestAdapter,
      "application/json",
      body
    );
    return requestInfo;
  }

  /**
   * Returns a request builder with the provided arbitrary URL. Using this method means any other path or query parameters are ignored.
   * @param rawUrl The raw URL to use for the request builder.
   * @return a {@link OrdersRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public OrdersRequestBuilder withUrl(
    @jakarta.annotation.Nonnull final String rawUrl
  ) {
    Objects.requireNonNull(rawUrl);
    return new OrdersRequestBuilder(rawUrl, requestAdapter);
  }

  /**
   * Configuration for the request such as headers, query parameters, and middleware options.
   */
  @jakarta.annotation.Generated("com.microsoft.kiota")
  public class GetRequestConfiguration extends BaseRequestConfiguration {}

  /**
   * Configuration for the request such as headers, query parameters, and middleware options.
   */
  @jakarta.annotation.Generated("com.microsoft.kiota")
  public class PostRequestConfiguration extends BaseRequestConfiguration {}
}
