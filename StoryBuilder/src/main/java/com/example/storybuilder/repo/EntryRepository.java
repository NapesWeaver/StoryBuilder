package com.example.storybuilder.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.storybuilder.pojo.Entry;

public interface EntryRepository extends PagingAndSortingRepository<Entry, Integer>{
	public List<Entry> findAll();
	public Optional<Entry> findById(Integer id);
	public List<Entry> findAllByContentContainingOrderByDateDesc(String content, Pageable Page);
	public List<Entry> findAllByOrderByDateDesc(Pageable page);
}
