package com.example.storybuilder.repo;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.storybuilder.pojo.Entry;

public interface EntryRepository extends PagingAndSortingRepository<Entry, Integer>{
	public List<Entry> findAll();
	public Optional<Entry> findById(Integer id);
	public List<Entry> findAllByContentContainingOrderByDateDesc(String content, Pageable Page);
	public List<Entry> findAllByOrderByDateDesc(Pageable page);
	
	@Transactional
	@Modifying
	@Query(value = "delete from " + "entries " + "where entries.entry_id = :id", nativeQuery = true)
	public void deleteEntryById(int id);
}
