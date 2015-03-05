package com.eteks.sweethome3d.adaptive.security.extractingobjs;

import java.util.Set;

import com.eteks.sweethome3d.adaptive.security.buildingGraph.BuildingSecurityGraph;
import com.eteks.sweethome3d.adaptive.security.buildingGraphObjects.BuildingObjectContained;
import com.eteks.sweethome3d.adaptive.security.buildingGraphObjects.attributes.BuildingObjectAttribute;
import com.eteks.sweethome3d.model.UserPreferences;

public abstract class SecurityExtractor {

  protected BuildingSecurityGraph securityGraph = BuildingSecurityGraph.getInstance();
  
  protected ConfigLoader configLoader;
  protected UserPreferences preferences;
  
  public SecurityExtractor( UserPreferences preferences)
  {
    this.securityGraph.clearAll();
    this.configLoader = this.getConfig(preferences); 
    this.preferences = preferences;
  }
  
  protected void setAbilitiesAndAttributes(BuildingObjectContained objectCont) {
    this.setAttributes(objectCont);
    this.setAbilities (objectCont);
  }

  private void setAbilities(BuildingObjectContained objectCont) {
     
     ConfigLoader cfg = ConfigLoader.getInstance();
     String originalName = objectCont.getOriginalName();
     if(originalName == null)
         throw new IllegalStateException("original name is null!" + "objectCont : " + objectCont);
     Set<ObjectAbility> abilities = cfg.getObjectAbilities(originalName);
     if(abilities == null)
       return;
     objectCont.setAbilities(abilities);
    
  }

  private void setAttributes(BuildingObjectContained objectCont) {
      
    ConfigLoader cfg = ConfigLoader.getInstance();
    String originalName = objectCont.getOriginalName();
    Set<BuildingObjectAttribute> attrs = cfg.getPossibleAttributesForObject(originalName);
    if(attrs == null)
      return;
    objectCont.addAllAttributes(attrs);
    
  }
  
  
  protected abstract  ConfigLoader getConfig(UserPreferences preferences);
  public abstract BuildingSecurityGraph getGraph() throws Exception ;
  
  
}
