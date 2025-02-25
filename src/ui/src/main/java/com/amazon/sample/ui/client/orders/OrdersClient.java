package com.amazon.sample.ui.client.orders;

import com.amazon.sample.ui.client.orders.orders.OrdersRequestBuilder;
import com.microsoft.kiota.ApiClientBuilder;
import com.microsoft.kiota.BaseRequestBuilder;
import com.microsoft.kiota.RequestAdapter;
import com.microsoft.kiota.serialization.JsonParseNodeFactory;
import com.microsoft.kiota.serialization.JsonSerializationWriterFactory;
import com.microsoft.kiota.serialization.ParseNodeFactoryRegistry;
import com.microsoft.kiota.serialization.SerializationWriterFactoryRegistry;
import java.util.HashMap;
import java.util.Objects;

/**
 * The main entry point of the SDK, exposes the configuration and the fluent API.
 */
@jakarta.annotation.Generated("com.microsoft.kiota")
public class OrdersClient extends BaseRequestBuilder {

  /**
   * The orders property
   * @return a {@link OrdersRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public OrdersRequestBuilder orders() {
    return new OrdersRequestBuilder(pathParameters, requestAdapter);
  }

  /**
   * Instantiates a new {@link OrdersClient} and sets the default values.
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public OrdersClient(
    @jakarta.annotation.Nonnull final RequestAdapter requestAdapter
  ) {
    super(requestAdapter, "{+baseurl}");
    this.pathParameters = new HashMap<>();
    ApiClientBuilder.registerDefaultSerializer(() ->
      new JsonSerializationWriterFactory()
    );
    ApiClientBuilder.registerDefaultDeserializer(() ->
      new JsonParseNodeFactory()
    );
    if (
      requestAdapter.getBaseUrl() == null ||
      requestAdapter.getBaseUrl().isEmpty()
    ) {
      requestAdapter.setBaseUrl("http://localhost:8080");
    }
    pathParameters.put("baseurl", requestAdapter.getBaseUrl());
  }
}
