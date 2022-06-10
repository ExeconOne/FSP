package pl.execon.fsp.oracle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class FspTestObj {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String text;
    private int number;
    private LocalDateTime dateTime;
    private double floatingPointNumber;
    private LocalDate date;
    private float floatNumber;
    private boolean isBlue;
    private Colour colour;
    private long longValue;
    private Timestamp timestamp;


  public enum Colour{
      BLUE, RED, GREEN
  }
}
