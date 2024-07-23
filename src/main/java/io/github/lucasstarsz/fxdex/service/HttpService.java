package io.github.lucasstarsz.fxdex.service;

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public interface HttpService {

    HttpClient getClient();

    HttpRequest getDefaultDexRequest();

    HttpRequest buildDexRequest(int dexId) throws URISyntaxException;
}
