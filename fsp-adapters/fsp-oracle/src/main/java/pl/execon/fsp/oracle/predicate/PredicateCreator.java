package pl.execon.fsp.oracle.predicate;

import lombok.AllArgsConstructor;
import pl.execon.fsp.core.FilterInfo;
import pl.execon.fsp.core.Operation;
import pl.execon.fsp.oracle.exception.FilteringException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class PredicateCreator<T> {

    private Root<T> root;
    private CriteriaBuilder criteriaBuilder;

    public Predicate toPredicate(FilterInfo filter) {
        Operation operation = filter.getOperation();
        switch (operation) {
            case EQUALS:
                return new EqualsPredicate<T>().of(root, criteriaBuilder, filter);

            case NOT_EQUALS:
                return new EqualsPredicate<T>().of(root, criteriaBuilder, filter).not();

            case CONTAINS:
                return new IncludePredicate<T>().of(root, criteriaBuilder, filter);

            case IN:
                return new SeriesPredicate<T>().of(root, criteriaBuilder, filter);

            case NOT_IN:
                return new SeriesPredicate<T>().of(root, criteriaBuilder, filter).not();

            case GREATER_THAN:
                return new GreaterThanPredicate<T>().of(root, criteriaBuilder, filter);

            case LESS_THAN:
                return criteriaBuilder.and(
                        new GreaterThanPredicate<T>().of(root, criteriaBuilder, filter).not(),
                        new EqualsPredicate<T>().of(root, criteriaBuilder, filter).not()
                );

            case GREATER_OR_EQUALS:
                return criteriaBuilder.or(
                        new GreaterThanPredicate<T>().of(root, criteriaBuilder, filter),
                        new EqualsPredicate<T>().of(root, criteriaBuilder, filter)
                );

            case LESS_OR_EQUALS:
                return criteriaBuilder.or(
                        new GreaterThanPredicate<T>().of(root, criteriaBuilder, filter).not()
                );

            default:
                throw new FilteringException("Unknown operator: " + operation.name());
        }
    }

}
