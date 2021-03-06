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
	public List<Entry> serachEntriess(
			@RequestParam String text,
			@RequestParam int limit,
			@RequestParam int offset) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();		
		Pageable page = PageRequest.of(offset / limit, limit);		
		List<Entry> entries = (List<Entry>) EntryRepo.findAllByContentContainingOrderByDateDesc(text, page);
		
		for (Entry entry: entries) {
			if (name.equals(entry.getUser().getName())) {
				entry.setEditable(true);
			}
		}
		return entries;
	}	
	
	@RequestMapping("/get-entries")
	public List<Entry> getEntries(@RequestParam int limit, @RequestParam int offset) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		Pageable page = PageRequest.of(offset / limit, limit);
		List<Entry> entries = (List<Entry>) EntryRepo.findAllByOrderByDateDesc(page);
		
		for (Entry entry: entries) {			
			if (name.equals(entry.getUser().getName())) {
				entry.setEditable(true);
			}
		}		
		return entries;
	}
	
	@RequestMapping("/get-vollies")
	public List<Volley> getVolleys(@RequestParam int entryId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();		
		Entry entry = EntryRepo.findById(entryId).get();		
		List<Volley> vollies = (List<Volley>) VolleyRepo.findAllByEntryOrderByDateAsc(entry);
		
		for (Volley volley: vollies) {			
			if (name.equals(volley.getUser().getName())) {
				volley.setEditable(true);
			}
		}	
		return vollies;
	}
	
	@RequestMapping("/save-entry")
	public Entry saveEntry(@RequestParam String content, @RequestParam int id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = UserRepo.findFirstByName(name);
		
		if (id > 0) {// Existing Entry
			Entry entry = EntryRepo.findById(id).get();
			if(user.getName().equals(entry.getUser().getName())) {
				entry.setContent(content);
				entry.setDate(new Date());
				entry.setFlagCount(0);
				entry = EntryRepo.save(entry);
				return entry;
			}
		} else {// New Entry
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
		
		if (id > 0) {// Existing Volley
			Volley volley = VolleyRepo.findById(id).get();
			if(user.getName().equals(volley.getUser().getName())) {
				volley.setContent(content);
				volley.setDate(new Date());
				volley.setFlagCount(0);
				volley.setSiblingId(0);
				volley.setIsEntry(false);
				volley.setHidden(false);
				volley = VolleyRepo.save(volley);
				return volley;
			}
		} else {// New Volley
			Entry entry = EntryRepo.findById(entryId).get();
			Volley volley = new Volley();
			volley.setUser(user);
			volley.setEntry(entry);
			volley.setContent(content);
			volley.setDate(new Date());
			volley.setFlagCount(0);
			volley.setSiblingId(0);
			volley.setIsEntry(false);
			volley.setHidden(false);
			volley = VolleyRepo.save(volley);	
			return volley;
		}		
		return null;
	}
	
	@RequestMapping("/volley-append")
	public Volley saveVolleyAsEntry(
			@RequestParam String content,
			@RequestParam int id,
			@RequestParam int entryId,
			@RequestParam int siblingId) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = UserRepo.findFirstByName(name);
		
		if (id > 0) {
			Volley volley = VolleyRepo.findById(id).get();
			if(user.getName().equals(volley.getUser().getName())) {
				volley.setContent(content);
				volley.setDate(new Date());
				volley.setFlagCount(0);
				volley.setSiblingId(0);
				volley.setIsEntry(false);
				volley.setHidden(false);
				volley = VolleyRepo.save(volley);
				return volley;
			}
		} else {
			Entry entry = EntryRepo.findById(entryId).get();
			Volley volley = new Volley();
			List<Volley> vollies = (List<Volley>) VolleyRepo.findAllByEntryOrderByDateAsc(entry);
			for (Volley unusedVolley : vollies ) {
				if(unusedVolley.getIsEntry() == false && unusedVolley.isHidden() == false && unusedVolley.getId() != siblingId) {					
					unusedVolley.setHidden(true);
					unusedVolley.setSiblingId(siblingId);
					unusedVolley = VolleyRepo.save(unusedVolley);
				}
				if(unusedVolley.getId() == siblingId) {
					unusedVolley.setIsEntry(true);
					unusedVolley.setSiblingId(siblingId);
					unusedVolley = VolleyRepo.save(unusedVolley);
				}
			}
			volley.setUser(user);
			volley.setEntry(entry);
			volley.setContent(content);
			volley.setDate(new Date());
			volley.setFlagCount(0);
			volley.setSiblingId(0);
			volley.setIsEntry(false);
			volley.setHidden(false);
			volley = VolleyRepo.save(volley);			
			return volley;
		}
		return null;
	}
	
	@RequestMapping("/delete-entry")
	public void deleteEntry(@RequestParam int id) {		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();		
		User user = UserRepo.findFirstByName(name);
		Entry entry = EntryRepo.findById(id).get();

		if (user.getName().equals(entry.getUser().getName())) {
			EntryRepo.deleteEntryById(id);
		}
	}
	
	@RequestMapping("/delete-volley")
	public Volley deleteVolleyAsEntry(@RequestParam int id, @RequestParam int entryId, @RequestParam int volleyId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = UserRepo.findFirstByName(name);
		Volley volley = VolleyRepo.findById(id).get();
		Entry entry = EntryRepo.findById(entryId).get();	
		List<Volley> vollies = (List<Volley>) VolleyRepo.findAllByEntryOrderByDateAsc(entry);
		
		for (Volley unusedVolley : vollies ) {			
			if(unusedVolley.getSiblingId() == volleyId) {				
				unusedVolley.setSiblingId(0);
				unusedVolley.setIsEntry(false);
				unusedVolley.setHidden(false);
				unusedVolley = VolleyRepo.save(unusedVolley);
			}
		}		
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
	
	@RequestMapping("/get-entry-flags") 
	public String getEntryFlags() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();		
		User user = UserRepo.findFirstByName(name);
		String entryFlags = user.getEntryFlags();		
		return entryFlags;
	}
	
	@RequestMapping("/save-entry-flags")
	public User saveEntryFlags(@RequestParam String entryFlags) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = UserRepo.findFirstByName(name);		
		user.setEntryFlags(entryFlags);
		user = UserRepo.save(user);
		return user;
	}
	
	@RequestMapping("/get-volley-flags")
	public String getVolleyFlags() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();		
		User user = UserRepo.findFirstByName(name);	
		String volleyFlags = user.getVolleyFlags();	
		return volleyFlags;
	}
		
	@RequestMapping("/save-volley-flags")
	public User saveVolleyFlags(@RequestParam String volleyFlags) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName();
		User user = UserRepo.findFirstByName(name);		
		user.setVolleyFlags(volleyFlags);
		user = UserRepo.save(user);
		return user;
	}
}
