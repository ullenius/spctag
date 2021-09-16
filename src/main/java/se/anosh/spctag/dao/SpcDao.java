package se.anosh.spctag.dao;

import se.anosh.spctag.domain.Id666;

import java.io.*;

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
