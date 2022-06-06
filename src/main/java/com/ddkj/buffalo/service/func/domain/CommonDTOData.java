package com.ddkj.buffalo.service.func.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 基础数据类.这里的排序和excel里面的排序一致
 **/
@Getter
@Setter
@EqualsAndHashCode
@Accessors(chain = true)
public class CommonDTOData {
    private String key;             //795-48-1
    private String typeName;        //头饰|2
    private String resourceId;      //795
    private String rank;            //1排名
    private Integer hours;          //48小时
    private String id;              //0（用于记录非商城id）
    private Integer actGift;        //作为vip等级
    private Integer remark;         //作为礼物数量
}