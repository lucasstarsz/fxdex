package io.github.lucasstarsz.fxdex.service;

import io.github.lucasstarsz.fxdex.misc.ApiLinks;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;

import static io.github.lucasstarsz.fxdex.misc.ApiConversionTables.PokedexNameToIdMap;

public class DexRequestOptions {
    private final Property<URI> linkProperty;

    public DexRequestOptions() throws URISyntaxException {
        this(DexRequestType.Default, null);
    }

    public DexRequestOptions(DexRequestType requestType, String link) throws URISyntaxException {
        linkProperty = new SimpleObjectProperty<>();

        switch (requestType) {
            case DexEntry -> setLinkAsDexEntry(link);
            case DexList -> setLinkAsDexList(link);
            default -> linkProperty.setValue(new URI(ApiLinks.AllDexesUrl));
        }
    }

    public Property<URI> linkProperty() {
        return new SimpleObjectProperty<>(linkProperty.getValue());
    }

    private void setLinkAsDexEntry(String dexEntry) throws URISyntaxException {
        linkProperty.setValue(new URI(ApiLinks.DexEntryUrl + dexEntry.toLowerCase() + '/'));
    }

    private void setLinkAsDexList(String pokedex) throws URISyntaxException {
        linkProperty.setValue(new URI(ApiLinks.DexUrl + PokedexNameToIdMap.get(pokedex.toLowerCase()) + '/'));
    }

    public HttpRequest buildGetRequest(HttpService httpService) {
        return HttpRequest.newBuilder(httpService.getDefaultDexRequest(), (n, v) -> true)
                .uri(linkProperty.getValue())
                .GET()
                .build();
    }

    public static DexRequestOptions defaultOptions() throws URISyntaxException {
        return new DexRequestOptions();
    }
}
