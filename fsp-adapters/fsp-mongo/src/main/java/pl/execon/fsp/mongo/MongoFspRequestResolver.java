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

    private Criteria joinCriteria(List<Criteria> criteria, Function<Criteria[], Criteria> joiner) {
        return switch (criteria.size()) {
            case 0 -> new Criteria();
            case 1 -> criteria.get(0);
            default -> joiner.apply(criteria.toArray(new Criteria[]{}));
        };
    }

    private void addPaginationAndSorting() {
        Sort sorting = isNull(fspRequest.getSort())
                ? Sort.unsorted()
                : Sort.by(fspRequest.getSort().stream()
                .map(this::asOrder)
                .toList());

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

        return switch (filterInfo.getOperation()) {
            case EQUALS -> field.is(value);
            case NOT_EQUALS -> field.ne(value);
            case IN -> field.in((List<Object>) value);
            case NOT_IN -> field.nin((List<Object>) value);
            case GREATER_THAN -> field.gt(value);
            case LESS_THAN -> field.lt(value);
            case GREATER_OR_EQUALS -> field.gte(value);
            case LESS_OR_EQUALS -> field.lte(value);
            case CONTAINS -> field.regex(Pattern.compile(value.toString(), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE));
        };
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
