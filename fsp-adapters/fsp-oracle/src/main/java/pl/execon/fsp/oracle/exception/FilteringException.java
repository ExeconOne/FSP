package pl.execon.fsp.oracle.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FilteringException extends RuntimeException {
    private final String message;
}
