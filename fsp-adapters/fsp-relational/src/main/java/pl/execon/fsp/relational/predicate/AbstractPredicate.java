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

import pl.execon.fsp.core.FilterInfo;
import pl.execon.fsp.relational.exception.FilteringException;

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
