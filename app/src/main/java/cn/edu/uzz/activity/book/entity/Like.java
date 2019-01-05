package cn.edu.uzz.activity.book.entity;

import java.io.Serializable;

/**
 * Created by 10616 on 2017/12/3.
 */

public class Like implements Serializable{
	private String account;
	private String bookname;
	private int booktype;
	private int bookid;
	private String picture;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getBookname() {
		return bookname;
	}

	public void setBookname(String bookname) {
		this.bookname = bookname;
	}

	public int getBooktype() {
		return booktype;
	}

	public void setBooktype(int booktype) {
		this.booktype = booktype;
	}

	public int getBookid() {
		return bookid;
	}

	public void setBookid(int bookid) {
		this.bookid = bookid;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
}
