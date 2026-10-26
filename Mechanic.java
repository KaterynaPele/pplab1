package carservice;

public class Mechanic {
    private final String id;
    private final String name;
    private final Specialization specialization;
    private boolean busy = false;

    public Mechanic(String id, String name, Specialization specialization) {
        if (id == null || id.isBlank()) throw new BusinessException("Mechanic ID cannot be empty");
        if (name == null || name.isBlank()) throw new BusinessException("Mechanic name cannot be empty");
        if (specialization == null) throw new BusinessException("Mechanic specialization required");
        this.id = id;
        this.name = name;
        this.specialization = specialization;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Specialization getSpecialization() { return specialization; }

    // Методи для контролю зайнятості механіка
    public boolean isBusy() { return busy; }
    public void setBusy(boolean busy) { this.busy = busy; }

    @Override
    public String toString() {
        return "Mechanic{" + id + " " + name + " spec=" + specialization + " busy=" + busy + "}";
    }
}
