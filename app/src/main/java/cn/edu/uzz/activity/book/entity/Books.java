package cn.edu.uzz.activity.book.entity;

import java.io.Serializable;

public class Books implements Serializable {
	
	private int id;
	private String name;
	private String publish;
	private String version;
	private String picture;
	private String writer;
	private String endtime;
	private String  ISBN;
	private String rentstatus;	
	private String substatus;
	private String price;
	private String time;
	private int type;


	public int getType() {
		return type;
	}

	public void setType(int i) {
		this.type = i;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPublish() {
		return publish;
	}

	public void setPublish(String publish) {
		this.publish = publish;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getRentstatus() {
		return rentstatus;
	}

	public void setRentstatus(String rentstatus) {
		this.rentstatus = rentstatus;
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}

	public String getSubstatus() {
		return substatus;
	}

	public void setSubstatus(String substatus) {
		this.substatus = substatus;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	


	
	
	
}
