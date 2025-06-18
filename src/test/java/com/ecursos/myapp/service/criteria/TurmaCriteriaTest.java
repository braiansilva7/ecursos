package com.ecursos.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TurmaCriteriaTest {

    @Test
    void newTurmaCriteriaHasAllFiltersNullTest() {
        var turmaCriteria = new TurmaCriteria();
        assertThat(turmaCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void turmaCriteriaFluentMethodsCreatesFiltersTest() {
        var turmaCriteria = new TurmaCriteria();

        setAllFilters(turmaCriteria);

        assertThat(turmaCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void turmaCriteriaCopyCreatesNullFilterTest() {
        var turmaCriteria = new TurmaCriteria();
        var copy = turmaCriteria.copy();

        assertThat(turmaCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(turmaCriteria)
        );
    }

    @Test
    void turmaCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var turmaCriteria = new TurmaCriteria();
        setAllFilters(turmaCriteria);

        var copy = turmaCriteria.copy();

        assertThat(turmaCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(turmaCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var turmaCriteria = new TurmaCriteria();

        assertThat(turmaCriteria).hasToString("TurmaCriteria{}");
    }

    private static void setAllFilters(TurmaCriteria turmaCriteria) {
        turmaCriteria.id();
        turmaCriteria.inicio();
        turmaCriteria.termino();
        turmaCriteria.ano();
        turmaCriteria.statusCurso();
        turmaCriteria.modalidade();
        turmaCriteria.qtdVagas();
        turmaCriteria.numeroBca();
        turmaCriteria.cursoId();
        turmaCriteria.distinct();
    }

    private static Condition<TurmaCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getInicio()) &&
                condition.apply(criteria.getTermino()) &&
                condition.apply(criteria.getAno()) &&
                condition.apply(criteria.getStatusCurso()) &&
                condition.apply(criteria.getModalidade()) &&
                condition.apply(criteria.getQtdVagas()) &&
                condition.apply(criteria.getNumeroBca()) &&
                condition.apply(criteria.getCursoId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TurmaCriteria> copyFiltersAre(TurmaCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getInicio(), copy.getInicio()) &&
                condition.apply(criteria.getTermino(), copy.getTermino()) &&
                condition.apply(criteria.getAno(), copy.getAno()) &&
                condition.apply(criteria.getStatusCurso(), copy.getStatusCurso()) &&
                condition.apply(criteria.getModalidade(), copy.getModalidade()) &&
                condition.apply(criteria.getQtdVagas(), copy.getQtdVagas()) &&
                condition.apply(criteria.getNumeroBca(), copy.getNumeroBca()) &&
                condition.apply(criteria.getCursoId(), copy.getCursoId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
