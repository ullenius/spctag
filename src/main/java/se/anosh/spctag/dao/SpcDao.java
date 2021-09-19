package se.anosh.spctag.dao;

import se.anosh.spctag.domain.Id666;
import se.anosh.spctag.domain.Xid6;

import java.io.*;

public interface SpcDao {
	
	public Id666 read() throws IOException;
	public Xid6 readXid6() throws IOException;

}
