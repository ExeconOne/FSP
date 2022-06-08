package pl.execon.fsp.oracle;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import pl.execon.fsp.core.FilterInfo;
import pl.execon.fsp.core.FspFilterOperator;
import pl.execon.fsp.core.FspRequest;
import pl.execon.fsp.oracle.exception.FilteringException;
import pl.execon.fsp.oracle.predicate.PredicateCreator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

class FilteringSpecification<T> implements Specification<T> {

    private final transient List<FilterInfo> filters;

    private transient PredicateCreator<T> predicateCreator;

    private transient CriteriaBuilder criteriaBuilder;

    public FilteringSpecification(FspRequest request) {
        this.filters = request.getFilter();
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        this.criteriaBuilder = criteriaBuilder;
        predicateCreator = new PredicateCreator<>(root, criteriaBuilder);

        if (filters.isEmpty()) {
            return criteriaBuilder.conjunction();
        }

        return filters.stream()
                .map(PredicateWithSuffix::new)
                .reduce(PredicateWithSuffix::join)
                .orElseThrow(() -> new FilteringException("Filters are empty"))
                .predicate;
    }

    @AllArgsConstructor
    class PredicateWithSuffix {

        Predicate predicate;
        FspFilterOperator operator;

        PredicateWithSuffix(FilterInfo filter) {
            predicate = predicateCreator.toPredicate(filter);
            operator = filter.getOperator();
        }

        PredicateWithSuffix join(PredicateWithSuffix anotherPredicate) {
            switch (operator) {
                case OR:
                    Predicate joinedOrPredicate = criteriaBuilder.or(predicate, anotherPredicate.predicate);
                    return new PredicateWithSuffix(joinedOrPredicate, anotherPredicate.operator);

                case AND:
                    Predicate joinedAndPredicate = criteriaBuilder.and(predicate, anotherPredicate.predicate);
                    return new PredicateWithSuffix(joinedAndPredicate, anotherPredicate.operator);

                default:
                    throw new FilteringException("Unknown sufix: " + operator);
            }
        }
    }
}
