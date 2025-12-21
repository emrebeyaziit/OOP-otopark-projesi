import java.time.LocalDateTime;
import java.time.Duration;

// Abstract Transaction sınıfı
public abstract class Transaction {
    private String transactionId;
    private LocalDateTime transactionTime;
    private Ticket ticket;
    private Employee processedBy;

    public Transaction(String transactionId, Ticket ticket, Employee processedBy) {
        this.transactionId = transactionId;
        this.ticket = ticket;
        this.processedBy = processedBy;
        this.transactionTime = LocalDateTime.now();
    }

    // Abstract methods
    public abstract String getTransactionType();
    public abstract void execute() throws ParkingException;

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Employee getProcessedBy() {
        return processedBy;
    }

    @Override
    public String toString() {
        return "Transaction ID: " + transactionId +
                ", Type: " + getTransactionType() +
                ", Time: " + transactionTime +
                ", Processed by: " + processedBy.getName();
    }
}

// EntryTransaction - Giriş işlemi
class EntryTransaction extends Transaction {
    private ParkingSpot assignedSpot;

    public EntryTransaction(String transactionId, Ticket ticket,
                            Employee processedBy, ParkingSpot assignedSpot) {
        super(transactionId, ticket, processedBy);
        this.assignedSpot = assignedSpot;
    }

    @Override
    public String getTransactionType() {
        return "ENTRY";
    }

    @Override
    public void execute() throws ParkingException {
        try {
            // Bilet geçerliliğini kontrol et
            if (!getTicket().isValid()) {
                throw new TicketExpiredException(getTicket().getTicketId());
            }

            // Aracı park et
            assignedSpot.parkVehicle(getTicket().getVehicle());
            getTicket().setAssignedSpot(assignedSpot);

            // Müşteri ziyaret sayısını artır
            getTicket().getCustomer().incrementVisitCount();

            System.out.println("=== ENTRY TRANSACTION SUCCESSFUL ===");
            System.out.println("Vehicle: " + getTicket().getVehicle().getLicensePlate());
            System.out.println("Assigned Spot: " + assignedSpot.getSpotId());
            System.out.println("Floor: " + assignedSpot.getFloor());
            System.out.println("Entry Time: " + getTransactionTime());
            System.out.println("====================================");

        } catch (Exception e) {
            throw new ParkingException("Entry transaction failed: " + e.getMessage());
        }
    }

    public ParkingSpot getAssignedSpot() {
        return assignedSpot;
    }
}

// ExitTransaction - Çıkış işlemi
class ExitTransaction extends Transaction {
    private Payment payment;
    private double totalFee;
    private Duration parkingDuration;

    public ExitTransaction(String transactionId, Ticket ticket,
                           Employee processedBy) {
        super(transactionId, ticket, processedBy);
        calculateParkingDuration();
        calculateTotalFee();
    }

    @Override
    public String getTransactionType() {
        return "EXIT";
    }

    private void calculateParkingDuration() {
        LocalDateTime entryTime = getTicket().getIssueTime();
        LocalDateTime exitTime = getTransactionTime();
        this.parkingDuration = Duration.between(entryTime, exitTime);
    }

    private void calculateTotalFee() {
        this.totalFee = getTicket().calculateFee(parkingDuration);
    }

    @Override
    public void execute() throws ParkingException {
        try {
            ParkingSpot spot = getTicket().getAssignedSpot();

            if (spot == null) {
                throw new ParkingException("No parking spot assigned to this ticket!");
            }

            // Ücreti göster
            System.out.println("=== EXIT TRANSACTION ===");
            System.out.println("Vehicle: " + getTicket().getVehicle().getLicensePlate());
            System.out.println("Parking Duration: " + formatDuration(parkingDuration));
            System.out.println("Total Fee: " + totalFee + " TL");
            System.out.println("========================");

            // Ödeme yapılmadıysa işlem tamamlanamaz
            if (payment == null && totalFee > 0) {
                throw new PaymentFailedException(
                        "Payment required before exit!",
                        totalFee,
                        "NONE"
                );
            }

            // Aracı çıkar
            spot.removeVehicle();

            System.out.println("EXIT SUCCESSFUL!");
            System.out.println("Spot " + spot.getSpotId() + " is now available.");

        } catch (Exception e) {
            throw new ParkingException("Exit transaction failed: " + e.getMessage());
        }
    }

    public void processPayment(Payment payment) throws PaymentFailedException {
        if (payment.processPayment(totalFee)) {
            this.payment = payment;
            System.out.println("Payment processed successfully!");
        } else {
            throw new PaymentFailedException(
                    "Payment processing failed!",
                    totalFee,
                    payment.getPaymentMethod()
            );
        }
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return hours + " hours, " + minutes + " minutes";
    }

    public double getTotalFee() {
        return totalFee;
    }

    public Duration getParkingDuration() {
        return parkingDuration;
    }

    public Payment getPayment() {
        return payment;
    }
}

// PaymentTransaction - Ödeme işlemi
class PaymentTransaction extends Transaction {
    private Payment payment;
    private double amount;
    private boolean isSuccessful;

    public PaymentTransaction(String transactionId, Ticket ticket,
                              Employee processedBy, Payment payment, double amount) {
        super(transactionId, ticket, processedBy);
        this.payment = payment;
        this.amount = amount;
        this.isSuccessful = false;
    }

    @Override
    public String getTransactionType() {
        return "PAYMENT";
    }

    @Override
    public void execute() throws ParkingException {
        try {
            System.out.println("=== PROCESSING PAYMENT ===");
            System.out.println("Amount: " + amount + " TL");
            System.out.println("Method: " + payment.getPaymentMethod());

            boolean success = payment.processPayment(amount);
            this.isSuccessful = success;

            if (!success) {
                throw new PaymentFailedException(
                        "Payment could not be processed!",
                        amount,
                        payment.getPaymentMethod()
                );
            }

            System.out.println("PAYMENT SUCCESSFUL!");
            System.out.println("=========================");

        } catch (Exception e) {
            throw new PaymentFailedException(
                    "Payment transaction failed: " + e.getMessage(),
                    amount,
                    payment.getPaymentMethod()
            );
        }
    }

    public Payment getPayment() {
        return payment;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}