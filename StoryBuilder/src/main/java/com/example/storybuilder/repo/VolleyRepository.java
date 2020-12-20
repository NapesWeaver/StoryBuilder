package com.example.storybuilder.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.storybuilder.pojo.Entry;
import com.example.storybuilder.pojo.Volley;

public interface VolleyRepository extends PagingAndSortingRepository<Volley, Integer>{
	public List<Volley> findAll();
	public Optional<Volley> findById(Integer id);
	public List<Volley> findAllByEntryOrderByDateAsc(Entry entry);
	@Query(value = "SELECT volley_entry_id FROM vollies WHERE is_hidden = false", nativeQuery = true)
	public List<Integer> getVolleyEntryIds();
}
