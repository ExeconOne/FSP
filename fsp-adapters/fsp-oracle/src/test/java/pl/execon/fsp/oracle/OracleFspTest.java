package pl.execon.fsp.oracle;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import pl.execon.fsp.core.FilterInfo;
import pl.execon.fsp.core.FspFilterOperator;
import pl.execon.fsp.core.FspRequest;
import pl.execon.fsp.core.FspResponse;
import pl.execon.fsp.core.Operation;
import pl.execon.fsp.core.PageInfo;
import pl.execon.fsp.core.SortInfo;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = FspTestRepository.class)
@EnableAutoConfiguration
class OracleFspTest {

    @Autowired
    private FspTestRepository repository;

    @BeforeAll
    static void beforeAll(@Autowired FspTestRepository repository) {
        List<FspTestObj> init = List.of(
                new FspTestObj(1L, "some text", 12, LocalDateTime.of(2022, 3, 15, 1, 1)),
                new FspTestObj(2L, "a some lorem", 13, LocalDateTime.of(2022, 3, 18, 20, 20)),
                new FspTestObj(3L, "Lorem Ipsum has been the industry's", 25, LocalDateTime.of(2022, 2, 1, 15, 15)),
                new FspTestObj(4L, "scrambled it to make a type specimen book", 121, LocalDateTime.of(1990, 4, 11, 21, 37))
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
                .filter(List.of(new FilterInfo("text", Operation.EQUALS, "some text")))
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
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(0).getDate())
        );
    }

    @Test
    void filterByTextNotEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(List.of(new FilterInfo("text", Operation.NOT_EQUALS, "some text")))
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
                .filter(List.of(new FilterInfo("number", Operation.NOT_IN, List.of(12, 13))))
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
    void filterByTextContains() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(List.of(new FilterInfo("text", Operation.CONTAINS, "some")))
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
    void filterWithOrOperatorAndTextEquals() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(List.of(
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
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(0).getDate()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 18, 20, 20), fspResult.getContent().get(1).getDate())
        );
    }

    @Test
    void filterWithOrOperatorAndDateLessOrEqualsSoredAsc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(List.of(
                        new FilterInfo("date", Operation.LESS_OR_EQUALS, LocalDateTime.of(2022, 3, 18, 20, 20), FspFilterOperator.OR))
                )
                .page(new PageInfo(0, 10))
                .sort(List.of(new SortInfo("date", SortInfo.Direction.ASC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(4, fspResult.getElementsCount()),
                () -> assertEquals(4, fspResult.getContent().size()),
                () -> assertEquals(LocalDateTime.of(1990, 4, 11, 21, 37), fspResult.getContent().get(0).getDate()),
                () -> assertEquals(LocalDateTime.of(2022, 2, 1, 15, 15), fspResult.getContent().get(1).getDate()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(2).getDate()),
                () -> assertEquals( LocalDateTime.of(2022, 3, 18, 20, 20), fspResult.getContent().get(3).getDate())
        );
    }

    @Test
    void filterWithOrOperatorAndDateGreaterOrEqualsSoredDesc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(List.of(
                        new FilterInfo("date", Operation.GREATER_OR_EQUALS, LocalDateTime.of(2022, 2, 1, 15, 15), FspFilterOperator.OR)
                ))
                .page(new PageInfo(0, 10))
                .sort(List.of(new SortInfo("date", SortInfo.Direction.DESC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 18, 20, 20), fspResult.getContent().get(0).getDate()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(1).getDate()),
                () -> assertEquals(LocalDateTime.of(2022, 2, 1, 15, 15), fspResult.getContent().get(2).getDate())
        );
    }

    @Test
    void filterWithNumberIn() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(List.of(new FilterInfo("number", Operation.IN, List.of(12, 13))))
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
    void filterWithOrOperatorAndDateLessThanSoredAsc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(List.of(
                        new FilterInfo("date", Operation.LESS_THAN, LocalDateTime.of(2022, 3, 18, 20, 20), FspFilterOperator.OR))
                )
                .page(new PageInfo(0, 10))
                .sort(List.of(new SortInfo("date", SortInfo.Direction.ASC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(3, fspResult.getElementsCount()),
                () -> assertEquals(3, fspResult.getContent().size()),
                () -> assertEquals(LocalDateTime.of(1990, 4, 11, 21, 37), fspResult.getContent().get(0).getDate()),
                () -> assertEquals(LocalDateTime.of(2022, 2, 1, 15, 15), fspResult.getContent().get(1).getDate()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(2).getDate())
        );
    }

    @Test
    void filterWithOrOperatorAndDateGreaterThanSoredDesc() {
        //given
        FspRequest fspRequest = FspRequest.builder()
                .filter(List.of(
                        new FilterInfo("date", Operation.GREATER_THAN, "2022-03-15T00:00", FspFilterOperator.OR)
                ))
                .page(new PageInfo(0, 10))
                .sort(List.of(new SortInfo("date", SortInfo.Direction.DESC)))
                .build();

        //when
        FspResponse<FspTestObj> fspResult = repository.findFsp(fspRequest);

        //then
        assertAll(
                () -> assertEquals(2, fspResult.getElementsCount()),
                () -> assertEquals(2, fspResult.getContent().size()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 18, 20, 20), fspResult.getContent().get(0).getDate()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 15, 1, 1), fspResult.getContent().get(1).getDate())
        );
    }

}
