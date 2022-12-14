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
package pl.execon.fsp.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.execon.fsp.core.FspFilterOperator;

/**
 * Class containing all filtering parameters
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilterInfo {

    /**
     * Value by which field will be filtered
     */
    private String by;

    /**
     * Filter operation
     */
    private Operation operation;

    /**
     * Value which
     */
    private Object value;

    /**
     * Filter operator
     */
    private FspFilterOperator operator;

    /**
     * Default constructor for {@link FilterInfo} with default {@link FspFilterOperator#AND}
     */
    public FilterInfo(String by, Operation operation, Object value) {
        this.by = by;
        this.operation = operation;
        this.value = value;
        this.operator = FspFilterOperator.AND;
    }
}
