package pl.execon.fsp.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class FspRequest {

    public static final int INITIAL_PAGE_NUMBER = 0;

    private List<FilterInfo> filter = new ArrayList<>();
    private FspFilterOperator filterOperator;
    private List<SortInfo> sort;
    private PageInfo page;

    public boolean hasPageInfo() {
        return nonNull(page);
    }
}
