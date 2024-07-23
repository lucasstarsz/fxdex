package io.github.lucasstarsz.fxdex.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import static io.github.lucasstarsz.fxdex.ApiLinks.AllDexesUrl;
import static io.github.lucasstarsz.fxdex.ApiLinks.DexEntryUrl;
import static io.github.lucasstarsz.fxdex.ApiLinks.DexUrl;
import io.github.lucasstarsz.fxdex.App;

public class DefaultHttpService implements HttpService {

    private final HttpClient client;
    private final HttpRequest dexesRequest;

    @Inject
    public DefaultHttpService(@Named("dexThreadHandler") ExecutorService dexThreadHandler) throws URISyntaxException {

        dexesRequest = HttpRequest.newBuilder()
                .uri(new URI(AllDexesUrl))
                .GET()
                .build();

        client = HttpClient.newBuilder()
                .executor(dexThreadHandler)
                .build();

        // allow for platform-appropriate shutdown
        App.DexThreadHandler.setValue(dexThreadHandler);
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

    @Override
    public HttpResponse<String> getString(HttpRequest request) throws IOException, InterruptedException {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public HttpRequest buildDexEntryRequest(String dexEntry) throws URISyntaxException {
        return HttpRequest.newBuilder(dexesRequest, (n, v) -> true)
                .uri(new URI(DexEntryUrl + dexEntry + "/"))
                .build();
    }
}
