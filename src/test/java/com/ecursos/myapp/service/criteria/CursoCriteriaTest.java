package com.ecursos.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CursoCriteriaTest {

    @Test
    void newCursoCriteriaHasAllFiltersNullTest() {
        var cursoCriteria = new CursoCriteria();
        assertThat(cursoCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void cursoCriteriaFluentMethodsCreatesFiltersTest() {
        var cursoCriteria = new CursoCriteria();

        setAllFilters(cursoCriteria);

        assertThat(cursoCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void cursoCriteriaCopyCreatesNullFilterTest() {
        var cursoCriteria = new CursoCriteria();
        var copy = cursoCriteria.copy();

        assertThat(cursoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(cursoCriteria)
        );
    }

    @Test
    void cursoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var cursoCriteria = new CursoCriteria();
        setAllFilters(cursoCriteria);

        var copy = cursoCriteria.copy();

        assertThat(cursoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(cursoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var cursoCriteria = new CursoCriteria();

        assertThat(cursoCriteria).hasToString("CursoCriteria{}");
    }

    private static void setAllFilters(CursoCriteria cursoCriteria) {
        cursoCriteria.id();
        cursoCriteria.cursoNome();
        cursoCriteria.cursoSigla();
        cursoCriteria.empresa();
        cursoCriteria.tipoId();
        cursoCriteria.distinct();
    }

    private static Condition<CursoCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCursoNome()) &&
                condition.apply(criteria.getCursoSigla()) &&
                condition.apply(criteria.getEmpresa()) &&
                condition.apply(criteria.getTipoId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CursoCriteria> copyFiltersAre(CursoCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCursoNome(), copy.getCursoNome()) &&
                condition.apply(criteria.getCursoSigla(), copy.getCursoSigla()) &&
                condition.apply(criteria.getEmpresa(), copy.getEmpresa()) &&
                condition.apply(criteria.getTipoId(), copy.getTipoId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
