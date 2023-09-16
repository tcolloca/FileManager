package org.filemanager.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class SimpleFile implements AutoCloseable {

  private File file;
  private Scanner scanner;

  protected SimpleFile(File file) {
    this.file = file;
    try {
      this.scanner = new Scanner(file);
    } catch (FileNotFoundException e) {
      throw new IllegalStateException();
    }
  }

  public String getPath() {
    return file.getPath();
  }

  public boolean isEmpty() {
    return !scanner.hasNext();
  }

  public void override(Object obj) {
    write(obj, false);
  }

  public void append(Object obj) {
    write(obj, true);
  }

  public void appendln(Object obj) {
    append(obj);
    append("\n");
  }

  @SuppressWarnings("unchecked")
  public <T> T read(Class<T> clazz) {
    if (clazz.equals(String.class)) {
      return (T) scanner.next();
    } else if (clazz.equals(Integer.class)) {
      return (T) (Integer) scanner.nextInt();
    } else if (clazz.equals(Double.class)) {
      return (T) (Double) scanner.nextDouble();
    } else if (clazz.equals(Float.class)) {
      return (T) (Float) scanner.nextFloat();
    } else if (clazz.equals(Boolean.class)) {
      return (T) (Boolean) scanner.nextBoolean();
    } else if (clazz.equals(Long.class)) {
      return (T) (Long) scanner.nextLong();
    } else if (clazz.equals(BigInteger.class)) {
      return (T) scanner.nextBigInteger();
    } else if (clazz.equals(BigDecimal.class)) {
      return (T) scanner.nextBigDecimal();
    } else if (clazz.equals(Byte.class)) {
      return (T) (Byte) scanner.nextByte();
    } else if (clazz.equals(Short.class)) {
      return (T) (Short) scanner.nextShort();
    } else {
      throw new UnsopportedClassException(clazz.getCanonicalName());
    }
  }

  public boolean canRead(Class<?> clazz) {
    if (clazz.equals(String.class)) {
      return scanner.hasNext();
    } else if (clazz.equals(Integer.class)) {
      return scanner.hasNextInt();
    } else if (clazz.equals(Double.class)) {
      return scanner.hasNextDouble();
    } else if (clazz.equals(Float.class)) {
      return scanner.hasNextFloat();
    } else if (clazz.equals(Boolean.class)) {
      return scanner.hasNextBoolean();
    } else if (clazz.equals(Long.class)) {
      return scanner.hasNextLong();
    } else if (clazz.equals(BigInteger.class)) {
      return scanner.hasNextBigInteger();
    } else if (clazz.equals(BigDecimal.class)) {
      return scanner.hasNextBigDecimal();
    } else if (clazz.equals(Byte.class)) {
      return scanner.hasNextByte();
    } else if (clazz.equals(Short.class)) {
      return scanner.hasNextShort();
    } else {
      throw new UnsopportedClassException(clazz.getCanonicalName());
    }
  }

  public String readLine() {
    return scanner.nextLine();
  }

  public boolean hasLine() {
    return scanner.hasNextLine();
  }

  public String readAll() {
    List<String> lines = getLines();
    return String.join("\r\n", lines.subList(0, lines.size()));
  }

  public String readFirstLines(int n) {
    List<String> lines = getLines();
    return String.join("\r\n", lines.subList(0, Math.min(n, lines.size())));
  }

  public String readLastLines(int n) {
    List<String> lines = getLines();
    return String.join("\r\n", lines.subList(Math.max(lines.size() - n, 0), lines.size()));
  }

  public String readBetweenLinesFromEnd(int start, int end) {
    List<String> lines = getLines();
    Collections.reverse(lines);
    List<String> subList =
        lines.subList(Math.min(lines.size(), Math.max(start, 0)), Math.min(lines.size(), end));
    Collections.reverse(subList);
    return String.join("\r\n", subList);
  }

  public String readBetweenLines(int start, int end) {
    List<String> lines = getLines();
    return String.join("\r\n",
        lines.subList(Math.min(lines.size(), Math.max(start, 0)), Math.min(lines.size(), end)));
  }

  public List<String> getLines() {
    List<String> lines = new ArrayList<>();
    while (scanner.hasNextLine()) {
      lines.add(scanner.nextLine());
    }
    return lines;
  }

  private void write(Object obj, boolean append) {
    PrintWriter output = null;
    try {
      output = new PrintWriter(new BufferedWriter(new FileWriter(file, append)));
      write(output, obj);
    } catch (IOException e) {
      throw new FileIOException(e.getMessage());
    } finally {
      if (output != null) {
        output.close();
      }
    }
  }

  private void write(PrintWriter output, Object obj) {
    if (obj instanceof String) {
      output.print((String) obj);
    } else if (obj instanceof Integer) {
      output.print(obj);
    } else if (obj instanceof Double) {
      output.print(obj);
    } else if (obj instanceof Float) {
      output.print(obj);
    } else if (obj instanceof Character) {
      output.print(obj);
    } else if (obj instanceof Boolean) {
      output.print(obj);
    } else if (obj instanceof Long) {
      output.print(obj);
    } else {
      output.print(obj.toString());
    }
  }

  public File getFile() {
    return file;
  }

  @Override
  public void finalize() {
    if (scanner != null) {
      scanner.close();
    }
  }

  public boolean delete() {
    scanner.close();
    try {
      Files.delete(Paths.get(file.getPath()));
      return true;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void close() {
    scanner.close();
  }
}
