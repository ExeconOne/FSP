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
package pl.execon.fsp.oracle;

import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pl.execon.fsp.core.FspRequest;
import pl.execon.fsp.core.SortInfo;

import java.util.stream.Collectors;

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
                .collect(Collectors.toList()));
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
