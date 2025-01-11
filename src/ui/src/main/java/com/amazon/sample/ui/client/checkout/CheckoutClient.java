package com.amazon.sample.ui.client.checkout;

import com.amazon.sample.ui.client.checkout.checkout.CheckoutRequestBuilder;
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
public class CheckoutClient extends BaseRequestBuilder {

  /**
   * The checkout property
   * @return a {@link CheckoutRequestBuilder}
   */
  @jakarta.annotation.Nonnull
  public CheckoutRequestBuilder checkout() {
    return new CheckoutRequestBuilder(pathParameters, requestAdapter);
  }

  /**
   * Instantiates a new {@link CheckoutClient} and sets the default values.
   * @param requestAdapter The request adapter to use to execute the requests.
   */
  public CheckoutClient(
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
      requestAdapter.setBaseUrl("http://localhost:8000");
    }
    pathParameters.put("baseurl", requestAdapter.getBaseUrl());
  }
}
