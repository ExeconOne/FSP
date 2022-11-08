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
package pl.execon.fsp.oracle.predicate;

import lombok.AllArgsConstructor;
import pl.execon.fsp.core.FilterInfo;
import pl.execon.fsp.core.Operation;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class PredicateCreator<T> {

    private Root<T> root;
    private CriteriaBuilder criteriaBuilder;

    public Predicate toPredicate(FilterInfo filter) {
        Operation operation = filter.getOperation();
        Predicate predicate = null;
        switch (operation) {
            case EQUALS:
                predicate = new EqualsPredicate<T>().of(root, criteriaBuilder, filter);
                break;

            case NOT_EQUALS:
                predicate = new EqualsPredicate<T>().of(root, criteriaBuilder, filter).not();
                break;

            case CONTAINS:
                predicate =  new IncludePredicate<T>().of(root, criteriaBuilder, filter);
                break;

            case IN:
                predicate =  new SeriesPredicate<T>().of(root, criteriaBuilder, filter);
                break;

            case NOT_IN:
                predicate =  new SeriesPredicate<T>().of(root, criteriaBuilder, filter).not();
                break;

            case GREATER_THAN:
                predicate =  new GreaterThanPredicate<T>().of(root, criteriaBuilder, filter);
                break;

            case LESS_THAN:
                predicate =  criteriaBuilder.and(
                        new GreaterThanPredicate<T>().of(root, criteriaBuilder, filter).not(),
                        new EqualsPredicate<T>().of(root, criteriaBuilder, filter).not()
                );
                break;

            case GREATER_OR_EQUALS:
                predicate =  criteriaBuilder.or(
                        new GreaterThanPredicate<T>().of(root, criteriaBuilder, filter),
                        new EqualsPredicate<T>().of(root, criteriaBuilder, filter)
                );
                break;

            case LESS_OR_EQUALS:
                predicate =  criteriaBuilder.or(
                        new GreaterThanPredicate<T>().of(root, criteriaBuilder, filter).not()
                );
                break;
        }
        return predicate;
    }

}
