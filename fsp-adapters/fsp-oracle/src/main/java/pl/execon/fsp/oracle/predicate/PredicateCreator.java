package pl.execon.fsp.oracle.predicate;

import lombok.AllArgsConstructor;
import pl.execon.fsp.core.FilterInfo;
import pl.execon.fsp.core.Operation;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class PredicateCreator<T> {

    private Root<T> root;
    private CriteriaBuilder criteriaBuilder;

    public Predicate toPredicate(FilterInfo filter) {
        Operation operation = filter.getOperation();
        Predicate predicate = null;
        switch (operation) {
            case EQUALS:
                predicate = new EqualsPredicate<T>().of(root, criteriaBuilder, filter);
                break;

            case NOT_EQUALS:
                predicate = new EqualsPredicate<T>().of(root, criteriaBuilder, filter).not();
                break;

            case CONTAINS:
                predicate =  new IncludePredicate<T>().of(root, criteriaBuilder, filter);
                break;

            case IN:
                predicate =  new SeriesPredicate<T>().of(root, criteriaBuilder, filter);
                break;

            case NOT_IN:
                predicate =  new SeriesPredicate<T>().of(root, criteriaBuilder, filter).not();
                break;

            case GREATER_THAN:
                predicate =  new GreaterThanPredicate<T>().of(root, criteriaBuilder, filter);
                break;

            case LESS_THAN:
                predicate =  criteriaBuilder.and(
                        new GreaterThanPredicate<T>().of(root, criteriaBuilder, filter).not(),
                        new EqualsPredicate<T>().of(root, criteriaBuilder, filter).not()
                );
                break;

            case GREATER_OR_EQUALS:
                predicate =  criteriaBuilder.or(
                        new GreaterThanPredicate<T>().of(root, criteriaBuilder, filter),
                        new EqualsPredicate<T>().of(root, criteriaBuilder, filter)
                );
                break;

            case LESS_OR_EQUALS:
                predicate =  criteriaBuilder.or(
                        new GreaterThanPredicate<T>().of(root, criteriaBuilder, filter).not()
                );
                break;
        }
        return predicate;
    }

}
