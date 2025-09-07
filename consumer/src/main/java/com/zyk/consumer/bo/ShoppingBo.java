package com.zyk.consumer.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ShoppingBo {

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "类别编码")
    private String kindNo;

}
