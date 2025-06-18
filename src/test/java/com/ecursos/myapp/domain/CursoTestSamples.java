package com.ecursos.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CursoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Curso getCursoSample1() {
        return new Curso().id(1L).cursoNome("cursoNome1").cursoSigla("cursoSigla1").empresa("empresa1");
    }

    public static Curso getCursoSample2() {
        return new Curso().id(2L).cursoNome("cursoNome2").cursoSigla("cursoSigla2").empresa("empresa2");
    }

    public static Curso getCursoRandomSampleGenerator() {
        return new Curso()
            .id(longCount.incrementAndGet())
            .cursoNome(UUID.randomUUID().toString())
            .cursoSigla(UUID.randomUUID().toString())
            .empresa(UUID.randomUUID().toString());
    }
}
