import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// SecurityCamera sınıfı
class SecurityCamera {
    private String cameraId;
    private String location;
    private int floor;
    private boolean isActive;
    private boolean isRecording;
    private String quality; // "720p", "1080p", "4K"
    private List<CameraRecording> recordings;

    public SecurityCamera(String cameraId, String location, int floor, String quality) {
        this.cameraId = cameraId;
        this.location = location;
        this.floor = floor;
        this.quality = quality;
        this.isActive = true;
        this.isRecording = true;
        this.recordings = new ArrayList<>();
    }

    // Kayıt başlat
    public CameraRecording startRecording(String eventType) {
        if (!isActive) {
            System.out.println("Camera " + cameraId + " is not active!");
            return null;
        }

        String recordingId = cameraId + "_REC_" + System.currentTimeMillis();
        CameraRecording recording = new CameraRecording(
                recordingId, this, eventType, LocalDateTime.now()
        );

        recordings.add(recording);
        return recording;
    }

    // Plaka tanıma simülasyonu
    public String recognizeLicensePlate(Vehicle vehicle) {
        if (!isActive) return null;

        // Simülasyon: %90 başarı oranı
        boolean success = Math.random() < 0.90;

        if (success) {
            System.out.println("📹 Camera " + cameraId + " recognized plate: " +
                    vehicle.getLicensePlate());
            return vehicle.getLicensePlate();
        } else {
            System.out.println("📹 Camera " + cameraId + " failed to recognize plate");
            return "UNKNOWN";
        }
    }

    // Getters and Setters
    public String getCameraId() {
        return cameraId;
    }

    public String getLocation() {
        return location;
    }

    public int getFloor() {
        return floor;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        this.isRecording = recording;
    }

    public List<CameraRecording> getRecordings() {
        return recordings;
    }

    @Override
    public String toString() {
        return "Camera " + cameraId + " [" + location + ", Floor " + floor + "] - " +
                (isActive ? "ACTIVE" : "INACTIVE") + " - " + quality;
    }
}

// CameraRecording - Kamera kaydı
class CameraRecording {
    private String recordingId;
    private SecurityCamera camera;
    private String eventType; // "ENTRY", "EXIT", "INCIDENT", "ROUTINE"
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long fileSizeMB;

    public CameraRecording(String recordingId, SecurityCamera camera,
                           String eventType, LocalDateTime startTime) {
        this.recordingId = recordingId;
        this.camera = camera;
        this.eventType = eventType;
        this.startTime = startTime;
        this.endTime = null;
        this.fileSizeMB = 0;
    }

    public void stopRecording() {
        this.endTime = LocalDateTime.now();
        // Simülasyon: dosya boyutu
        long durationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        this.fileSizeMB = durationMinutes * 50; // 50 MB per minute
    }

    // Getters
    public String getRecordingId() {
        return recordingId;
    }

    public SecurityCamera getCamera() {
        return camera;
    }

    public String getEventType() {
        return eventType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public long getFileSizeMB() {
        return fileSizeMB;
    }
}

// IncidentReport - Olay raporu
class IncidentReport {
    private String reportId;
    private String incidentType; // "ACCIDENT", "THEFT", "VANDALISM", "SUSPICIOUS"
    private String description;
    private LocalDateTime reportTime;
    private LocalDateTime incidentTime;
    private String location;
    private int floor;
    private List<String> involvedVehicles;
    private SecurityGuard reportedBy;
    private List<CameraRecording> relatedRecordings;
    private String status; // "OPEN", "INVESTIGATING", "RESOLVED", "CLOSED"
    private String severity; // "LOW", "MEDIUM", "HIGH", "CRITICAL"

    public IncidentReport(String reportId, String incidentType, String description,
                          LocalDateTime incidentTime, String location, int floor,
                          SecurityGuard reportedBy) {
        this.reportId = reportId;
        this.incidentType = incidentType;
        this.description = description;
        this.reportTime = LocalDateTime.now();
        this.incidentTime = incidentTime;
        this.location = location;
        this.floor = floor;
        this.reportedBy = reportedBy;
        this.involvedVehicles = new ArrayList<>();
        this.relatedRecordings = new ArrayList<>();
        this.status = "OPEN";
        this.severity = determineSeverity(incidentType);
    }

    private String determineSeverity(String type) {
        switch (type.toUpperCase()) {
            case "THEFT":
            case "VANDALISM":
                return "HIGH";
            case "ACCIDENT":
                return "MEDIUM";
            case "SUSPICIOUS":
                return "LOW";
            default:
                return "MEDIUM";
        }
    }

    public void addInvolvedVehicle(String licensePlate) {
        involvedVehicles.add(licensePlate);
    }

    public void addRecording(CameraRecording recording) {
        relatedRecordings.add(recording);
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
        System.out.println("Incident " + reportId + " status updated to: " + newStatus);
    }

    public void printReport() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        System.out.println("\n╔═══════ INCIDENT REPORT ═══════╗");
        System.out.println("Report ID: " + reportId);
        System.out.println("Type: " + incidentType);
        System.out.println("Severity: " + severity);
        System.out.println("Status: " + status);
        System.out.println("Location: " + location + ", Floor " + floor);
        System.out.println("Incident Time: " + incidentTime.format(formatter));
        System.out.println("Reported By: " + reportedBy.getName());
        System.out.println("\nDescription:");
        System.out.println(description);

        if (!involvedVehicles.isEmpty()) {
            System.out.println("\nInvolved Vehicles:");
            for (String plate : involvedVehicles) {
                System.out.println("- " + plate);
            }
        }

        if (!relatedRecordings.isEmpty()) {
            System.out.println("\nRelated Recordings: " + relatedRecordings.size());
        }

        System.out.println("╚═══════════════════════════════╝\n");
    }

    // Getters
    public String getReportId() {
        return reportId;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public String getStatus() {
        return status;
    }

    public String getSeverity() {
        return severity;
    }

    public List<String> getInvolvedVehicles() {
        return involvedVehicles;
    }
}

// SecuritySystem - Ana güvenlik sistemi
public class SecuritySystem {
    private List<SecurityCamera> cameras;
    private List<IncidentReport> incidentReports;
    private List<SecurityGuard> securityGuards;
    private Map<String, List<String>> vehicleAccessLog; // licensePlate -> timestamps

    public SecuritySystem() {
        this.cameras = new ArrayList<>();
        this.incidentReports = new ArrayList<>();
        this.securityGuards = new ArrayList<>();
        this.vehicleAccessLog = new HashMap<>();
    }

    // Kamera ekle
    public void addCamera(SecurityCamera camera) {
        cameras.add(camera);
        System.out.println("✓ Camera added: " + camera.getCameraId() +
                " at " + camera.getLocation());
    }

    // Katlar için kameraları otomatik başlat
    public void initializeCameras(int totalFloors) {
        System.out.println("\n🎥 Initializing security cameras...\n");

        for (int floor = 0; floor < totalFloors; floor++) {
            // Her katta 4 kamera
            addCamera(new SecurityCamera("CAM_F" + floor + "_ENTRY",
                    "Entry Point", floor, "1080p"));
            addCamera(new SecurityCamera("CAM_F" + floor + "_EXIT",
                    "Exit Point", floor, "1080p"));
            addCamera(new SecurityCamera("CAM_F" + floor + "_CENTER",
                    "Center Area", floor, "4K"));
            addCamera(new SecurityCamera("CAM_F" + floor + "_CORNER",
                    "Corner Area", floor, "720p"));
        }

        System.out.println("✓ Total cameras installed: " + cameras.size() + "\n");
    }

    // Araç girişi kaydet
    public void logVehicleEntry(Vehicle vehicle, ParkingSpot spot) {
        String licensePlate = vehicle.getLicensePlate();
        LocalDateTime now = LocalDateTime.now();

        // Access log'a ekle
        vehicleAccessLog.putIfAbsent(licensePlate, new ArrayList<>());
        vehicleAccessLog.get(licensePlate).add("ENTRY: " + now);

        // İlgili kamerayı bul ve plaka tanıma
        SecurityCamera camera = findCameraByLocation("Entry Point", spot.getFloor());
        if (camera != null) {
            camera.recognizeLicensePlate(vehicle);
            CameraRecording recording = camera.startRecording("ENTRY");
            if (recording != null) {
                // Simülasyon: 2 dakika sonra kayıt durdur
                recording.stopRecording();
            }
        }

        System.out.println("🔒 Security: Vehicle " + licensePlate + " entry logged");
    }

    // Araç çıkışı kaydet
    public void logVehicleExit(Vehicle vehicle, ParkingSpot spot) {
        String licensePlate = vehicle.getLicensePlate();
        LocalDateTime now = LocalDateTime.now();

        // Access log'a ekle
        if (vehicleAccessLog.containsKey(licensePlate)) {
            vehicleAccessLog.get(licensePlate).add("EXIT: " + now);
        }

        // İlgili kamerayı bul
        SecurityCamera camera = findCameraByLocation("Exit Point", spot.getFloor());
        if (camera != null) {
            camera.recognizeLicensePlate(vehicle);
            CameraRecording recording = camera.startRecording("EXIT");
            if (recording != null) {
                recording.stopRecording();
            }
        }

        System.out.println("🔒 Security: Vehicle " + licensePlate + " exit logged");
    }

    // Olay raporu oluştur
    public IncidentReport createIncidentReport(String incidentType, String description,
                                               LocalDateTime incidentTime, String location,
                                               int floor, SecurityGuard guard) {
        String reportId = "INC" + System.currentTimeMillis();

        IncidentReport report = new IncidentReport(
                reportId, incidentType, description, incidentTime,
                location, floor, guard
        );

        incidentReports.add(report);

        System.out.println("\n⚠️ INCIDENT REPORT CREATED");
        System.out.println("Report ID: " + reportId);
        System.out.println("Type: " + incidentType);
        System.out.println("Severity: " + report.getSeverity());

        // İlgili kamera kayıtlarını bul
        List<SecurityCamera> nearbyCamera = findCamerasByFloor(floor);
        for (SecurityCamera camera : nearbyCamera) {
            if (!camera.getRecordings().isEmpty()) {
                CameraRecording lastRecording = camera.getRecordings()
                        .get(camera.getRecordings().size() - 1);
                report.addRecording(lastRecording);
            }
        }

        return report;
    }

    // Güvenlik görevlisi ekle
    public void addSecurityGuard(SecurityGuard guard) {
        securityGuards.add(guard);
    }

    // Kamera bul (lokasyon ve kat)
    private SecurityCamera findCameraByLocation(String location, int floor) {
        for (SecurityCamera camera : cameras) {
            if (camera.getLocation().equals(location) && camera.getFloor() == floor) {
                return camera;
            }
        }
        return null;
    }

    // Kattaki tüm kameraları bul
    private List<SecurityCamera> findCamerasByFloor(int floor) {
        List<SecurityCamera> floorCameras = new ArrayList<>();
        for (SecurityCamera camera : cameras) {
            if (camera.getFloor() == floor) {
                floorCameras.add(camera);
            }
        }
        return floorCameras;
    }

    // Araç erişim geçmişini göster
    public void showVehicleAccessHistory(String licensePlate) {
        System.out.println("\n═══ VEHICLE ACCESS HISTORY ═══");
        System.out.println("License Plate: " + licensePlate);

        if (vehicleAccessLog.containsKey(licensePlate)) {
            List<String> logs = vehicleAccessLog.get(licensePlate);
            System.out.println("Total Access: " + logs.size());
            for (String log : logs) {
                System.out.println("- " + log);
            }
        } else {
            System.out.println("No access history found.");
        }
        System.out.println("══════════════════════════════\n");
    }

    // Sistem raporu
    public void generateSecurityReport() {
        System.out.println("\n╔════════ SECURITY SYSTEM REPORT ════════╗");
        System.out.println("Total Cameras: " + cameras.size());

        long activeCameras = cameras.stream().filter(SecurityCamera::isActive).count();
        System.out.println("Active Cameras: " + activeCameras);

        long totalRecordings = cameras.stream()
                .mapToLong(c -> c.getRecordings().size())
                .sum();
        System.out.println("Total Recordings: " + totalRecordings);

        System.out.println("\nIncident Reports:");
        System.out.println("Total: " + incidentReports.size());

        Map<String, Long> incidentsByType = new HashMap<>();
        Map<String, Long> incidentsBySeverity = new HashMap<>();

        for (IncidentReport report : incidentReports) {
            incidentsByType.merge(report.getIncidentType(), 1L, Long::sum);
            incidentsBySeverity.merge(report.getSeverity(), 1L, Long::sum);
        }

        if (!incidentsByType.isEmpty()) {
            System.out.println("\nBy Type:");
            incidentsByType.forEach((type, count) ->
                    System.out.println("- " + type + ": " + count));
        }

        if (!incidentsBySeverity.isEmpty()) {
            System.out.println("\nBy Severity:");
            incidentsBySeverity.forEach((severity, count) ->
                    System.out.println("- " + severity + ": " + count));
        }

        long openIncidents = incidentReports.stream()
                .filter(r -> r.getStatus().equals("OPEN"))
                .count();
        System.out.println("\nOpen Incidents: " + openIncidents);

        System.out.println("\nVehicles Tracked: " + vehicleAccessLog.size());

        System.out.println("╚════════════════════════════════════════╝\n");
    }

    // Getters
    public List<SecurityCamera> getCameras() {
        return cameras;
    }

    public List<IncidentReport> getIncidentReports() {
        return incidentReports;
    }

    public List<IncidentReport> getOpenIncidents() {
        List<IncidentReport> openIncidents = new ArrayList<>();
        for (IncidentReport report : incidentReports) {
            if (report.getStatus().equals("OPEN")) {
                openIncidents.add(report);
            }
        }
        return openIncidents;
    }
}