package com.example.storybuilder.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="users")
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="user_id")
	private int id;	
	@Column(name="username")	
	private String name;	
	@Column(name="email")
	private String email;	
	@Column(name="password")
	private String passwordHashed;	
	@Column(name="enabled")
	private Boolean enabled;
	@Column(name="entry_flags")
	private String entryFlags;
	@Column(name="volley_flags")
	private String volleyFlags;
	
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPasswordHashed() {
		return passwordHashed;
	}
	public void setPasswordHashed(String passwordHashed) {
		this.passwordHashed = passwordHashed;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public String getEntryFlags() {
		return entryFlags;
	}
	public void setEntryFlags(String entryFlags) {
		this.entryFlags = entryFlags;
	}
	public String getVolleyFlags() {
		return volleyFlags;
	}
	public void setVolleyFlags(String volleyFlags) {
		this.volleyFlags = volleyFlags;
	}			
}
