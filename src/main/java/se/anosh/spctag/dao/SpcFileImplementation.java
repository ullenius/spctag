package se.anosh.spctag.dao;

import se.anosh.spctag.domain.Id666;

import java.io.*;

public class SpcFileImplementation implements SpcDao {

	private SpcFileReader spcFile;

	public SpcFileImplementation(String filename) throws IOException {
		spcFile = new SpcFileReader(filename);
	}

	@Override
	public Id666 read() throws IOException {
		return spcFile.getId666();
	}

	@Override
	public void update(String song) {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("not yet implemented");
	}

}
