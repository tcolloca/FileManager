package org.filemanager.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.fathzer.soft.javaluator.DoubleEvaluator;

public class Properties {

  private final String path;
  private final Map<String, Map<String, String>> categoriesMap =
      Collections.synchronizedMap(new HashMap<>());

  public Properties(String path) {
    if (path == null || path.isEmpty()) {
      throw new IllegalArgumentException("Path is null or empty.");
    }
    this.path = path;
    SimpleFile file = FileManager.open(path);
    String currentCategory = null;
    Map<String, String> currentCategoryMap = null;
    while (file.hasLine()) {
      String line = file.readLine().trim();
      if (line.isEmpty()) {
        continue;
      }
      if (line.startsWith("[") && line.endsWith("]")) {
        if (currentCategory != null) {
          categoriesMap.put(currentCategory, currentCategoryMap);
        }
        currentCategory = line.substring(1, line.length() - 1);
        currentCategoryMap = new HashMap<String, String>();
      } else {
        if (currentCategory == null) {
          throw new IllegalArgumentException("File starts with no category!");
        }
        String[] keyValue = line.split("=", 2);
        if (keyValue.length < 2) {
          throw new IllegalArgumentException(
              "Key value expected separated by '='. Instead found: " + line);
        }
        String value = keyValue[1].trim();
        if (value.startsWith("{{") && value.endsWith("}}")) {
          String newPath = value.substring(2, value.length() - 2);
          String[] pathVars = newPath.split("\\.", 3);
          if (pathVars.length < 3) {
            throw new IllegalArgumentException("External variable path is too short: " + newPath);
          }
          Properties varProps = new Properties(pathVars[0] + ".properties");
          value = varProps.getString(pathVars[1], pathVars[2]);
        }
        currentCategoryMap.put(keyValue[0].trim(), value);
      }
    }
    if (currentCategory != null) {
      categoriesMap.put(currentCategory, currentCategoryMap);
    }
  }
  
  public List<String> listCategories() {
    return new ArrayList<>(categoriesMap.keySet());
  }
  
  public List<String> listSubCategories(String category) {
    if (!categoriesMap.containsKey(category)) {
      throw new IllegalArgumentException("category not found: " + category);
    }
    return new ArrayList<>(categoriesMap.get(category).keySet());
  }

  public String getString(String category, String key) {
    if (category == null) {
      throw new IllegalArgumentException("category is null.");
    }
    if (key == null) {
      throw new IllegalArgumentException("key is null.");
    }
    if (!categoriesMap.containsKey(category)) {
      throw new IllegalArgumentException("category not found: " + category);
    }
    if (!categoriesMap.get(category).containsKey(key)) {
      throw new IllegalArgumentException("key not found: " + key);
    }
    return categoriesMap.get(category).get(key);
  }

  public String getString(String category, String key, String defaultValue) {
    if (category == null) {
      throw new IllegalArgumentException("category is null.");
    }
    if (key == null) {
      throw new IllegalArgumentException("key is null.");
    }
    if (!categoriesMap.containsKey(category)) {
      return defaultValue;
    }
    if (!categoriesMap.get(category).containsKey(key)) {
      return defaultValue;
    }
    return categoriesMap.get(category).get(key);
  }

  public int getInt(String category, String key) {
    DoubleEvaluator evaluator = new DoubleEvaluator();
    return evaluator.evaluate(getString(category, key)).intValue();
  }

  public int getInt(String category, String key, int defaultValue) {
    DoubleEvaluator evaluator = new DoubleEvaluator();
    return evaluator.evaluate(getString(category, key, String.valueOf(defaultValue))).intValue();
  }

  public long getLong(String category, String key) {
    DoubleEvaluator evaluator = new DoubleEvaluator();
    return evaluator.evaluate(getString(category, key)).longValue();
  }

  public long getLong(String category, String key, long defaultValue) {
    DoubleEvaluator evaluator = new DoubleEvaluator();
    return evaluator.evaluate(getString(category, key, String.valueOf(defaultValue))).intValue();
  }

  public float getFloat(String category, String key) {
    DoubleEvaluator evaluator = new DoubleEvaluator();
    return evaluator.evaluate(getString(category, key)).floatValue();
  }

  public float getFloat(String category, String key, float defaultValue) {
    DoubleEvaluator evaluator = new DoubleEvaluator();
    return evaluator.evaluate(getString(category, key, String.valueOf(defaultValue))).intValue();
  }

  public double getDouble(String category, String key) {
    DoubleEvaluator evaluator = new DoubleEvaluator();
    return evaluator.evaluate(getString(category, key));
  }

  public double getDouble(String category, String key, double defaultValue) {
    DoubleEvaluator evaluator = new DoubleEvaluator();
    return evaluator.evaluate(getString(category, key, String.valueOf(defaultValue))).intValue();
  }

  public boolean getBoolean(String category, String key) {
    return Boolean.parseBoolean(getString(category, key));
  }

  public boolean getBoolean(String category, String key, boolean defaultValue) {
    return Boolean.parseBoolean(getString(category, key, String.valueOf(defaultValue)));
  }

  public byte getByte(String category, String key) {
    return Byte.parseByte(getString(category, key));
  }

  public byte getBoolean(String category, String key, byte defaultValue) {
    return Byte.parseByte(getString(category, key, String.valueOf(defaultValue)));
  }

  public List<String> getList(String category, String key, String delimiter) {

    return Arrays.asList(getString(category, key).split(delimiter));
  }

  public List<String> getList(String category, String key, String delimiter,
      List<String> defaultValue) {
    try {
      return Arrays.asList(getString(category, key).split(delimiter));
    } catch (IllegalArgumentException e) {
      return defaultValue;
    }
  }

  public void addString(String category, String key, String value) {
    if (category == null) {
      throw new IllegalArgumentException("category is null.");
    }
    if (key == null) {
      throw new IllegalArgumentException("key is null.");
    }
    categoriesMap.putIfAbsent(category, new HashMap<>());
    categoriesMap.get(category).put(key, value);
    save();
  }

  public void addInt(String category, String key, int value) {
    addString(category, key, String.valueOf(value));
  }

  public void addLong(String category, String key, long value) {
    addString(category, key, String.valueOf(value));
  }

  public void addFloat(String category, String key, float value) {
    addString(category, key, String.valueOf(value));
  }

  public void addDouble(String category, String key, double value) {
    addString(category, key, String.valueOf(value));
  }

  public void addBoolean(String category, String key, boolean value) {
    addString(category, key, String.valueOf(value));
  }

  public void addByte(String category, String key, byte value) {
    addString(category, key, String.valueOf(value));
  }

  public void save() {
    StringBuilder content = new StringBuilder();
    for (String category : categoriesMap.keySet()) {
      content.append(String.format("[%s]\r\n", category));
      for (Entry<String, String> entry : categoriesMap.get(category).entrySet()) {
        content.append(String.format("%s = %s\r\n", entry.getKey(), entry.getValue()));
      }
      content.append("\r\n");
    }
    try {
      Files.write(Paths.get(path), content.toString().getBytes());
    } catch (IOException e) {
      throw new RuntimeException("Failed saving properties.", e);
    }
  }

  public boolean contains(String category) {
    return categoriesMap.containsKey(category);
  }
  
  public boolean contains(String category, String subcategory) {
    if (!contains(category)) {
      throw new IllegalArgumentException("Unknown category: " + category);
    }
    return categoriesMap.get(category).containsKey(subcategory);
  }
}
