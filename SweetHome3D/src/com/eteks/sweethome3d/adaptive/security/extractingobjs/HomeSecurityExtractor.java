package com.eteks.sweethome3d.adaptive.security.extractingobjs;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.eteks.sweethome3d.adaptive.security.assets.Asset;
import com.eteks.sweethome3d.adaptive.security.assets.AssetType;
import com.eteks.sweethome3d.adaptive.security.assets.DoorObject;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.BuildinLinkWallWithDoor;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.BuildingLinkEdge;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.BuildingLinkWall;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.BuildingRoomNode;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.BuildingSecurityGraph;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.wrapper.IdObject;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.wrapper.IdRoom;
import com.eteks.sweethome3d.adaptive.security.extractingobjs.SavedConfigurationsLoader.SecurityNameAndMap;
import com.eteks.sweethome3d.adaptive.security.parserobjects.Vector3D;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.model.RoomGeoSmart;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.model.Wall;

public class HomeSecurityExtractor extends SecurityExtractor {

  private Home home;
  
  public HomeSecurityExtractor(Home home, UserPreferences preferences) {
    super(preferences);
    this.home = home;
    
  }

  @Override
  public BuildingSecurityGraph getGraph() throws Exception {
    return this.makeGraph(home, preferences);
  }
  
  
  /**
   * <pre>
   *   1) Graph <V, E>  where V are rooms filled with objects
   *       and there is an edge between room1 and room2 iif 
   *       (they are linked by a door   OR   they are linked by a wall)
   *  
   *
   *  2)  Each node of the graph is a room with all its objects  
   *      if a  forniture is inside the room
   *      we put it inside the graphEdge 
   *  
   *  </pre>
   **/
   
  protected BuildingSecurityGraph makeGraph(Home home, UserPreferences preferences)
  {

    List<Wall> walls = new ArrayList<Wall>(home.getWalls());
    List<Room> rooms = new ArrayList<Room>(home.getRooms());
    List<HomePieceOfFurniture> fornitures = new ArrayList<HomePieceOfFurniture>(home.getFurniture());

    List<BuildingRoomNode>  roomNodes = new ArrayList<BuildingRoomNode>();
    List<BuildingLinkEdge>  linkEdges = new ArrayList<BuildingLinkEdge>();
    
    BuildingSecurityGraph securityGraph = BuildingSecurityGraph.getInstance();
    securityGraph.clearAll();
     
    this.preferences = preferences;

    for (Room r : rooms)
    {
      RoomGeoSmart rg = new RoomGeoSmart(r);
      List<Asset> objectsInside = new ArrayList<Asset>();
      BuildingRoomNode brn = new BuildingRoomNode(rg.getName(), 
          rg.getShape(), objectsInside);  
      
      brn.setId(r.getId());
      brn.setName(r.getName());
      if(r.getName() == null || r.getName().equals(""))
      {
        r.setName(r.getId());
      }
      securityGraph.putBuildingRoom(new IdRoom(r.getId()), brn);
      
      for(HomePieceOfFurniture pieceOfForn : fornitures)
      {
        Point fornitPoint = new Point((int)pieceOfForn.getX() * 1000 , (int)pieceOfForn.getY() * 1000);

        Polygon roomShape = r.getPolygon1000xBigger();

        boolean isFornitureInsideRoom = roomShape.contains(fornitPoint) && (pieceOfForn.getLevel() == r.getLevel());

        if (isFornitureInsideRoom)
        {
          Vector3D position = pieceOfForn.getPosition();
          
          
          SavedConfigurationsLoader cfg = this.getConfig(preferences);
          SecurityNameAndMap namesConv = cfg.getNamesConventions();
          Map<String, AssetType> catalog = namesConv.sweetCatalogToType;
          String name = pieceOfForn.getOriginalName();
          if(name == null)
          {
            throw new IllegalStateException("tha name should be not null!!!");
          }
          AssetType typeObj = catalog.get(name);
          if(typeObj == null)
          {
            typeObj = AssetType.UNKNOWN_OBJECT;
          }
          if(typeObj == null)
          {
            int pippo=0;
            pippo++;
          }
          
          Asset objCont = typeObj.getBuildingObjectOfType(position);
          objCont.setId(pieceOfForn.getId());
          objCont.setName(pieceOfForn.getName());
          objCont.setOriginalName(pieceOfForn.getOriginalName());
          
          super.setAbilitiesAndAttributes(objCont);
          
          securityGraph.putObjectCont(new IdObject(pieceOfForn.getId()), objCont);
          securityGraph.putObjectRoom(new IdObject(pieceOfForn.getId()), brn);
          
          brn.addObjectContained(objCont);
        }
      }

      roomNodes.add(brn);
      
    }
    
    securityGraph.setRoomNodeList(roomNodes);
    
    
    /* 3) now we have the edges:  rooms filled of objects, we now need the links  */

    List<HomePieceOfFurniture> doors = new ArrayList<HomePieceOfFurniture>();
    for (HomePieceOfFurniture hpf : fornitures)
    {
      if (hpf.isDoorOrWindow())
        doors.add( hpf);
    }

    // iterate on all the rooms and then intersect the bounding rectangles
    // if the (increased) bounding rect intersect, then we check wether there is a door
    
    for (int i=0; i< rooms.size(); i++)
    {
      for (int j=i+1; j< rooms.size(); j++)
      {
        Room r1 = rooms.get(i), r2 = rooms.get(j);
        if (r1 != r2 &&  areIntersected(r1, r2))
        {

          BuildingLinkEdge link = null;
          //TODO: put wall correctly
          
          String roomId1 = r1.getId();
          String roomId2 = r2.getId();
          if(r1.getName() != null && r1.getName().length() > 0)
          {
            roomId1 = r1.getName();
          }
          
          if(r2.getName() != null && r2.getName().length() > 0)
          {
            roomId2 = r2.getName();
          }
          
          boolean areRoomsLinkedByDoor = false;
          for (HomePieceOfFurniture d : doors )
          {  
            if ( areLinkedRoomsAndDoor(r1, r2, d))
            {
              areRoomsLinkedByDoor = true;
              // there is a link bw the E with r1 and the E with r2
              //graphRooms.
              DoorObject dobj = new DoorObject();
              dobj.setId(d.getId());

              
              dobj.setIdRoom1(roomId1);
              dobj.setIdRoom1(roomId2);
              link = new BuildinLinkWallWithDoor(null, dobj, roomId1, roomId2);
              //TODO: put wall correctly
            }
          }
          if(!areRoomsLinkedByDoor) 
              link = new BuildingLinkWall(null, roomId1, roomId2);
         
          linkEdges.add(link);
        }
      }
    }
    
    
    securityGraph.setRoomNodeList(roomNodes);
    securityGraph.setLinkEdgeList(linkEdges);
    securityGraph.setNotLinkingWalls(walls);
    
    return securityGraph;

  }

  private boolean areIntersected(Room r1, Room r2) {
     //         METHOD 1:
     boolean inters1 = r1.intersectApprox(r2, 50);
    
    //          METHOD 2:
    
    RoomGeoSmart rg1 = new RoomGeoSmart(r1);
    RoomGeoSmart rg2 = new RoomGeoSmart(r2);
    
    boolean inters2 = rg2.intersectBordered(rg1, 15);
    
    //          COMPARISON
    //    if(inters1 != inters2)
    //    {
    //      int pippo = 1231;
    //      pippo++;
    //    }
    
    return inters2;
  }

  protected SavedConfigurationsLoader getConfig(UserPreferences preferences) {
    return SavedConfigurationsLoader.getInstance(preferences);
  }

  private boolean areLinkedRoomsAndDoor(Room r1, Room r2, HomePieceOfFurniture d)
  {

    Polygon pol1 = r1.getPolygon();
    Polygon pol2 = r2.getPolygon();

    float xc = d.getX();
    float yc = d.getY();
    float edge = d.getDepth() * 5f ;
    float upx = xc - edge / 2;
    float upy = yc - edge / 2;


    boolean inters1 = pol1.intersects(upx, upy, edge, edge);
    boolean inters2 = pol2.intersects(upx, upy, edge, edge);


    return inters1 && inters2;
  }

  
  

}
