package com.ecursos.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TipoCriteriaTest {

    @Test
    void newTipoCriteriaHasAllFiltersNullTest() {
        var tipoCriteria = new TipoCriteria();
        assertThat(tipoCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void tipoCriteriaFluentMethodsCreatesFiltersTest() {
        var tipoCriteria = new TipoCriteria();

        setAllFilters(tipoCriteria);

        assertThat(tipoCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void tipoCriteriaCopyCreatesNullFilterTest() {
        var tipoCriteria = new TipoCriteria();
        var copy = tipoCriteria.copy();

        assertThat(tipoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(tipoCriteria)
        );
    }

    @Test
    void tipoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var tipoCriteria = new TipoCriteria();
        setAllFilters(tipoCriteria);

        var copy = tipoCriteria.copy();

        assertThat(tipoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(tipoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var tipoCriteria = new TipoCriteria();

        assertThat(tipoCriteria).hasToString("TipoCriteria{}");
    }

    private static void setAllFilters(TipoCriteria tipoCriteria) {
        tipoCriteria.id();
        tipoCriteria.categoria();
        tipoCriteria.distinct();
    }

    private static Condition<TipoCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) && condition.apply(criteria.getCategoria()) && condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TipoCriteria> copyFiltersAre(TipoCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCategoria(), copy.getCategoria()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
