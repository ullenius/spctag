package se.anosh.spctag.dao;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 
 * Defines CRUD-operations on the DAO
 * 
 * @author Anosh D. Ullenius <anosh@anosh.se>
 *
 */
public interface SpcDao {
	
	public Id666 read() throws FileNotFoundException, IOException;
	public void update(String song); // example
	public void remove(); // remove all fields

}
