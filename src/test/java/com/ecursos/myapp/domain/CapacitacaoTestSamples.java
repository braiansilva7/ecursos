package com.ecursos.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CapacitacaoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Capacitacao getCapacitacaoSample1() {
        return new Capacitacao().id(1L).sigpes("sigpes1");
    }

    public static Capacitacao getCapacitacaoSample2() {
        return new Capacitacao().id(2L).sigpes("sigpes2");
    }

    public static Capacitacao getCapacitacaoRandomSampleGenerator() {
        return new Capacitacao().id(longCount.incrementAndGet()).sigpes(UUID.randomUUID().toString());
    }
}
