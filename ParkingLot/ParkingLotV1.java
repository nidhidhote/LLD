import java.util.ArrayList;
import java.util.*;
import java.util.List;

/**
 * Parking lot
 * -Spot
 * -id
 * -Floor
 * -SpotStatus
 * -Vehicle
 * -isElectricChargeAvailable?//v1
 * -isSpeciallyAbledCanPark?//v1
 *
 *
 * Vehicle
 * -vehicleId
 * -vehicleType
 *
 * VehicleType {enum}
 * -motorbike (preferences: small, medium, large)
 * -car (medium, large)
 * -Truck (large)
 *
 * SpotType {enum}
 * -small
 * -medium
 * -large
 *
 * SpotStatus {enum}
 * -Free
 * -Occupied
 *
 * Parking lot will be having multiple floors
 * Floor
 * -floorNumber
 * -List<ParkingSpot>
 *
 *
 * Public APIs
 * park: if spot is available, spot status is free, then mark it as occupied
 * unPark: if it is occupied then mark it as free
 *
 * For MVP considering only park and unpark APIs
 *
 *
 *
 *
 *
 * */


enum VehicleType {
    MotorBike,
    Car,
    Truck
}

enum SpotType{
    Small,
    Medium,
    Large
}

enum SpotStatus{
    Free,
    Occupied
}
public class ParkingLotV1 {
    public class ParkingLot {
        Map<Spot, SpotStatus> spotStatusMap = new HashMap<>();
        Vehicle vehicle;
        ParkingLot(Spot spot){
            spotStatusMap.put(spot,spot.spotStatus);
        }
        public String park(Vehicle vehicle){
            //in spot status map, is any spot is free or not
            for(Map.Entry<Spot,SpotStatus> sp: spotStatusMap.entrySet()){
              //  System.out.println("*****************"+sp.getValue());
                if (sp.getKey().spotType == vehicle.spotType && sp.getValue()== SpotStatus.Free){
                    sp.setValue(SpotStatus.Occupied);
                    sp.getKey().vehicle = vehicle;
                    return "Vehicle "+ vehicle.vehicleId+" got parked at "+ sp.getKey().spotId;
                }
            }
            return "No parking spot is empty";
        }
        public void unPark(Vehicle vehicle){
            for(Map.Entry<Spot,SpotStatus> sp: spotStatusMap.entrySet()){
                if (sp.getValue()== SpotStatus.Occupied && sp.getKey().vehicle.vehicleId == vehicle.vehicleId){
                    sp.setValue(SpotStatus.Free);
                    sp.getKey().vehicle = null;
                    System.out.println("Vehicle "+ vehicle.vehicleId+" got unparked at "+ sp.getKey().spotId);
                }
            }
        }

    }
    public class Spot{
        private final int spotId;
       // Map<Integer,SpotType>spotStatusMap = new HashMap<>();
         Floor floor;
        Vehicle vehicle;
        SpotType spotType;
        SpotStatus spotStatus;
        Spot(SpotType spotType, Floor floor){
            this.spotType = spotType;
            this.spotStatus = SpotStatus.Free ;
            this.spotId = floor.floorNumber+spotType.ordinal();
        }
    }
    public class Floor{
        private final int floorNumber;
       // List<Spot> spots = new ArrayList<>();
        Floor(int floorNumber){
            this.floorNumber = floorNumber;
        }
    }
    public class Vehicle{
        private final int vehicleId;
        private final SpotType spotType;
        Vehicle(int vehicleId, SpotType spotType){
            this.vehicleId = vehicleId;
            this.spotType = spotType;
        }
    }
    public static void main(String[] args) {
        //test
        //creat parking spots
        ParkingLotV1 parkingLotV1 = new ParkingLotV1();
        Spot spot = parkingLotV1.new Spot(SpotType.Medium, parkingLotV1.new Floor(1));
        ParkingLot parkingLot = parkingLotV1.new ParkingLot(spot);
        Vehicle vehicle = parkingLotV1.new Vehicle(1,SpotType.Large);
        System.out.println(parkingLot.park(vehicle));
    }
}
