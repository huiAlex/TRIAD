package edu.nd.dronology.services.extensions.areamapping.internal;

//Class used for automatic parsing of JSON file with GSON library

import java.util.ArrayList;
import java.util.List;

public class RiverList{
  private List<MapNode> nodes;
  public RiverList(){
      nodes = new ArrayList<MapNode>();
  }
  public List<MapNode> getNodes(){
      return nodes;
  }
}
