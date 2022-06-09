package pl.execon.fsp.oracle.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.apache.commons.lang3.EnumUtils.getEnum;
import static org.apache.commons.lang3.math.NumberUtils.createNumber;

class EqualsPredicate<T> extends AbstractPredicate<T> {

    @Override
    protected Predicate createPredicate(Path field, Class fieldClass, Object target, CriteriaBuilder criteriaBuilder) {
        if (fieldClass.equals(String.class))
            return criteriaBuilder.equal(field, target.toString());

        if (fieldClass.equals(Long.class) || fieldClass.equals(long.class))
            return criteriaBuilder.equal(field, Long.valueOf(target.toString()));

        if (fieldClass.equals(Integer.class) || fieldClass.equals(int.class))
            return criteriaBuilder.equal(field, Integer.valueOf(target.toString()));

        if (fieldClass.equals(Double.class) || fieldClass.equals(double.class))
            return criteriaBuilder.equal(field, Double.valueOf(target.toString()));

        if (fieldClass.equals(Float.class) || fieldClass.equals(float.class))
            return criteriaBuilder.equal(field, Float.valueOf(target.toString()));

        if (fieldClass.isEnum())
            return criteriaBuilder.equal(field, (getEnum(fieldClass, target.toString())));

        if (fieldClass.equals(Boolean.class) || fieldClass.equals(boolean.class))
            return criteriaBuilder.equal(field, Boolean.parseBoolean(target.toString()));

        if (fieldClass.equals(LocalDateTime.class))
            return criteriaBuilder.equal(field, LocalDateTime.parse(target.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        if (fieldClass.equals(LocalDate.class))
            return criteriaBuilder.equal(field, LocalDate.parse(target.toString(), DateTimeFormatter.ISO_LOCAL_DATE));

        if (fieldClass.equals(Timestamp.class))
            return criteriaBuilder.equal(field, Timestamp.valueOf(LocalDateTime.parse(target.toString(), LOCAL_DATE_TIME_FORMATTER)));

        if (isNumericClass(fieldClass))
            return criteriaBuilder.equal(field, createNumber(target.toString()));

        return null;
    }
}
