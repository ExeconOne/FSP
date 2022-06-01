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

/**
 * FspRequest class containing fields
 */
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class FspRequest {

    public static final int INITIAL_PAGE_NUMBER = 0;

    /**
     * List with {@link FilterInfo} objects
     */
    private List<FilterInfo> filter = new ArrayList<>();

    /**
     * Filter operator
     */
    private FspFilterOperator filterOperator;

    /**
     * List with {@link SortInfo} objects
     */
    private List<SortInfo> sort;

    /**
     * Field containing {@link FilterInfo} 
     */
    private PageInfo page;

    /**
     * @return information if {@link PageInfo} is not null
     */
    public boolean hasPageInfo() {
        return nonNull(page);
    }
}
