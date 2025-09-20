package fr.moussax.blightedMC.core.players.data;

public class PlayerData {
  private String name;
  private String uuid;
  private int favors;
  private double mana;

  public PlayerData(String name, String uuid, int favors, double mana) {
    this.name = name;
    this.uuid = uuid;
    this.favors = favors;
    this.mana = mana;
  }

  public String getName() { return name; }
  public String getUuid() { return uuid; }
  public int getFavors() { return favors; }
  public double getMana() { return mana; }

  public void setName(String name) { this.name = name; }
  public void setUuid(String uuid) { this.uuid = uuid; }
  public void setFavors(int favors) { this.favors = favors; }
  public void setMana(double mana) { this.mana = mana; }
}
