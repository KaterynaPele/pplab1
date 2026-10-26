package carservice;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CarService {
    private final Map<String, Client> clients = new HashMap<>();
    private final Map<String, Car> cars = new HashMap<>();
    private final Map<String, Mechanic> mechanics = new HashMap<>();
    private final Map<String, Order> orders = new HashMap<>();
    private final AtomicInteger idGen = new AtomicInteger(1);

    public void registerClient(Client c) {
        if (c == null) throw new BusinessException("Client is null");
        if (clients.containsKey(c.getId())) throw new BusinessException("Client id already exists");
        clients.put(c.getId(), c);
    }

    public void registerCar(Car car) {
        if (car == null) throw new BusinessException("Car is null");
        if (cars.containsKey(car.getVin())) throw new BusinessException("VIN already registered");
        cars.put(car.getVin(), car);
    }

    public void registerMechanic(Mechanic m) {
        if (m == null) throw new BusinessException("Mechanic is null");
        if (mechanics.containsKey(m.getId())) throw new BusinessException("Mechanic id exists");
        mechanics.put(m.getId(), m);
    }

    // Створити замовлення для зареєстрованого VIN
    public Order createOrderFor(String orderId, String vin) {
        Car car = cars.get(vin);
        if (car == null) throw new BusinessException("Car not registered");
        String id = (orderId == null || orderId.isBlank()) ? String.valueOf(idGen.getAndIncrement()) : orderId;
        if (orders.containsKey(id)) throw new BusinessException("Order id already exists");
        Order o = new Order(id, car);
        orders.put(id, o);
        return o;
    }

    public Mechanic findMechanicById(String id) { return mechanics.get(id); }
    public Order findOrder(String id) { return orders.get(id); }
    public Collection<Mechanic> getMechanics() { return mechanics.values(); }
    public Client getClientById(String id) { return clients.get(id); }
    public Collection<Order> getOrders() { return orders.values(); }
}
