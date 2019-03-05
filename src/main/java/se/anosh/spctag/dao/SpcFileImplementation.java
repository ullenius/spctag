package se.anosh.spctag.dao;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SpcFileImplementation implements SpcDao {

	private SpcFileReader spcFile;
	
	public SpcFileImplementation(String filename) throws FileNotFoundException, IOException {
		spcFile = new SpcFileReader(filename);
	}
	
	
	@Override
	public Id666 read() throws FileNotFoundException, IOException {
		
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
