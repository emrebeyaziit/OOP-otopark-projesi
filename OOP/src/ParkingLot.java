import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Ana otopark sistemi sınıfı - YENİ ÖZELLİKLERLE GÜNCELLENDİ
public class ParkingLot {
    private String name;
    private String address;
    private int totalFloors;
    private List<Floor> floors;
    private List<Gate> gates;
    private Map<String, Ticket> activeTickets;
    private List<Transaction> transactionHistory;
    private List<Customer> customers;
    private List<Employee> employees;
    private double totalRevenue;

    // YENİ: Eklenen servisler
    private NotificationService notificationService;
    private CarWashService carWashService;
    private SecuritySystem securitySystem;
    private ChargingStationManager chargingManager;

    public ParkingLot(String name, String address, int totalFloors) {
        this.name = name;
        this.address = address;
        this.totalFloors = totalFloors;
        this.floors = new ArrayList<>();
        this.gates = new ArrayList<>();
        this.activeTickets = new HashMap<>();
        this.transactionHistory = new ArrayList<>();
        this.customers = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.totalRevenue = 0.0;

        // YENİ: Servisleri başlat
        this.notificationService = NotificationService.getInstance();
        this.carWashService = new CarWashService();
        this.securitySystem = new SecuritySystem();
        this.chargingManager = new ChargingStationManager();

        initializeFloors();
        initializeGates();
        initializeServices();
    }

    private void initializeFloors() {
        for (int i = 0; i < totalFloors; i++) {
            floors.add(new Floor(i, 20));
        }
    }

    private void initializeGates() {
        gates.add(new Gate("G1", "ENTRY"));
        gates.add(new Gate("G2", "EXIT"));
    }

    // YENİ: Servisleri başlat
    private void initializeServices() {
        // Güvenlik kameralarını kur
        securitySystem.initializeCameras(totalFloors);

        // Elektrikli araç şarj istasyonlarını bul ve kur
        List<ElectricChargingSpot> evSpots = new ArrayList<>();
        for (Floor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (spot instanceof ElectricChargingSpot) {
                    evSpots.add((ElectricChargingSpot) spot);
                }
            }
        }
        chargingManager.initializeStations(evSpots);

        // Yıkama çalışanlarını ekle
        carWashService.addWashWorker(new WashWorker(
                "W001", "Kemal Yıkama", "05551112233",
                "kemal@parking.com", "WASH001", 7000
        ));
        carWashService.addWashWorker(new WashWorker(
                "W002", "Elif Temizlik", "05552223344",
                "elif@parking.com", "WASH002", 7000
        ));
    }

    // GÜNCELLEME: Araç girişi - bildirim ve güvenlik eklendi
    public Ticket enterVehicle(Vehicle vehicle, Customer customer, String ticketType)
            throws ParkingException {

        if (!customers.contains(customer)) {
            customers.add(customer);
        }

        ParkingSpot availableSpot = findAvailableSpot(vehicle);
        if (availableSpot == null) {
            throw new SpotNotAvailableException(vehicle.getVehicleType());
        }

        String ticketId = "T" + System.currentTimeMillis();
        Ticket ticket = createTicket(ticketId, customer, vehicle, ticketType);

        Employee attendant = findAvailableAttendant();
        EntryTransaction entry = new EntryTransaction(
                "ET" + System.currentTimeMillis(),
                ticket,
                attendant,
                availableSpot
        );

        entry.execute();

        activeTickets.put(ticket.getTicketId(), ticket);
        transactionHistory.add(entry);

        // YENİ: Güvenlik kaydı
        securitySystem.logVehicleEntry(vehicle, availableSpot);

        // YENİ: Bildirim gönder
        notificationService.sendEntryNotification(customer, ticket, availableSpot);

        // YENİ: Elektrikli araç ise şarj başlat
        if (vehicle instanceof ElectricVehicle) {
            ElectricVehicle ev = (ElectricVehicle) vehicle;
            if (ev.isNeedsCharging() && availableSpot instanceof ElectricChargingSpot) {
                System.out.println("\n🔋 Electric vehicle detected. Starting charging...");
                chargingManager.startCharging(ev, 100); // %100'e kadar şarj
            }
        }

        return ticket;
    }

    // GÜNCELLEME: Araç çıkışı - bildirim ve güvenlik eklendi
    public void exitVehicle(String ticketId, Payment payment) throws ParkingException {
        Ticket ticket = activeTickets.get(ticketId);

        if (ticket == null) {
            throw new ParkingException("Ticket not found: " + ticketId);
        }

        Employee attendant = findAvailableAttendant();
        ExitTransaction exit = new ExitTransaction(
                "XT" + System.currentTimeMillis(),
                ticket,
                attendant
        );

        if (exit.getTotalFee() > 0 && payment != null) {
            exit.processPayment(payment);
            totalRevenue += exit.getTotalFee();
        }

        exit.execute();

        activeTickets.remove(ticketId);
        transactionHistory.add(exit);

        // YENİ: Güvenlik kaydı
        securitySystem.logVehicleExit(ticket.getVehicle(), ticket.getAssignedSpot());

        // YENİ: Çıkış bildirimi gönder
        notificationService.sendExitNotification(
                ticket.getCustomer(),
                ticket,
                exit.getTotalFee()
        );
    }

    // YENİ: Araç yıkama servisi siparişi
    public WashOrder orderCarWash(String ticketId, int packageIndex) {
        Ticket ticket = activeTickets.get(ticketId);

        if (ticket == null) {
            System.out.println("❌ Ticket not found!");
            return null;
        }

        WashOrder order = carWashService.createWashOrder(ticket, packageIndex);

        // Bildirimi gönder
        if (order != null) {
            Customer customer = ticket.getCustomer();
            SMSNotification sms = new SMSNotification(
                    "SMS" + System.currentTimeMillis(),
                    customer.getPhone(),
                    "Araç yıkama siparişiniz alındı. Sipariş No: " + order.getOrderId() +
                            ". Tahmini süre: " + order.getWashPackage().getEstimatedDuration() + " dk."
            );
            sms.send();
        }

        return order;
    }

    // YENİ: Olay raporu oluştur
    public IncidentReport reportIncident(String incidentType, String description,
                                         String location, int floor) {
        SecurityGuard guard = findSecurityGuard();
        if (guard == null) {
            System.out.println("❌ No security guard available!");
            return null;
        }

        return securitySystem.createIncidentReport(
                incidentType, description, LocalDateTime.now(),
                location, floor, guard
        );
    }

    // YENİ: Şarj rezervasyonu oluştur
    public ChargingReservation reserveCharging(Customer customer, ElectricVehicle vehicle,
                                               LocalDateTime scheduledTime,
                                               int durationMinutes) {
        return chargingManager.createReservation(
                customer, vehicle, scheduledTime, durationMinutes, "Type2"
        );
    }

    private ParkingSpot findAvailableSpot(Vehicle vehicle) {
        for (Floor floor : floors) {
            ParkingSpot spot = floor.findAvailableSpot(vehicle);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    private Ticket createTicket(String ticketId, Customer customer,
                                Vehicle vehicle, String type) {
        switch (type.toUpperCase()) {
            case "HOURLY":
                return new HourlyTicket(ticketId, customer, vehicle);
            case "DAILY":
                return new DailyTicket(ticketId, customer, vehicle);
            case "MONTHLY":
                return new MonthlySubscription(ticketId, customer, vehicle);
            case "YEARLY":
                return new YearlySubscription(ticketId, customer, vehicle, false);
            case "VIP":
                return new VIPMembership(ticketId, customer, vehicle);
            default:
                return new HourlyTicket(ticketId, customer, vehicle);
        }
    }

    private Employee findAvailableAttendant() {
        for (Employee emp : employees) {
            if (emp instanceof ParkingAttendant) {
                return emp;
            }
        }
        return employees.isEmpty() ?
                new ParkingAttendant("E001", "Default Attendant",
                        "0000000000", "default@parking.com",
                        "ATT001", 5000, 0) :
                employees.get(0);
    }

    private SecurityGuard findSecurityGuard() {
        for (Employee emp : employees) {
            if (emp instanceof SecurityGuard) {
                return (SecurityGuard) emp;
            }
        }
        return null;
    }

    // YENİ: Kapsamlı raporlama
    public void generateComprehensiveReport() {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     COMPREHENSIVE PARKING LOT REPORT          ║");
        System.out.println("╚════════════════════════════════════════════════╝");

        generateDailyReport();
        generateVehicleTypeReport();

        // Servis raporları
        carWashService.generateReport();
        securitySystem.generateSecurityReport();
        chargingManager.generateChargingReport();
        notificationService.printStatistics();
    }

    public void generateDailyReport() {
        System.out.println("\n========== DAILY REPORT ==========");
        System.out.println("Parking Lot: " + name);
        System.out.println("Date: " + LocalDateTime.now().toLocalDate());

        System.out.println("\nTotal Revenue: " + totalRevenue + " TL");

        int totalSpots = totalFloors * 20;
        int occupiedSpots = activeTickets.size();
        double occupancyRate = (occupiedSpots * 100.0) / totalSpots;
        System.out.println("Occupancy Rate: " + String.format("%.2f", occupancyRate) + "%");
        System.out.println("Available Spots: " + (totalSpots - occupiedSpots));

        long todayEntries = transactionHistory.stream()
                .filter(t -> t instanceof EntryTransaction)
                .count();
        long todayExits = transactionHistory.stream()
                .filter(t -> t instanceof ExitTransaction)
                .count();

        System.out.println("\nTransactions:");
        System.out.println("- Entries: " + todayEntries);
        System.out.println("- Exits: " + todayExits);

        System.out.println("==================================\n");
    }

    public void generateVehicleTypeReport() {
        System.out.println("\n===== VEHICLE TYPE DISTRIBUTION =====");

        Map<String, Integer> vehicleCount = new HashMap<>();
        for (Ticket ticket : activeTickets.values()) {
            String type = ticket.getVehicle().getVehicleType();
            vehicleCount.put(type, vehicleCount.getOrDefault(type, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : vehicleCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("=====================================\n");
    }

    // YENİ: Servis getter'ları
    public CarWashService getCarWashService() {
        return carWashService;
    }

    public SecuritySystem getSecuritySystem() {
        return securitySystem;
    }

    public ChargingStationManager getChargingManager() {
        return chargingManager;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    // Mevcut getter'lar
    public void addEmployee(Employee employee) {
        employees.add(employee);

        // Güvenlik görevlisi ise security system'e ekle
        if (employee instanceof SecurityGuard) {
            securitySystem.addSecurityGuard((SecurityGuard) employee);
        }
    }

    public String getName() {
        return name;
    }

    public int getTotalCapacity() {
        return totalFloors * 20;
    }

    public int getOccupiedSpots() {
        return activeTickets.size();
    }

    public double getTotalRevenue() {
        // Tüm servislerin gelirini topla
        double washRevenue = carWashService.getTotalRevenue();
        double chargingRevenue = chargingManager.getTotalRevenue();
        return totalRevenue + washRevenue + chargingRevenue;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }
}

// Floor sınıfı (değişiklik yok)
class Floor {
    private int floorNumber;
    private List<ParkingSpot> spots;

    public Floor(int floorNumber, int spotsPerFloor) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
        initializeSpots(spotsPerFloor);
    }

    private void initializeSpots(int count) {
        for (int i = 0; i < count; i++) {
            String spotId = "F" + floorNumber + "-S" + i;

            if (i < 2) {
                spots.add(new DisabledSpot(spotId, floorNumber));
            } else if (i < 5) {
                spots.add(new CompactSpot(spotId, floorNumber));
            } else if (i < 8) {
                spots.add(new VIPSpot(spotId, floorNumber, true));
            } else if (i < 10) {
                spots.add(new ElectricChargingSpot(spotId, floorNumber, 50));
            } else {
                spots.add(new RegularSpot(spotId, floorNumber));
            }
        }
    }

    public ParkingSpot findAvailableSpot(Vehicle vehicle) {
        for (ParkingSpot spot : spots) {
            if (!spot.isOccupied() && spot.canFitVehicle(vehicle)) {
                return spot;
            }
        }
        return null;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public List<ParkingSpot> getSpots() {
        return spots;
    }
}

// Gate sınıfı (değişiklik yok)
class Gate {
    private String gateId;
    private String type;
    private boolean isOpen;

    public Gate(String gateId, String type) {
        this.gateId = gateId;
        this.type = type;
        this.isOpen = true;
    }

    public void open() {
        this.isOpen = true;
        System.out.println("Gate " + gateId + " opened.");
    }

    public void close() {
        this.isOpen = false;
        System.out.println("Gate " + gateId + " closed.");
    }

    public String getGateId() {
        return gateId;
    }

    public String getType() {
        return type;
    }

    public boolean isOpen() {
        return isOpen;
    }
}