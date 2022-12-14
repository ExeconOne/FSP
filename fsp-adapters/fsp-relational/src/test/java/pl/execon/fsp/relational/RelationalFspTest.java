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
package pl.execon.fsp.relational;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.execon.fsp.core.FilterInfo;
import pl.execon.fsp.core.FspFilterOperator;
import pl.execon.fsp.core.FspRequest;
import pl.execon.fsp.core.FspResponse;
import pl.execon.fsp.core.Operation;
import pl.execon.fsp.core.PageInfo;
import pl.execon.fsp.core.SortInfo;
import pl.execon.fsp.relational.exception.FilteringException;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({SpringExtension.class})
@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = {FspTestRepository.class, FspInnerObjTestRepository.class})
@EnableAutoConfiguration
class RelationalFspTest {

    @Autowired
    private FspTestRepository repository;

    @BeforeAll
    static void beforeAll(@Autowired FspTestRepository repository, @Autowired FspInnerObjTestRepository innerObjTestRepository) {
        InnerTestObj innerTestObj = new InnerTestObj(1L, "a");
        InnerTestObj innerTestObj2 = new InnerTestObj(2L, "b");
        innerObjTestRepository.save(innerTestObj);
        innerObjTestRepository.save(innerTestObj2);
        List<FspTestObj> init = Arrays.asList(
                new FspTestObj(1L, "some text", 12, LocalDateTime.of(2022, 3, 15, 1, 1), 12.5, LocalDate.of(2022, 3, 15), 5.5f, true, FspTestObj.Colour.BLUE, 5L, Timestamp.valueOf(LocalDateTime.of(2022, 3, 15, 1, 1)), innerTestObj),
                new FspTestObj(2L, "a some lorem", 13, LocalDateTime.of(2022, 3, 18, 20, 20), 13.5, LocalDate.of(2022, 3, 18), 3.7f, false, FspTestObj.Colour.GREEN, 245L, Timestamp.valueOf(LocalDateTime.of(2022, 3, 18, 20, 20)), innerTestObj),
                new FspTestObj(3L, "Lorem Ipsum has been the industry's", 25, LocalDateTime.of(2022, 2, 1, 15, 15), 25.4, LocalDate.of(2022, 2, 1), 67.8f, true, FspTestObj.Colour.BLUE, 33L, Timestamp.valueOf(LocalDateTime.of(2022, 2, 1, 15, 15)), innerTestObj2),
                new FspTestObj(4L, "scrambled it to make a type specimen book", 121, LocalDateTime.of(1990, 4, 11, 21, 37), 121.6, LocalDate.of(1990, 4, 11), 47.5f, false, FspTestObj.Colour.RED, 19L, Timestamp.valueOf(LocalDateTime.of(1990, 4, 11, 21, 37)), innerTestObj2)
        );
        repository.saveAll(init);
    }


    @Test
    void findAllWithEmptyFspRequest() {
        //given
        FspRequest fspRequest = new FspRequest();
        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);
        //then
        assertAll(
                () -> assertEquals(4, fspResult.getElementsCount()),
                () -> assertEquals(4, fspResult.getContent().size())
        );
    }

    @Test
    void filterByTextEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("text", Operation.EQUALS, "some text")))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(1, fspResult.getElementsCount()),
                () -> assertEquals(1, fspResult.getContent().size()),
                () -> assertEquals(1, fspResult.getContent().get(0).getId()),
                () -> assertEquals("some text", fspResult.getContent().get(0).getText()),
                () -> assertEquals(12, fspResult.getContent().get(0).getNumber()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(0).getDateTime())
        );
    }

    @Test
    void filterByNumberEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("number", Operation.EQUALS, 12)))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(1, fspResult.getElementsCount()),
                () -> assertEquals(1, fspResult.getContent().size()),
                () -> assertEquals(1, fspResult.getContent().get(0).getId()),
                () -> assertEquals("some text", fspResult.getContent().get(0).getText()),
                () -> assertEquals(12, fspResult.getContent().get(0).getNumber()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(0).getDateTime())
        );
    }

    @Test
    void filterByFloatNumberEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("floatNumber", Operation.EQUALS, 5.5f)))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(1, fspResult.getElementsCount()),
                () -> assertEquals(1, fspResult.getContent().size()),
                () -> assertEquals(1, fspResult.getContent().get(0).getId()),
                () -> assertEquals("some text", fspResult.getContent().get(0).getText()),
                () -> assertEquals(5.5f, fspResult.getContent().get(0).getFloatNumber()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(0).getDateTime())
        );
    }

    @Test
    void filterByBooleanEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("isBlue", Operation.EQUALS, true)))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(1L, fspResult.getContent().get(0).getId()),
                () -> assertEquals(3L, fspResult.getContent().get(1).getId()),
                () -> assertEquals("some text", fspResult.getContent().get(0).getText()),
                () -> assertEquals("Lorem Ipsum has been the industry's", fspResult.getContent().get(1).getText())

        );
    }

    @Test
    void filterByLongEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("longValue", Operation.EQUALS, 245L)))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(1, fspResult.getElementsCount()),
                () -> assertEquals(1, fspResult.getContent().size()),
                () -> assertEquals(2L, fspResult.getContent().get(0).getId())
        );
    }

    @Test
    void filterByEnumEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("colour", Operation.EQUALS, FspTestObj.Colour.BLUE)))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(1, fspResult.getContent().get(0).getId()),
                () -> assertEquals("some text", fspResult.getContent().get(0).getText()),
                () -> assertEquals("Lorem Ipsum has been the industry's", fspResult.getContent().get(1).getText())

        );
    }

    @Test
    void filterByFloatingPointNumberEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("floatingPointNumber", Operation.EQUALS, 12.5)))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(1, fspResult.getElementsCount()),
                () -> assertEquals(1, fspResult.getContent().size()),
                () -> assertEquals(1, fspResult.getContent().get(0).getId()),
                () -> assertEquals("some text", fspResult.getContent().get(0).getText()),
                () -> assertEquals(12.5, fspResult.getContent().get(0).getFloatingPointNumber()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(0).getDateTime())
        );
    }

    @Test
    void filterByDateTimeEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("dateTime", Operation.EQUALS, LocalDateTime.of(2022, 3, 15, 1, 1))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(1, fspResult.getElementsCount()),
                () -> assertEquals(1, fspResult.getContent().size()),
                () -> assertEquals(1, fspResult.getContent().get(0).getId()),
                () -> assertEquals("some text", fspResult.getContent().get(0).getText()),
                () -> assertEquals(12, fspResult.getContent().get(0).getNumber()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(0).getDateTime())
        );
    }

    @Test
    void filterByTimestampEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("timestamp", Operation.EQUALS, "2022-03-18 20:20:00")))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(1, fspResult.getElementsCount()),
                () -> assertEquals(1, fspResult.getContent().size()),
                () -> assertEquals(2L, fspResult.getContent().get(0).getId()),
                () -> assertEquals("a some lorem", fspResult.getContent().get(0).getText()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 18, 20, 20), fspResult.getContent().get(0).getDateTime())
        );
    }

    @Test
    void filterByDateEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("date", Operation.EQUALS, LocalDate.of(2022, 3, 15))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(1, fspResult.getElementsCount()),
                () -> assertEquals(1, fspResult.getContent().size()),
                () -> assertEquals(1, fspResult.getContent().get(0).getId()),
                () -> assertEquals("some text", fspResult.getContent().get(0).getText()),
                () -> assertEquals(12, fspResult.getContent().get(0).getNumber()),
                () -> assertEquals(LocalDate.of(2022, 3, 15), fspResult.getContent().get(0).getDate())
        );
    }

    @Test
    void filterByTextNotEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("text", Operation.NOT_EQUALS, "some text")))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size())
        );
    }

    @Test
    void filterByNumberNotEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("number", Operation.NOT_EQUALS, 12)))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size())
        );
    }

    @Test
    void filterByFloatingPointNumberNotEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("floatingPointNumber", Operation.NOT_EQUALS, 12.5)))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size())
        );
    }

    @Test
    void filterByDateTimeNotEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("dateTime", Operation.NOT_EQUALS, (LocalDateTime.of(2022, 3, 15, 1, 1)))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size())
        );
    }

    @Test
    void filterByDateNotEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("date", Operation.NOT_EQUALS, (LocalDate.of(2022, 3, 15)))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size())
        );
    }

    @Test
    void filterByNumberNotIn() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("number", Operation.NOT_IN, Arrays.asList(12, 13))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(25, fspResult.getContent().get(0).getNumber()),
                () -> assertEquals(121, fspResult.getContent().get(1).getNumber())
        );
    }

    @Test
    void filterByFloatNumberIn() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("floatNumber", Operation.IN, Arrays.asList(5.5f, 47.5f))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(5.5f, fspResult.getContent().get(0).getFloatNumber()),
                () -> assertEquals(47.5f, fspResult.getContent().get(1).getFloatNumber())
        );
    }

    @Test
    void filterByEnumIn() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("colour", Operation.IN, Arrays.asList(FspTestObj.Colour.BLUE, FspTestObj.Colour.GREEN))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(FspTestObj.Colour.BLUE, fspResult.getContent().get(0).getColour()),
                () -> assertEquals(FspTestObj.Colour.GREEN, fspResult.getContent().get(1).getColour()),
                () -> assertEquals(FspTestObj.Colour.BLUE, fspResult.getContent().get(2).getColour())
        );
    }

    @Test
    void filterByBooleanIn() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("isBlue", Operation.IN, Arrays.asList(true))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(1L, fspResult.getContent().get(0).getId()),
                () -> assertEquals(3L, fspResult.getContent().get(1).getId()),
                () -> assertEquals(FspTestObj.Colour.BLUE, fspResult.getContent().get(0).getColour()),
                () -> assertEquals(FspTestObj.Colour.BLUE, fspResult.getContent().get(1).getColour())
        );
    }

    @Test
    void filterByLongValueIn() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("longValue", Operation.IN, Arrays.asList(5L, 19L))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(1L, fspResult.getContent().get(0).getId()),
                () -> assertEquals(4L, fspResult.getContent().get(1).getId()),
                () -> assertEquals(5L, fspResult.getContent().get(0).getLongValue()),
                () -> assertEquals(19L, fspResult.getContent().get(1).getLongValue())
        );
    }

    @Test
    void filterByFloatingPointNumberNotIn() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("floatingPointNumber", Operation.NOT_IN, Arrays.asList(12.5, 13.5))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(25.4, fspResult.getContent().get(0).getFloatingPointNumber()),
                () -> assertEquals(121.6, fspResult.getContent().get(1).getFloatingPointNumber())
        );
    }

    @Test
    void filterByTextNotIn() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("text", Operation.NOT_IN, Arrays.asList("some text", "a some lorem"))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals("Lorem Ipsum has been the industry's", fspResult.getContent().get(0).getText()),
                () -> assertEquals("scrambled it to make a type specimen book", fspResult.getContent().get(1).getText())
        );
    }

    @Test
    void filterByDateTimeNotIn() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("dateTime", Operation.NOT_IN, Arrays.asList((LocalDateTime.of(2022, 3, 15, 1, 1))))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 18, 20, 20), fspResult.getContent().get(0).getDateTime()),
                () -> assertEquals(LocalDateTime.of(2022, 2, 1, 15, 15), fspResult.getContent().get(1).getDateTime()),
                () -> assertEquals(LocalDateTime.of(1990, 4, 11, 21, 37), fspResult.getContent().get(2).getDateTime())
        );
    }

    @Test
    void filterByDateNotIn() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("date", Operation.NOT_IN, Arrays.asList((LocalDate.of(2022, 3, 15))))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(LocalDate.of(2022, 3, 18), fspResult.getContent().get(0).getDate()),
                () -> assertEquals(LocalDate.of(2022, 2, 1), fspResult.getContent().get(1).getDate()),
                () -> assertEquals(LocalDate.of(1990, 4, 11), fspResult.getContent().get(2).getDate())
        );
    }

    @Test
    void filterByTextContains() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("text", Operation.CONTAINS, "some")))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(1L, fspResult.getContent().get(0).getId()),
                () -> assertEquals(2L, fspResult.getContent().get(1).getId())
        );
    }

    @Test
    void filterByNumberContains() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("number", Operation.CONTAINS, 1)))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(1L, fspResult.getContent().get(0).getId()),
                () -> assertEquals(2L, fspResult.getContent().get(1).getId()),
                () -> assertEquals(4L, fspResult.getContent().get(2).getId())
        );
    }

    @Test
    void filterByFloatingPointNumberContains() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("floatingPointNumber", Operation.CONTAINS, 1)))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(1L, fspResult.getContent().get(0).getId()),
                () -> assertEquals(2L, fspResult.getContent().get(1).getId()),
                () -> assertEquals(4L, fspResult.getContent().get(2).getId())
        );
    }

    @Test
    void filterByDateTimeContains() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("dateTime", Operation.CONTAINS, 2022)))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(1L, fspResult.getContent().get(0).getId()),
                () -> assertEquals(2L, fspResult.getContent().get(1).getId()),
                () -> assertEquals(3L, fspResult.getContent().get(2).getId())
        );
    }

    @Test
    void filterByDateContains() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("date", Operation.CONTAINS, 2022)))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(1L, fspResult.getContent().get(0).getId()),
                () -> assertEquals(2L, fspResult.getContent().get(1).getId()),
                () -> assertEquals(3L, fspResult.getContent().get(2).getId())
        );
    }

    @Test
    void filterWithOrOperatorAndTextEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("text", Operation.EQUALS, "some text", FspFilterOperator.OR),
                        new FilterInfo("text", Operation.EQUALS, "a some lorem", FspFilterOperator.OR)))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(1L, fspResult.getContent().get(0).getId()),
                () -> assertEquals("some text", fspResult.getContent().get(0).getText()),
                () -> assertEquals(12, fspResult.getContent().get(0).getNumber()),
                () -> assertEquals(2L, fspResult.getContent().get(1).getId()),
                () -> assertEquals("a some lorem", fspResult.getContent().get(1).getText()),
                () -> assertEquals(13, fspResult.getContent().get(1).getNumber()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(0).getDateTime()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 18, 20, 20), fspResult.getContent().get(1).getDateTime())
        );
    }

    @Test
    void filterWithAndOperatorAndLongValueEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("longValue", Operation.EQUALS, 5L, FspFilterOperator.AND),
                        new FilterInfo("id", Operation.EQUALS, 1L, FspFilterOperator.AND)))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(1, fspResult.getElementsCount()),
                () -> assertEquals(1, fspResult.getContent().size()),
                () -> assertEquals(1L, fspResult.getContent().get(0).getId()),
                () -> assertEquals("some text", fspResult.getContent().get(0).getText()),
                () -> assertEquals(12, fspResult.getContent().get(0).getNumber()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(0).getDateTime())
        );
    }


    @Test
    void filterWithOrOperatorAndDateTimeLessOrEqualsSoredAsc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("dateTime", Operation.LESS_OR_EQUALS, LocalDateTime.of(2022, 3, 18, 20, 20), FspFilterOperator.OR))
                )
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("dateTime", SortInfo.Direction.ASC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(4, fspResult.getElementsCount()),
                () -> assertEquals(4, fspResult.getContent().size()),
                () -> assertEquals(LocalDateTime.of(1990, 4, 11, 21, 37), fspResult.getContent().get(0).getDateTime()),
                () -> assertEquals(LocalDateTime.of(2022, 2, 1, 15, 15), fspResult.getContent().get(1).getDateTime()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(2).getDateTime()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 18, 20, 20), fspResult.getContent().get(3).getDateTime())
        );
    }


    @Test
    void filterWithOrOperatorAndFloatingPointNumberLessOrEqualsSoredAsc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("floatingPointNumber", Operation.LESS_OR_EQUALS, 25.4, FspFilterOperator.OR))
                )
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("floatingPointNumber", SortInfo.Direction.ASC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(12.5, fspResult.getContent().get(0).getFloatingPointNumber()),
                () -> assertEquals(13.5, fspResult.getContent().get(1).getFloatingPointNumber()),
                () -> assertEquals(25.4, fspResult.getContent().get(2).getFloatingPointNumber())
        );
    }

    @Test
    void filterWithOrOperatorAndNumberLessOrEqualsSoredAsc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("number", Operation.LESS_OR_EQUALS, 25, FspFilterOperator.OR))
                )
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("number", SortInfo.Direction.ASC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(12, fspResult.getContent().get(0).getNumber()),
                () -> assertEquals(13, fspResult.getContent().get(1).getNumber()),
                () -> assertEquals(25, fspResult.getContent().get(2).getNumber())
        );
    }


    @Test
    void filterWithOrOperatorAndDateTimeGreaterOrEqualsSoredDesc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("dateTime", Operation.GREATER_OR_EQUALS, LocalDateTime.of(2022, 2, 1, 15, 15), FspFilterOperator.OR)
                ))
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("dateTime", SortInfo.Direction.DESC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 18, 20, 20), fspResult.getContent().get(0).getDateTime()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(1).getDateTime()),
                () -> assertEquals(LocalDateTime.of(2022, 2, 1, 15, 15), fspResult.getContent().get(2).getDateTime())
        );
    }

    @Test
    void filterWithOrOperatorAndDateGreaterOrEqualsSoredDesc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("date", Operation.GREATER_OR_EQUALS, LocalDate.of(2022, 2, 1), FspFilterOperator.OR)
                ))
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("date", SortInfo.Direction.DESC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(LocalDate.of(2022, 3, 18), fspResult.getContent().get(0).getDate()),
                () -> assertEquals(LocalDate.of(2022, 3, 15), fspResult.getContent().get(1).getDate()),
                () -> assertEquals(LocalDate.of(2022, 2, 1), fspResult.getContent().get(2).getDate())
        );
    }

    @Test
    void filterWithOrOperatorAndFloatingPointNumberGreaterOrEqualsSoredDesc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("floatingPointNumber", Operation.GREATER_OR_EQUALS, 13.5, FspFilterOperator.OR)
                ))
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("floatingPointNumber", SortInfo.Direction.DESC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(121.6, fspResult.getContent().get(0).getFloatingPointNumber()),
                () -> assertEquals(25.4, fspResult.getContent().get(1).getFloatingPointNumber()),
                () -> assertEquals(13.5, fspResult.getContent().get(2).getFloatingPointNumber())
        );
    }


    @Test
    void filterWithOrOperatorAndNumberGreaterOrEqualsSoredDesc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("number", Operation.GREATER_OR_EQUALS, 13, FspFilterOperator.OR)
                ))
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("number", SortInfo.Direction.DESC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(121, fspResult.getContent().get(0).getNumber()),
                () -> assertEquals(25, fspResult.getContent().get(1).getNumber()),
                () -> assertEquals(13, fspResult.getContent().get(2).getNumber())
        );
    }


    @Test
    void filterWithNumberIn() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("number", Operation.IN, Arrays.asList(12, 13))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(12, fspResult.getContent().get(0).getNumber()),
                () -> assertEquals(13, fspResult.getContent().get(1).getNumber())
        );
    }

    @Test
    void filterWithFloatingPointNumberIn() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("floatingPointNumber", Operation.IN, Arrays.asList(12.5, 13.5))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(12.5, fspResult.getContent().get(0).getFloatingPointNumber()),
                () -> assertEquals(13.5, fspResult.getContent().get(1).getFloatingPointNumber())
        );
    }

    @Test
    void filterWithTextIn() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("text", Operation.IN, Arrays.asList("some text", "a some lorem"))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals("some text", fspResult.getContent().get(0).getText()),
                () -> assertEquals("a some lorem", fspResult.getContent().get(1).getText())
        );
    }

    @Test
    void filterWithDataTimeIn() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("dateTime", Operation.IN, Arrays.asList(LocalDateTime.of(2022, 3, 15, 1, 1), LocalDateTime.of(2022, 3, 18, 20, 20)))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(0).getDateTime()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 18, 20, 20), fspResult.getContent().get(1).getDateTime())
        );
    }

    @Test
    void filterWithDateIn() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("date", Operation.IN, Arrays.asList(LocalDate.of(2022, 3, 15), LocalDate.of(2022, 3, 18)))))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(LocalDate.of(2022, 3, 15), fspResult.getContent().get(0).getDate()),
                () -> assertEquals(LocalDate.of(2022, 3, 18), fspResult.getContent().get(1).getDate())
        );
    }


    @Test
    void filterWithOrOperatorAndDateTimeLessThanSoredAsc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("dateTime", Operation.LESS_THAN, LocalDateTime.of(2022, 3, 18, 20, 20), FspFilterOperator.OR)))
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("dateTime", SortInfo.Direction.ASC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(LocalDateTime.of(1990, 4, 11, 21, 37), fspResult.getContent().get(0).getDateTime()),
                () -> assertEquals(LocalDateTime.of(2022, 2, 1, 15, 15), fspResult.getContent().get(1).getDateTime()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(2).getDateTime())
        );
    }

    @Test
    void filterWithOrOperatorAndDateLessThanSoredAsc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("date", Operation.LESS_THAN, LocalDate.of(2022, 3, 18), FspFilterOperator.OR)))
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("date", SortInfo.Direction.ASC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(LocalDate.of(1990, 4, 11), fspResult.getContent().get(0).getDate()),
                () -> assertEquals(LocalDate.of(2022, 2, 1), fspResult.getContent().get(1).getDate()),
                () -> assertEquals(LocalDate.of(2022, 3, 15), fspResult.getContent().get(2).getDate())
        );
    }

    @Test
    void filterWithOrOperatorAndNumberLessThanSoredAsc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("number", Operation.LESS_THAN, 25, FspFilterOperator.OR)))
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("number", SortInfo.Direction.ASC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(12, fspResult.getContent().get(0).getNumber()),
                () -> assertEquals(13, fspResult.getContent().get(1).getNumber())
        );
    }

    @Test
    void filterWithOrOperatorAndFloatingPointNumberLessThanSoredAsc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("floatingPointNumber", Operation.LESS_THAN, 25.4, FspFilterOperator.OR)))
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("floatingPointNumber", SortInfo.Direction.ASC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(12.5, fspResult.getContent().get(0).getFloatingPointNumber()),
                () -> assertEquals(13.5, fspResult.getContent().get(1).getFloatingPointNumber())
        );
    }

    @Test
    void filterWithOrOperatorAndDateTimeGreaterThanSoredDesc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("dateTime", Operation.GREATER_THAN, "2022-03-15T00:00", FspFilterOperator.OR)
                ))
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("dateTime", SortInfo.Direction.DESC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 18, 20, 20), fspResult.getContent().get(0).getDateTime()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(1).getDateTime())
        );
    }

    @Test
    void filterWithOrOperatorAndDateGreaterThanSoredDesc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("date", Operation.GREATER_THAN, LocalDate.of(2022, 3, 15), FspFilterOperator.OR)
                ))
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("date", SortInfo.Direction.DESC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(1, fspResult.getElementsCount()),
                () -> assertEquals(1, fspResult.getContent().size()),
                () -> assertEquals(LocalDate.of(2022, 3, 18), fspResult.getContent().get(0).getDate())
        );
    }

    @Test
    void filterWithOrOperatorAndFloatNumberGreaterThanSoredDesc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("floatNumber", Operation.GREATER_THAN, 5.5f, FspFilterOperator.OR)
                ))
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("floatNumber", SortInfo.Direction.DESC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(67.8f, fspResult.getContent().get(0).getFloatNumber()),
                () -> assertEquals(47.5f, fspResult.getContent().get(1).getFloatNumber())
        );
    }

    @Test
    void filterWithOrOperatorAndLongValueGreaterThanSoredDesc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("longValue", Operation.GREATER_THAN, 19L, FspFilterOperator.OR)
                ))
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("longValue", SortInfo.Direction.DESC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(245L, fspResult.getContent().get(0).getLongValue()),
                () -> assertEquals(33L, fspResult.getContent().get(1).getLongValue())
        );
    }

    @Test
    void filterWithOrOperatorAndTimestampGreaterThanSoredDesc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("timestamp", Operation.GREATER_THAN, "2022-01-09 15:01:25", FspFilterOperator.OR)
                ))
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("timestamp", SortInfo.Direction.DESC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 18, 20, 20), fspResult.getContent().get(0).getDateTime()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(1).getDateTime()),
                () -> assertEquals(LocalDateTime.of(2022, 2, 1, 15, 15), fspResult.getContent().get(2).getDateTime())
        );
    }

    @Test
    void filterWithOrOperatorAndNumberGreaterThanSoredDesc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("number", Operation.GREATER_THAN, 13, FspFilterOperator.OR)
                ))
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("number", SortInfo.Direction.DESC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(25, fspResult.getContent().get(1).getNumber()),
                () -> assertEquals(121, fspResult.getContent().get(0).getNumber())
        );
    }

    @Test
    void filterWithOrOperatorAndFloatingPointNumberGreaterThanSoredDesc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(
                        new FilterInfo("floatingPointNumber", Operation.GREATER_THAN, 13.5, FspFilterOperator.OR)
                ))
                .page(new PageInfo(0, 10))
                .sort(Arrays.asList(new SortInfo("floatingPointNumber", SortInfo.Direction.DESC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(25.4, fspResult.getContent().get(1).getFloatingPointNumber()),
                () -> assertEquals(121.6, fspResult.getContent().get(0).getFloatingPointNumber())
        );
    }

    @Test
    void shouldThrowsFilteringException() throws FilteringException {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("text", Operation.GREATER_THAN, "test")))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FilteringException exception = assertThrows(FilteringException.class, () -> repository.findFsp(fspRequest));

        //then
        assertTrue(exception.getMessage().contains("Unsupported field/operator combination:"));

    }

    @Test
    void shouldThrowsFilteringExceptionWhenEqualsObject() throws FilteringException {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo("innerObj", Operation.EQUALS, "test")))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FilteringException exception = assertThrows(FilteringException.class, () -> repository.findFsp(fspRequest));

        //then
        assertTrue(exception.getMessage().contains("Unsupported field/operator combination:"));

    }

    @ParameterizedTest
    @CsvSource({"innerObj.innerText"})
    void shouldReturnFspForInnerObjectEquals(String value) throws FilteringException {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(Arrays.asList(new FilterInfo(value, Operation.IN, Arrays.asList("a", "b"))))
                .sort(Arrays.asList(new SortInfo("innerObj.innerText", SortInfo.Direction.DESC)))
                .page(new PageInfo(0, 10))
                .build();

        //when
        FspResponse<FspTestObj> fsp = repository.findFsp(fspRequest);
        //then

        assertAll(
                () -> assertEquals(4, fsp.getElementsCount()),
                () -> assertEquals("a", fsp.getContent().get(3).getInnerObj().getInnerText()),
                () -> assertEquals("b", fsp.getContent().get(0).getInnerObj().getInnerText())
        );
    }
}


