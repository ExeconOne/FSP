package pl.execon.fsp.oracle.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class GreaterThanPredicate<T> extends AbstractPredicate<T> {

    @Override
    protected Predicate createPredicate(Path field, Class fieldClass, Object target, CriteriaBuilder criteriaBuilder) {

        if (fieldClass.equals(LocalDateTime.class))
            return criteriaBuilder.greaterThan(field, LocalDateTime.parse(target.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        if (fieldClass.equals(Timestamp.class))
            return criteriaBuilder.greaterThan(field, Timestamp.valueOf(LocalDateTime.parse(target.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        if (fieldClass.equals(LocalDate.class))
            return criteriaBuilder.greaterThan(field, Timestamp.valueOf(LocalDateTime.parse(target.toString(), DateTimeFormatter.ISO_LOCAL_DATE)));

        if (fieldClass.equals(Long.class) || fieldClass.equals(long.class))
            return criteriaBuilder.greaterThan(field, Long.valueOf(target.toString()));

        if (fieldClass.equals(Integer.class) || fieldClass.equals(int.class))
            return criteriaBuilder.greaterThan(field, Integer.valueOf(target.toString()));

        if (fieldClass.equals(Double.class) || fieldClass.equals(double.class))
            return criteriaBuilder.greaterThan(field, Double.valueOf(target.toString()));

        if (fieldClass.equals(Float.class) || fieldClass.equals(float.class))
            return criteriaBuilder.greaterThan(field, Float.valueOf(target.toString()));

        return null;
    }
}
