package entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
@Data
@Getter
@Setter
@ToString
public class Result implements Serializable {
    private boolean success;
    private String message;

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
