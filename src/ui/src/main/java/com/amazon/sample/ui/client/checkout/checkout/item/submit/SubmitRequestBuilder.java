package com.amazon.sample.ui.client.checkout.checkout.item.submit;

import com.amazon.sample.ui.client.checkout.models.CheckoutSubmitted;
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
 * Builds and executes requests for operations under /checkout/{customerId}/submit
 */
@jakarta.annotation.Generated("com.microsoft.kiota")
public class SubmitRequestBuilder extends BaseRequestBuilder {

  /**
   * Instantiates a new {@link SubmitRequestBuilder} and sets the default values.
   * @param pathParameters Path parameters for the request
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public SubmitRequestBuilder(
    @jakarta.annotation.Nonnull final HashMap<String, Object> pathParameters,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(
      requestAdapter,
      "{+baseurl}/checkout/{customerId}/submit",
      pathParameters
    );
  }

  /**
   * Instantiates a new {@link SubmitRequestBuilder} and sets the default values.
   * @param rawUrl The raw URL to use for the request builder.
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public SubmitRequestBuilder(
    @jakarta.annotation.Nonnull final String rawUrl,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(requestAdapter, "{+baseurl}/checkout/{customerId}/submit", rawUrl);
  }

  /**
   * @return a {@link CheckoutSubmitted}
   */
  @jakarta.annotation.Nullable
  public CheckoutSubmitted post() {
    return post(null);
  }

  /**
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link CheckoutSubmitted}
   */
  @jakarta.annotation.Nullable
  public CheckoutSubmitted post(
    @jakarta.annotation.Nullable final java.util.function.Consumer<
      PostRequestConfiguration
    > requestConfiguration
  ) {
    final RequestInformation requestInfo = toPostRequestInformation(
      requestConfiguration
    );
    return this.requestAdapter.send(
        requestInfo,
        null,
        CheckoutSubmitted::createFromDiscriminatorValue
      );
  }

  /**
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toPostRequestInformation() {
    return toPostRequestInformation(null);
  }

  /**
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toPostRequestInformation(
    @jakarta.annotation.Nullable final java.util.function.Consumer<
      PostRequestConfiguration
    > requestConfiguration
  ) {
    final RequestInformation requestInfo = new RequestInformation(
      HttpMethod.POST,
      urlTemplate,
      pathParameters
    );
    requestInfo.configure(requestConfiguration, PostRequestConfiguration::new);
    requestInfo.headers.tryAdd("Accept", "application/json");
    return requestInfo;
  }

  /**
   * Returns a request builder with the provided arbitrary URL. Using this method means any other path or query parameters are ignored.
   * @param rawUrl The raw URL to use for the request builder.
   * @return a {@link SubmitRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public SubmitRequestBuilder withUrl(
    @jakarta.annotation.Nonnull final String rawUrl
  ) {
    Objects.requireNonNull(rawUrl);
    return new SubmitRequestBuilder(rawUrl, requestAdapter);
  }

  /**
   * Configuration for the request such as headers, query parameters, and middleware options.
   */
  @jakarta.annotation.Generated("com.microsoft.kiota")
  public class PostRequestConfiguration extends BaseRequestConfiguration {}
}
