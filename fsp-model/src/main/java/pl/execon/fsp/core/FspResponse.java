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
