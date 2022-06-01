package pl.execon.fsp.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class containing sort info parameters.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SortInfo {

    /**
     * Value by which field will be sorted
     */
    protected String by;

    /**
     * Sorting direction
     */
    protected Direction direction;


    /**
     * Enum with values for sort direction
     */
    public enum Direction{
        ASC, DESC
    }
}
