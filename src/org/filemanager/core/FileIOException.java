package org.filemanager.core;

public class FileIOException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = 3306212602439150290L;

  public FileIOException() {}

  public FileIOException(String message) {
    super(message);
  }
}
