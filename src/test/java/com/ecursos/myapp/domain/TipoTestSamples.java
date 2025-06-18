package com.ecursos.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TipoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Tipo getTipoSample1() {
        return new Tipo().id(1L).categoria("categoria1");
    }

    public static Tipo getTipoSample2() {
        return new Tipo().id(2L).categoria("categoria2");
    }

    public static Tipo getTipoRandomSampleGenerator() {
        return new Tipo().id(longCount.incrementAndGet()).categoria(UUID.randomUUID().toString());
    }
}
