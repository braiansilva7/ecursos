package com.ecursos.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class MilitarCriteriaTest {

    @Test
    void newMilitarCriteriaHasAllFiltersNullTest() {
        var militarCriteria = new MilitarCriteria();
        assertThat(militarCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void militarCriteriaFluentMethodsCreatesFiltersTest() {
        var militarCriteria = new MilitarCriteria();

        setAllFilters(militarCriteria);

        assertThat(militarCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void militarCriteriaCopyCreatesNullFilterTest() {
        var militarCriteria = new MilitarCriteria();
        var copy = militarCriteria.copy();

        assertThat(militarCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(militarCriteria)
        );
    }

    @Test
    void militarCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var militarCriteria = new MilitarCriteria();
        setAllFilters(militarCriteria);

        var copy = militarCriteria.copy();

        assertThat(militarCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(militarCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var militarCriteria = new MilitarCriteria();

        assertThat(militarCriteria).hasToString("MilitarCriteria{}");
    }

    private static void setAllFilters(MilitarCriteria militarCriteria) {
        militarCriteria.id();
        militarCriteria.saram();
        militarCriteria.nomeCompleto();
        militarCriteria.nomeGuerra();
        militarCriteria.om();
        militarCriteria.telefone();
        militarCriteria.statusMilitar();
        militarCriteria.forca();
        militarCriteria.nrAntiguidade();
        militarCriteria.ultimaPromocao();
        militarCriteria.cpf();
        militarCriteria.email();
        militarCriteria.postoId();
        militarCriteria.distinct();
    }

    private static Condition<MilitarCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getSaram()) &&
                condition.apply(criteria.getNomeCompleto()) &&
                condition.apply(criteria.getNomeGuerra()) &&
                condition.apply(criteria.getOm()) &&
                condition.apply(criteria.getTelefone()) &&
                condition.apply(criteria.getStatusMilitar()) &&
                condition.apply(criteria.getForca()) &&
                condition.apply(criteria.getNrAntiguidade()) &&
                condition.apply(criteria.getUltimaPromocao()) &&
                condition.apply(criteria.getCpf()) &&
                condition.apply(criteria.getEmail()) &&
                condition.apply(criteria.getPostoId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<MilitarCriteria> copyFiltersAre(MilitarCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getSaram(), copy.getSaram()) &&
                condition.apply(criteria.getNomeCompleto(), copy.getNomeCompleto()) &&
                condition.apply(criteria.getNomeGuerra(), copy.getNomeGuerra()) &&
                condition.apply(criteria.getOm(), copy.getOm()) &&
                condition.apply(criteria.getTelefone(), copy.getTelefone()) &&
                condition.apply(criteria.getStatusMilitar(), copy.getStatusMilitar()) &&
                condition.apply(criteria.getForca(), copy.getForca()) &&
                condition.apply(criteria.getNrAntiguidade(), copy.getNrAntiguidade()) &&
                condition.apply(criteria.getUltimaPromocao(), copy.getUltimaPromocao()) &&
                condition.apply(criteria.getCpf(), copy.getCpf()) &&
                condition.apply(criteria.getEmail(), copy.getEmail()) &&
                condition.apply(criteria.getPostoId(), copy.getPostoId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
