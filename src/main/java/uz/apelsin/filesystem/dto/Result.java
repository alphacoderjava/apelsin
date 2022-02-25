package uz.apelsin.filesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private boolean status;
    private String message;
    public Result(boolean status){
        this.status = status;
    }
    public Result(String message){
        this.message= message;
    }
}
