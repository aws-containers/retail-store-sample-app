package com.amazon.sample.ui.client.checkout.checkout.item.update;

import com.amazon.sample.ui.client.checkout.models.Checkout;
import com.amazon.sample.ui.client.checkout.models.CheckoutRequest;
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
 * Builds and executes requests for operations under /checkout/{customerId}/update
 */
@jakarta.annotation.Generated("com.microsoft.kiota")
public class UpdateRequestBuilder extends BaseRequestBuilder {

  /**
   * Instantiates a new {@link UpdateRequestBuilder} and sets the default values.
   * @param pathParameters Path parameters for the request
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public UpdateRequestBuilder(
    @jakarta.annotation.Nonnull final HashMap<String, Object> pathParameters,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(
      requestAdapter,
      "{+baseurl}/checkout/{customerId}/update",
      pathParameters
    );
  }

  /**
   * Instantiates a new {@link UpdateRequestBuilder} and sets the default values.
   * @param rawUrl The raw URL to use for the request builder.
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public UpdateRequestBuilder(
    @jakarta.annotation.Nonnull final String rawUrl,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(requestAdapter, "{+baseurl}/checkout/{customerId}/update", rawUrl);
  }

  /**
   * @param body The request body
   * @return a {@link Checkout}
   */
  @jakarta.annotation.Nullable
  public Checkout post(@jakarta.annotation.Nonnull final CheckoutRequest body) {
    return post(body, null);
  }

  /**
   * @param body The request body
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link Checkout}
   */
  @jakarta.annotation.Nullable
  public Checkout post(
    @jakarta.annotation.Nonnull final CheckoutRequest body,
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
        Checkout::createFromDiscriminatorValue
      );
  }

  /**
   * @param body The request body
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toPostRequestInformation(
    @jakarta.annotation.Nonnull final CheckoutRequest body
  ) {
    return toPostRequestInformation(body, null);
  }

  /**
   * @param body The request body
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toPostRequestInformation(
    @jakarta.annotation.Nonnull final CheckoutRequest body,
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
   * @return a {@link UpdateRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public UpdateRequestBuilder withUrl(
    @jakarta.annotation.Nonnull final String rawUrl
  ) {
    Objects.requireNonNull(rawUrl);
    return new UpdateRequestBuilder(rawUrl, requestAdapter);
  }

  /**
   * Configuration for the request such as headers, query parameters, and middleware options.
   */
  @jakarta.annotation.Generated("com.microsoft.kiota")
  public class PostRequestConfiguration extends BaseRequestConfiguration {}
}
