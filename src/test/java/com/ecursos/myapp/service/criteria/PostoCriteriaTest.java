package com.ecursos.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PostoCriteriaTest {

    @Test
    void newPostoCriteriaHasAllFiltersNullTest() {
        var postoCriteria = new PostoCriteria();
        assertThat(postoCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void postoCriteriaFluentMethodsCreatesFiltersTest() {
        var postoCriteria = new PostoCriteria();

        setAllFilters(postoCriteria);

        assertThat(postoCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void postoCriteriaCopyCreatesNullFilterTest() {
        var postoCriteria = new PostoCriteria();
        var copy = postoCriteria.copy();

        assertThat(postoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(postoCriteria)
        );
    }

    @Test
    void postoCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var postoCriteria = new PostoCriteria();
        setAllFilters(postoCriteria);

        var copy = postoCriteria.copy();

        assertThat(postoCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(postoCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var postoCriteria = new PostoCriteria();

        assertThat(postoCriteria).hasToString("PostoCriteria{}");
    }

    private static void setAllFilters(PostoCriteria postoCriteria) {
        postoCriteria.id();
        postoCriteria.postoSigla();
        postoCriteria.descricao();
        postoCriteria.prioridade();
        postoCriteria.orgao();
        postoCriteria.codSigpes();
        postoCriteria.distinct();
    }

    private static Condition<PostoCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getPostoSigla()) &&
                condition.apply(criteria.getDescricao()) &&
                condition.apply(criteria.getPrioridade()) &&
                condition.apply(criteria.getOrgao()) &&
                condition.apply(criteria.getCodSigpes()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PostoCriteria> copyFiltersAre(PostoCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getPostoSigla(), copy.getPostoSigla()) &&
                condition.apply(criteria.getDescricao(), copy.getDescricao()) &&
                condition.apply(criteria.getPrioridade(), copy.getPrioridade()) &&
                condition.apply(criteria.getOrgao(), copy.getOrgao()) &&
                condition.apply(criteria.getCodSigpes(), copy.getCodSigpes()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
