package com.eteks.sweethome3d.adaptive.security.assets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.eteks.sweethome3d.adaptive.security.assets.attributes.BuildingObjectAttribute;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.BuildingSecurityGraph;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.wrapper.IdObject;
import com.eteks.sweethome3d.adaptive.security.parserobjects.Vector3D;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.swing.objstatus.representation.StatusOfObjectForView;


public abstract class Asset extends BuildingGraphPart {

  protected AssetType objectType;
  private Vector3D position;
  private List<Asset> objectContained = new ArrayList<Asset>();
  private List<BuildingObjectAttribute> attributes = new ArrayList<BuildingObjectAttribute>();
  private Set<ObjectAbility> abilities = new TreeSet<ObjectAbility>();
  private String originalName;


  public Asset(Vector3D position) {
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

  protected void clearAttributes()
  {
    this.attributes.clear();

  }
  
  public boolean canConnect() {
    return this.getAbilities().contains(ObjectAbility.CONNECT);
  }
  

  public Vector3D getPosition() {
    return position;
  }

  public void setPosition(Vector3D position) {
    this.position = position;
  }
  
  public AssetType getType()
  {
    return this.objectType;
  }



  public List<Asset> getObjectContained() {
    return objectContained;
  }



  public void setObjectContained(List<Asset> objectContained) {
    this.objectContained = objectContained;
  }

  public void addObjectContained(Asset cont)
  {
    this.objectContained.add(cont);
  }


  public List<BuildingObjectAttribute> getAttributes()
  {
    return this.attributes;
  }

  private void addObjectContained(String objectCont)
  {
    BuildingSecurityGraph segrapg = BuildingSecurityGraph.getInstance();
    String id = this.getIdFromTableString(objectCont);
    Asset boc = segrapg.getObjectContainedFromObj(new IdObject(id));
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

  
  public Set<ObjectAbility> getAbilities() {
    return abilities;
  }

  public void setAbilities(Set<ObjectAbility> abilities) {
    this.abilities = abilities;
  }


  public void setObjectsContainedFromView(StatusOfObjectForView status)
  {
    List<String> objs =  status.getObjectContainedLst();
    if(objs == null)
      return ;

    this.objectContained.clear();
    int pippo = 123;
    pippo ++;
    for(String objectCont : objs)
    {
      this.addObjectContained(objectCont);
    }

  }


  public List<String> getObjectConainedStr() {

    List<String> objsContained = new ArrayList<String>();
    for(Asset boc : this.getObjectContained())
    {
      String bocStr = boc.getStringRepresent();
      objsContained.add(bocStr);
    }
    return objsContained;
  }

  public List<String> getObjectConainedIDStr() {
    List<String> ids = new ArrayList<String>();  
    for(Asset boc : this.getObjectContained())
    {
      String id = boc.getId();
      ids.add(id);
    }
    return ids;
  }


  /**
   * Returns representaion suitable for table  (CSV like)
   * this.getId() + "," + this.getTypeAsString()
   * @return
   */
  public String getStringRepresent()
  {
    return this.getId() + ","  + this.getName() + "," + this.getTypeAsString();
  }

  public String getTypeAsString()
  {
    if(this.getType() == null)
      return "UNKNOWN TYPE";
    return this.getType().name();
  }

  

  
  public Set<String> getAttributesStr() {
    Set<String> strs = new HashSet<String>();
    for(BuildingObjectAttribute attribute : this.attributes)
    {
      attribute.toStringTable(); 
    }
    return strs;
  }

  public  abstract StatusOfObjectForView getStatusForView();

  public  abstract void  setStatusFromView(StatusOfObjectForView status);

  
  //TODO: generalize with custom objs
  public String typeString()
  {
    return "" + this.objectType;
  }
  

  @Override
  public String toString()
  {
    String s =  (this.objectType != null ? this.objectType.toString() : "[no obj type] object")  +
        "ID: " + this.getId() +
        "\n\tOriginalName: " + this.getOriginalName();
    return s;

  }

  public String getOriginalName() {
    if(this.originalName == null)
      return this.name;
    return originalName;
  }

  public void setOriginalName(String originalName) {
    this.originalName = originalName;
  }

  public void addAllAttributes(Set<BuildingObjectAttribute> attrs) {
    this.attributes.addAll(attrs);

  }

  


}
