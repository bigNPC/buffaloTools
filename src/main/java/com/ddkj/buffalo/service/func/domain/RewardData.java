package com.ddkj.buffalo.service.func.domain;

import lombok.Data;

import java.util.Map;

@Data
public class RewardData {
    /**
     * 单时间代表那天发奖励,范围时间则验证
     * 格式1|2022-02-05 05:00:00|2022-02-05 05:00:00
     * 格式2|2022-02-06 05:00:00   某天某时发奖
     * 格式3|2月                   某月份末发奖
     */
    private String activityTime;

    private Map<String, Map<String, ActResourceDTO>> reward;

}
