package se.anosh.spctag.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import se.anosh.spctag.dao.Model;
import se.anosh.spctag.dao.SpcDao;

public class SpcManager implements SpcService {
	
	private SpcDao dao;
	
	// dependency injection
	public SpcManager(SpcDao dao) {
		this.dao = Objects.requireNonNull(dao);
	}

	@Override
	public Model read(String filename) throws FileNotFoundException, IOException {
		
		return dao.read(filename);
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
