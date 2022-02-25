package uz.apelsin.filesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Filter {
    private String name;

    private Date period;
    private Date last;

    private Long sizeLeft;
    private Long sizeRight;
}
