import java.time.LocalDateTime;
import java.time.Duration;

// Temel bilet sınıfı (Abstract)
public abstract class Ticket {
    private String ticketId;
    private LocalDateTime issueTime;
    private Customer customer;
    private Vehicle vehicle;
    private ParkingSpot assignedSpot;

    public Ticket(String ticketId, Customer customer, Vehicle vehicle) {
        this.ticketId = ticketId;
        this.customer = customer;
        this.vehicle = vehicle;
        this.issueTime = LocalDateTime.now();
    }

    // Abstract methods
    public abstract String getTicketType();
    public abstract double calculateFee(Duration parkingDuration);
    public abstract boolean isValid();

    // Getters and Setters
    public String getTicketId() {
        return ticketId;
    }

    public LocalDateTime getIssueTime() {
        return issueTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getAssignedSpot() {
        return assignedSpot;
    }

    public void setAssignedSpot(ParkingSpot spot) {
        this.assignedSpot = spot;
    }

    @Override
    public String toString() {
        return "Ticket ID: " + ticketId +
                ", Type: " + getTicketType() +
                ", Issue Time: " + issueTime;
    }
}

// HourlyTicket sınıfı
class HourlyTicket extends Ticket {
    private static final double BASE_RATE_PER_HOUR = 100;

    public HourlyTicket(String ticketId, Customer customer, Vehicle vehicle) {
        super(ticketId, customer, vehicle);
    }

    @Override
    public String getTicketType() {
        return "Hourly Ticket";
    }

    @Override
    public double calculateFee(Duration parkingDuration) {
        long hours = parkingDuration.toHours();
        if (hours == 0) hours = 1; // Minimum 1 saat

        double baseFee = hours * BASE_RATE_PER_HOUR;
        double vehicleMultiplier = getVehicle().getSizeMultiplier();
        double spotMultiplier = getAssignedSpot() != null ?
                getAssignedSpot().getPriceMultiplier() : 1.0;

        return baseFee * vehicleMultiplier * spotMultiplier;
    }

    @Override
    public boolean isValid() {
        return true; // Her zaman geçerli
    }
}

// DailyTicket sınıfı
class DailyTicket extends Ticket {
    private static final double DAILY_RATE = 500;
    private LocalDateTime expiryTime;

    public DailyTicket(String ticketId, Customer customer, Vehicle vehicle) {
        super(ticketId, customer, vehicle);
        this.expiryTime = getIssueTime().plusDays(1);
    }

    @Override
    public String getTicketType() {
        return "Daily Ticket";
    }

    @Override
    public double calculateFee(Duration parkingDuration) {
        long days = parkingDuration.toDays();
        if (days == 0) days = 1; // Minimum 1 gün

        double baseFee = days * DAILY_RATE;
        double vehicleMultiplier = getVehicle().getSizeMultiplier();
        double spotMultiplier = getAssignedSpot() != null ?
                getAssignedSpot().getPriceMultiplier() : 1.0;

        return baseFee * vehicleMultiplier * spotMultiplier;
    }

    @Override
    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiryTime);
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }
}

// MonthlySubscription sınıfı
class MonthlySubscription extends Ticket {
    private static final double MONTHLY_RATE = 3500;
    private LocalDateTime expiryTime;
    private int remainingEntries;

    public MonthlySubscription(String ticketId, Customer customer, Vehicle vehicle) {
        super(ticketId, customer, vehicle);
        this.expiryTime = getIssueTime().plusMonths(1);
        this.remainingEntries = -1; // Sınırsız giriş
    }

    @Override
    public String getTicketType() {
        return "Monthly Subscription";
    }

    @Override
    public double calculateFee(Duration parkingDuration) {
        // Abonelik ücreti önceden ödenmiş
        return 0.0;
    }

    @Override
    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiryTime);
    }

    public double getSubscriptionFee() {
        return MONTHLY_RATE;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }
}

// YearlySubscription sınıfı
class YearlySubscription extends Ticket {
    private static final double YEARLY_RATE = 30000.0;
    private LocalDateTime expiryTime;
    private boolean hasVIPAccess;

    public YearlySubscription(String ticketId, Customer customer,
                              Vehicle vehicle, boolean hasVIPAccess) {
        super(ticketId, customer, vehicle);
        this.expiryTime = getIssueTime().plusYears(1);
        this.hasVIPAccess = hasVIPAccess;
    }

    @Override
    public String getTicketType() {
        return "Yearly Subscription";
    }

    @Override
    public double calculateFee(Duration parkingDuration) {
        return 0.0; // Abonelik önceden ödenmiş
    }

    @Override
    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiryTime);
    }

    public double getSubscriptionFee() {
        return hasVIPAccess ? YEARLY_RATE * 1.5 : YEARLY_RATE;
    }

    public boolean hasVIPAccess() {
        return hasVIPAccess;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }
}

// VIPMembership sınıfı
class VIPMembership extends Ticket {
    private static final double VIP_MONTHLY_RATE = 6000.0;
    private LocalDateTime expiryTime;
    private boolean hasValetService;
    private boolean hasChargingAccess;

    public VIPMembership(String ticketId, Customer customer, Vehicle vehicle) {
        super(ticketId, customer, vehicle);
        this.expiryTime = getIssueTime().plusMonths(1);
        this.hasValetService = true;
        this.hasChargingAccess = true;
    }

    @Override
    public String getTicketType() {
        return "VIP Membership";
    }

    @Override
    public double calculateFee(Duration parkingDuration) {
        return 0.0; // VIP üyelik ücretsiz park
    }

    @Override
    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiryTime);
    }

    public double getMembershipFee() {
        return VIP_MONTHLY_RATE;
    }

    public boolean hasValetService() {
        return hasValetService;
    }

    public boolean hasChargingAccess() {
        return hasChargingAccess;
    }
}