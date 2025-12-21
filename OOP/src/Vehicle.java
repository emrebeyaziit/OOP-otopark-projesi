// Temel araç sınıfı (Abstract)
public abstract class Vehicle {
    private String licensePlate;
    private String color;
    private String brand;
    private String model;

    public Vehicle(String licensePlate, String color, String brand, String model) {
        this.licensePlate = licensePlate;
        this.color = color;
        this.brand = brand;
        this.model = model;
    }

    // Abstract methods
    public abstract String getVehicleType();
    public abstract double getSizeMultiplier(); // Ücret hesabı için

    // Getters and Setters
    public String getLicensePlate() {
        return licensePlate;
    }

    public String getColor() {
        return color;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    @Override
    public String toString() {
        return getVehicleType() + " - " + brand + " " + model +
                " (" + licensePlate + ")";
    }
}

// Car sınıfı
class Car extends Vehicle {
    private int numberOfDoors;

    public Car(String licensePlate, String color, String brand,
               String model, int numberOfDoors) {
        super(licensePlate, color, brand, model);
        this.numberOfDoors = numberOfDoors;
    }

    @Override
    public String getVehicleType() {
        return "Car";
    }

    @Override
    public double getSizeMultiplier() {
        return 1.0; // Normal ücret
    }

    public int getNumberOfDoors() {
        return numberOfDoors;
    }
}

// Motorcycle sınıfı
class Motorcycle extends Vehicle {
    private int engineCapacity;

    public Motorcycle(String licensePlate, String color, String brand,
                      String model, int engineCapacity) {
        super(licensePlate, color, brand, model);
        this.engineCapacity = engineCapacity;
    }

    @Override
    public String getVehicleType() {
        return "Motorcycle";
    }

    @Override
    public double getSizeMultiplier() {
        return 0.5; // Daha ucuz
    }

    public int getEngineCapacity() {
        return engineCapacity;
    }
}

// Van sınıfı
class Van extends Vehicle {
    private int capacity;

    public Van(String licensePlate, String color, String brand,
               String model, int capacity) {
        super(licensePlate, color, brand, model);
        this.capacity = capacity;
    }

    @Override
    public String getVehicleType() {
        return "Van";
    }

    @Override
    public double getSizeMultiplier() {
        return 1.5; // Daha pahalı
    }

    public int getCapacity() {
        return capacity;
    }
}

// Truck sınıfı
class Truck extends Vehicle {
    private double loadCapacity;

    public Truck(String licensePlate, String color, String brand,
                 String model, double loadCapacity) {
        super(licensePlate, color, brand, model);
        this.loadCapacity = loadCapacity;
    }

    @Override
    public String getVehicleType() {
        return "Truck";
    }

    @Override
    public double getSizeMultiplier() {
        return 2.0; // En pahalı
    }

    public double getLoadCapacity() {
        return loadCapacity;
    }
}

// ElectricVehicle sınıfı
class ElectricVehicle extends Car {
    private int batteryCapacity;
    private boolean needsCharging;

    public ElectricVehicle(String licensePlate, String color, String brand,
                           String model, int numberOfDoors, int batteryCapacity) {
        super(licensePlate, color, brand, model, numberOfDoors);
        this.batteryCapacity = batteryCapacity;
        this.needsCharging = false;
    }

    @Override
    public String getVehicleType() {
        return "Electric Car";
    }

    @Override
    public double getSizeMultiplier() {
        return 1.2; // Şarj hizmeti için ek ücret
    }

    public int getBatteryCapacity() {
        return batteryCapacity;
    }

    public boolean isNeedsCharging() {
        return needsCharging;
    }

    public void setNeedsCharging(boolean needsCharging) {
        this.needsCharging = needsCharging;
    }
}