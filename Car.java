package carservice;

public class Car {
    private final String vin;
    private final String make;
    private final String model;
    private final int year;
    private final Client owner;

    public Car(String vin, String make, String model, int year, Client owner) {
        if (vin == null || vin.isBlank()) throw new BusinessException("VIN cannot be empty");
        if (owner == null) throw new BusinessException("Car must have an owner");
        if (year < 1886) throw new BusinessException("Year is invalid");
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.year = year;
        this.owner = owner;
    }

    public String getVin() { return vin; }
    public Client getOwner() { return owner; }

    public String toString() {
        return "Car{" + vin + " " + make + " " + model + " " + year + " owner=" + owner.getName() + "}";
    }
}
