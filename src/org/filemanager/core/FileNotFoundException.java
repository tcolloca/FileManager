package org.filemanager.core;

public class FileNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -155056959015984449L;

	public FileNotFoundException(String path) {
		super(path);
	}
}
