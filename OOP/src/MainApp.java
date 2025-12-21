import java.util.*;
import java.time.LocalDateTime;

public class MainApp {
    private static Scanner scanner = new Scanner(System.in);
    private static ParkingLot parkingLot;
    private static Map<String, Ticket> activeTickets = new HashMap<>();

    public static void main(String[] args) {
        initializeSystem();
        showWelcomeScreen();

        while (true) {
            try {
                showMainMenu();
                int choice = getIntInput("Seçiminiz: ");

                if (choice == 0) {
                    exitSystem();
                    break;
                }

                handleMainMenuChoice(choice);

            } catch (Exception e) {
                System.out.println("\n❌ Hata: " + e.getMessage());
                pressEnterToContinue();
            }
        }
    }

    private static void initializeSystem() {
        System.out.println("Sistem başlatılıyor...");
        parkingLot = new ParkingLot("City Center Premium Parking",
                "İstanbul, Turkey", 3);

        // Çalışanları ekle
        parkingLot.addEmployee(new ParkingAttendant(
                "E001", "Ahmet Yılmaz", "05551234567",
                "ahmet@parking.com", "ATT001", 8000, 0
        ));
        parkingLot.addEmployee(new Manager(
                "E002", "Mehmet Kaya", "05556661122",
                "mehmet@parking.com", "MGR001", 15000, "Operations"
        ));
        parkingLot.addEmployee(new SecurityGuard(
                "E003", "Fatma Şahin", "05553334455",
                "fatma@parking.com", "SEC001", 7000, "Morning"
        ));

        System.out.println("✓ Sistem hazır!\n");
    }

    private static void showWelcomeScreen() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║                                                    ║");
        System.out.println("║        OTOPARK YÖNETİM SİSTEMİ v2.0               ║");
        System.out.println("║                                                    ║");
        System.out.println("║    🚗 Park Sistemi                                ║");
        System.out.println("║    🧼 Araç Yıkama Servisi                        ║");
        System.out.println("║    🔒 Güvenlik Sistemi                           ║");
        System.out.println("║    ⚡ EV Şarj İstasyonları                       ║");
        System.out.println("║                                                    ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        // Başlangıç kapasitesi
        System.out.println("\n  🎯 SİSTEM HAZIR!");
        System.out.println("  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  📊 Toplam Kapasite: " + parkingLot.getTotalCapacity() + " araç");
        System.out.println("  🟢 Tüm park yerleri müsait");
        System.out.println("  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        System.out.println("\n  Hoş Geldiniz! Devam etmek için ENTER'a basın...");
        scanner.nextLine();
    }

    private static void showMainMenu() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║               ANA MENÜ                             ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println();

        // CANLI KAPASİTE GÖSTERGESİ
        showLiveCapacity();

        System.out.println("  ═══════════════════════════════════════════════════");
        System.out.println();
        System.out.println("  1️⃣  Araç Giriş İşlemi");
        System.out.println("  2️⃣  Araç Çıkış İşlemi");
        System.out.println("  3️⃣  Aktif Biletleri Görüntüle");
        System.out.println();
        System.out.println("  4️⃣  Araç Yıkama Servisi");
        System.out.println("  5️⃣  Elektrikli Araç Şarj");
        System.out.println("  6️⃣  Güvenlik İşlemleri");
        System.out.println();
        System.out.println("  7️⃣  Raporlar ve İstatistikler");
        System.out.println("  8️⃣  Müşteri İşlemleri");
        System.out.println();
        System.out.println("  0️⃣  Çıkış");
        System.out.println();
        System.out.println("  ═══════════════════════════════════════════════════");
        System.out.println();
    }

    private static void handleMainMenuChoice(int choice) throws ParkingException {
        switch (choice) {
            case 1:
                vehicleEntryProcess();
                break;
            case 2:
                vehicleExitProcess();
                break;
            case 3:
                viewActiveTickets();
                break;
            case 4:
                carWashMenu();
                break;
            case 5:
                evChargingMenu();
                break;
            case 6:
                securityMenu();
                break;
            case 7:
                reportsMenu();
                break;
            case 8:
                customerMenu();
                break;
            default:
                System.out.println("\n❌ Geçersiz seçim!");
                pressEnterToContinue();
        }
    }

    private static void vehicleEntryProcess() throws ParkingException {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║          ARAÇ GİRİŞ İŞLEMİ                        ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");

        // Müşteri bilgileri
        System.out.println("📝 MÜŞTERİ BİLGİLERİ");
        String customerId = "C" + System.currentTimeMillis();
        String name = getStringInput("Ad Soyad: ");
        String phone = getStringInput("Telefon (05XXXXXXXXX): ");
        String email = getStringInput("Email: ");
        String licensePlate = getStringInput("Araç Plakası (34ABC123): ").toUpperCase();

        Customer customer = new Customer(customerId, name, phone, email, licensePlate);

        // Araç tipi seçimi
        System.out.println("\n🚗 ARAÇ TİPİ SEÇİMİ");
        System.out.println("1. Otomobil");
        System.out.println("2. Motosiklet");
        System.out.println("3. Kamyonet");
        System.out.println("4. Elektrikli Araç");

        int vehicleChoice = getIntInput("Seçim: ");
        Vehicle vehicle = createVehicle(vehicleChoice, licensePlate);

        if (vehicle == null) {
            System.out.println("\n❌ Geçersiz araç tipi!");
            pressEnterToContinue();
            return;
        }

        // Bilet tipi seçimi
        System.out.println("\n🎫 BİLET TİPİ SEÇİMİ");
        System.out.println("1. Saatlik (10 TL/saat)");
        System.out.println("2. Günlük (80 TL/gün)");
        System.out.println("3. Aylık Abonelik (2000 TL/ay)");
        System.out.println("4. Yıllık Abonelik (20000 TL/yıl)");
        System.out.println("5. VIP Üyelik (5000 TL/ay)");

        int ticketChoice = getIntInput("Seçim: ");
        String ticketType = getTicketType(ticketChoice);

        // İşlemi gerçekleştir
        System.out.println("\n⏳ İşlem yapılıyor...");
        Ticket ticket = parkingLot.enterVehicle(vehicle, customer, ticketType);

        activeTickets.put(ticket.getTicketId(), ticket);

        System.out.println("\n✅ ARAÇ BAŞARIYLA PARK EDİLDİ!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("Bilet No: " + ticket.getTicketId());
        System.out.println("Müşteri: " + customer.getName());
        System.out.println("Araç: " + vehicle.getLicensePlate());
        System.out.println("Park Yeri: " + ticket.getAssignedSpot().getSpotId());
        System.out.println("Kat: " + ticket.getAssignedSpot().getFloor());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // Güncel kapasite durumu
        System.out.println("\n📊 GÜNCEL KAPASİTE:");
        int newOccupied = parkingLot.getOccupiedSpots();
        int newAvailable = parkingLot.getTotalCapacity() - newOccupied;
        System.out.println("Dolu: " + newOccupied + " | Boş: " + newAvailable);

        pressEnterToContinue();
    }

    private static void vehicleExitProcess() throws ParkingException {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║          ARAÇ ÇIKIŞ İŞLEMİ                        ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");

        if (activeTickets.isEmpty()) {
            System.out.println("⚠️  Aktif bilet bulunamadı!");
            pressEnterToContinue();
            return;
        }

        // Aktif biletleri göster
        System.out.println("📋 AKTİF BİLETLER:\n");
        int index = 1;
        List<String> ticketIds = new ArrayList<>(activeTickets.keySet());

        for (String ticketId : ticketIds) {
            Ticket ticket = activeTickets.get(ticketId);
            System.out.println(index + ". Bilet: " + ticketId);
            System.out.println("   Araç: " + ticket.getVehicle().getLicensePlate());
            System.out.println("   Müşteri: " + ticket.getCustomer().getName());
            System.out.println();
            index++;
        }

        int choice = getIntInput("Çıkış yapacak bileti seçin (1-" + ticketIds.size() + "): ");

        if (choice < 1 || choice > ticketIds.size()) {
            System.out.println("\n❌ Geçersiz seçim!");
            pressEnterToContinue();
            return;
        }

        String selectedTicketId = ticketIds.get(choice - 1);
        Ticket ticket = activeTickets.get(selectedTicketId);

        // Ücret hesapla (simülasyon için sabit değer)
        double fee = 50.0; // Gerçekte calculateFee ile hesaplanacak

        System.out.println("\n💰 ÖDEME BİLGİLERİ");
        System.out.println("Toplam Ücret: " + fee + " TL");
        System.out.println("\nÖdeme Yöntemi Seçin:");
        System.out.println("1. Nakit");
        System.out.println("2. Kredi Kartı");
        System.out.println("3. Mobil Ödeme");

        int paymentChoice = getIntInput("Seçim: ");
        Payment payment = createPayment(paymentChoice, fee);

        if (payment == null) {
            System.out.println("\n❌ Geçersiz ödeme yöntemi!");
            pressEnterToContinue();
            return;
        }

        // Çıkış işlemi
        System.out.println("\n⏳ İşlem yapılıyor...");
        parkingLot.exitVehicle(selectedTicketId, payment);
        activeTickets.remove(selectedTicketId);

        System.out.println("\n✅ ÇIKIŞ İŞLEMİ TAMAMLANDI!");
        System.out.println("İyi yolculuklar dileriz!");

        // Güncel kapasite durumu
        System.out.println("\n📊 GÜNCEL KAPASİTE:");
        int newOccupied = parkingLot.getOccupiedSpots();
        int newAvailable = parkingLot.getTotalCapacity() - newOccupied;
        System.out.println("Dolu: " + newOccupied + " | Boş: " + newAvailable);

        pressEnterToContinue();
    }

    private static void viewActiveTickets() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║          AKTİF BİLETLER                           ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");

        if (activeTickets.isEmpty()) {
            System.out.println("⚠️  Aktif bilet bulunamadı!");
        } else {
            for (Map.Entry<String, Ticket> entry : activeTickets.entrySet()) {
                Ticket ticket = entry.getValue();
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("Bilet No: " + ticket.getTicketId());
                System.out.println("Müşteri: " + ticket.getCustomer().getName());
                System.out.println("Araç: " + ticket.getVehicle().getLicensePlate());
                System.out.println("Park Yeri: " + ticket.getAssignedSpot().getSpotId());
                System.out.println("Tip: " + ticket.getTicketType());
                System.out.println();
            }
        }

        pressEnterToContinue();
    }

    private static void carWashMenu() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║          ARAÇ YIKAMA SERVİSİ                      ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");

        if (activeTickets.isEmpty()) {
            System.out.println("⚠️  Park edilmiş araç bulunamadı!");
            pressEnterToContinue();
            return;
        }

        // Paketleri göster
        parkingLot.getCarWashService().displayPackages();

        // Bilet seç
        System.out.println("📋 PARK EDİLMİŞ ARAÇLAR:\n");
        int index = 1;
        List<String> ticketIds = new ArrayList<>(activeTickets.keySet());

        for (String ticketId : ticketIds) {
            Ticket ticket = activeTickets.get(ticketId);
            System.out.println(index + ". Araç: " + ticket.getVehicle().getLicensePlate() +
                    " (Bilet: " + ticketId + ")");
            index++;
        }

        int ticketChoice = getIntInput("\nYıkama yapılacak aracı seçin: ");
        if (ticketChoice < 1 || ticketChoice > ticketIds.size()) {
            System.out.println("\n❌ Geçersiz seçim!");
            pressEnterToContinue();
            return;
        }

        int packageChoice = getIntInput("Paket seçin (1-4): ");

        String selectedTicketId = ticketIds.get(ticketChoice - 1);
        WashOrder order = parkingLot.orderCarWash(selectedTicketId, packageChoice - 1);

        if (order != null) {
            System.out.println("\n✅ Yıkama siparişi alındı!");
            System.out.println("Sipariş işleniyor...");
            parkingLot.getCarWashService().processAllOrders();
        }

        pressEnterToContinue();
    }

    private static void evChargingMenu() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║          ELEKTRİKLİ ARAÇ ŞARJ SİSTEMİ            ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");

        System.out.println("1. Şarj İstasyonlarını Görüntüle");
        System.out.println("2. Şarj Rezervasyonu Yap");
        System.out.println("3. Geri Dön");

        int choice = getIntInput("\nSeçim: ");

        switch (choice) {
            case 1:
                parkingLot.getChargingManager().listAllStations();
                break;
            case 2:
                System.out.println("\nŞarj rezervasyonu özelliği geliştirme aşamasında...");
                break;
            case 3:
                return;
        }

        pressEnterToContinue();
    }

    private static void securityMenu() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║          GÜVENLİK SİSTEMİ                         ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");

        System.out.println("1. Olay Raporu Oluştur");
        System.out.println("2. Açık Olayları Görüntüle");
        System.out.println("3. Araç Erişim Geçmişi");
        System.out.println("4. Geri Dön");

        int choice = getIntInput("\nSeçim: ");

        switch (choice) {
            case 1:
                createIncidentReport();
                break;
            case 2:
                viewOpenIncidents();
                break;
            case 3:
                viewVehicleHistory();
                break;
            case 4:
                return;
        }

        pressEnterToContinue();
    }

    private static void createIncidentReport() {
        System.out.println("\n📝 OLAY RAPORU OLUŞTUR\n");

        System.out.println("Olay Tipi:");
        System.out.println("1. Kaza (ACCIDENT)");
        System.out.println("2. Hırsızlık (THEFT)");
        System.out.println("3. Vandalizm (VANDALISM)");
        System.out.println("4. Şüpheli Aktivite (SUSPICIOUS)");

        int typeChoice = getIntInput("Seçim: ");
        String[] types = {"ACCIDENT", "THEFT", "VANDALISM", "SUSPICIOUS"};
        String incidentType = (typeChoice >= 1 && typeChoice <= 4) ?
                types[typeChoice - 1] : "SUSPICIOUS";

        String description = getStringInput("Açıklama: ");
        int floor = getIntInput("Kat: ");
        String location = getStringInput("Konum: ");

        parkingLot.reportIncident(incidentType, description, location, floor);

        System.out.println("\n✅ Olay raporu oluşturuldu!");
    }

    private static void viewOpenIncidents() {
        System.out.println("\n📋 AÇIK OLAYLAR\n");

        List<IncidentReport> openIncidents = parkingLot.getSecuritySystem().getOpenIncidents();

        if (openIncidents.isEmpty()) {
            System.out.println("✓ Açık olay bulunamadı.");
        } else {
            for (IncidentReport report : openIncidents) {
                report.printReport();
            }
        }
    }

    private static void viewVehicleHistory() {
        String licensePlate = getStringInput("\nAraç Plakası: ").toUpperCase();
        parkingLot.getSecuritySystem().showVehicleAccessHistory(licensePlate);
    }

    private static void reportsMenu() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║          RAPORLAR VE İSTATİSTİKLER               ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");

        System.out.println("1. Günlük Rapor");
        System.out.println("2. Araç Tipi Dağılımı");
        System.out.println("3. Kapsamlı Rapor (Tüm Servisler)");
        System.out.println("4. Yıkama Servisi Raporu");
        System.out.println("5. Güvenlik Raporu");
        System.out.println("6. Şarj Servisi Raporu");
        System.out.println("7. Geri Dön");

        int choice = getIntInput("\nSeçim: ");

        System.out.println();

        switch (choice) {
            case 1:
                parkingLot.generateDailyReport();
                break;
            case 2:
                parkingLot.generateVehicleTypeReport();
                break;
            case 3:
                parkingLot.generateComprehensiveReport();
                break;
            case 4:
                parkingLot.getCarWashService().generateReport();
                break;
            case 5:
                parkingLot.getSecuritySystem().generateSecurityReport();
                break;
            case 6:
                parkingLot.getChargingManager().generateChargingReport();
                break;
            case 7:
                return;
        }

        pressEnterToContinue();
    }

    private static void customerMenu() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║          MÜŞTERİ İŞLEMLERİ                        ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");

        System.out.println("1. Aktif Müşterileri Görüntüle");
        System.out.println("2. Bildirim Geçmişi");
        System.out.println("3. Geri Dön");

        int choice = getIntInput("\nSeçim: ");

        switch (choice) {
            case 1:
                System.out.println("\nAktif müşteri sayısı: " + activeTickets.size());
                break;
            case 2:
                parkingLot.getNotificationService().printStatistics();
                break;
            case 3:
                return;
        }

        pressEnterToContinue();
    }

    // Yardımcı metodlar
    private static Vehicle createVehicle(int choice, String licensePlate) {
        String color = getStringInput("Renk: ");
        String brand = getStringInput("Marka: ");
        String model = getStringInput("Model: ");

        switch (choice) {
            case 1:
                int doors = getIntInput("Kapı sayısı: ");
                return new Car(licensePlate, color, brand, model, doors);
            case 2:
                int engineCapacity = getIntInput("Motor hacmi (cc): ");
                return new Motorcycle(licensePlate, color, brand, model, engineCapacity);
            case 3:
                int capacity = getIntInput("Yük kapasitesi: ");
                return new Van(licensePlate, color, brand, model, capacity);
            case 4:
                int doors2 = getIntInput("Kapı sayısı: ");
                int battery = getIntInput("Batarya kapasitesi (kWh): ");
                ElectricVehicle ev = new ElectricVehicle(licensePlate, color, brand, model, doors2, battery);
                String needsCharge = getStringInput("Şarj gerekiyor mu? (E/H): ");
                ev.setNeedsCharging(needsCharge.equalsIgnoreCase("E"));
                return ev;
            default:
                return null;
        }
    }

    private static String getTicketType(int choice) {
        switch (choice) {
            case 1:
                return "HOURLY";
            case 2:
                return "DAILY";
            case 3:
                return "MONTHLY";
            case 4:
                return "YEARLY";
            case 5:
                return "VIP";
            default:
                return "HOURLY";
        }
    }

    private static Payment createPayment(int choice, double amount) {
        String paymentId = "PAY" + System.currentTimeMillis();

        switch (choice) {
            case 1:
                double cash = getDoubleInput("Verilen nakit: ");
                return new CashPayment(paymentId, amount, cash);
            case 2:
                String cardNumber = getStringInput("Kart numarası: ");
                String cardHolder = getStringInput("Kart sahibi: ");
                String expiry = getStringInput("Son kullanma (MM/YY): ");
                String cvv = getStringInput("CVV: ");
                return new CreditCardPayment(paymentId, amount, cardNumber, cardHolder, expiry, cvv);
            case 3:
                String phone = getStringInput("Telefon numarası: ");
                String provider = getStringInput("Sağlayıcı (ApplePay/GooglePay/SamsungPay): ");
                return new MobilePayment(paymentId, amount, phone, provider);
            default:
                return null;
        }
    }

    private static void exitSystem() {
        clearScreen();
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║                                                    ║");
        System.out.println("║     Otopark Yönetim Sisteminden Çıkılıyor...     ║");
        System.out.println("║                                                    ║");
        System.out.println("║            Güle güle! 👋                          ║");
        System.out.println("║                                                    ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");

        // Son rapor
        System.out.println("📊 GÜNLÜK ÖZET:");
        System.out.println("Toplam Gelir: " + parkingLot.getTotalRevenue() + " TL");
        System.out.println("İşlem Sayısı: " + parkingLot.getTransactionHistory().size());
        System.out.println();
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        // Windows için alternatif:
        for (int i = 0; i < 50; i++) System.out.println();
    }

    private static void pressEnterToContinue() {
        System.out.println("\nDevam etmek için ENTER'a basın...");
        scanner.nextLine();
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Geçerli bir sayı girin: ");
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // Buffer temizle
        return value;
    }

    private static double getDoubleInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            scanner.next();
            System.out.print("Geçerli bir sayı girin: ");
        }
        double value = scanner.nextDouble();
        scanner.nextLine(); // Buffer temizle
        return value;
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    // CANLI KAPASİTE GÖSTERGESİ
    private static void showLiveCapacity() {
        int totalCapacity = parkingLot.getTotalCapacity();
        int occupied = parkingLot.getOccupiedSpots();
        int available = totalCapacity - occupied;
        double occupancyRate = (occupied * 100.0) / totalCapacity;

        System.out.println("  ┌──────────────────────────────────────────────────┐");
        System.out.println("  │         🚗 CANLI KAPASİTE GÖSTERGESİ            │");
        System.out.println("  ├──────────────────────────────────────────────────┤");

        // Doluluk oranı renklendirme
        String statusColor = "";
        String status = "";
        if (occupancyRate < 50) {
            statusColor = "🟢";
            status = "BOŞ";
        } else if (occupancyRate < 80) {
            statusColor = "🟡";
            status = "ORTA";
        } else if (occupancyRate < 100) {
            statusColor = "🟠";
            status = "DOLU";
        } else {
            statusColor = "🔴";
            status = "TAM DOLU";
        }

        System.out.println("  │                                                  │");
        System.out.println("  │  " + statusColor + " DURUM: " + status +
                " ".repeat(Math.max(0, 43 - status.length())) + "│");
        System.out.println("  │                                                  │");

        // Grafik çubuk gösterge
        int barLength = 40;
        int filledLength = (int) ((occupied * barLength) / totalCapacity);
        String bar = "█".repeat(filledLength) + "░".repeat(barLength - filledLength);

        System.out.println("  │  [" + bar + "] " + String.format("%.1f%%", occupancyRate) +
                " ".repeat(Math.max(0, 3 - String.format("%.1f", occupancyRate).length())) + "│");
        System.out.println("  │                                                  │");

        // Sayısal bilgiler
        System.out.println("  │  📊 Toplam Kapasite : " + totalCapacity + " araç" +
                " ".repeat(Math.max(0, 25 - String.valueOf(totalCapacity).length())) + "│");
        System.out.println("  │  🚗 Park Edilmiş    : " + occupied + " araç" +
                " ".repeat(Math.max(0, 25 - String.valueOf(occupied).length())) + "│");
        System.out.println("  │  ✅ Boş Park Yeri   : " + available + " araç" +
                " ".repeat(Math.max(0, 25 - String.valueOf(available).length())) + "│");
        System.out.println("  │                                                  │");

        // Kat bazlı durum
        System.out.println("  │  📍 KAT BAZLI DURUM:                            │");
        for (int i = 0; i < 3; i++) {
            int floorCapacity = 20;
            int floorOccupied = countOccupiedInFloor(i);
            int floorAvailable = floorCapacity - floorOccupied;
            double floorRate = (floorOccupied * 100.0) / floorCapacity;

            String floorStatus = floorRate < 50 ? "🟢" : floorRate < 80 ? "🟡" : "🔴";

            System.out.println("  │     " + floorStatus + " Kat " + i + " : " +
                    floorOccupied + "/" + floorCapacity +
                    " (Boş: " + floorAvailable + ")" +
                    " ".repeat(Math.max(0, 23 - (String.valueOf(floorOccupied).length() +
                            String.valueOf(floorCapacity).length() +
                            String.valueOf(floorAvailable).length()))) + "│");
        }

        System.out.println("  │                                                  │");

        // Gelir bilgisi
        System.out.println("  │  💰 Günlük Gelir    : " +
                String.format("%.2f", parkingLot.getTotalRevenue()) + " TL" +
                " ".repeat(Math.max(0, 23 - String.format("%.2f", parkingLot.getTotalRevenue()).length())) + "│");

        // Servis durumları
        System.out.println("  │                                                  │");
        System.out.println("  │  🔧 SERVİS DURUMLARI:                           │");
        System.out.println("  │     🧼 Yıkama Kuyruğu  : " +
                parkingLot.getCarWashService().getPendingOrderCount() + " araç" +
                " ".repeat(Math.max(0, 20 - String.valueOf(parkingLot.getCarWashService().getPendingOrderCount()).length())) + "│");
        System.out.println("  │     ⚡ Şarj İstasyonu  : " +
                getAvailableChargingStations() + " müsait" +
                " ".repeat(Math.max(0, 18 - String.valueOf(getAvailableChargingStations()).length())) + "│");
        System.out.println("  │     🔒 Açık Olaylar   : " +
                parkingLot.getSecuritySystem().getOpenIncidents().size() + " adet" +
                " ".repeat(Math.max(0, 20 - String.valueOf(parkingLot.getSecuritySystem().getOpenIncidents().size()).length())) + "│");

        System.out.println("  │                                                  │");
        System.out.println("  └──────────────────────────────────────────────────┘");
        System.out.println();
    }

    private static int countOccupiedInFloor(int floorNumber) {
        int count = 0;
        for (Ticket ticket : activeTickets.values()) {
            if (ticket.getAssignedSpot() != null &&
                    ticket.getAssignedSpot().getFloor() == floorNumber) {
                count++;
            }
        }
        return count;
    }

    private static int getAvailableChargingStations() {
        return (int) parkingLot.getChargingManager().getChargingStations().stream()
                .filter(ChargingStation::isAvailable)
                .count();
    }
}



