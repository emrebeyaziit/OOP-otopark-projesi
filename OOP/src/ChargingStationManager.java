import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;

// ChargingStation - Şarj istasyonu
class ChargingStation {
    private String stationId;
    private ElectricChargingSpot spot;
    private int maxPowerKW; // Maximum charging power
    private String chargerType; // "Type2", "CCS", "CHAdeMO"
    private boolean isOperational;
    private double pricePerKWh;
    private ChargingSession currentSession;

    public ChargingStation(String stationId, ElectricChargingSpot spot,
                           int maxPowerKW, String chargerType, double pricePerKWh) {
        this.stationId = stationId;
        this.spot = spot;
        this.maxPowerKW = maxPowerKW;
        this.chargerType = chargerType;
        this.isOperational = true;
        this.pricePerKWh = pricePerKWh;
        this.currentSession = null;
    }

    public boolean isAvailable() {
        return isOperational && currentSession == null && !spot.isOccupied();
    }

    public ChargingSession startCharging(ElectricVehicle vehicle, int targetPercentage) {
        if (!isAvailable()) {
            System.out.println("❌ Charging station not available!");
            return null;
        }

        String sessionId = stationId + "_SES_" + System.currentTimeMillis();
        currentSession = new ChargingSession(
                sessionId, this, vehicle, targetPercentage
        );

        currentSession.start();
        return currentSession;
    }

    public void stopCharging() {
        if (currentSession != null) {
            currentSession.stop();
            currentSession = null;
        }
    }

    // Getters and Setters
    public String getStationId() {
        return stationId;
    }

    public int getMaxPowerKW() {
        return maxPowerKW;
    }

    public String getChargerType() {
        return chargerType;
    }

    public boolean isOperational() {
        return isOperational;
    }

    public void setOperational(boolean operational) {
        this.isOperational = operational;
    }

    public double getPricePerKWh() {
        return pricePerKWh;
    }

    public ChargingSession getCurrentSession() {
        return currentSession;
    }

    public ElectricChargingSpot getSpot() {
        return spot;
    }

    @Override
    public String toString() {
        return "Station " + stationId + " [" + chargerType + ", " + maxPowerKW + "kW] - " +
                (isAvailable() ? "AVAILABLE" : "BUSY");
    }
}

// ChargingSession - Şarj seansı
class ChargingSession {
    private String sessionId;
    private ChargingStation station;
    private ElectricVehicle vehicle;
    private int initialBatteryPercentage;
    private int targetBatteryPercentage;
    private int currentBatteryPercentage;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double energyConsumedKWh;
    private double totalCost;
    private String status; // "CHARGING", "COMPLETED", "INTERRUPTED"
    private int estimatedTimeMinutes;

    public ChargingSession(String sessionId, ChargingStation station,
                           ElectricVehicle vehicle, int targetPercentage) {
        this.sessionId = sessionId;
        this.station = station;
        this.vehicle = vehicle;
        this.initialBatteryPercentage = 30; // Simülasyon: başlangıç %30
        this.targetBatteryPercentage = targetPercentage;
        this.currentBatteryPercentage = initialBatteryPercentage;
        this.energyConsumedKWh = 0;
        this.totalCost = 0;
        this.status = "PENDING";
    }

    public void start() {
        this.startTime = LocalDateTime.now();
        this.status = "CHARGING";

        // Tahmini süre hesapla
        int batteryCapacity = vehicle.getBatteryCapacity();
        int neededPercentage = targetBatteryPercentage - initialBatteryPercentage;
        double neededKWh = (batteryCapacity * neededPercentage) / 100.0;
        this.estimatedTimeMinutes = (int)((neededKWh / station.getMaxPowerKW()) * 60);

        System.out.println("\n⚡ CHARGING STARTED");
        System.out.println("Session ID: " + sessionId);
        System.out.println("Vehicle: " + vehicle.getLicensePlate());
        System.out.println("Battery: " + initialBatteryPercentage + "% → " +
                targetBatteryPercentage + "%");
        System.out.println("Estimated Time: " + estimatedTimeMinutes + " minutes");
        System.out.println("Charging Power: " + station.getMaxPowerKW() + " kW");
    }

    public void stop() {
        this.endTime = LocalDateTime.now();
        this.status = "COMPLETED";

        // Gerçek süreyi hesapla
        long actualMinutes = Duration.between(startTime, endTime).toMinutes();
        if (actualMinutes == 0) actualMinutes = estimatedTimeMinutes; // Simülasyon için

        // Enerji tüketimini hesapla
        int chargedPercentage = targetBatteryPercentage - initialBatteryPercentage;
        this.energyConsumedKWh = (vehicle.getBatteryCapacity() * chargedPercentage) / 100.0;

        // Maliyet hesapla
        this.totalCost = energyConsumedKWh * station.getPricePerKWh();

        // Aracın batarya seviyesini güncelle
        this.currentBatteryPercentage = targetBatteryPercentage;
        vehicle.setNeedsCharging(false);

        System.out.println("\n✓ CHARGING COMPLETED");
        System.out.println("Duration: " + actualMinutes + " minutes");
        System.out.println("Energy Consumed: " + String.format("%.2f", energyConsumedKWh) + " kWh");
        System.out.println("Total Cost: " + String.format("%.2f", totalCost) + " TL");
        System.out.println("Battery Level: " + currentBatteryPercentage + "%");
    }

    public void interrupt(String reason) {
        this.endTime = LocalDateTime.now();
        this.status = "INTERRUPTED";
        System.out.println("\n⚠️ Charging interrupted: " + reason);
    }

    // Getters
    public String getSessionId() {
        return sessionId;
    }

    public ChargingStation getStation() {
        return station;
    }

    public ElectricVehicle getVehicle() {
        return vehicle;
    }

    public String getStatus() {
        return status;
    }

    public double getEnergyConsumedKWh() {
        return energyConsumedKWh;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }
}

// ChargingReservation - Şarj rezervasyonu
class ChargingReservation {
    private String reservationId;
    private Customer customer;
    private ElectricVehicle vehicle;
    private ChargingStation station;
    private LocalDateTime reservationTime;
    private LocalDateTime scheduledTime;
    private int durationMinutes;
    private String status; // "PENDING", "CONFIRMED", "ACTIVE", "COMPLETED", "CANCELLED"

    public ChargingReservation(String reservationId, Customer customer,
                               ElectricVehicle vehicle, ChargingStation station,
                               LocalDateTime scheduledTime, int durationMinutes) {
        this.reservationId = reservationId;
        this.customer = customer;
        this.vehicle = vehicle;
        this.station = station;
        this.reservationTime = LocalDateTime.now();
        this.scheduledTime = scheduledTime;
        this.durationMinutes = durationMinutes;
        this.status = "PENDING";
    }

    public void confirm() {
        this.status = "CONFIRMED";
        System.out.println("✓ Charging reservation confirmed: " + reservationId);
    }

    public void cancel() {
        this.status = "CANCELLED";
        System.out.println("✗ Charging reservation cancelled: " + reservationId);
    }

    public void activate() {
        this.status = "ACTIVE";
    }

    public void complete() {
        this.status = "COMPLETED";
    }

    // Getters
    public String getReservationId() {
        return reservationId;
    }

    public ChargingStation getStation() {
        return station;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public String getStatus() {
        return status;
    }

    public Customer getCustomer() {
        return customer;
    }
}

// ChargingStationManager - Şarj istasyonu yöneticisi
public class ChargingStationManager {
    private List<ChargingStation> chargingStations;
    private List<ChargingSession> sessionHistory;
    private List<ChargingReservation> reservations;
    private double totalEnergyDelivered;
    private double totalRevenue;

    public ChargingStationManager() {
        this.chargingStations = new ArrayList<>();
        this.sessionHistory = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.totalEnergyDelivered = 0;
        this.totalRevenue = 0;
    }

    // Şarj istasyonlarını başlat
    public void initializeStations(List<ElectricChargingSpot> evSpots) {
        System.out.println("\n⚡ Initializing charging stations...\n");

        int stationCounter = 1;
        for (ElectricChargingSpot spot : evSpots) {
            String stationId = "CS" + String.format("%03d", stationCounter++);

            // Farklı tip şarj istasyonları
            String chargerType;
            int power;
            double price;

            if (stationCounter % 3 == 0) {
                chargerType = "CCS"; // Fast charger
                power = 150;
                price = 8.0; // TL per kWh
            } else if (stationCounter % 3 == 1) {
                chargerType = "Type2"; // Standard
                power = 50;
                price = 6.0;
            } else {
                chargerType = "CHAdeMO"; // Fast
                power = 100;
                price = 7.0;
            }

            ChargingStation station = new ChargingStation(
                    stationId, spot, power, chargerType, price
            );

            chargingStations.add(station);
        }

        System.out.println("✓ Total charging stations: " + chargingStations.size() + "\n");
    }

    // Müsait istasyon bul
    public ChargingStation findAvailableStation(String preferredType) {
        // Önce tercih edilen tipi ara
        if (preferredType != null) {
            for (ChargingStation station : chargingStations) {
                if (station.isAvailable() &&
                        station.getChargerType().equals(preferredType)) {
                    return station;
                }
            }
        }

        // Bulamazsa herhangi bir müsait istasyon
        for (ChargingStation station : chargingStations) {
            if (station.isAvailable()) {
                return station;
            }
        }

        return null;
    }

    // Şarj başlat
    public ChargingSession startCharging(ElectricVehicle vehicle, int targetPercentage) {
        ChargingStation station = findAvailableStation(null);

        if (station == null) {
            System.out.println("❌ No available charging stations!");
            return null;
        }

        ChargingSession session = station.startCharging(vehicle, targetPercentage);

        if (session != null) {
            // Simülasyon: hemen tamamla
            station.stopCharging();
            sessionHistory.add(session);

            totalEnergyDelivered += session.getEnergyConsumedKWh();
            totalRevenue += session.getTotalCost();
        }

        return session;
    }

    // Rezervasyon oluştur
    public ChargingReservation createReservation(Customer customer,
                                                 ElectricVehicle vehicle,
                                                 LocalDateTime scheduledTime,
                                                 int durationMinutes,
                                                 String preferredChargerType) {
        ChargingStation station = findAvailableStation(preferredChargerType);

        if (station == null) {
            System.out.println("❌ No available stations for reservation!");
            return null;
        }

        String reservationId = "RES" + System.currentTimeMillis();
        ChargingReservation reservation = new ChargingReservation(
                reservationId, customer, vehicle, station, scheduledTime, durationMinutes
        );

        reservation.confirm();
        reservations.add(reservation);

        System.out.println("\n📅 CHARGING RESERVATION CREATED");
        System.out.println("Reservation ID: " + reservationId);
        System.out.println("Station: " + station.getStationId());
        System.out.println("Scheduled: " + scheduledTime);
        System.out.println("Duration: " + durationMinutes + " minutes");

        return reservation;
    }

    // Tüm istasyonları listele
    public void listAllStations() {
        System.out.println("\n╔════════ CHARGING STATIONS ════════╗");

        Map<String, Integer> stationsByType = new HashMap<>();
        int available = 0;

        for (ChargingStation station : chargingStations) {
            System.out.println(station);

            String type = station.getChargerType();
            stationsByType.put(type, stationsByType.getOrDefault(type, 0) + 1);

            if (station.isAvailable()) available++;
        }

        System.out.println("\nSummary:");
        System.out.println("Total Stations: " + chargingStations.size());
        System.out.println("Available: " + available);

        System.out.println("\nBy Type:");
        stationsByType.forEach((type, count) ->
                System.out.println("- " + type + ": " + count));

        System.out.println("╚════════════════════════════════════╝\n");
    }

    // Rapor oluştur
    public void generateChargingReport() {
        System.out.println("\n╔══════ CHARGING SERVICE REPORT ══════╗");
        System.out.println("Total Sessions: " + sessionHistory.size());
        System.out.println("Total Energy Delivered: " +
                String.format("%.2f", totalEnergyDelivered) + " kWh");
        System.out.println("Total Revenue: " +
                String.format("%.2f", totalRevenue) + " TL");

        if (!sessionHistory.isEmpty()) {
            double avgEnergy = totalEnergyDelivered / sessionHistory.size();
            double avgRevenue = totalRevenue / sessionHistory.size();

            System.out.println("\nAverage per Session:");
            System.out.println("- Energy: " + String.format("%.2f", avgEnergy) + " kWh");
            System.out.println("- Revenue: " + String.format("%.2f", avgRevenue) + " TL");
        }

        System.out.println("\nReservations:");
        System.out.println("Total: " + reservations.size());
        long active = reservations.stream()
                .filter(r -> r.getStatus().equals("CONFIRMED") ||
                        r.getStatus().equals("ACTIVE"))
                .count();
        System.out.println("Active: " + active);

        System.out.println("\nStations Status:");
        long operational = chargingStations.stream()
                .filter(ChargingStation::isOperational)
                .count();
        long available = chargingStations.stream()
                .filter(ChargingStation::isAvailable)
                .count();
        System.out.println("Operational: " + operational + "/" + chargingStations.size());
        System.out.println("Available: " + available + "/" + chargingStations.size());

        System.out.println("╚═════════════════════════════════════╝\n");
    }

    // Getters
    public List<ChargingStation> getChargingStations() {
        return chargingStations;
    }

    public List<ChargingSession> getSessionHistory() {
        return sessionHistory;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}