package com.eteks.sweethome3d.adaptive.security.ifcSecurity;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eteks.sweethome3d.adaptive.reachabletree.SecurityGraphEdge;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.BuildinLinkWallWithDoor;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.BuildingLinkEdge;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.BuildingLinkWall;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.BuildingRoomNode;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.BuildingSecurityGraph;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.SessionIdentifierGenerator;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.wrapper.IdObject;
import com.eteks.sweethome3d.adaptive.security.buildingGraph.wrapper.IdRoom;
import com.eteks.sweethome3d.adaptive.security.buildingGraphObjects.BuildingObjectContained;
import com.eteks.sweethome3d.adaptive.security.buildingGraphObjects.BuildingObjectType;
import com.eteks.sweethome3d.adaptive.security.buildingGraphObjects.DoorObject;
import com.eteks.sweethome3d.adaptive.security.ifcSecurity.ConfigLoader.SecurityNameAndMap;
import com.eteks.sweethome3d.adaptive.security.parserobjects.Vector3D;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomePieceOfFurniture;
import com.eteks.sweethome3d.model.Room;
import com.eteks.sweethome3d.model.RoomGeoSmart;
import com.eteks.sweethome3d.model.UserPreferences;
import com.eteks.sweethome3d.model.Wall;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;

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
     
    Map<Room, SecurityGraphEdge> graphRooms = new HashMap<Room, SecurityGraphEdge>();
    List<SecurityGraphEdge>   graphRoomsList = new ArrayList<SecurityGraphEdge>();

    for (Room r : rooms)
    {
      SecurityGraphEdge roomToFill = new SecurityGraphEdge(r);
      RoomGeoSmart rg = new RoomGeoSmart(r);
      List<BuildingObjectContained> objectsInside = new ArrayList<BuildingObjectContained>();
      BuildingRoomNode brn = new BuildingRoomNode(rg.getName(), 
          rg.getShape(), objectsInside);  
      
      brn.setId(r.getId());
      securityGraph.putBuildingRoom(new IdRoom(r.getId()), brn);
      
      for(HomePieceOfFurniture pieceOfForn : fornitures)
      {
        Point fornitPoint = new Point((int)pieceOfForn.getX() * 1000 , (int)pieceOfForn.getY() * 1000);

        Polygon roomShape = r.getPolygon1000xBigger();

        boolean isFornitureInsideRoom = roomShape.contains(fornitPoint) && (pieceOfForn.getLevel() == r.getLevel());

        if (isFornitureInsideRoom)
        {
          Vector3D position = pieceOfForn.getPosition();
          
          roomToFill.addPieceOfForniture(pieceOfForn);
         
          ConfigLoader cfg = ConfigLoader.getInstance(preferences);
          SecurityNameAndMap namesConv = cfg.namesConventionsSweetHome;
          Map<String, BuildingObjectType> catalog = namesConv.catalog;
          String name = pieceOfForn.getName();
          BuildingObjectType typeObj = catalog.get(name);
          if(typeObj == null)
          {
            typeObj = BuildingObjectType.UNKNOWN_OBJECT;
          }
          if(typeObj == null)
          {
            int pippo=0;
            pippo++;
          }
          
          BuildingObjectContained objCont = typeObj.getBuildingObjectOfType(position);
          objCont.setId(pieceOfForn.getId());
          
          securityGraph.putObjectCont(new IdObject(pieceOfForn.getId()), objCont);
          securityGraph.putObjectRoom(new IdObject(pieceOfForn.getId()), brn);
          
          brn.addObjectContained(objCont);
        }
      }

      roomNodes.add(brn);
      graphRoomsList.add(roomToFill);
      graphRooms.put(r, roomToFill);

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
    Set<SecurityGraphEdge> graphOfRe = new HashSet<SecurityGraphEdge>();
    for (int i=0; i< rooms.size(); i++)
    {
      for (int j=i+1; j< rooms.size(); j++)
      {
        Room r1 = rooms.get(i), r2 = rooms.get(j);
        if (r1 != r2 &&  r1.intersectApprox(r2, 50))
        {

          for (HomePieceOfFurniture d : doors )
          {
            boolean areRoomsLinkedByDoor = areLinkedRoomsAndDoor(r1, r2, d);
            BuildingLinkEdge link;
            if ( areRoomsLinkedByDoor)
            {
              // there is a link bw the E with r1 and the E with r2
              //graphRooms.
              SecurityGraphEdge e1 = graphRooms.get(r1);
              SecurityGraphEdge e2 = graphRooms.get(r2);
              e1.addNeighbour(e2);
              e2.addNeighbour(e1);
              
              graphOfRe.add(e1);
              graphOfRe.add(e2);
              DoorObject dobj = new DoorObject();
              dobj.setId(d.getId());
              dobj.setIdRoom1(r1.getId());
              dobj.setIdRoom1(r2.getId());
              link = new BuildinLinkWallWithDoor(null, dobj, r1.getId(), r1.getId());
              
              //TODO: put wall correctly
              
            }
            else
            {
              //TODO: put wall correctly
              link = new BuildingLinkWall(null, r1.getId(), r2.getId());
            }
            String linkID = SessionIdentifierGenerator.getInstance().nextSessionId();
            link.setId(linkID);
            
            
          }

        }
      }
    }
    
    
    securityGraph.setRoomNodeList(roomNodes);
    securityGraph.setLinkEdgeList(linkEdges);
    securityGraph.setNotLinkingWalls(walls);
    return securityGraph;

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
