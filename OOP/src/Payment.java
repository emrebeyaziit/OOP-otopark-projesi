import java.time.LocalDateTime;

// Payment interface
interface Payable {
    boolean processPayment(double amount);
    String getPaymentMethod();
}

// Abstract Payment sınıfı
public abstract class Payment implements Payable {
    private String paymentId;
    private double amount;
    private LocalDateTime paymentTime;
    private boolean isSuccessful;

    public Payment(String paymentId, double amount) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentTime = LocalDateTime.now();
        this.isSuccessful = false;
    }

    // Abstract method
    public abstract boolean validate();

    // Getters
    public String getPaymentId() {
        return paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    protected void setSuccessful(boolean successful) {
        this.isSuccessful = successful;
    }

    @Override
    public String toString() {
        return "Payment ID: " + paymentId +
                ", Method: " + getPaymentMethod() +
                ", Amount: " + amount + " TL" +
                ", Status: " + (isSuccessful ? "SUCCESS" : "FAILED");
    }
}

// CashPayment sınıfı
class CashPayment extends Payment {
    private double receivedAmount;
    private double change;

    public CashPayment(String paymentId, double amount, double receivedAmount) {
        super(paymentId, amount);
        this.receivedAmount = receivedAmount;
        this.change = 0;
    }

    @Override
    public String getPaymentMethod() {
        return "Cash";
    }

    @Override
    public boolean validate() {
        return receivedAmount >= getAmount();
    }

    @Override
    public boolean processPayment(double amount) {
        if (validate()) {
            this.change = receivedAmount - amount;
            setSuccessful(true);
            System.out.println("Cash payment processed successfully.");
            System.out.println("Change: " + change + " TL");
            return true;
        } else {
            System.out.println("Insufficient cash! Need " +
                    (amount - receivedAmount) + " TL more.");
            return false;
        }
    }

    public double getReceivedAmount() {
        return receivedAmount;
    }

    public double getChange() {
        return change;
    }
}

// CreditCardPayment sınıfı
class CreditCardPayment extends Payment {
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;

    public CreditCardPayment(String paymentId, double amount,
                             String cardNumber, String cardHolderName,
                             String expiryDate, String cvv) {
        super(paymentId, amount);
        this.cardNumber = maskCardNumber(cardNumber);
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    @Override
    public String getPaymentMethod() {
        return "Credit Card";
    }

    @Override
    public boolean validate() {
        // Basit validasyon
        if (cardNumber == null || cardNumber.length() < 16) return false;
        if (cvv == null || cvv.length() != 3) return false;
        if (expiryDate == null || expiryDate.isEmpty()) return false;
        return true;
    }

    @Override
    public boolean processPayment(double amount) {
        if (validate()) {
            // Simülasyon: %95 başarı oranı
            boolean success = Math.random() < 0.95;
            setSuccessful(success);

            if (success) {
                System.out.println("Credit card payment processed successfully.");
                System.out.println("Card: " + cardNumber);
            } else {
                System.out.println("Credit card payment failed! Please try again.");
            }
            return success;
        } else {
            System.out.println("Invalid credit card information!");
            return false;
        }
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() < 4) return cardNumber;
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    public String getCardNumber() {
        return cardNumber;
    }
}

// MobilePayment sınıfı
class MobilePayment extends Payment {
    private String phoneNumber;
    private String provider; // "ApplePay", "GooglePay", "SamsungPay"
    private String transactionCode;

    public MobilePayment(String paymentId, double amount,
                         String phoneNumber, String provider) {
        super(paymentId, amount);
        this.phoneNumber = phoneNumber;
        this.provider = provider;
        this.transactionCode = generateTransactionCode();
    }

    @Override
    public String getPaymentMethod() {
        return "Mobile Payment (" + provider + ")";
    }

    @Override
    public boolean validate() {
        // Telefon numarası kontrolü
        if (phoneNumber == null || phoneNumber.length() != 11) return false;
        // Provider kontrolü
        if (!provider.equals("ApplePay") &&
                !provider.equals("GooglePay") &&
                !provider.equals("SamsungPay")) return false;
        return true;
    }

    @Override
    public boolean processPayment(double amount) {
        if (validate()) {
            // Simülasyon: %98 başarı oranı
            boolean success = Math.random() < 0.98;
            setSuccessful(success);

            if (success) {
                System.out.println("Mobile payment processed successfully.");
                System.out.println("Provider: " + provider);
                System.out.println("Transaction Code: " + transactionCode);
            } else {
                System.out.println("Mobile payment failed! Please try again.");
            }
            return success;
        } else {
            System.out.println("Invalid mobile payment information!");
            return false;
        }
    }

    private String generateTransactionCode() {
        return "TXN" + System.currentTimeMillis();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProvider() {
        return provider;
    }

    public String getTransactionCode() {
        return transactionCode;
    }
}

// OnlinePayment sınıfı (Bonus)
class OnlinePayment extends Payment {
    private String email;
    private String paymentGateway; // "PayPal", "Stripe", "Iyzico"
    private String confirmationToken;

    public OnlinePayment(String paymentId, double amount,
                         String email, String paymentGateway) {
        super(paymentId, amount);
        this.email = email;
        this.paymentGateway = paymentGateway;
        this.confirmationToken = null;
    }

    @Override
    public String getPaymentMethod() {
        return "Online Payment (" + paymentGateway + ")";
    }

    @Override
    public boolean validate() {
        return email != null && email.contains("@");
    }

    @Override
    public boolean processPayment(double amount) {
        if (validate()) {
            boolean success = Math.random() < 0.97;
            setSuccessful(success);

            if (success) {
                this.confirmationToken = "CONF" + System.currentTimeMillis();
                System.out.println("Online payment processed successfully.");
                System.out.println("Gateway: " + paymentGateway);
                System.out.println("Confirmation: " + confirmationToken);
            } else {
                System.out.println("Online payment failed!");
            }
            return success;
        }
        return false;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }
}