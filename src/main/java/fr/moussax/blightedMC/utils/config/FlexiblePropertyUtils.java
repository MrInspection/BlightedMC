package fr.moussax.blightedMC.utils.config;

import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A PropertyUtils that allows flexible mapping between YAML keys and Java bean/field names.
 * It supports snake_case and kebab-case YAML keys by mapping them to camelCase Java names.
 */
public class FlexiblePropertyUtils extends PropertyUtils {

  private static String normalize(String name) {
    if (name == null) return null;
    return name.replace("_", "")
      .replace("-", "")
      .replace(" ", "")
      .toLowerCase(Locale.ROOT);
  }

  @Override
  public Property getProperty(Class<?> type, String name) {
    try {
      return super.getProperty(type, name);
    } catch (Exception ignored) {}

    Map<String, Property> combined = new HashMap<>();
    combined.putAll(getPropertiesMap(type, BeanAccess.FIELD));
    combined.putAll(getPropertiesMap(type, BeanAccess.PROPERTY));

    for (Map.Entry<String, Property> e : combined.entrySet()) {
      if (e.getKey().equalsIgnoreCase(name)) {
        return e.getValue();
      }
    }

    String target = normalize(name);
    for (Map.Entry<String, Property> e : combined.entrySet()) {
      if (normalize(e.getKey()).equals(target)) {
        return e.getValue();
      }
    }

    return super.getProperty(type, name);
  }
}
