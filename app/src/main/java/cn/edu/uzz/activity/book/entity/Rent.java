package cn.edu.uzz.activity.book.entity;

import java.io.Serializable;

public class Rent implements Serializable{
	private String account;
	private String bookanme;
	private String nowdate;
	private String endtime;
	private String picture;
	private int bookid;
	private int booktype;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getBookanme() {
		return bookanme;
	}

	public void setBookanme(String bookanme) {
		this.bookanme = bookanme;
	}

	public String getNowdate() {
		return nowdate;
	}

	public void setNowdate(String nowdate) {
		this.nowdate = nowdate;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public int getBookid() {
		return bookid;
	}

	public void setBookid(int bookid) {
		this.bookid = bookid;
	}

	public int getBooktype() {
		return booktype;
	}

	public void setBooktype(int booktype) {
		this.booktype = booktype;
	}
}
