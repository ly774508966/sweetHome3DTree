package com.eteks.sweethome3d.adaptive.security.buildingGraphObjects;

import java.util.ArrayList;
import java.util.List;

import com.eteks.sweethome3d.adaptive.security.buildingGraph.BuildingSecurityGraph;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.wrapper.IdObject;
import com.eteks.sweethome3d.adaptive.security.parserobjects.Vector3D;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.swing.objstatus.representation.StatusOfObjectForView;


public abstract class BuildingObjectContained extends BuildingGraphPart {

  protected BuildingObjectType objectType;
  private Vector3D position;
  private List<BuildingObjectContained> objectContained = new ArrayList<BuildingObjectContained>();
  
  public BuildingObjectContained(Vector3D position) {
    this.setPosition(position);
  }
 
  /**
   * The 3D representation of the object wrapped inside the SweetHome HomePiece object
   * @param preferences
   * @return
   */
  public  HomePieceOfFurniture getPieceOfForniture(UserPreferences preferences)
  {
    return preferences.getPieceOfForniture(objectType);
  }
  
  @Override
  public String toString()
  {
    return (this.objectType != null ? this.objectType.toString() : "object")  +
           "ID:" + this.getId();
           
  }
  
  //TODO: generalize with custom objs
  public String typeString()
  {
    return "" + this.objectType;
  }
  

  public Vector3D getPosition() {
    return position;
  }

  public BuildingObjectType getType()
  {
    return this.objectType;
  }
  
  public void setPosition(Vector3D position) {
    this.position = position;
  }
  
  public  abstract StatusOfObjectForView getStatusForView();
  
  public  abstract void  setStatusFromView(StatusOfObjectForView status);

  public void setObjectsContainedFromView(StatusOfObjectForView status)
  {
    List<String> objs =  status.getObjectContainedLst();
    if(objs == null)
        return ;
    
    this.objectContained.clear();
    
    for(String objectCont : objs)
    {
      this.addObjectContained(objectCont);
    }
 
  }


  public List<String> getObjectConainedStr() {
    
    List<String> objsContained = new ArrayList<String>();
    for(BuildingObjectContained boc : this.getObjectContained())
    {
      String bocStr = boc.getStringRepresent();
      objsContained.add(bocStr);
    }
    return objsContained;
  }
  
  /**
   * Returns representaion suitable for table  (CSV like)
   * this.getId() + "," + this.getTypeAsString()
   * @return
   */
  public String getStringRepresent()
  {
    return this.getId() + "," + this.getTypeAsString();
  }
  
  public String getTypeAsString()
  {
    return this.getType().name();
  }
  
  public List<BuildingObjectContained> getObjectContained() {
    return objectContained;
  }

  public void setObjectContained(List<BuildingObjectContained> objectContained) {
    this.objectContained = objectContained;
  }
  
  public void addObjectContained(BuildingObjectContained cont)
  {
    this.objectContained.add(cont);
  }
  
  
  private void addObjectContained(String objectCont)
  {
    BuildingSecurityGraph segrapg = BuildingSecurityGraph.getInstance();
    String id = this.getIdFromTableString(objectCont);
    BuildingObjectContained boc = segrapg.getObjectContainedFromObj(new IdObject(id));
    this.addObjectContained(boc);
  }
  /**
   * Assuming that the string is the form:  ID,TYPE
   * It returns  (s.split(","))[0];
   * @param s: the table string
   * @return the id
   */
  private String getIdFromTableString(String s)
  {
    return   (s.split(","))[0];
  }
  
}
