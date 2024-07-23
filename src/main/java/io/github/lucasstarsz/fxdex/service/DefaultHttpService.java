package io.github.lucasstarsz.fxdex.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import static io.github.lucasstarsz.fxdex.ApiLinks.AllDexesUrl;
import static io.github.lucasstarsz.fxdex.ApiLinks.DexUrl;
import static io.github.lucasstarsz.fxdex.App.DexThreadHandler;

public class DefaultHttpService implements HttpService {

    private final HttpClient client;
    private final HttpRequest dexesRequest;

    public DefaultHttpService() throws URISyntaxException {
        dexesRequest = HttpRequest.newBuilder()
                .uri(new URI(AllDexesUrl))
                .GET()
                .build();

        client = HttpClient.newBuilder()
                .executor(DexThreadHandler)
                .build();
    }

    @Override
    public HttpClient getClient() {
        return client;
    }

    @Override
    public HttpRequest getDefaultDexRequest() {
        return dexesRequest;
    }

    @Override
    public HttpRequest buildDexRequest(int dexId) throws URISyntaxException {
        return HttpRequest.newBuilder(dexesRequest, (n, v) -> true)
                .uri(new URI(DexUrl + dexId + "/"))
                .build();
    }
}
