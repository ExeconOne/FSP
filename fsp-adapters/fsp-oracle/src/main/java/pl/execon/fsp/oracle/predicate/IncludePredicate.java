package pl.execon.fsp.oracle.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

class IncludePredicate<T> extends AbstractPredicate<T> {
    @Override
    protected Predicate createPredicate(Path field, Class fieldClass, Object target, CriteriaBuilder criteriaBuilder) {
        if (fieldClass.equals(String.class)) {
            return criteriaBuilder.like(criteriaBuilder.upper(field), "%" + target.toString().toUpperCase() + "%");
        } else {
            return criteriaBuilder.like(criteriaBuilder.upper(field.as(String.class)), "%" + target.toString().toUpperCase() + "%");
        }
    }
}
