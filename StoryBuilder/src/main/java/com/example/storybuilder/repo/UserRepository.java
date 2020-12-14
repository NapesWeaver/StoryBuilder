package com.example.storybuilder.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.storybuilder.pojo.User;

public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
	public User findFirstByName(String name);
}
