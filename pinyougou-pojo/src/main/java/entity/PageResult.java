package entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
@Data
@ToString
public class PageResult implements Serializable {
    //总记录数
    private long total;
    //当前页结果
    private List rows;


    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }
}
