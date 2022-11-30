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
 *     This file and the accompanying materials are made available under the
 *     terms of the MNA which accompanies this distribution, and
 *     is available at /license/Execon One End User License Agreement.docx
 *
 * ---------------------------------------------------------------------
 *
 * Any modifications to this file must keep this entire header intact.
 */
package pl.execon.fsp.relational.predicate;

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
            return criteriaBuilder.greaterThan(field, Timestamp.valueOf(LocalDateTime.parse(target.toString(), LOCAL_DATE_TIME_FORMATTER)));

        if (fieldClass.equals(LocalDate.class))
            return criteriaBuilder.greaterThan(field, LocalDate.parse(target.toString(), DateTimeFormatter.ISO_LOCAL_DATE));

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
