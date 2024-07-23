package io.github.lucasstarsz.fxdex.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface HttpService {

    HttpClient getClient();

    HttpRequest getDefaultDexRequest();

    HttpRequest buildDexRequest(int dexId) throws URISyntaxException;

    HttpResponse<String> getString(HttpRequest request) throws IOException, InterruptedException;

    public HttpRequest buildDexEntryRequest(String currentDexEntry) throws URISyntaxException;
}
