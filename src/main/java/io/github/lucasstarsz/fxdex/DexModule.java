package io.github.lucasstarsz.fxdex;

import com.google.inject.AbstractModule;

import io.github.lucasstarsz.fxdex.service.DefaultHttpService;
import io.github.lucasstarsz.fxdex.service.DexService;
import io.github.lucasstarsz.fxdex.service.HttpService;
import io.github.lucasstarsz.fxdex.service.PokeApiDexService;

public class DexModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DexService.class).to(PokeApiDexService.class);
        bind(HttpService.class).to(DefaultHttpService.class);
    }
}
