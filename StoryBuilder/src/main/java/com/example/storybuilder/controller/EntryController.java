package com.example.storybuilder.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.storybuilder.pojo.Entry;
import com.example.storybuilder.pojo.User;
import com.example.storybuilder.pojo.Volley;
import com.example.storybuilder.repo.EntryRepository;
import com.example.storybuilder.repo.UserRepository;
import com.example.storybuilder.repo.VolleyRepository;

@RestController
public class EntryController {
	private UserRepository UserRepo;
	private EntryRepository EntryRepo;
	private VolleyRepository VolleyRepo;
	
	@Autowired
	public EntryController(UserRepository UserRepo, EntryRepository EntryRepo, VolleyRepository VolleyRepo) {
		this.UserRepo = UserRepo;
		this.EntryRepo = EntryRepo;
		this.VolleyRepo = VolleyRepo;
	}
	
	@RequestMapping("/search-entries")
	public List<Entry> serachEntrys(
			@RequestParam String query,
			@RequestParam int limit,
			@RequestParam int offset) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();		
		Pageable page = PageRequest.of(offset / limit, limit);		
		List<Entry> entries = (List<Entry>) EntryRepo.findAllByContentContainingOrderByDateDesc(query, page);
		
		for (Entry entry: entries) {
			if (name.equals(entry.getUser().getName())) {
				entry.setEditable(true);
			}
		}
		return entries;
	}	
	
	@RequestMapping("/get-entries")
	public List<Entry> getEntries() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		List<Entry> entries = (List<Entry>) EntryRepo.findAll();
		List<Integer> volleyEntryIds = VolleyRepo.getVolleyEntryIds();
		
		for (Entry entry: entries) {			
			int volleyCount = 0;			
			if (name.equals(entry.getUser().getName())) {
				entry.setEditable(true);
			}
			for (int volleyEntryId: volleyEntryIds) {
				if (volleyEntryId == entry.getId()) {					
					volleyCount += 1;
				}
				entry.setVolleyCount(volleyCount);
			}
		}		
		return entries;
	}
	
	@RequestMapping("/get-vollies")
	public List<Volley> getVolleys(@RequestParam int entryId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();		
		Entry entry = EntryRepo.findById(entryId).get();		
		//List<Volley> vollies = (List<Volley>) VolleyRepo.findByEntryId(EntryId);
		List<Volley> vollies = (List<Volley>) VolleyRepo.findAllByEntryOrderByDateAsc(entry);
		
		for (Volley volley: vollies) {			
			if (name.equals(volley.getUser().getName())) {
				volley.setEditable(true);
			}
		}	
		return vollies;
	}
	
	@RequestMapping("/get-entry-flags") 
	public String getEntryFlags() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();		
		User user = UserRepo.findFirstByName(name);
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXGet " + user.getEntryFlags());		
		String entryFlags = user.getEntryFlags();		
		return entryFlags;
	}
	
	@RequestMapping("/save-entry-flags")
	public User saveEntryFlags(@RequestParam String entryFlags) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = UserRepo.findFirstByName(name);		
		user.setEntryFlags(entryFlags);
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXSave " + user.getEntryFlags());
		user = UserRepo.save(user);
		return user;
	}
	
	@RequestMapping("/get-volley-flags")
	public String getVolleyFlags() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();		
		User user = UserRepo.findFirstByName(name);
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXGet " + user.getVolleyFlags());		
		String volleyFlags = user.getVolleyFlags();	
		return volleyFlags;
	}
		
	@RequestMapping("/save-volley-flags")
	public User saveVolleyFlags(@RequestParam String volleyFlags) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = UserRepo.findFirstByName(name);		
		user.setEntryFlags(volleyFlags);
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXSave " + user.getVolleyFlags());
		user = UserRepo.save(user);
		return user;
	}
	
	@RequestMapping("/save-entry")
	public Entry saveEntry(@RequestParam String content, @RequestParam int id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = UserRepo.findFirstByName(name);
		
		if (id > 0) {
			Entry entry = EntryRepo.findById(id).get();
			if(user.getName().equals(entry.getUser().getName())) {
				entry.setContent(content);
				entry.setDate(new Date());
				entry.setFlagCount(0);
				entry = EntryRepo.save(entry);
				return entry;
			}
		} else {
			Entry entry = new Entry();
			entry.setUser(user);
			entry.setContent(content);
			entry.setDate(new Date());
			entry.setFlagCount(0);
			entry = EntryRepo.save(entry);			
			return entry;
		}
		return null;
	}
	
	@RequestMapping("/save-volley")
	public Volley saveVolley(
			@RequestParam String content,
			@RequestParam int id,
			@RequestParam int entryId) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = UserRepo.findFirstByName(name);
		
		if (id > 0) {
			Volley volley = VolleyRepo.findById(id).get();
			if(user.getName().equals(volley.getUser().getName())) {
				volley.setIsEntry(false);
				volley.setContent(content);
				volley.setDate(new Date());
				volley.setFlagCount(0);
				volley = VolleyRepo.save(volley);
				return volley;
			}
		} else {
			Entry entry = EntryRepo.findById(entryId).get();
			Volley volley = new Volley();
			volley.setUser(user);
			volley.setIsEntry(false);
			volley.setContent(content);
			volley.setDate(new Date());
			volley.setFlagCount(0);
			volley.setEntry(entry);
			volley = VolleyRepo.save(volley);			
			return volley;
		}		
		return null;
	}
	
	@RequestMapping("/delete-entry")
	public Entry deleteEntry(@RequestParam int id) {		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();		
		User user = UserRepo.findFirstByName(name);
		Entry entry = EntryRepo.findById(id).get();

		if (user.getName().equals(entry.getUser().getName())) {
			EntryRepo.delete(entry);
		}
		return entry;
	}
	
	@RequestMapping("/delete-volley")
	public Volley deleteVolley(@RequestParam int id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = UserRepo.findFirstByName(name);
		Volley volley = VolleyRepo.findById(id).get();
		
		if (user.getName().equals(volley.getUser().getName())) {			
			VolleyRepo.delete(volley);
		}		
		return volley;
	}
	
	@RequestMapping("/flag-entry")
	public Entry flagEntry(@RequestParam int id, @RequestParam boolean flagged) {		
		Entry entry = EntryRepo.findById(id).get();		
		 if (flagged) {
			 entry.setFlagCount(1);
		 } else {
			 entry.setFlagCount(-1);
		 }
		entry = EntryRepo.save(entry);
		return entry;
	}
	
	@RequestMapping("/flag-volley")
	public Volley flagVolley(@RequestParam int id, @RequestParam boolean flagged) {
		Volley volley = VolleyRepo.findById(id).get();
		if (flagged) {
			volley.setFlagCount(1);
		} else {
			volley.setFlagCount(-1);
		}
		volley = VolleyRepo.save(volley);
		return volley;
	}
}
