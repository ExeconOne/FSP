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
package pl.execon.fsp.oracle;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import pl.execon.fsp.core.FilterInfo;
import pl.execon.fsp.core.FspFilterOperator;
import pl.execon.fsp.core.FspRequest;
import pl.execon.fsp.oracle.exception.FilteringException;
import pl.execon.fsp.oracle.predicate.PredicateCreator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

class FilteringSpecification<T> implements Specification<T> {

    private final transient List<FilterInfo> filters;

    private transient PredicateCreator<T> predicateCreator;

    private transient CriteriaBuilder criteriaBuilder;

    public FilteringSpecification(FspRequest request) {
        this.filters = request.getFilter();
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        this.criteriaBuilder = criteriaBuilder;
        predicateCreator = new PredicateCreator<>(root, criteriaBuilder);

        if (filters.isEmpty()) {
            return criteriaBuilder.conjunction();
        }

        return filters.stream()
                .map(PredicateWithSuffix::new)
                .reduce(PredicateWithSuffix::join)
                .orElseThrow(() -> new FilteringException("Filters are empty"))
                .predicate;
    }

    @AllArgsConstructor
    class PredicateWithSuffix {

        Predicate predicate;
        FspFilterOperator operator;

        PredicateWithSuffix(FilterInfo filter) {
            predicate = predicateCreator.toPredicate(filter);
            operator = filter.getOperator();
        }

        PredicateWithSuffix join(PredicateWithSuffix anotherPredicate) {
            PredicateWithSuffix predicateWithSuffix = null;
            switch (operator) {
                case OR:
                    Predicate joinedOrPredicate = criteriaBuilder.or(predicate, anotherPredicate.predicate);
                    predicateWithSuffix = new PredicateWithSuffix(joinedOrPredicate, anotherPredicate.operator);
                    break;

                case AND:
                    Predicate joinedAndPredicate = criteriaBuilder.and(predicate, anotherPredicate.predicate);
                    predicateWithSuffix = new PredicateWithSuffix(joinedAndPredicate, anotherPredicate.operator);
                    break;
            }
            return predicateWithSuffix;
        }
    }
}
