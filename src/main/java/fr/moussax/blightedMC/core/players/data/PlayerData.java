package fr.moussax.blightedMC.core.players.data;

public class PlayerData {
  private String name;
  private String uuid;
  private int gems;
  private double mana;

  public PlayerData(String name, String uuid, int favors, double mana) {
    this.name = name;
    this.uuid = uuid;
    this.gems = favors;
    this.mana = mana;
  }

  public String getName() { return name; }
  public String getUuid() { return uuid; }
  public int getGems() { return gems; }
  public double getMana() { return mana; }

  public void setName(String name) { this.name = name; }
  public void setUuid(String uuid) { this.uuid = uuid; }
  public void setGems(int gems) { this.gems = gems; }
  public void setMana(double mana) { this.mana = mana; }
}
