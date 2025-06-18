package com.ecursos.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PostoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Posto getPostoSample1() {
        return new Posto().id(1L).postoSigla("postoSigla1").descricao("descricao1").prioridade(1).orgao(1).codSigpes(1);
    }

    public static Posto getPostoSample2() {
        return new Posto().id(2L).postoSigla("postoSigla2").descricao("descricao2").prioridade(2).orgao(2).codSigpes(2);
    }

    public static Posto getPostoRandomSampleGenerator() {
        return new Posto()
            .id(longCount.incrementAndGet())
            .postoSigla(UUID.randomUUID().toString())
            .descricao(UUID.randomUUID().toString())
            .prioridade(intCount.incrementAndGet())
            .orgao(intCount.incrementAndGet())
            .codSigpes(intCount.incrementAndGet());
    }
}
