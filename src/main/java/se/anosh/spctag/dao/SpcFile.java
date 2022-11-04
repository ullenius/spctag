package se.anosh.spctag.dao;

import se.anosh.spctag.domain.Id666;
import se.anosh.spctag.domain.Xid6;

import java.io.*;
import java.util.Objects;

public final class SpcFile implements SpcDao {

	private final SpcFileReader spcFile;

	public SpcFile(String filename) throws IOException {
		spcFile = new SpcFileReader(Objects.requireNonNull(filename));
	}

	@Override
	public Id666 read() {
		return spcFile.getId666();
	}

	@Override
	public Xid6 readXid6() throws IOException {
		Xid6Reader xid6Reader = new Xid6Reader(spcFile.getFilename());
		return xid6Reader.getXid6();
	}

}
