package cn.edu.uzz.activity.book.entity;

import java.io.Serializable;

public class User implements Serializable{
	private int id;
	private String username;
	private String password;
	private String usersex;
	private String userage;
	private String openId;
	private String phone;
	private String addr;
	private String rdateString;
	private int character_num;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsersex() {
		return usersex;
	}
	public void setUsersex(String usersex) {
		this.usersex = usersex;
	}
	public String getUserage() {
		return userage;
	}
	public void setUserage(String userage) {
		this.userage = userage;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getRdateString() {
		return rdateString;
	}
	public void setRdateString(String rdateString) {
		this.rdateString = rdateString;
	}

	public int getCharacter_num() {
		return character_num;
	}

	public void setCharacter_num(int character_num) {
		this.character_num = character_num;
	}
}
