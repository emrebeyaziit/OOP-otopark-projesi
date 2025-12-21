import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("╔════════════════════════════════════════════════╗");
            System.out.println("║   ADVANCED PARKING LOT MANAGEMENT SYSTEM      ║");
            System.out.println("║   with Notifications, Car Wash, Security      ║");
            System.out.println("║   and EV Charging Features                     ║");
            System.out.println("╚════════════════════════════════════════════════╝\n");

            // Sistem başlatma
            ParkingLot parkingLot = new ParkingLot("City Center Premium Parking",
                    "İstanbul, Turkey", 3);

            // Çalışanları ekle
            setupEmployees(parkingLot);

            System.out.println("\n✓ System initialized successfully!");
            System.out.println("Total Capacity: " + parkingLot.getTotalCapacity() + " spots");
            System.out.println("Total Floors: 3");
            System.out.println("Security Cameras: Active");
            System.out.println("Car Wash Service: Available");
            System.out.println("EV Charging Stations: Operational\n");

            // Test senaryoları
            runBasicScenarios(parkingLot);
            runCarWashScenarios(parkingLot);
            runSecurityScenarios(parkingLot);
            runChargingScenarios(parkingLot);

            // Kapsamlı rapor
            System.out.println("\n" + "=".repeat(60));
            parkingLot.generateComprehensiveReport();

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void setupEmployees(ParkingLot parkingLot) {
        System.out.println("\n>>> Setting up employees...");

        parkingLot.addEmployee(new ParkingAttendant(
                "E001", "Ahmet Yılmaz", "05551234567",
                "ahmet@parking.com", "ATT001", 8000, 0
        ));

        parkingLot.addEmployee(new ParkingAttendant(
                "E002", "Ayşe Demir", "05559876543",
                "ayse@parking.com", "ATT002", 8000, 1
        ));

        parkingLot.addEmployee(new Manager(
                "E003", "Mehmet Kaya", "05556661122",
                "mehmet@parking.com", "MGR001", 15000, "Operations"
        ));

        parkingLot.addEmployee(new SecurityGuard(
                "E004", "Fatma Şahin", "05553334455",
                "fatma@parking.com", "SEC001", 7000, "Morning"
        ));

        parkingLot.addEmployee(new SecurityGuard(
                "E005", "Hasan Güven", "05554445566",
                "hasan@parking.com", "SEC002", 7000, "Evening"
        ));

        System.out.println("✓ Employees added: 5 staff members");
    }

    private static void runBasicScenarios(ParkingLot parkingLot) throws ParkingException {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       BASIC PARKING SCENARIOS         ║");
        System.out.println("╚════════════════════════════════════════╝");

        // SENARYO 1: Normal araba girişi (Bildirimle)
        System.out.println("\n>>> SCENARIO 1: Regular Car Entry with Notifications");
        System.out.println("-".repeat(50));

        Customer customer1 = new Customer(
                "C001", "Ali Veli", "05551111111",
                "ali@email.com", "34ABC123"
        );

        Car car1 = new Car("34ABC123", "Black", "Toyota", "Corolla", 4);

        Ticket ticket1 = parkingLot.enterVehicle(car1, customer1, "HOURLY");
        System.out.println("✓ Ticket issued: " + ticket1.getTicketId());

        simulateWait(2);

        System.out.println("\n>>> Exiting vehicle...");
        CashPayment payment1 = new CashPayment("P001", 20, 50);
        parkingLot.exitVehicle(ticket1.getTicketId(), payment1);

        // SENARYO 2: VIP Müşteri
        System.out.println("\n\n>>> SCENARIO 2: VIP Customer Entry");
        System.out.println("-".repeat(50));

        Customer customer2 = new Customer(
                "C002", "Zeynep Yıldız", "05552222222",
                "zeynep@email.com", "34XYZ789"
        );

        Car car2 = new Car("34XYZ789", "White", "BMW", "5 Series", 4);

        Ticket ticket2 = parkingLot.enterVehicle(car2, customer2, "VIP");
        System.out.println("✓ VIP Membership activated: " + ticket2.getTicketId());
    }

    private static void runCarWashScenarios(ParkingLot parkingLot) throws ParkingException {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       CAR WASH SERVICE SCENARIOS      ║");
        System.out.println("╚════════════════════════════════════════╝");

        // SENARYO 3: Araç yıkama servisi
        System.out.println("\n>>> SCENARIO 3: Car Wash Service Order");
        System.out.println("-".repeat(50));

        Customer customer3 = new Customer(
                "C003", "Can Öztürk", "05553333333",
                "can@email.com", "34CAN456"
        );

        Van van = new Van("34CAN456", "Blue", "Mercedes", "Sprinter", 15);

        Ticket ticket3 = parkingLot.enterVehicle(van, customer3, "DAILY");

        // Paketleri göster
        parkingLot.getCarWashService().displayPackages();

        // Full paket sipariş et (index 2)
        WashOrder washOrder = parkingLot.orderCarWash(ticket3.getTicketId(), 2);

        // Tüm yıkama siparişlerini işle
        parkingLot.getCarWashService().processAllOrders();
    }

    private static void runSecurityScenarios(ParkingLot parkingLot) throws ParkingException {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       SECURITY SYSTEM SCENARIOS       ║");
        System.out.println("╚════════════════════════════════════════╝");

        // SENARYO 4: Güvenlik olayı raporu
        System.out.println("\n>>> SCENARIO 4: Security Incident Report");
        System.out.println("-".repeat(50));

        IncidentReport incident1 = parkingLot.reportIncident(
                "ACCIDENT",
                "Küçük çarpma olayı. Araçlardan biri park yerinden çıkarken " +
                        "yan taraftaki araca hafif çarptı. Hasar minimal.",
                "Parking Area Center",
                1
        );

        if (incident1 != null) {
            incident1.addInvolvedVehicle("34ABC123");
            incident1.addInvolvedVehicle("34XYZ789");
            incident1.printReport();
        }

        // SENARYO 5: Şüpheli aktivite
        System.out.println("\n>>> SCENARIO 5: Suspicious Activity Report");
        System.out.println("-".repeat(50));

        IncidentReport incident2 = parkingLot.reportIncident(
                "SUSPICIOUS",
                "Kamera görüntülerinde bir kişinin araçların etrafında " +
                        "şüpheli şekilde dolaştığı tespit edildi.",
                "Floor 2 Corner",
                2
        );

        if (incident2 != null) {
            incident2.printReport();
            incident2.updateStatus("INVESTIGATING");
        }

        // Araç erişim geçmişini göster
        parkingLot.getSecuritySystem().showVehicleAccessHistory("34ABC123");
    }

    private static void runChargingScenarios(ParkingLot parkingLot) throws ParkingException {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    EV CHARGING SERVICE SCENARIOS      ║");
        System.out.println("╚════════════════════════════════════════╝");

        // SENARYO 6: Elektrikli araç şarjı
        System.out.println("\n>>> SCENARIO 6: Electric Vehicle Charging");
        System.out.println("-".repeat(50));

        Customer customer4 = new Customer(
                "C004", "Deniz Arslan", "05554444444",
                "deniz@email.com", "34EV111"
        );

        ElectricVehicle ev1 = new ElectricVehicle(
                "34EV111", "White", "Tesla", "Model 3", 4, 75
        );
        ev1.setNeedsCharging(true);

        Ticket ticket4 = parkingLot.enterVehicle(ev1, customer4, "MONTHLY");
        System.out.println("✓ Electric vehicle parked and charging started");

        // SENARYO 7: Şarj rezervasyonu
        System.out.println("\n\n>>> SCENARIO 7: Charging Reservation");
        System.out.println("-".repeat(50));

        Customer customer5 = new Customer(
                "C005", "Elif Kaya", "05555555555",
                "elif@email.com", "34EV222"
        );

        ElectricVehicle ev2 = new ElectricVehicle(
                "34EV222", "Black", "BMW", "iX3", 4, 80
        );

        LocalDateTime scheduledTime = LocalDateTime.now().plusHours(2);
        ChargingReservation reservation = parkingLot.reserveCharging(
                customer5, ev2, scheduledTime, 60
        );

        // Şarj istasyonlarını listele
        parkingLot.getChargingManager().listAllStations();

        // SENARYO 8: Başka bir elektrikli araç
        System.out.println("\n>>> SCENARIO 8: Another EV Charging");
        System.out.println("-".repeat(50));

        Customer customer6 = new Customer(
                "C006", "Murat Elektrik", "05556666666",
                "murat@email.com", "34EV333"
        );

        ElectricVehicle ev3 = new ElectricVehicle(
                "34EV333", "Red", "Porsche", "Taycan", 4, 93
        );
        ev3.setNeedsCharging(true);

        Ticket ticket6 = parkingLot.enterVehicle(ev3, customer6, "HOURLY");
    }

    private static void simulateWait(int hours) {
        System.out.println("... simulating " + hours + " hour(s) parking time ...");
    }
}