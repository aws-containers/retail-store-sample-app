package com.amazon.sample.ui.client.cart.carts.item.items;

import com.amazon.sample.ui.client.cart.carts.item.items.item.WithItemItemRequestBuilder;
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
 * Builds and executes requests for operations under /carts/{customerId}/items
 */
@jakarta.annotation.Generated("com.microsoft.kiota")
public class ItemsRequestBuilder extends BaseRequestBuilder {

  /**
   * Gets an item from the com.amazon.sample.ui.client.cart.carts.item.items.item collection
   * @param itemId Unique identifier of the item
   * @return a {@link WithItemItemRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public WithItemItemRequestBuilder byItemId(
    @jakarta.annotation.Nonnull final String itemId
  ) {
    Objects.requireNonNull(itemId);
    final HashMap<String, Object> urlTplParams = new HashMap<String, Object>(
      this.pathParameters
    );
    urlTplParams.put("itemId", itemId);
    return new WithItemItemRequestBuilder(urlTplParams, requestAdapter);
  }

  /**
   * Instantiates a new {@link ItemsRequestBuilder} and sets the default values.
   * @param pathParameters Path parameters for the request
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public ItemsRequestBuilder(
    @jakarta.annotation.Nonnull final HashMap<String, Object> pathParameters,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(
      requestAdapter,
      "{+baseurl}/carts/{customerId}/items",
      pathParameters
    );
  }

  /**
   * Instantiates a new {@link ItemsRequestBuilder} and sets the default values.
   * @param rawUrl The raw URL to use for the request builder.
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public ItemsRequestBuilder(
    @jakarta.annotation.Nonnull final String rawUrl,
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(requestAdapter, "{+baseurl}/carts/{customerId}/items", rawUrl);
  }

  /**
   * Retrieve items from a cart
   * @return a {@link java.util.List<Item>}
   */
  @jakarta.annotation.Nullable
  public java.util.List<Item> get() {
    return get(null);
  }

  /**
   * Retrieve items from a cart
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link java.util.List<Item>}
   */
  @jakarta.annotation.Nullable
  public java.util.List<Item> get(
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
        Item::createFromDiscriminatorValue
      );
  }

  /**
   * Update an item in a cart
   * @param body The request body
   */
  public void patch(@jakarta.annotation.Nonnull final Item body) {
    patch(body, null);
  }

  /**
   * Update an item in a cart
   * @param body The request body
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   */
  public void patch(
    @jakarta.annotation.Nonnull final Item body,
    @jakarta.annotation.Nullable final java.util.function.Consumer<
      PatchRequestConfiguration
    > requestConfiguration
  ) {
    Objects.requireNonNull(body);
    final RequestInformation requestInfo = toPatchRequestInformation(
      body,
      requestConfiguration
    );
    this.requestAdapter.sendPrimitive(requestInfo, null, Void.class);
  }

  /**
   * Add an item to a cart
   * @param body The request body
   * @return a {@link Item}
   */
  @jakarta.annotation.Nullable
  public Item post(@jakarta.annotation.Nonnull final Item body) {
    return post(body, null);
  }

  /**
   * Add an item to a cart
   * @param body The request body
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link Item}
   */
  @jakarta.annotation.Nullable
  public Item post(
    @jakarta.annotation.Nonnull final Item body,
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
        Item::createFromDiscriminatorValue
      );
  }

  /**
   * Retrieve items from a cart
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toGetRequestInformation() {
    return toGetRequestInformation(null);
  }

  /**
   * Retrieve items from a cart
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
   * Update an item in a cart
   * @param body The request body
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toPatchRequestInformation(
    @jakarta.annotation.Nonnull final Item body
  ) {
    return toPatchRequestInformation(body, null);
  }

  /**
   * Update an item in a cart
   * @param body The request body
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toPatchRequestInformation(
    @jakarta.annotation.Nonnull final Item body,
    @jakarta.annotation.Nullable final java.util.function.Consumer<
      PatchRequestConfiguration
    > requestConfiguration
  ) {
    Objects.requireNonNull(body);
    final RequestInformation requestInfo = new RequestInformation(
      HttpMethod.PATCH,
      urlTemplate,
      pathParameters
    );
    requestInfo.configure(requestConfiguration, PatchRequestConfiguration::new);
    requestInfo.setContentFromParsable(
      requestAdapter,
      "application/json",
      body
    );
    return requestInfo;
  }

  /**
   * Add an item to a cart
   * @param body The request body
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toPostRequestInformation(
    @jakarta.annotation.Nonnull final Item body
  ) {
    return toPostRequestInformation(body, null);
  }

  /**
   * Add an item to a cart
   * @param body The request body
   * @param requestConfiguration Configuration for the request such as headers, query parameters, and middleware options.
   * @return a {@link RequestInformation}
   */
  @jakarta.annotation.Nonnull
  public RequestInformation toPostRequestInformation(
    @jakarta.annotation.Nonnull final Item body,
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
   * @return a {@link ItemsRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public ItemsRequestBuilder withUrl(
    @jakarta.annotation.Nonnull final String rawUrl
  ) {
    Objects.requireNonNull(rawUrl);
    return new ItemsRequestBuilder(rawUrl, requestAdapter);
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
  public class PatchRequestConfiguration extends BaseRequestConfiguration {}

  /**
   * Configuration for the request such as headers, query parameters, and middleware options.
   */
  @jakarta.annotation.Generated("com.microsoft.kiota")
  public class PostRequestConfiguration extends BaseRequestConfiguration {}
}
