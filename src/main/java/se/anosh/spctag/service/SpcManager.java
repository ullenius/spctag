package se.anosh.spctag.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import se.anosh.spctag.dao.Id666;
import se.anosh.spctag.dao.SpcDao;

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
