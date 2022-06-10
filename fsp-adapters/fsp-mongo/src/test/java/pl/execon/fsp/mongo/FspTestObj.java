package pl.execon.fsp.mongo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Document
@AllArgsConstructor
public class FspTestObj {

    @Id
    private String id;

    private String text;
    private int number;
    private LocalDateTime date;
    private InnerTestObj innerTestObj;


    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class InnerTestObj {
        private String innerText;
    }
}
