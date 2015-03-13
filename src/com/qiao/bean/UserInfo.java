package com.qiao.bean;

import cn.bmob.v3.BmobObject;

public class UserInfo extends BmobObject {
	
	private String PlatformName;//平台名称
	private String UserName;//用户名称
	private String UserId;//用户id
	private String UserIcon;//用户icon
	private String UserPoints;//用户点数
	private String Gender;//用户性别
	public String getPlatformName() {
		return PlatformName;
	}
	public void setPlatformName(String platformName) {
		PlatformName = platformName;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getUserIcon() {
		return UserIcon;
	}
	public void setUserIcon(String userIcon) {
		UserIcon = userIcon;
	}
	public String getUserPoints() {
		return UserPoints;
	}
	public void setUserPoints(String userPoints) {
		UserPoints = userPoints;
	}
	public String getGender() {
		return Gender;
	}
	public void setGender(String gender) {
		Gender = gender;
	}
	
	

	
	
}
