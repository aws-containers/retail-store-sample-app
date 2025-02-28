package com.amazon.sample.ui.client.cart.carts.item.merge;

import com.microsoft.kiota.BaseRequestBuilder;
import com.microsoft.kiota.BaseRequestConfiguration;
import com.microsoft.kiota.HttpMethod;
import com.microsoft.kiota.QueryParameters;
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
 * Builds and executes requests for operations under /carts/{customerId}/merge
 */
@jakarta.annotation.Generated("com.microsoft.kiota")
public class MergeRequestBuilder extends BaseRequestBuilder {

  /**
   * Instantiates a new {@link MergeRequestBuilder} and sets the default values.
   * @param pathParameters Path parameters for the request
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public MergeRequestBuilder(
    @jakarta.annotation.Nonnull final HashMap<String, Object> pathParameters,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(
      requestAdapter,
      "{+baseurl}/carts/{customerId}/merge?sessionId={sessionId}",
      pathParameters
    );
  }

  /**
   * Instantiates a new {@link MergeRequestBuilder} and sets the default values.
   * @param rawUrl The raw URL to use for the request builder.
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public MergeRequestBuilder(
    @jakarta.annotation.Nonnull final String rawUrl,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(
      requestAdapter,
      "{+baseurl}/carts/{customerId}/merge?sessionId={sessionId}",
      rawUrl
    );
  }

  /**
   * Merge two carts contents
   */
  public void get() {
    get(null);
  }

  /**
   * Merge two carts contents
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   */
  public void get(
    @jakarta.annotation.Nullable final java.util.function.Consumer<
      GetRequestConfiguration
    > requestConfiguration
  ) {
    final RequestInformation requestInfo = toGetRequestInformation(
      requestConfiguration
    );
    this.requestAdapter.sendPrimitive(requestInfo, null, Void.class);
  }

  /**
   * Merge two carts contents
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toGetRequestInformation() {
    return toGetRequestInformation(null);
  }

  /**
   * Merge two carts contents
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
    requestInfo.configure(
      requestConfiguration,
      GetRequestConfiguration::new,
      x -> x.queryParameters
    );
    return requestInfo;
  }

  /**
   * Returns a request builder with the provided arbitrary URL. Using this method means any other path or query parameters are ignored.
   * @param rawUrl The raw URL to use for the request builder.
   * @return a {@link MergeRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public MergeRequestBuilder withUrl(
    @jakarta.annotation.Nonnull final String rawUrl
  ) {
    Objects.requireNonNull(rawUrl);
    return new MergeRequestBuilder(rawUrl, requestAdapter);
  }

  /**
   * Merge two carts contents
   */
  @jakarta.annotation.Generated("com.microsoft.kiota")
  public class GetQueryParameters implements QueryParameters {

    @jakarta.annotation.Nullable
    public String sessionId;

    /**
     * Extracts the query parameters into a map for the URI template parsing.
     * @return a {@link Map<String, Object>}
     */
    @jakarta.annotation.Nonnull
    public Map<String, Object> toQueryParameters() {
      final Map<String, Object> allQueryParams = new HashMap();
      allQueryParams.put("sessionId", sessionId);
      return allQueryParams;
    }
  }

  /**
   * Configuration for the request such as headers, query parameters, and middleware options.
   */
  @jakarta.annotation.Generated("com.microsoft.kiota")
  public class GetRequestConfiguration extends BaseRequestConfiguration {

    /**
     * Request query parameters
     */
    @jakarta.annotation.Nullable
    public GetQueryParameters queryParameters = new GetQueryParameters();
  }
}
