package com.eteks.sweethome3d.swing;

import java.awt.event.KeyEvent;

import javax.swing.Action;

import com.eteks.sweethome3d.viewcontroller.HomeView.ActionType;

public class ActionCoolFactory {

  public Action createAction(ActionType type, Object controller, String methodName)
  {
    return this.createAction(true, false, "", type, controller, methodName);
  }
  
  public Action createAction(String label, ActionType type, Object controller, String methodName)
  {
    return this.createAction(false, false, label, type, controller, methodName);
  }
  
  public Action createAction(boolean defaultText, ActionType type, Object controller, String methodName)
  {
    return this.createAction(false, true, "", type, controller, methodName);
  }
  
  
  public Action createActionDefaultLbl(ActionType type, Object controller, String methodName)
  {
    return this.createAction(true, false, "", type, controller, methodName);
  }
  
  private Action createAction(boolean iconOnly, boolean defaultText, String labelText, ActionType type, Object controller, String methodName)
  {
    String  iconName = "", desc = ""; Integer mnemonic = 0;
    String defaultTxt = "";
    String textToPut = labelText;
    
    switch(type)
    {
      case OPEN_IFC:
      {

        iconName = "open_ifc";
        desc = "Click to import a home from an Ifc File";
        mnemonic = new Integer(KeyEvent.VK_I);
        break;
      }
      
      case SHOW_STATUS:
      {
         iconName = "edit_status";
         desc = "Click to edit the status of the object";
         mnemonic = new Integer(KeyEvent.VK_E);
         break;
      }
      case ADD_FILE:
      {
        iconName = "add_file";
        desc = "Click to add a new file";
        mnemonic = new Integer(KeyEvent.VK_A); 
        break;
      }
      case ADD_LINK:
      {

        iconName = "et_link";
        desc = "Click to connect the 2 selected objects";
        mnemonic = new Integer(KeyEvent.VK_C);
        break;
      }
      case REFRESH_GRAPH:
      {

        iconName = "refresh";
        desc = "Click to refresh the state";
        mnemonic = new Integer(KeyEvent.VK_R);
        break;
      }
      case SHOW_GRAPH:
      {

        iconName = "show_graph";
        desc = "Click to display the Graph";
        mnemonic = new Integer(KeyEvent.VK_G);
        break;
      }
      case FILTER:
      {

        iconName = "filter";
        desc = "Click to choose which kind of elements to show";
        mnemonic = new Integer(KeyEvent.VK_F);
        break;
      }  
      case TOGGLE_CONNECTION:
      {
        defaultTxt = "Toggle Connectable visibility";
        iconName = "toggle_conn";
        desc = "Toggle visibility of Connectable elements";
        mnemonic = new Integer(KeyEvent.VK_T);
        break;
      }  
      default:
      {
       
        iconName = "edit_status";
        desc = "Just click";
        mnemonic = new Integer(KeyEvent.VK_E);
        
        break;
      }
      
    }
    
    if(defaultText)
       textToPut = defaultTxt;
    
    if(iconOnly )
        textToPut = "";
    try {
      Action a = new ControllerActionIconEasy
            (textToPut, iconName, desc, mnemonic, controller, methodName);
      return a;
    } catch (Exception ex) {
      
      ex.printStackTrace();
      return null;
    }

    
  }
  
}
