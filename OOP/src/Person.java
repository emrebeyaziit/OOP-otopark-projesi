// Temel kişi sınıfı (Abstract)
public abstract class Person {
    private String id;
    private String name;
    private String phone;
    private String email;

    // Constructor
    public Person(String id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    // Abstract method - alt sınıflar implement edecek
    public abstract String getRole();

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // toString override
    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Role: " + getRole();
    }
}

// Customer sınıfı
class Customer extends Person {
    private String licensePlate;
    private int visitCount;

    public Customer(String id, String name, String phone, String email, String licensePlate) {
        super(id, name, phone, email);
        this.licensePlate = licensePlate;
        this.visitCount = 0;
    }

    @Override
    public String getRole() {
        return "Customer";
    }

    public void incrementVisitCount() {
        this.visitCount++;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public int getVisitCount() {
        return visitCount;
    }
}

// Employee abstract sınıfı
abstract class Employee extends Person {
    private String employeeId;
    private double salary;

    public Employee(String id, String name, String phone, String email,
                    String employeeId, double salary) {
        super(id, name, phone, email);
        this.employeeId = employeeId;
        this.salary = salary;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}

// ParkingAttendant sınıfı
class ParkingAttendant extends Employee {
    private int assignedFloor;

    public ParkingAttendant(String id, String name, String phone, String email,
                            String employeeId, double salary, int assignedFloor) {
        super(id, name, phone, email, employeeId, salary);
        this.assignedFloor = assignedFloor;
    }

    @Override
    public String getRole() {
        return "Parking Attendant";
    }

    public int getAssignedFloor() {
        return assignedFloor;
    }
}

// Manager sınıfı
class Manager extends Employee {
    private String department;

    public Manager(String id, String name, String phone, String email,
                   String employeeId, double salary, String department) {
        super(id, name, phone, email, employeeId, salary);
        this.department = department;
    }

    @Override
    public String getRole() {
        return "Manager";
    }

    public String getDepartment() {
        return department;
    }
}

// SecurityGuard sınıfı
class SecurityGuard extends Employee {
    private String shift; // "Morning", "Evening", "Night"

    public SecurityGuard(String id, String name, String phone, String email,
                         String employeeId, double salary, String shift) {
        super(id, name, phone, email, employeeId, salary);
        this.shift = shift;
    }

    @Override
    public String getRole() {
        return "Security Guard";
    }

    public String getShift() {
        return shift;
    }
}