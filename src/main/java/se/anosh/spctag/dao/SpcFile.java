package se.anosh.spctag.dao;

import se.anosh.spctag.domain.Id666;
import se.anosh.spctag.domain.Xid6;

import java.io.*;

public class SpcFile implements SpcDao {

	private final SpcFileReader spcFile;
	private Xid6Reader xid6Reader;

	public SpcFile(String filename) throws IOException {
		spcFile = new SpcFileReader(filename);
	}

	@Override
	public Id666 read() throws IOException {
		return spcFile.getId666();
	}

	@Override
	public Xid6 readXid6() throws IOException {
		xid6Reader = new Xid6Reader(spcFile.getFilename());
		return xid6Reader.getXid6();
	}

}
