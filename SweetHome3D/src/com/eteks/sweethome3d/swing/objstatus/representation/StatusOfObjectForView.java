package com.eteks.sweethome3d.swing.objstatus.representation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.eteks.sweethome3d.adaptive.security.buildingGraphObjects.BuildingObjectContained;

public  class StatusOfObjectForView
{
  private final String lifeStatus;
  private final List<String> files;
  private final List<String> objContainedInside;
  private final Map<BuildingObjectContained, BuildingObjectContained> objContains;

  public StatusOfObjectForView(  final List<String> objContainedInside,
                                 final Map<BuildingObjectContained, BuildingObjectContained> objContains,
                                 final String lifeStatus, final List<String> files)
  {
    this.lifeStatus =  lifeStatus;
    this.objContains = objContains;
    this.objContainedInside = objContainedInside;
    if(files == null)
    {
      this.files = null;
    }
    else
    {
      this.files = Collections.unmodifiableList(files);
    }
  }

  public StatusOfObjectForView(  final String lifeStatus, final List<String> files)
  {
    this(null,null, lifeStatus, files);
  }
   

  public StatusOfObjectForView(  final String lifeStatus, final List<String> files, final List<String> objectsContained)
  {
    this(objectsContained, null, lifeStatus, files);
  }

  public String getLifeStatus() {
    return lifeStatus;
  }

  public List<String> getFiles() {
    return files;
  }

  @Override
  public String toString()
  {
    String s= "";
    s = s + " Status of Life: " + lifeStatus;
    if(files != null && files.size() != 0)
    {
      s = "\n Files Contained: \n";
      for(String fileStr : this.files)
      {
        s = s + fileStr + "\n";
      }
    }
    else if (files == null)
    {
      s = s + "\n This object can't contain files";
    }
    else
    {
      s = s + "\n No files at the moment (but they could be present in the future)";
    }
    
    if(this.objContainedInside == null)
    {
      s = s + "\n the object can't contain files";
    }
    else if(this.objContainedInside != null && this.objContainedInside.size() == 0)
    {
      s = s + "\n the object does not contain objects but it could";
    }
    else
    {
      for(String oi : this.objContainedInside)
      {
        s = s + "\n object contained: \n" +
              oi;
      }
    }
    
    
    return s;

  }
  
  public List<String> getObjectContainedLst()
  {
    return this.objContainedInside;
  }
  
  public Map<BuildingObjectContained, BuildingObjectContained> getObjContains() {
    return objContains;
  }


}
