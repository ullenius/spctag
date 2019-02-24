package se.anosh.spctag.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import se.anosh.spctag.dao.Model;

/**
 * 
 * Service layer
 * 
 * @author Anosh D. Ullenius <anosh@anosh.se>
 *
 */
public interface SpcService {
	
	public Model read() throws FileNotFoundException, IOException;
	public void deleteSpcTags();
	public void update(String song);
	

}
