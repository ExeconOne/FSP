package pl.execon.fsp.core;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.security.InvalidParameterException;

/**
 * Class containing page info
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
public class PageInfo {
    /**
     * Page number
     */
    protected int number;
    /**
     * Page size
     */
    protected int size;

    /**
     * Default constructor for {@link PageInfo}
     */
    public PageInfo(int number, int size) {
        if(number < 0 || size <= 0) throw new InvalidParameterException();
        this.number = number;
        this.size = size;
    }
}
