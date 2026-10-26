package carservice;

public class Client {
    private final String id;
    private final String name;

    public Client(String id, String name) {
        if (id == null || id.isBlank()) throw new BusinessException("Client id cannot be empty");
        if (name == null || name.isBlank()) throw new BusinessException("Client name cannot be empty");
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public String toString() {
        return "Client{" + id + " - " + name + "}";
    }
}
