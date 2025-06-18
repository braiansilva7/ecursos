package com.ecursos.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CapacitacaoCriteriaTest {

    @Test
    void newCapacitacaoCriteriaHasAllFiltersNullTest() {
        var capacitacaoCriteria = new CapacitacaoCriteria();
        assertThat(capacitacaoCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void capacitacaoCriteriaFluentMethodsCreatesFiltersTest() {
        var capacitacaoCriteria = new CapacitacaoCriteria();

        setAllFilters(capacitacaoCriteria);

        assertThat(capacitacaoCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void capacitacaoCriteriaCopyCreatesNullFilterTest() {
        var capacitacaoCriteria = new CapacitacaoCriteria();
        var copy = capacitacaoCriteria.copy();

        assertThat(capacitacaoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(capacitacaoCriteria)
        );
    }

    @Test
    void capacitacaoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var capacitacaoCriteria = new CapacitacaoCriteria();
        setAllFilters(capacitacaoCriteria);

        var copy = capacitacaoCriteria.copy();

        assertThat(capacitacaoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(capacitacaoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var capacitacaoCriteria = new CapacitacaoCriteria();

        assertThat(capacitacaoCriteria).hasToString("CapacitacaoCriteria{}");
    }

    private static void setAllFilters(CapacitacaoCriteria capacitacaoCriteria) {
        capacitacaoCriteria.id();
        capacitacaoCriteria.capacitacaoStatus();
        capacitacaoCriteria.sigpes();
        capacitacaoCriteria.militarId();
        capacitacaoCriteria.turmaId();
        capacitacaoCriteria.distinct();
    }

    private static Condition<CapacitacaoCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCapacitacaoStatus()) &&
                condition.apply(criteria.getSigpes()) &&
                condition.apply(criteria.getMilitarId()) &&
                condition.apply(criteria.getTurmaId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CapacitacaoCriteria> copyFiltersAre(CapacitacaoCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCapacitacaoStatus(), copy.getCapacitacaoStatus()) &&
                condition.apply(criteria.getSigpes(), copy.getSigpes()) &&
                condition.apply(criteria.getMilitarId(), copy.getMilitarId()) &&
                condition.apply(criteria.getTurmaId(), copy.getTurmaId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
