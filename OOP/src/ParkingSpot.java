// Temel park yeri sınıfı (Abstract)
public abstract class ParkingSpot {
    private String spotId;
    private int floor;
    private boolean isOccupied;
    private Vehicle parkedVehicle;

    public ParkingSpot(String spotId, int floor) {
        this.spotId = spotId;
        this.floor = floor;
        this.isOccupied = false;
        this.parkedVehicle = null;
    }

    // Abstract methods
    public abstract String getSpotType();
    public abstract double getPriceMultiplier();
    public abstract boolean canFitVehicle(Vehicle vehicle);

    // Park etme metodu
    public void parkVehicle(Vehicle vehicle) throws Exception {
        if (isOccupied) {
            throw new Exception("Spot is already occupied!");
        }
        if (!canFitVehicle(vehicle)) {
            throw new Exception("Vehicle cannot fit in this spot!");
        }
        this.parkedVehicle = vehicle;
        this.isOccupied = true;
    }

    // Aracı çıkarma metodu
    public Vehicle removeVehicle() throws Exception {
        if (!isOccupied) {
            throw new Exception("No vehicle parked in this spot!");
        }
        Vehicle vehicle = this.parkedVehicle;
        this.parkedVehicle = null;
        this.isOccupied = false;
        return vehicle;
    }

    // Getters
    public String getSpotId() {
        return spotId;
    }

    public int getFloor() {
        return floor;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    @Override
    public String toString() {
        return getSpotType() + " - " + spotId + " (Floor " + floor + ") - " +
                (isOccupied ? "OCCUPIED" : "AVAILABLE");
    }
}

// RegularSpot sınıfı
class RegularSpot extends ParkingSpot {

    public RegularSpot(String spotId, int floor) {
        super(spotId, floor);
    }

    @Override
    public String getSpotType() {
        return "Regular Spot";
    }

    @Override
    public double getPriceMultiplier() {
        return 1.0;
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        // Truck hariç hepsini alır
        return !(vehicle instanceof Truck);
    }
}

// CompactSpot sınıfı (Küçük araçlar için)
class CompactSpot extends ParkingSpot {

    public CompactSpot(String spotId, int floor) {
        super(spotId, floor);
    }

    @Override
    public String getSpotType() {
        return "Compact Spot";
    }

    @Override
    public double getPriceMultiplier() {
        return 0.8; // Daha ucuz
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        // Sadece motor ve küçük arabalar
        return vehicle instanceof Motorcycle ||
                (vehicle instanceof Car && !(vehicle instanceof Van));
    }
}

// DisabledSpot sınıfı
class DisabledSpot extends ParkingSpot {
    private boolean requiresPermit;

    public DisabledSpot(String spotId, int floor) {
        super(spotId, floor);
        this.requiresPermit = true;
    }

    @Override
    public String getSpotType() {
        return "Disabled Spot";
    }

    @Override
    public double getPriceMultiplier() {
        return 0.5; // İndirimli
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle instanceof Car || vehicle instanceof Van;
    }
}

// VIPSpot sınıfı
class VIPSpot extends ParkingSpot {
    private boolean hasValet;

    public VIPSpot(String spotId, int floor, boolean hasValet) {
        super(spotId, floor);
        this.hasValet = hasValet;
    }

    @Override
    public String getSpotType() {
        return "VIP Spot";
    }

    @Override
    public double getPriceMultiplier() {
        return 2.5; // Premium fiyat
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        return true; // Hepsini alır
    }

    public boolean hasValet() {
        return hasValet;
    }
}

// ElectricChargingSpot sınıfı
class ElectricChargingSpot extends ParkingSpot {
    private int chargingPower; // kW
    private boolean isCharging;

    public ElectricChargingSpot(String spotId, int floor, int chargingPower) {
        super(spotId, floor);
        this.chargingPower = chargingPower;
        this.isCharging = false;
    }

    @Override
    public String getSpotType() {
        return "Electric Charging Spot";
    }

    @Override
    public double getPriceMultiplier() {
        return 1.8; // Şarj ücreti dahil
    }

    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle instanceof ElectricVehicle;
    }

    @Override
    public void parkVehicle(Vehicle vehicle) throws Exception {
        super.parkVehicle(vehicle);
        if (vehicle instanceof ElectricVehicle) {
            ElectricVehicle ev = (ElectricVehicle) vehicle;
            if (ev.isNeedsCharging()) {
                this.isCharging = true;
            }
        }
    }

    public int getChargingPower() {
        return chargingPower;
    }

    public boolean isCharging() {
        return isCharging;
    }
}