package fr.moussax.blightedMC.core.players.data;

public class PlayerData {
  private String name;
  private String uuid;
  private int favors;

  public PlayerData(String name, String uuid, int favors) {
    this.name = name;
    this.uuid = uuid;
    this.favors = favors;
  }

  public String getName() { return name; }
  public String getUuid() { return uuid; }
  public int getFavors() { return favors; }

  public void setName(String name) { this.name = name; }
  public void setUuid(String uuid) { this.uuid = uuid; }
  public void setFavors(int favors) { this.favors = favors; }
}
