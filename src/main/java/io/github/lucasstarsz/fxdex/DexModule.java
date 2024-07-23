package io.github.lucasstarsz.fxdex;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import io.github.lucasstarsz.fxdex.service.DefaultHttpService;
import io.github.lucasstarsz.fxdex.service.DexService;
import io.github.lucasstarsz.fxdex.service.HttpService;
import io.github.lucasstarsz.fxdex.service.PokeApiDexService;

public class DexModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DexService.class).to(PokeApiDexService.class);
        bind(HttpService.class).to(DefaultHttpService.class);

        bind(ExecutorService.class)
                .annotatedWith(Names.named("dexThreadHandler"))
                .toInstance(Executors.newFixedThreadPool(4));
    }
}
