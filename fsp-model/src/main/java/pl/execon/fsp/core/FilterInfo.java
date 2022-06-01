package pl.execon.fsp.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class containing all filtering parameters
 */
@Getter
@AllArgsConstructor
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
