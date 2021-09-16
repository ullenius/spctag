package se.anosh.spctag.service;

import java.io.*;

import se.anosh.spctag.domain.Id666;

public interface SpcService {
	
	public Id666 read() throws IOException;
}
