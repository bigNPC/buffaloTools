package com.ddkj.buffalo.service.func.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 基础数据类.这里的排序和excel里面的排序一致
 **/
@Getter
@Setter
@EqualsAndHashCode
public class GiftDtoData {
    private String typeName;    //奖励类型名称:头饰、座驾、个人徽章、房间徽章、房间背景
    private Integer resourceId; //正式后台id
    private String rank;        //排名
    private Integer uid;        //用户
    private String days;        //天数或小时
    private String remark;      //其他
}