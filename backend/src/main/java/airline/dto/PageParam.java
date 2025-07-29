package airline.dto;


import lombok.Data;

@Data
public class PageParam {
    private Integer pageSize =10;
    private Integer pageNum = 0;
}
