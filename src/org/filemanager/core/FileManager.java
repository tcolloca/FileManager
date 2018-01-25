package org.filemanager.core;

import java.io.File;
import java.io.IOException;

public class FileManager {

	private FileManager() {
	}

	public static SimpleFile open(String path) {
		if (!exists(path)) {
			throw new FileNotFoundException(path);
		}
		return new SimpleFile(new File(path));
	}

	public static SimpleFile create(String path) {
		File file = new File(path);
		try {
			if (file.createNewFile()) {
				return new SimpleFile(file);
			}
			throw new FileAlreadyExistsException();
		} catch (IOException e) {
			throw new FileIOException(e.getMessage());
		}
	}

	public static SimpleFile createAndOverride(String path) {
		if (delete(path)) {
			return create(path);
		}
		throw new IllegalStateException("File couldn't be deleted.");
	}

	public static SimpleFile override(SimpleFile file) {
		if (delete(file)) {
			return create(file.getPath());
		}
		throw new IllegalStateException("File couldn't be deleted.");
	}
	
	public static SimpleFile openOrCreate(String path) {
		if (!exists(path)) {
			return create(path);
		}
		return open(path);
	}

	public static boolean delete(String path) {
		File file = new File(path);
		return file.delete();
	}
	
	public static boolean delete(SimpleFile file) {
		return file.delete();
	}

	public static boolean exists(String path) {
		File file = new File(path);
		return file.exists();
	}
	
	public static void main(String[] args) {
		
	}
}
