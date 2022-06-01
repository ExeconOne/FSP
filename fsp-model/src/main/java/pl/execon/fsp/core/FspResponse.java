package pl.execon.fsp.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.function.Function;

/**
 * Class containing filtering, sorting and paging result
 */
@Getter
@NoArgsConstructor
@ToString
public class FspResponse<T> {

    /**
     * List with result of FSP operation
     */
    List<T> content;

    /**
     * Fsp response information
     */
    FspResponseInfo info;

    /**
     * Constructor for {@link FspResponse} without paging information
     */
    public FspResponse(List<T> content) {
        this.content = content;
        this.info = new FspResponseInfo((long) content.size());
    }

    /**
     * Constructor for {@link FspResponse} with paging information
     */
    public FspResponse(FspRequest request, List<T> content, long totalElementsCount) {
        this.content = content;
        this.info = new FspResponseInfo(
                request.getPage().number,
                totalElementsCount,
                pagesTotal(request, totalElementsCount));
    }

    private FspResponse(List<T> content, FspResponseInfo info) {
        this.content = content;
        this.info = info;
    }

    /**
     * This method allows mapping document objects to DTO objects
     * @param conversion
     * @param <U>
     * @return FspResponse with mapped document objects
     */
    public <U> FspResponse<U> map(Function<List<T>, List<U>> conversion) {
        return new FspResponse<>(conversion.apply(content), info);
    }

    /**
     * @return returned total elements count
     */
    public Long getElementsCount() {
        return info.totalElements;
    }

    /**
     * @return total number of pages
     */
    public Long getTotalPages(){
        return info.totalPages;
    }

    /**
     * Class containing response info
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class FspResponseInfo {
        /**
         * Field with current page number
         */
        Integer currentPage;
        /**
         * Field with number of total elements
         */
        Long totalElements;
        /**
         * Field with total pages information
         */
        Long totalPages;

        private FspResponseInfo(Long totalElements) {
            this.totalElements = totalElements;
        }
    }

    private Long pagesTotal(FspRequest request, long elementsTotal) {
        return (long) Math.ceil(1.0 * elementsTotal / request.getPage().size);
    }

}
