package com.example.storybuilder.pojo;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="authorities")
public class Authority {	
	@EmbeddedId
	private AuthorityKey key;

	public AuthorityKey getKey() {
		return key;
	}

	public void setKey(AuthorityKey key) {
		this.key = key;
	}
}
