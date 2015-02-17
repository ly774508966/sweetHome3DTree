package com.eteks.sweethome3d.adaptive.security.buildingGraphObjects;

import com.eteks.sweethome3d.adaptive.security.parserobjects.Vector3D;

public class PCObject extends FileHolder {

  public PCObject(Vector3D position)
  {
    super(position);
    this.objectType = BuildingObjectType.PC;
  }
  
}
