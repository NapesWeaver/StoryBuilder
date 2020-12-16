package com.example.storybuilder.pojo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="vollies")
public class Volley {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="volley_id")
	private int id;
	@ManyToOne
	@JoinColumn(name="volley_user_id")
	private User user;
	@ManyToOne
	@JoinColumn(name="volley_entry_id")
	private Entry entry;
	@Column(name="volley_content")
	private String content;
	@Column(name="volley_date")
	private Date date;
	@Column(name="volley_flag_count")
	private int flagCount;
	@Column(name="sibling_id")
	private int siblingId;
	@Column(name="is_entry")
	private Boolean isEntry;
	@Column(name="is_hidden")
	private boolean isHidden;
	@Transient
	private boolean editable;
	
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
	public Entry getEntry() {
		return entry;
	}
	public void setEntry(Entry entry) {
		this.entry = entry;
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
	public int getSiblingId() {
		return siblingId;
	}
	public void setSiblingId(int siblingId) {
		this.siblingId = siblingId;
	}
	public Boolean getIsEntry() {
		return isEntry;
	}
	public void setIsEntry(Boolean isEntry) {
		this.isEntry = isEntry;
	}
	public boolean isHidden() {
		return isHidden;
	}
	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}

