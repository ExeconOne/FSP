package pl.execon.fsp.oracle.predicate;

import pl.execon.fsp.core.FilterInfo;
import pl.execon.fsp.oracle.exception.FilteringException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

abstract class AbstractPredicate<T> {

    private static final String EXCEPTION_MESSAGE = "Unsupported field/operator combination: %s(%s) with operator %s";
    static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Predicate of(Root<T> root, CriteriaBuilder criteriaBuilder, FilterInfo filter) {
        List<String> filterFields = new ArrayList<>();
        Path field;
        if (filter.getBy().contains(".")) {
            filterFields.addAll(Arrays.asList(filter.getBy().split("\\.")));
        }

        if (!filterFields.isEmpty()) {
            field = root.get(filterFields.get(0));
            filterFields.remove(filterFields.get(0));
            for (String filterField : filterFields) {
               field = field.get(filterField);
            }
        } else {
            field  = root.get(filter.getBy());
        }

        Class fieldClass = field.getJavaType();
        Object target = filter.getValue();

        return Optional.ofNullable(createPredicate(field, fieldClass, target, criteriaBuilder))
                .orElseThrow(() -> new FilteringException(String.format(EXCEPTION_MESSAGE, filter.getBy(), fieldClass, filter.getOperator())));
    }

    protected abstract Predicate createPredicate(Path field, Class fieldClass, Object target, CriteriaBuilder criteriaBuilder);

}
