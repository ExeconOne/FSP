package pl.execon.fsp.oracle.predicate;

import pl.execon.fsp.core.FilterInfo;
import pl.execon.fsp.oracle.exception.FilteringException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Optional;

abstract class AbstractPredicate<T> {

    private static final String EXCEPTION_MESSAGE = "Unsupported field/operator combination: %s(%s) with operator %s";


    public Predicate of(Root<T> root, CriteriaBuilder criteriaBuilder, FilterInfo filter) {
        Path field = root.get(filter.getBy());

        Class fieldClass = field.getJavaType();
        Object target = filter.getValue();

        return Optional.ofNullable(createPredicate(field, fieldClass, target, criteriaBuilder))
                .orElseThrow(() -> new FilteringException(String.format(EXCEPTION_MESSAGE, filter.getBy(), fieldClass, filter.getOperator())));
    }

    protected abstract Predicate createPredicate(Path field, Class fieldClass, Object target, CriteriaBuilder criteriaBuilder);

    boolean isNumericClass(Class clazz) {
        return Number.class.isAssignableFrom(clazz)
                || clazz.equals(int.class)
                || clazz.equals(long.class)
                || clazz.equals(float.class)
                || clazz.equals(double.class);
    }

}
