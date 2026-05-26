package com.amazon.sample.ui.client.recommendations.recommendations;

import com.amazon.sample.ui.client.recommendations.recommendations.item.WithProductItemRequestBuilder;
import com.microsoft.kiota.BaseRequestBuilder;
import com.microsoft.kiota.RequestAdapter;
import java.util.HashMap;
import java.util.Objects;
/**
 * Builds and executes requests for operations under /recommendations
 */
@jakarta.annotation.Generated("com.microsoft.kiota")
public class RecommendationsRequestBuilder extends BaseRequestBuilder {
    /**
     * Gets an item from the com.amazon.sample.ui.client.recommendations.recommendations.item collection
     * @param productId product ID to get recommendations for
     * @return a {@link WithProductItemRequestBuilder}
     */
    @jakarta.annotation.Nonnull
    public WithProductItemRequestBuilder byProductId(@jakarta.annotation.Nonnull final String productId) {
        Objects.requireNonNull(productId);
        final HashMap<String, Object> urlTplParams = new HashMap<String, Object>(this.pathParameters);
        urlTplParams.put("productId", productId);
        return new WithProductItemRequestBuilder(urlTplParams, requestAdapter);
    }
    /**
     * Instantiates a new {@link RecommendationsRequestBuilder} and sets the default values.
     * @param pathParameters Path parameters for the request
     * @param requestAdapter The request adapter to use to execute the requests.
     */
    public RecommendationsRequestBuilder(@jakarta.annotation.Nonnull final HashMap<String, Object> pathParameters, @jakarta.annotation.Nonnull final RequestAdapter requestAdapter) {
        super(requestAdapter, "{+baseurl}/recommendations", pathParameters);
    }
    /**
     * Instantiates a new {@link RecommendationsRequestBuilder} and sets the default values.
     * @param rawUrl The raw URL to use for the request builder.
     * @param requestAdapter The request adapter to use to execute the requests.
     */
    public RecommendationsRequestBuilder(@jakarta.annotation.Nonnull final String rawUrl, @jakarta.annotation.Nonnull final RequestAdapter requestAdapter) {
        super(requestAdapter, "{+baseurl}/recommendations", rawUrl);
    }
}
