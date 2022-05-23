package pl.execon.fsp.core;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.function.Function;

@Getter
@NoArgsConstructor
@ToString
public class FspResponse<T> {

    List<T> content;
    FspResponseInfo info;

    public FspResponse(List<T> content) {
        this.content = content;
        this.info = new FspResponseInfo((long) content.size());
    }

    public FspResponse(FspRequest request, List<T> content, long elementsTotal) {
        this.content = content;
        this.info = new FspResponseInfo(
                request.getPage().number,
                elementsTotal,
                pagesTotal(request, elementsTotal));
    }

    private FspResponse(List<T> content, FspResponseInfo info) {
        this.content = content;
        this.info = info;
    }

    public <U> FspResponse<U> map(Function<List<T>, List<U>> conversion) {
        return new FspResponse<>(conversion.apply(content), info);
    }

    public Long getElementsCount() {
        return info.totalElements;
    }

    public Long getTotalPages(){
        return info.totalPages;
    }

    public void setElementsCount(long totalElements) {
        info.setTotalElements(totalElements);
    }

    private Long pagesTotal(FspRequest request, long elementsTotal) {
        return (long) Math.ceil(1.0 * elementsTotal / request.getPage().size);
    }

    @Getter
    @NoArgsConstructor
    static class FspResponseInfo {
        Integer currentPage;
        Long totalElements;
        Long totalPages;

        public FspResponseInfo(Long totalElements) {
            this.totalElements = totalElements;
        }

        public FspResponseInfo(Integer currentPage, Long totalElements, Long totalPages) {
            this.currentPage = currentPage;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }

        public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    }

}
