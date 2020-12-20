package com.example.storybuilder.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="Entries")
@SecondaryTable(name="entry_vollies")
public class Entry {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="entry_id")
	private int id;
	@ManyToOne
	@JoinColumn(name="entry_user_id", nullable=false)
	private User user;
	@Column(name="entry_content")
	private String content;
	@Column(name="entry_date")
	private Date date;
	@Column(name="entry_flag_count")
	private int flagCount;
	@Transient
	private boolean editable;
	//@Transient
	@Column(name="volley_count", table="entry_vollies", insertable=false, updatable=false)
	private Integer volleyCount;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getFlagCount() {
		return flagCount;
	}
	public void setFlagCount(int flagCount) {
		this.flagCount += flagCount;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public Integer getVolleyCount() {
		return volleyCount;
	}
}
