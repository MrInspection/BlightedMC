package fr.moussax.blightedMC.core.items.crafting;

import java.util.ArrayList;
import java.util.HashMap;

public class ShapeEncoder {
  private final String line1;
  private final String line2;
  private final String line3;

  private final HashMap<Character, CraftingObject> keys = new HashMap<>();

  public ShapeEncoder(String line1, String line2, String line3) {
    if(line1.length() != 3 || line2.length() != 3 || line3.length() != 3) {
      throw new StringIndexOutOfBoundsException("Each crafting line must be 3 characters long");
    }
    this.line1 = line1;
    this.line2 = line2;
    this.line3 = line3;
  }

  public void bindKey(char key, CraftingObject object) {
    keys.put(key, object);
  }

  public ArrayList<CraftingObject> encodeCraftingRecipe() {
    ArrayList<CraftingObject> objects = new ArrayList<>();
    String[] lines = {line1, line2, line3};

    for(String line : lines) {
      for(char c : line.toCharArray()) {
        if(c == ' ' || !keys.containsKey(c)) {
          objects.add(new CraftingObject(null, 0));
        } else {
          objects.add(keys.get(c));
        }
      }
    }

    if(objects.size() != 9) {
      throw new RuntimeException("Failed to encode crafting recipe");
    }

    return objects;
  }
}
