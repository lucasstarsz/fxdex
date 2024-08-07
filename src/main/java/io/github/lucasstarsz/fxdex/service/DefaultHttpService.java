/* Copyright 2024 Andrew Dey

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

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

import static io.github.lucasstarsz.fxdex.misc.ApiLinks.AllDexesUrl;

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
        return HttpRequest.newBuilder(dexesRequest, (n, v) -> true).build();
    }

    public HttpResponse<String> get(DexRequestOptions options) throws IOException, InterruptedException {
        return client.send(options.buildGetRequest(this), HttpResponse.BodyHandlers.ofString());
    }
}
