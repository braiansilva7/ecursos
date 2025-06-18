package com.ecursos.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TurmaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Turma getTurmaSample1() {
        return new Turma().id(1L).ano(1).qtdVagas("qtdVagas1").numeroBca("numeroBca1");
    }

    public static Turma getTurmaSample2() {
        return new Turma().id(2L).ano(2).qtdVagas("qtdVagas2").numeroBca("numeroBca2");
    }

    public static Turma getTurmaRandomSampleGenerator() {
        return new Turma()
            .id(longCount.incrementAndGet())
            .ano(intCount.incrementAndGet())
            .qtdVagas(UUID.randomUUID().toString())
            .numeroBca(UUID.randomUUID().toString());
    }
}
