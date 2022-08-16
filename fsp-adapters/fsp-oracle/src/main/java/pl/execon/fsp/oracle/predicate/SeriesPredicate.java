/*
 * This file Copyright © 2022 Execon One Sp. z o.o. (https://execon.pl/). All rights reserved.
 *
 * This product is dual-licensed under both the MIT and the Execon One License.
 *
 * ---------------------------------------------------------------------
 *
 * The MIT License:
 *
 *     Permission is hereby granted, free of charge, to any person obtaining a copy
 *     of this software and associated documentation files (the "Software"), to deal
 *     in the Software without restriction, including without limitation the rights
 *     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *     copies of the Software, and to permit persons to whom the Software is
 *     furnished to do so, subject to the following conditions:
 *
 *     The above copyright notice and this permission notice shall be included in
 *     all copies or substantial portions of the Software.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *     THE SOFTWARE.
 *
 * ---------------------------------------------------------------------
 *
 * The Execon One License:
 *
 *     this file and the accompanying materials are made available under the
 *     terms of the MNA which accompanies this distribution, and
 *     is available at /license/Execon One End User License Agreement.docx
 *
 * ---------------------------------------------------------------------
 *
 * Any modifications to this file must keep this entire header intact.
 */
package pl.execon.fsp.oracle.predicate;

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
                    .map(obj -> LocalDate.parse(obj, DateTimeFormatter.ISO_LOCAL_DATE))
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

        return field.in((List<Object>) target);
    }
}
