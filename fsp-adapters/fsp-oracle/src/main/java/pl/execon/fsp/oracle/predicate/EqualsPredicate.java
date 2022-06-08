package pl.execon.fsp.oracle.predicate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.apache.commons.lang3.EnumUtils.getEnum;
import static org.apache.commons.lang3.math.NumberUtils.createNumber;

class EqualsPredicate<T> extends AbstractPredicate<T> {

    @Override
    protected Predicate createPredicate(Path field, Class fieldClass, Object target, CriteriaBuilder criteriaBuilder) {
        if (fieldClass.equals(String.class))
            return field.in(target);

        if (isNumericClass(fieldClass))
            return field.in(createNumber(target.toString()));

        if (fieldClass.isEnum())
            return field.in(getEnum(fieldClass, target.toString()));

        if (fieldClass.equals(LocalDateTime.class))
            return field.in(LocalDateTime.parse(target.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        if (fieldClass.equals(Timestamp.class))
            return field.in(Timestamp.valueOf(LocalDateTime.parse(target.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        return null;
    }
}
