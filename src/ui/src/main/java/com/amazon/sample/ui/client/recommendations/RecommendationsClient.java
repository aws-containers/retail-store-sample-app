package com.amazon.sample.ui.client.recommendations;

import com.amazon.sample.ui.client.recommendations.recommendations.RecommendationsRequestBuilder;
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
public class RecommendationsClient extends BaseRequestBuilder {
    /**
     * The recommendations property
     * @return a {@link RecommendationsRequestBuilder}
     */
    @jakarta.annotation.Nonnull
    public RecommendationsRequestBuilder recommendations() {
        return new RecommendationsRequestBuilder(pathParameters, requestAdapter);
    }
    /**
     * Instantiates a new {@link RecommendationsClient} and sets the default values.
     * @param requestAdapter The request adapter to use to execute the requests.
     */
    public RecommendationsClient(@jakarta.annotation.Nonnull final RequestAdapter requestAdapter) {
        super(requestAdapter, "{+baseurl}");
        this.pathParameters = new HashMap<>();
        ApiClientBuilder.registerDefaultSerializer(() -> new JsonSerializationWriterFactory());
        ApiClientBuilder.registerDefaultDeserializer(() -> new JsonParseNodeFactory());
        if (requestAdapter.getBaseUrl() == null || requestAdapter.getBaseUrl().isEmpty()) {
            requestAdapter.setBaseUrl("http://localhost:8080");
        }
        pathParameters.put("baseurl", requestAdapter.getBaseUrl());
    }
}
