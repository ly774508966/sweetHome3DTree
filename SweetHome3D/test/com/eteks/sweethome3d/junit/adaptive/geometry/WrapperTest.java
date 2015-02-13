package com.eteks.sweethome3d.junit.adaptive.geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.eteks.sweethome3d.adaptive.security.buildingGraph.wrapper.WrapperRect;
import com.eteks.sweethome3d.adaptive.security.parserobjects.Vector3D;
import com.eteks.sweethome3d.io.DefaultUserPreferences;
import com.eteks.sweethome3d.junit.adaptive.BasicTest;
import com.eteks.sweethome3d.junit.adaptive.ControllerTest;
import com.eteks.sweethome3d.junit.adaptive.graphhome.HomeToGraphTest;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.LengthUnit;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.swing.SwingViewFactory;
import com.eteks.sweethome3d.viewcontroller.ViewFactory;

public class WrapperTest extends BasicTest {

  
  @Override
  public void doStuffInsideMain(Home home, UserPreferences preferences) {
    super.setUp();
    prepareHome(home, preferences);
    
    WrapperRect  cubbyW  = new WrapperRect( cubbyRoom.getBoundingRoomRect3D());
    WrapperRect  livingW  = new WrapperRect( livingRoom.getBoundingRoomRect3D());
    WrapperRect  diningW  = new WrapperRect( diningRoom.getBoundingRoomRect3D());
    WrapperRect  kitchenW  = new WrapperRect( kitchen.getBoundingRoomRect3D());
    
    
    TreeSet<WrapperRect> rects = new TreeSet<WrapperRect>();
    List<WrapperRect> rectsList = new ArrayList<WrapperRect>();
    
    TreeMap<WrapperRect, String> rectsTreeMap = new TreeMap<WrapperRect, String>();
    
    rectsList.add(cubbyW);
    rectsList.add(livingW);
    rectsList.add(diningW);
    rectsList.add(kitchenW);
    
    rectsTreeMap.put(cubbyW, "");
    rectsTreeMap.put(livingW, "");
    rectsTreeMap.put(diningW, "");
    rectsTreeMap.put(kitchenW, "");
    
    rects.addAll(rectsList);
    System.out.println("list: " + rectsList);
    System.out.println("\n\nordered list : " + rects);
    System.out.println("_____________________");
    
    Vector3D inCubby = new Vector3D(100, 100, 0);
    
    for(WrapperRect w : rects)
    {
      if(w.equals(inCubby))
      {
        System.out.println(w);
      }
    }
    

  }
  
  public  static void main(String [] args) {
    ViewFactory viewFactory = new SwingViewFactory();
    UserPreferences preferences = new DefaultUserPreferences();
    preferences.setUnit(LengthUnit.METER);
    Home home = new Home();
    ControllerTest t = new ControllerTest(home, preferences, viewFactory);
    WrapperTest htg = new WrapperTest();
    htg.doStuffInsideMain(home, preferences);
    
    
  }
  

}