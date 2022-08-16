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
package pl.execon.fsp.mongo;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pl.execon.fsp.core.FilterInfo;
import pl.execon.fsp.core.FspFilterOperator;
import pl.execon.fsp.core.FspRequest;
import pl.execon.fsp.core.SortInfo;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pl.execon.fsp.core.FspRequest.INITIAL_PAGE_NUMBER;

class MongoFspRequestResolver {

    private final FspRequest fspRequest;
    private final Query query;

    public MongoFspRequestResolver(FspRequest fspRequest) {
        this.fspRequest = fspRequest;
        query = new Query();
    }

    public Query asQuery() {
        addFiltering();
        addPaginationAndSorting();
        return query;
    }

    private void addFiltering() {
        if (isNull(fspRequest.getFilter()) || fspRequest.getFilter().isEmpty())
            return;

        final List<Criteria> orCriteria = filterCriteria(
                filterInfo -> FspFilterOperator.OR.equals(filterInfo.getOperator()));
        Criteria orCriteriaJoined = joinCriteria(orCriteria, criteria -> new Criteria().orOperator(criteria));

        final List<Criteria> andCriteria = filterCriteria(
                filterInfo -> isNull(filterInfo.getOperator()) || FspFilterOperator.AND.equals(filterInfo.getOperator()));
        andCriteria.add(orCriteriaJoined);
        Criteria andCriteriaJoined = joinCriteria(andCriteria, criteria -> new Criteria().andOperator(criteria));

        query.addCriteria(andCriteriaJoined);
    }

    private List<Criteria> filterCriteria(Predicate<FilterInfo> condition) {
        return fspRequest.getFilter().stream()
                .filter(condition)
                .map(this::asCriteria)
                .collect(Collectors.toList());
    }

    private Criteria joinCriteria(List<Criteria> criterias, Function<Criteria[], Criteria> joiner) {
        Criteria criteria;
        switch (criterias.size()) {
            case 0:
                criteria = new Criteria();
                break;
            case 1:
                criteria = criterias.get(0);
                break;
            default:
                criteria = joiner.apply(criterias.toArray(new Criteria[]{}));
                break;
        }
        return criteria;
    }

    private void addPaginationAndSorting() {
        Sort sorting = isNull(fspRequest.getSort())
                ? Sort.unsorted()
                : Sort.by(fspRequest.getSort().stream()
                .map(this::asOrder)
                .collect(Collectors.toList()));

        if (nonNull(fspRequest.getPage())) {
            query.with(
                    PageRequest.of(
                            fspRequest.getPage().getNumber(),
                            fspRequest.getPage().getSize() - INITIAL_PAGE_NUMBER,
                            sorting));
        } else {
            query.with(sorting);
        }
    }

    private Sort.Order asOrder(SortInfo sortInfo) {
        return SortInfo.Direction.DESC.equals(sortInfo.getDirection())
                ? Sort.Order.desc(sortInfo.getBy())
                : Sort.Order.asc(sortInfo.getBy());
    }

    @SuppressWarnings("unchecked")
    private Criteria asCriteria(FilterInfo filterInfo) {
        Criteria field = Criteria.where(filterInfo.getBy());

        Object value = filterInfo.getValue();

        if (isValidLocalDateTime(value.toString())) {
            value = Date.from(LocalDateTime.parse(value.toString(), DateTimeFormatter.ISO_DATE_TIME).atZone(ZoneId.systemDefault()).toInstant());
        }

        Criteria criteria = new Criteria();
        switch (filterInfo.getOperation()) {
            case EQUALS:
                criteria = field.is(value);
                break;
            case NOT_EQUALS:
                criteria = field.ne(value);
                break;
            case IN:
                criteria = field.in((List<Object>) value);
                break;
            case NOT_IN:
                criteria = field.nin((List<Object>) value);
                break;
            case GREATER_THAN:
                criteria = field.gt(value);
                break;
            case LESS_THAN:
                criteria = field.lt(value);
                break;
            case GREATER_OR_EQUALS:
                criteria = field.gte(value);
                break;
            case LESS_OR_EQUALS:
                criteria = field.lte(value);
                break;
            case CONTAINS:
                criteria = field.regex(Pattern.compile(value.toString(), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
                break;
        }
        return criteria;
    }

    private boolean isValidLocalDateTime(String dateStr) {
        try {
            LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}
