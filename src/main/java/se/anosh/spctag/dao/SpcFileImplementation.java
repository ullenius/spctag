package se.anosh.spctag.dao;

import se.anosh.spctag.domain.Id666;
import se.anosh.spctag.domain.Xid6;

import java.io.*;

public class SpcFileImplementation implements SpcDao {

	private SpcFileReader spcFile;
	private Xid6Reader xid6Reader;

	public SpcFileImplementation(String filename) throws IOException {
		spcFile = new SpcFileReader(filename);
	}

	@Override
	public Id666 read() throws IOException {
		return spcFile.getId666();
	}

	@Override
	public Xid6 readXid6() throws IOException {
		return null;
	}

}
