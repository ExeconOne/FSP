package pl.execon.fsp.oracle.predicate;

import org.apache.commons.lang3.math.NumberUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.EnumUtils.getEnum;

class SeriesPredicate<T> extends AbstractPredicate<T> {

    @Override
    protected Predicate createPredicate(Path field, Class fieldClass, Object target, CriteriaBuilder criteriaBuilder) {

        if (fieldClass.isEnum()) {
            return field.in(((List<Object>) target).stream()
                    .map(e -> getEnum(fieldClass, e.toString()))
                    .collect(Collectors.toList()));
        }

        if (fieldClass.isAssignableFrom(Boolean.class) || fieldClass.isAssignableFrom(boolean.class)) {
            return field.in(((List<Object>) target).stream()
                    .map(e -> Boolean.parseBoolean(e.toString()))
                    .collect(Collectors.toList()));
        }

        if (fieldClass.isAssignableFrom(LocalDateTime.class)) {
            return field.in(((List<Object>) target).stream()
                    .map(Object::toString)
                    .map(obj -> LocalDateTime.parse(obj, DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .collect(Collectors.toList()));
        }

        if (fieldClass.isAssignableFrom(LocalDate.class)) {
            return field.in(((List<Object>) target).stream()
                    .map(Object::toString)
                    .map(obj -> LocalDateTime.parse(obj, DateTimeFormatter.ISO_LOCAL_DATE))
                    .collect(Collectors.toList()));
        }

        if (fieldClass.equals(Long.class) || fieldClass.equals(long.class))
            return field.in(((List<Object>) target).stream()
                    .map(Object::toString)
                    .map(Long::valueOf)
                    .collect(Collectors.toList()));

        if (fieldClass.equals(Integer.class) || fieldClass.equals(int.class))
            return field.in(((List<Object>) target).stream()
                    .map(Object::toString)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList()));

        if (fieldClass.equals(Double.class) || fieldClass.equals(double.class))
            return field.in(((List<Object>) target).stream()
                    .map(Object::toString)
                    .map(Double::valueOf)
                    .collect(Collectors.toList()));

        if (fieldClass.equals(Float.class) || fieldClass.equals(float.class))
            return field.in(((List<Object>) target).stream()
                    .map(Object::toString)
                    .map(Float::valueOf)
                    .collect(Collectors.toList()));

        if (isNumericClass(fieldClass)) {
            return field.in(((List<Object>) target).stream()
                    .map(Object::toString)
                    .map(NumberUtils::createNumber)
                    .collect(Collectors.toList()));
        }

        return field.in((List<Object>) target);
    }
}
