package edu.nd.dronology.services.extensions.areamapping.internal;



//Class defining nodes along the riverbanks
public class MapNode {
  double _latitude;
  double _longitude;
  int _riverSide;

  public MapNode(double latitude, double longitude, int riverside){
      _latitude = latitude;
      _longitude = longitude;
      _riverSide = riverside;
  }

  public double getLatitude() {
      return _latitude;
  }
  public double getLongitude(){
      return _longitude;
  }
  public int get_riverSide(){
      return _riverSide;
  }
}

