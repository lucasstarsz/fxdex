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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Defines the interface for handling and simplifying the creation of HTTP requests.
 *
 * @author Andrew Dey
 */
public interface HttpService {

    /** {@returns A standard instance of a PokeApi get request, configured to get information about each pokédex.} */
    HttpRequest getDefaultDexRequest();

    /** {@returns The {@link HttpClient client} instance used for this service.} */
    HttpClient getClient();

    /**
     * Creates a PokeApi pokédex get request, using the specified ID to signify which pokédex to get.
     *
     * @param options The {@link DexRequestOptions options} used to define the parameters of the dex request.
     * @return A {@link HttpResponse response} from the server regarding the get request used.
     * @throws IOException
     * @throws InterruptedException
     */
    default HttpResponse<String> get(DexRequestOptions options) throws IOException, InterruptedException {
        return getClient().send(options.buildGetRequest(this), HttpResponse.BodyHandlers.ofString());
    }
}
