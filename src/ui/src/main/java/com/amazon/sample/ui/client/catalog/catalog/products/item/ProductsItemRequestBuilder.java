package com.amazon.sample.ui.client.catalog.catalog.products.item;

import com.amazon.sample.ui.client.catalog.models.httputil.HTTPError;
import com.amazon.sample.ui.client.catalog.models.model.Product;
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
 * Builds and executes requests for operations under /catalog/products/{id}
 */
@jakarta.annotation.Generated("com.microsoft.kiota")
public class ProductsItemRequestBuilder extends BaseRequestBuilder {

  /**
   * Instantiates a new {@link ProductsItemRequestBuilder} and sets the default values.
   * @param pathParameters Path parameters for the request
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public ProductsItemRequestBuilder(
    @jakarta.annotation.Nonnull final HashMap<String, Object> pathParameters,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(requestAdapter, "{+baseurl}/catalog/products/{id}", pathParameters);
  }

  /**
   * Instantiates a new {@link ProductsItemRequestBuilder} and sets the default values.
   * @param rawUrl The raw URL to use for the request builder.
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public ProductsItemRequestBuilder(
    @jakarta.annotation.Nonnull final String rawUrl,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(requestAdapter, "{+baseurl}/catalog/products/{id}", rawUrl);
  }

  /**
   * Get catalog
   * @return a {@link Product}
   * @throws HTTPError When receiving a 400 status code
   * @throws HTTPError When receiving a 404 status code
   * @throws HTTPError When receiving a 500 status code
   */
  @jakarta.annotation.Nullable
  public Product get() {
    return get(null);
  }

  /**
   * Get catalog
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link Product}
   * @throws HTTPError When receiving a 400 status code
   * @throws HTTPError When receiving a 404 status code
   * @throws HTTPError When receiving a 500 status code
   */
  @jakarta.annotation.Nullable
  public Product get(
    @jakarta.annotation.Nullable final java.util.function.Consumer<
      GetRequestConfiguration
    > requestConfiguration
  ) {
    final RequestInformation requestInfo = toGetRequestInformation(
      requestConfiguration
    );
    final HashMap<String, ParsableFactory<? extends Parsable>> errorMapping =
      new HashMap<String, ParsableFactory<? extends Parsable>>();
    errorMapping.put("400", HTTPError::createFromDiscriminatorValue);
    errorMapping.put("404", HTTPError::createFromDiscriminatorValue);
    errorMapping.put("500", HTTPError::createFromDiscriminatorValue);
    return this.requestAdapter.send(
        requestInfo,
        errorMapping,
        Product::createFromDiscriminatorValue
      );
  }

  /**
   * Get catalog
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toGetRequestInformation() {
    return toGetRequestInformation(null);
  }

  /**
   * Get catalog
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
   * @return a {@link ProductsItemRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public ProductsItemRequestBuilder withUrl(
    @jakarta.annotation.Nonnull final String rawUrl
  ) {
    Objects.requireNonNull(rawUrl);
    return new ProductsItemRequestBuilder(rawUrl, requestAdapter);
  }

  /**
   * Configuration for the request such as headers, query parameters, and middleware options.
   */
  @jakarta.annotation.Generated("com.microsoft.kiota")
  public class GetRequestConfiguration extends BaseRequestConfiguration {}
}
