package pl.execon.fsp.oracle;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pl.execon.fsp.core.FspRequest;
import pl.execon.fsp.core.SortInfo;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static pl.execon.fsp.core.FspRequest.INITIAL_PAGE_NUMBER;

@Getter
class FilteringAndSortingSpecification {

    private final Sort sort;
    private final PageRequest pageRequest;

    public FilteringAndSortingSpecification(FspRequest fspRequest) {
        this.sort = extractSort(fspRequest);
        this.pageRequest = extractPageRequest(fspRequest);
    }

    private Sort extractSort(FspRequest fspRequest) {
        return isNull(fspRequest.getSort())
                ? Sort.unsorted()
                : Sort.by(fspRequest.getSort().stream()
                .map(this::asOrder)
                .toList());
    }

    private PageRequest extractPageRequest(FspRequest fspRequest) {
        if (nonNull(fspRequest.getPage())) {
            return PageRequest.of(
                    fspRequest.getPage().getNumber(),
                    fspRequest.getPage().getSize() - INITIAL_PAGE_NUMBER,
                    sort);
        } else {
            return null;
        }
    }

    private Sort.Order asOrder(SortInfo sortInfo) {
        return SortInfo.Direction.DESC.equals(sortInfo.getDirection())
                ? Sort.Order.desc(sortInfo.getBy())
                : Sort.Order.asc(sortInfo.getBy());
    }
}
