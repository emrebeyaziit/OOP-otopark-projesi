import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Notification interface
interface Notifiable {
    boolean send();
    String getNotificationType();
}

// Abstract Notification sınıfı
abstract class Notification implements Notifiable {
    private String notificationId;
    private String recipient;
    private String message;
    private LocalDateTime sentTime;
    private boolean isDelivered;

    public Notification(String notificationId, String recipient, String message) {
        this.notificationId = notificationId;
        this.recipient = recipient;
        this.message = message;
        this.sentTime = null;
        this.isDelivered = false;
    }

    protected void markAsDelivered() {
        this.isDelivered = true;
        this.sentTime = LocalDateTime.now();
    }

    // Getters
    public String getNotificationId() {
        return notificationId;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    @Override
    public String toString() {
        return getNotificationType() + " to " + recipient +
                " - " + (isDelivered ? "DELIVERED" : "PENDING");
    }
}

// SMSNotification sınıfı
class SMSNotification extends Notification {
    private String phoneNumber;

    public SMSNotification(String notificationId, String phoneNumber, String message) {
        super(notificationId, phoneNumber, message);
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getNotificationType() {
        return "SMS";
    }

    @Override
    public boolean send() {
        // SMS gönderme simülasyonu
        System.out.println("\n📱 Sending SMS...");
        System.out.println("To: " + phoneNumber);
        System.out.println("Message: " + getMessage());

        // Simülasyon: %95 başarı oranı
        boolean success = Math.random() < 0.95;

        if (success) {
            markAsDelivered();
            System.out.println("✓ SMS delivered successfully!");
        } else {
            System.out.println("✗ SMS delivery failed!");
        }

        return success;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}

// EmailNotification sınıfı
class EmailNotification extends Notification {
    private String emailAddress;
    private String subject;

    public EmailNotification(String notificationId, String emailAddress,
                             String subject, String message) {
        super(notificationId, emailAddress, message);
        this.emailAddress = emailAddress;
        this.subject = subject;
    }

    @Override
    public String getNotificationType() {
        return "EMAIL";
    }

    @Override
    public boolean send() {
        // Email gönderme simülasyonu
        System.out.println("\n📧 Sending Email...");
        System.out.println("To: " + emailAddress);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + getMessage());

        // Simülasyon: %98 başarı oranı
        boolean success = Math.random() < 0.98;

        if (success) {
            markAsDelivered();
            System.out.println("✓ Email sent successfully!");
        } else {
            System.out.println("✗ Email delivery failed!");
        }

        return success;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getSubject() {
        return subject;
    }
}

// PushNotification sınıfı
class PushNotification extends Notification {
    private String deviceToken;
    private String title;

    public PushNotification(String notificationId, String deviceToken,
                            String title, String message) {
        super(notificationId, deviceToken, message);
        this.deviceToken = deviceToken;
        this.title = title;
    }

    @Override
    public String getNotificationType() {
        return "PUSH";
    }

    @Override
    public boolean send() {
        // Push notification gönderme simülasyonu
        System.out.println("\n🔔 Sending Push Notification...");
        System.out.println("Device: " + deviceToken);
        System.out.println("Title: " + title);
        System.out.println("Message: " + getMessage());

        // Simülasyon: %97 başarı oranı
        boolean success = Math.random() < 0.97;

        if (success) {
            markAsDelivered();
            System.out.println("✓ Push notification delivered!");
        } else {
            System.out.println("✗ Push notification failed!");
        }

        return success;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getTitle() {
        return title;
    }
}

// NotificationService - Merkezi bildirim yönetimi
public class NotificationService {
    private static NotificationService instance;
    private java.util.List<Notification> notificationHistory;

    private NotificationService() {
        this.notificationHistory = new java.util.ArrayList<>();
    }

    // Singleton pattern
    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    // Giriş bildirimi gönder
    public void sendEntryNotification(Customer customer, Ticket ticket, ParkingSpot spot) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String time = LocalDateTime.now().format(formatter);

        // SMS gönder
        String smsMessage = "Aracınız " + ticket.getVehicle().getLicensePlate() +
                " başarıyla park edildi. Yer: " + spot.getSpotId() +
                ", Kat: " + spot.getFloor() + ". Zaman: " + time;

        SMSNotification sms = new SMSNotification(
                "SMS" + System.currentTimeMillis(),
                customer.getPhone(),
                smsMessage
        );
        sms.send();
        notificationHistory.add(sms);

        // Email gönder
        String emailMessage = "Sayın " + customer.getName() + ",\n\n" +
                "Aracınız başarıyla park edilmiştir.\n\n" +
                "Detaylar:\n" +
                "Araç Plakası: " + ticket.getVehicle().getLicensePlate() + "\n" +
                "Park Yeri: " + spot.getSpotId() + "\n" +
                "Kat: " + spot.getFloor() + "\n" +
                "Giriş Zamanı: " + time + "\n" +
                "Bilet No: " + ticket.getTicketId() + "\n\n" +
                "İyi günler dileriz!";

        EmailNotification email = new EmailNotification(
                "EMAIL" + System.currentTimeMillis(),
                customer.getEmail(),
                "Otopark Giriş Bildirimi",
                emailMessage
        );
        email.send();
        notificationHistory.add(email);
    }

    // Çıkış bildirimi gönder
    public void sendExitNotification(Customer customer, Ticket ticket, double fee) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String time = LocalDateTime.now().format(formatter);

        // SMS gönder
        String smsMessage = "Aracınız " + ticket.getVehicle().getLicensePlate() +
                " otoparktan çıkış yaptı. Ücret: " + fee + " TL. " +
                "Zaman: " + time;

        SMSNotification sms = new SMSNotification(
                "SMS" + System.currentTimeMillis(),
                customer.getPhone(),
                smsMessage
        );
        sms.send();
        notificationHistory.add(sms);

        // Fatura emaili
        String emailMessage = "Sayın " + customer.getName() + ",\n\n" +
                "Otoparktan çıkış işleminiz tamamlanmıştır.\n\n" +
                "FATURA BİLGİLERİ:\n" +
                "====================\n" +
                "Araç Plakası: " + ticket.getVehicle().getLicensePlate() + "\n" +
                "Çıkış Zamanı: " + time + "\n" +
                "Bilet No: " + ticket.getTicketId() + "\n" +
                "Toplam Ücret: " + fee + " TL\n\n" +
                "Bizi tercih ettiğiniz için teşekkür ederiz!";

        EmailNotification email = new EmailNotification(
                "EMAIL" + System.currentTimeMillis(),
                customer.getEmail(),
                "Otopark Çıkış Faturası",
                emailMessage
        );
        email.send();
        notificationHistory.add(email);
    }

    // Hatırlatma bildirimi
    public void sendReminderNotification(Customer customer, Ticket ticket, int hoursRemaining) {
        String smsMessage = "Hatırlatma: Aracınız " + ticket.getVehicle().getLicensePlate() +
                " için kalan süre: " + hoursRemaining + " saat. " +
                "Bilet: " + ticket.getTicketId();

        SMSNotification sms = new SMSNotification(
                "SMS" + System.currentTimeMillis(),
                customer.getPhone(),
                smsMessage
        );
        sms.send();
        notificationHistory.add(sms);
    }

    // Ödeme hatırlatması
    public void sendPaymentReminderNotification(Customer customer, double amount) {
        PushNotification push = new PushNotification(
                "PUSH" + System.currentTimeMillis(),
                "DEVICE_" + customer.getId(),
                "Ödeme Hatırlatması",
                "Ödemeniz gereken tutar: " + amount + " TL"
        );
        push.send();
        notificationHistory.add(push);
    }

    // Bildirim geçmişi
    public java.util.List<Notification> getNotificationHistory() {
        return notificationHistory;
    }

    // İstatistikler
    public void printStatistics() {
        System.out.println("\n=== NOTIFICATION STATISTICS ===");
        long smsCount = notificationHistory.stream()
                .filter(n -> n instanceof SMSNotification)
                .count();
        long emailCount = notificationHistory.stream()
                .filter(n -> n instanceof EmailNotification)
                .count();
        long pushCount = notificationHistory.stream()
                .filter(n -> n instanceof PushNotification)
                .count();
        long delivered = notificationHistory.stream()
                .filter(Notification::isDelivered)
                .count();

        System.out.println("Total Notifications: " + notificationHistory.size());
        System.out.println("SMS: " + smsCount);
        System.out.println("Email: " + emailCount);
        System.out.println("Push: " + pushCount);
        System.out.println("Delivered: " + delivered);
        System.out.println("===============================\n");
    }
}