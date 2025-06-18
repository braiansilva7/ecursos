package com.ecursos.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MilitarTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Militar getMilitarSample1() {
        return new Militar()
            .id(1L)
            .saram("saram1")
            .nomeCompleto("nomeCompleto1")
            .nomeGuerra("nomeGuerra1")
            .om("om1")
            .telefone("telefone1")
            .nrAntiguidade("nrAntiguidade1")
            .cpf("cpf1")
            .email("email1");
    }

    public static Militar getMilitarSample2() {
        return new Militar()
            .id(2L)
            .saram("saram2")
            .nomeCompleto("nomeCompleto2")
            .nomeGuerra("nomeGuerra2")
            .om("om2")
            .telefone("telefone2")
            .nrAntiguidade("nrAntiguidade2")
            .cpf("cpf2")
            .email("email2");
    }

    public static Militar getMilitarRandomSampleGenerator() {
        return new Militar()
            .id(longCount.incrementAndGet())
            .saram(UUID.randomUUID().toString())
            .nomeCompleto(UUID.randomUUID().toString())
            .nomeGuerra(UUID.randomUUID().toString())
            .om(UUID.randomUUID().toString())
            .telefone(UUID.randomUUID().toString())
            .nrAntiguidade(UUID.randomUUID().toString())
            .cpf(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString());
    }
}
