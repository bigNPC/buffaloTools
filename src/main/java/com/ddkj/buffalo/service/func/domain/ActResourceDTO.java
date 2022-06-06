package com.ddkj.buffalo.service.func.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class ActResourceDTO implements Serializable {

    private String id;

    private Integer resourceId;

    private String name;

    private Integer num;

    private Integer uid;

    private int type;

    private String image;
    private Integer limit;
    //可作为vip等级
    private Integer actGift = 0;

    /**
     * 男1，女2  0默认
     */
    private Integer maleOrFemale = 0;

    /**
     * 消耗的分值
     */
    private Integer consumeScore;
    private Object other;
    private Integer giftNumber;
    private Integer roomId;
    /**
     * 排名
     */
    private String rank;
    private Integer price;
    private List<ActResourceDTO> reward;


    public ActResourceDTO() {
    }

    public ActResourceDTO(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public ActResourceDTO(Integer id, Integer resourceId, Integer num, int type, String name, String image) {
        this.id = id.toString();
        this.resourceId = resourceId;
        this.name = name;
        this.num = num;
        this.type = type;
        this.image=image;
    }

    public ActResourceDTO(Integer id, Integer resourceId, Integer num, int type, Integer maleOrFemale, String name, String image) {
        this.id = id.toString();
        this.resourceId = resourceId;
        this.name = name;
        this.num = num;
        this.type = type;
        this.image = image;
        this.maleOrFemale = maleOrFemale;
    }

    public ActResourceDTO(Integer id, Integer resourceId, Integer num, int type, String name, String image, Integer consumeScore) {
        this.id = id.toString();
        this.resourceId = resourceId;
        this.name = name;
        this.num = num;
        this.type = type;
        this.image = image;
        this.consumeScore = consumeScore;
    }

    public ActResourceDTO(Integer id, Integer resourceId, Integer num, int type, Integer limit, Integer actGift, Integer consumeScore, Integer price, String image) {
        this.id = id.toString();
        this.resourceId = resourceId;
        this.limit = limit;
        this.num = num;
        this.type = type;
        this.actGift = actGift;
        this.consumeScore = consumeScore;
        this.price = price;
        this.image = image;
    }
}
