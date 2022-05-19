package pl.execon;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class FilterInfo {
    private String by;
    private Operation operation;
    private Object value;
    private FspFilterOperator operator;

    public FilterInfo(String by, Operation operation, Object value) {
        this.by = by;
        this.operation = operation;
        this.value = value;
        this.operator = FspFilterOperator.AND;
    }
}
