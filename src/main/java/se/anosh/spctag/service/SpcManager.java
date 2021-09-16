package se.anosh.spctag.service;

import java.io.*;
import java.util.Objects;

import se.anosh.spctag.dao.*;
import se.anosh.spctag.domain.Id666;

public class SpcManager implements SpcService {
	
	private SpcDao dao;
	
	// dependency injection
	public SpcManager(SpcDao dao) {
		this.dao = Objects.requireNonNull(dao);
	}

	@Override
	public Id666 read() throws FileNotFoundException, IOException {
		
		return dao.read();
	}

	@Override
	public void deleteSpcTags() {
		dao.remove();
	}

	@Override
	public void update(String song) {
		dao.update(song);
		
	}

}
