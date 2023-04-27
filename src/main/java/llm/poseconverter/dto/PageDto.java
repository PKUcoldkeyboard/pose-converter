package llm.poseconverter.dto;

import javax.validation.constraints.NotBlank;

public class PageDto {
    @NotBlank(message = "pageNum不能为空")
    private Long pageNum;
    @NotBlank(message = "pageSize不能为空")
    private Long pageSize;
    public Long getPageNum() {
        return pageNum;
    }
    public void setPageNum(Long pageNum) {
        this.pageNum = pageNum;
    }
    public Long getPageSize() {
        return pageSize;
    }
    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }
    
}
