package se.anosh.spctag.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import se.anosh.spctag.dao.Id666;

/**
 * 
 * Service layer
 * 
 * @author Anosh D. Ullenius <anosh@anosh.se>
 *
 */
public interface SpcService {
	
	public Id666 read() throws FileNotFoundException, IOException;
	public void deleteSpcTags();
	public void update(String song);
	

}
