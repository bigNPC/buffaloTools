package com.ddkj.buffalo.service.func.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 道具列表，承接ShopEnums
 */
public enum ResDtoEnums {
	DrawNull(0, 0, "Please Try Again","حظ سعيد المرة القادمة","再接再厉", "https://bobo-h5clf.nanshandjs.com/1647404112DrawNull.png", 0),
	Symbol(1, 8,"Symbol","لقب","徽章", "Symbol", 15),
	Frame(2, 2, "Frame", "إطار","头饰", "ShopProduct",14),
	Theme(3, 3, "Theme", "خلفية","房间背景", "ShopProduct",12),
	Car(4, 1, "Car", "سيارة","座驾", "ShopProduct", 100),
	Coins(5, -1, "Coins", "عملة ذهبية","金币", "CDNConstant.COINS", 501),
	CuteNumber(6, -1, "CuteNumber", "أيدي مميز","靓号", "CDNConstant.CUTE_NUMBER", 999),
	GiftBack(7, -1, "GiftBack", "هدية","背包免费礼物", "GiftBack", -1),
	Bubble(8, 9, "Bubble", "إطار دردشة","气泡", "ShopProduct",10),
	FLOAT(9, 7, "Homepages Effects", "تأثيرات", "飘屏/主页飘", "ShopProduct",11),
	LUCKY_TICKET(10, -1, "Draw Ticket", "تذكرة السحب ", "抽奖券", "http://bobo-ugc.nanshandjs.com/default/free_box.png", 13),
	VIP(11, -1, "VIP", "كبار الشخصيات", "VIP", "VIP", 500),


	OfficeMsg(-1, -1, "OfficeMsg", "OfficeMsg", "全服小助手", "", 0),
	OnlineRoom(-2, -1, "OnlineRoom", "OnlineRoom", "全服在线房间播报", "", 0),
	Activity(-1000, -1, "activityImage", "activityImage", "活动图片类型", "", 0),
	;

	@Getter
	public enum VIPImage{
		VIP1(1, "https://bobo-h5clf.nanshandjs.com/1647416698VIP1.png"),
		VIP2(2, "https://bobo-h5clf.nanshandjs.com/1647416704VIP2.png"),
		VIP3(3, "https://bobo-h5clf.nanshandjs.com/1647416708VIP3.png"),
		VIP4(4, "https://bobo-h5clf.nanshandjs.com/1647416713VIP4.png"),
		VIP5(5, "https://bobo-h5clf.nanshandjs.com/1647416717VIP5.png"),
		VIP6(6, "https://bobo-h5clf.nanshandjs.com/1647416725VIP6.png"),
		;
		private final Integer level;
		private final String image;
		VIPImage(Integer level, String image) {
			this.level = level;
			this.image = image;
		}
	}
	public static VIPImage getByVIPImageType(int type) {
		return Arrays.stream(VIPImage.values()).filter(e -> e.level.equals(type)).findFirst().orElse(null);
	}

	private final Integer type;		//功能道具类型
	private final Integer ShopType;	//商城类型
	private final String En;
	private final String Arab;
	private final String Cn;
	private final String imgType;	//道具大图(有活动小图,配置在活动json)
	private final Integer sortValue;//排序优先值

	ResDtoEnums(Integer type, Integer ShopType, String En, String Arab, String Cn, String imgType, Integer sortValue) {
		this.type = type;
		this.ShopType = ShopType;
		this.En = En;
		this.Arab = Arab;
		this.Cn = Cn;
		this.imgType = imgType;
		this.sortValue = sortValue;
	}
	public String En() {
		return this.En;
	}
	public String Ar() {
		return this.Arab;
	}
	public String Cn() {
		return this.Cn;
	}
	public Integer Type() {
		return this.type;
	}
	public String getImgType() {
		return imgType;
	}
	public Integer getSortValue() {
		return sortValue;
	}
	public Integer getShopType() {
		return ShopType;
	}

	public static ResDtoEnums getByType(int type) {
		return Arrays.stream(ResDtoEnums.values()).filter(e -> e.type.equals(type)).findFirst().orElse(null);
	}

	/**
	 * 根据类型名称获取类型
	 */
	public static Integer getRefIdType(String typeName) {
		ResDtoEnums resDtoEnums = Arrays.stream(ResDtoEnums.values()).filter(e -> e.Cn.contains(typeName)).findFirst().orElse(null);
		if(resDtoEnums == null){
			return null;
		}
		return resDtoEnums.Type();
	}


	/**
	 * 根据商城类型获取功能道具类型
	 */
	public static int getTypeByShopType(int shopType) {
		ResDtoEnums resDtoEnums = Arrays.stream(ResDtoEnums.values()).filter(e -> e.getShopType() == shopType).findFirst().orElse(null);
		if(resDtoEnums == null){
			return -1;
		}
		return resDtoEnums.Type();
	}

	/**
	 * 根据功能道具类型获取商城类型
	 */
	public static int getShopTypeByType(int type) {
		ResDtoEnums resDtoEnums = Arrays.stream(ResDtoEnums.values()).filter(e -> e.Type() == type).findFirst().orElse(null);
		if(resDtoEnums == null){
			return -1;
		}
		return resDtoEnums.getShopType();
	}

	/**
	 * 根据价值排序
	 */
	public static void getSortValueByDTOs(List<ActResourceDTO> collect) {
		collect.sort((o1, o2) -> getByType(o2.getType()).getSortValue() - getByType(o1.getType()).getSortValue());
	}


}

