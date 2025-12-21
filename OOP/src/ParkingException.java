// Temel exception sınıfı
public class ParkingException extends Exception {
    private String errorCode;

    public ParkingException(String message) {
        super(message);
        this.errorCode = "PARKING_ERROR";
    }

    public ParkingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return "ParkingException [" + errorCode + "]: " + getMessage();
    }
}

// SpotNotAvailableException - Yer bulunamadı
class SpotNotAvailableException extends ParkingException {
    private String vehicleType;

    public SpotNotAvailableException(String vehicleType) {
        super("No available parking spot for vehicle type: " + vehicleType,
                "SPOT_NOT_AVAILABLE");
        this.vehicleType = vehicleType;
    }

    public String getVehicleType() {
        return vehicleType;
    }
}

// InvalidVehicleException - Geçersiz araç
class InvalidVehicleException extends ParkingException {
    private String licensePlate;

    public InvalidVehicleException(String message, String licensePlate) {
        super(message, "INVALID_VEHICLE");
        this.licensePlate = licensePlate;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
}

// PaymentFailedException - Ödeme hatası
class PaymentFailedException extends ParkingException {
    private double amount;
    private String paymentMethod;

    public PaymentFailedException(String message, double amount, String paymentMethod) {
        super(message, "PAYMENT_FAILED");
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}

// SpotNotFoundException - Park yeri bulunamadı
class SpotNotFoundException extends ParkingException {
    private String spotId;

    public SpotNotFoundException(String spotId) {
        super("Parking spot not found with ID: " + spotId, "SPOT_NOT_FOUND");
        this.spotId = spotId;
    }

    public String getSpotId() {
        return spotId;
    }
}

// TicketExpiredException - Bilet süresi dolmuş
class TicketExpiredException extends ParkingException {
    private String ticketId;

    public TicketExpiredException(String ticketId) {
        super("Ticket has expired: " + ticketId, "TICKET_EXPIRED");
        this.ticketId = ticketId;
    }

    public String getTicketId() {
        return ticketId;
    }
}

// CapacityFullException - Kapasite dolu
class CapacityFullException extends ParkingException {
    private int totalCapacity;
    private int occupiedSpots;

    public CapacityFullException(int totalCapacity, int occupiedSpots) {
        super("Parking lot is full! Capacity: " + totalCapacity +
                ", Occupied: " + occupiedSpots, "CAPACITY_FULL");
        this.totalCapacity = totalCapacity;
        this.occupiedSpots = occupiedSpots;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public int getOccupiedSpots() {
        return occupiedSpots;
    }
}

// VehicleAlreadyParkedException - Araç zaten park edilmiş
class VehicleAlreadyParkedException extends ParkingException {
    private String licensePlate;
    private String currentSpotId;

    public VehicleAlreadyParkedException(String licensePlate, String currentSpotId) {
        super("Vehicle " + licensePlate + " is already parked at spot " + currentSpotId,
                "VEHICLE_ALREADY_PARKED");
        this.licensePlate = licensePlate;
        this.currentSpotId = currentSpotId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getCurrentSpotId() {
        return currentSpotId;
    }
}