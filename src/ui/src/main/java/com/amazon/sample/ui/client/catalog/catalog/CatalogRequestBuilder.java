package com.amazon.sample.ui.client.catalog.catalog;

import com.amazon.sample.ui.client.catalog.catalog.products.ProductsRequestBuilder;
import com.amazon.sample.ui.client.catalog.catalog.size.SizeRequestBuilder;
import com.amazon.sample.ui.client.catalog.catalog.tags.TagsRequestBuilder;
import com.microsoft.kiota.BaseRequestBuilder;
import com.microsoft.kiota.RequestAdapter;
import java.util.HashMap;
import java.util.Objects;

/**
 * Builds and executes requests for operations under /catalog
 */
@jakarta.annotation.Generated("com.microsoft.kiota")
public class CatalogRequestBuilder extends BaseRequestBuilder {

  /**
   * The products property
   * @return a {@link ProductsRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public ProductsRequestBuilder products() {
    return new ProductsRequestBuilder(pathParameters, requestAdapter);
  }

  /**
   * The size property
   * @return a {@link SizeRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public SizeRequestBuilder size() {
    return new SizeRequestBuilder(pathParameters, requestAdapter);
  }

  /**
   * The tags property
   * @return a {@link TagsRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public TagsRequestBuilder tags() {
    return new TagsRequestBuilder(pathParameters, requestAdapter);
  }

  /**
   * Instantiates a new {@link CatalogRequestBuilder} and sets the default values.
   * @param pathParameters Path parameters for the request
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public CatalogRequestBuilder(
    @jakarta.annotation.Nonnull final HashMap<String, Object> pathParameters,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(requestAdapter, "{+baseurl}/catalog", pathParameters);
  }

  /**
   * Instantiates a new {@link CatalogRequestBuilder} and sets the default values.
   * @param rawUrl The raw URL to use for the request builder.
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public CatalogRequestBuilder(
    @jakarta.annotation.Nonnull final String rawUrl,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(requestAdapter, "{+baseurl}/catalog", rawUrl);
  }
}
