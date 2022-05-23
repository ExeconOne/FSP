package pl.execon.fsp.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SortInfo {

    protected String by;
    protected Direction direction;

    public enum Direction{
        ASC,DESC
    }

}
