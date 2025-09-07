package com.zyk.consumer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ShoppingVo {

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "类别编码")
    private String kindNo;

}
