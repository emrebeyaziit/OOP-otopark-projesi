import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;

// WashPackage abstract sınıfı
abstract class WashPackage {
    private String packageId;
    private String packageName;
    private double basePrice;
    private int estimatedDuration; // dakika cinsinden

    public WashPackage(String packageId, String packageName,
                       double basePrice, int estimatedDuration) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.basePrice = basePrice;
        this.estimatedDuration = estimatedDuration;
    }

    // Abstract methods
    public abstract List<String> getServices();
    public abstract double calculatePrice(Vehicle vehicle);

    // Getters
    public String getPackageId() {
        return packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public int getEstimatedDuration() {
        return estimatedDuration;
    }

    @Override
    public String toString() {
        return packageName + " - " + basePrice + " TL (" +
                estimatedDuration + " min)";
    }
}

// BasicWashPackage - Dış yıkama
class BasicWashPackage extends WashPackage {

    public BasicWashPackage() {
        super("PKG_BASIC", "Dış Yıkama", 50.0, 15);
    }

    @Override
    public List<String> getServices() {
        return Arrays.asList(
                "Dış kaporta yıkama",
                "Jant temizliği",
                "Cam silme"
        );
    }

    @Override
    public double calculatePrice(Vehicle vehicle) {
        double price = getBasePrice();
        // Araç tipine göre fiyat artışı
        if (vehicle instanceof Truck) {
            price *= 2.0;
        } else if (vehicle instanceof Van) {
            price *= 1.5;
        } else if (vehicle instanceof Motorcycle) {
            price *= 0.5;
        }
        return price;
    }
}

// InteriorWashPackage - İç temizlik
class InteriorWashPackage extends WashPackage {

    public InteriorWashPackage() {
        super("PKG_INTERIOR", "İç Temizlik", 80.0, 30);
    }

    @Override
    public List<String> getServices() {
        return Arrays.asList(
                "Koltuk temizliği",
                "Döşeme temizliği",
                "Torpido temizliği",
                "Cam içi silme",
                "Havalandırma temizliği"
        );
    }

    @Override
    public double calculatePrice(Vehicle vehicle) {
        double price = getBasePrice();
        if (vehicle instanceof Truck || vehicle instanceof Van) {
            price *= 1.8;
        } else if (vehicle instanceof Motorcycle) {
            price *= 0.6;
        }
        return price;
    }
}

// FullWashPackage - Full paket
class FullWashPackage extends WashPackage {

    public FullWashPackage() {
        super("PKG_FULL", "Full Paket", 150.0, 60);
    }

    @Override
    public List<String> getServices() {
        List<String> services = new ArrayList<>();
        services.addAll(new BasicWashPackage().getServices());
        services.addAll(new InteriorWashPackage().getServices());
        services.add("Motor temizliği");
        services.add("Cila");
        services.add("Hava spreyi");
        return services;
    }

    @Override
    public double calculatePrice(Vehicle vehicle) {
        double price = getBasePrice();
        if (vehicle instanceof Truck) {
            price *= 2.5;
        } else if (vehicle instanceof Van) {
            price *= 1.8;
        } else if (vehicle instanceof Motorcycle) {
            price *= 0.7;
        }
        return price;
    }
}

// PremiumWashPackage - Premium detaylı temizlik
class PremiumWashPackage extends WashPackage {

    public PremiumWashPackage() {
        super("PKG_PREMIUM", "Premium Detaylı Temizlik", 300.0, 120);
    }

    @Override
    public List<String> getServices() {
        List<String> services = new ArrayList<>();
        services.addAll(new FullWashPackage().getServices());
        services.add("Seramik kaplama");
        services.add("Far parlatma");
        services.add("Jant boyama");
        services.add("Oto kuaför");
        services.add("Koku giderme (ozon)");
        return services;
    }

    @Override
    public double calculatePrice(Vehicle vehicle) {
        double price = getBasePrice();
        if (vehicle instanceof Truck) {
            price *= 3.0;
        } else if (vehicle instanceof Van) {
            price *= 2.0;
        } else if (vehicle instanceof Motorcycle) {
            price *= 0.8;
        }
        return price;
    }
}

// WashOrder - Yıkama siparişi
class WashOrder {
    private String orderId;
    private Ticket ticket;
    private WashPackage washPackage;
    private LocalDateTime orderTime;
    private LocalDateTime startTime;
    private LocalDateTime completionTime;
    private String status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private WashWorker assignedWorker;
    private double finalPrice;

    public WashOrder(String orderId, Ticket ticket, WashPackage washPackage) {
        this.orderId = orderId;
        this.ticket = ticket;
        this.washPackage = washPackage;
        this.orderTime = LocalDateTime.now();
        this.status = "PENDING";
        this.finalPrice = washPackage.calculatePrice(ticket.getVehicle());
    }

    public void startWash(WashWorker worker) {
        this.assignedWorker = worker;
        this.startTime = LocalDateTime.now();
        this.status = "IN_PROGRESS";
        System.out.println("🚿 Wash started by: " + worker.getName());
    }

    public void completeWash() {
        this.completionTime = LocalDateTime.now();
        this.status = "COMPLETED";
        if (assignedWorker != null) {
            assignedWorker.incrementCompletedJobs();
        }
        System.out.println("✓ Wash completed!");
    }

    public void cancelOrder() {
        this.status = "CANCELLED";
        System.out.println("✗ Wash order cancelled.");
    }

    // Getters
    public String getOrderId() {
        return orderId;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public WashPackage getWashPackage() {
        return washPackage;
    }

    public String getStatus() {
        return status;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public WashWorker getAssignedWorker() {
        return assignedWorker;
    }

    @Override
    public String toString() {
        return "Order " + orderId + " - " + washPackage.getPackageName() +
                " [" + status + "] - " + finalPrice + " TL";
    }
}

// WashWorker - Yıkama çalışanı
class WashWorker extends Employee {
    private int completedJobs;
    private boolean isAvailable;

    public WashWorker(String id, String name, String phone, String email,
                      String employeeId, double salary) {
        super(id, name, phone, email, employeeId, salary);
        this.completedJobs = 0;
        this.isAvailable = true;
    }

    @Override
    public String getRole() {
        return "Wash Worker";
    }

    public void incrementCompletedJobs() {
        this.completedJobs++;
    }

    public int getCompletedJobs() {
        return completedJobs;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
}

// CarWashService - Ana yıkama servisi
public class CarWashService {
    private List<WashPackage> availablePackages;
    private Queue<WashOrder> pendingOrders;
    private List<WashOrder> completedOrders;
    private List<WashWorker> washWorkers;
    private double totalRevenue;

    public CarWashService() {
        this.availablePackages = new ArrayList<>();
        this.pendingOrders = new LinkedList<>();
        this.completedOrders = new ArrayList<>();
        this.washWorkers = new ArrayList<>();
        this.totalRevenue = 0.0;

        initializePackages();
    }

    private void initializePackages() {
        availablePackages.add(new BasicWashPackage());
        availablePackages.add(new InteriorWashPackage());
        availablePackages.add(new FullWashPackage());
        availablePackages.add(new PremiumWashPackage());
    }

    // Paket seçim menüsü
    public void displayPackages() {
        System.out.println("\n╔══════════ CAR WASH PACKAGES ══════════╗");
        for (int i = 0; i < availablePackages.size(); i++) {
            WashPackage pkg = availablePackages.get(i);
            System.out.println((i + 1) + ". " + pkg);
            System.out.println("   Services:");
            for (String service : pkg.getServices()) {
                System.out.println("   - " + service);
            }
            System.out.println();
        }
        System.out.println("╚═══════════════════════════════════════╝\n");
    }

    // Yıkama siparişi oluştur
    public WashOrder createWashOrder(Ticket ticket, int packageIndex) {
        if (packageIndex < 0 || packageIndex >= availablePackages.size()) {
            System.out.println("Invalid package selection!");
            return null;
        }

        WashPackage selectedPackage = availablePackages.get(packageIndex);
        String orderId = "WASH" + System.currentTimeMillis();

        WashOrder order = new WashOrder(orderId, ticket, selectedPackage);
        pendingOrders.add(order);

        System.out.println("\n✓ Wash order created!");
        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Package: " + selectedPackage.getPackageName());
        System.out.println("Price: " + order.getFinalPrice() + " TL");
        System.out.println("Estimated time: " + selectedPackage.getEstimatedDuration() + " minutes");
        System.out.println("Queue position: " + pendingOrders.size());

        return order;
    }

    // Sıradaki işi başlat
    public void processNextOrder() {
        if (pendingOrders.isEmpty()) {
            System.out.println("No pending wash orders.");
            return;
        }

        WashWorker availableWorker = findAvailableWorker();
        if (availableWorker == null) {
            System.out.println("No available wash workers. Please wait.");
            return;
        }

        WashOrder order = pendingOrders.poll();
        availableWorker.setAvailable(false);
        order.startWash(availableWorker);

        // Simülasyon: işi tamamla
        order.completeWash();
        completedOrders.add(order);
        totalRevenue += order.getFinalPrice();
        availableWorker.setAvailable(true);
    }

    // Tüm bekleyen işleri işle
    public void processAllOrders() {
        System.out.println("\n🚿 Processing all wash orders...\n");
        while (!pendingOrders.isEmpty()) {
            processNextOrder();
        }
        System.out.println("\n✓ All wash orders completed!\n");
    }

    // Çalışan ekle
    public void addWashWorker(WashWorker worker) {
        washWorkers.add(worker);
        System.out.println("Wash worker added: " + worker.getName());
    }

    // Müsait çalışan bul
    private WashWorker findAvailableWorker() {
        for (WashWorker worker : washWorkers) {
            if (worker.isAvailable()) {
                return worker;
            }
        }
        return null;
    }

    // Rapor
    public void generateReport() {
        System.out.println("\n╔════════ CAR WASH SERVICE REPORT ════════╗");
        System.out.println("Total Orders: " + (completedOrders.size() + pendingOrders.size()));
        System.out.println("Completed: " + completedOrders.size());
        System.out.println("Pending: " + pendingOrders.size());
        System.out.println("Total Revenue: " + totalRevenue + " TL");

        if (!washWorkers.isEmpty()) {
            System.out.println("\nWorker Performance:");
            for (WashWorker worker : washWorkers) {
                System.out.println("- " + worker.getName() + ": " +
                        worker.getCompletedJobs() + " jobs");
            }
        }

        // En popüler paket
        Map<String, Integer> packageCount = new HashMap<>();
        for (WashOrder order : completedOrders) {
            String pkgName = order.getWashPackage().getPackageName();
            packageCount.put(pkgName, packageCount.getOrDefault(pkgName, 0) + 1);
        }

        if (!packageCount.isEmpty()) {
            System.out.println("\nMost Popular Package:");
            String mostPopular = Collections.max(packageCount.entrySet(),
                    Map.Entry.comparingByValue()).getKey();
            System.out.println("- " + mostPopular + " (" + packageCount.get(mostPopular) + " orders)");
        }

        System.out.println("╚═════════════════════════════════════════╝\n");
    }

    // Getters
    public List<WashPackage> getAvailablePackages() {
        return availablePackages;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public int getPendingOrderCount() {
        return pendingOrders.size();
    }
}