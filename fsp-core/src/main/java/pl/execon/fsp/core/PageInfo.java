package pl.execon.fsp.core;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.security.InvalidParameterException;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class PageInfo {
    protected int number;
    protected int size;

    public PageInfo(int number, int size) {
        if(number < 0 || size <= 0) throw new InvalidParameterException();
        this.number = number;
        this.size = size;
    }
}
