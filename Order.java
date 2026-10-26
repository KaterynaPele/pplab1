package carservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final String id;
    private final Car car;
    private final List<Work> works = new ArrayList<>();
    private OrderStatus status = OrderStatus.CREATED;
    private Mechanic mechanic;

    public Order(String id, Car car) {
        if (id == null || id.isBlank()) throw new BusinessException("Order id required");
        if (car == null) throw new BusinessException("Order must belong to a registered car");
        this.id = id;
        this.car = car;
    }

    public String getId() { return id; }
    public Car getCar() { return car; }
    public OrderStatus getStatus() { return status; }
    public Mechanic getMechanic() { return mechanic; }

    // повертаємо незмінну копію списку робіт (не даємо доступу на зміну ззовні)
    public List<Work> getWorks() { return Collections.unmodifiableList(works); }

    // додавання роботи
    public void addWork(Work w) {
        checkNotFinal();
        if (w == null) throw new BusinessException("Work cannot be null");
        works.add(w);
    }

    // діагностика: CREATED -> DIAGNOSED
    public void diagnose() {
        checkNotFinal();
        if (status != OrderStatus.CREATED) throw new BusinessException("Can diagnose only from CREATED status");
        status = OrderStatus.DIAGNOSED;
    }

    // погодження: CREATED|DIAGNOSED -> APPROVED, але не порожнє замовлення
    public void approve() {
        checkNotFinal();
        if (works.isEmpty()) throw new BusinessException("Cannot approve empty order without works");
        if (!(status == OrderStatus.CREATED || status == OrderStatus.DIAGNOSED))
            throw new BusinessException("Approve allowed only from CREATED or DIAGNOSED status");
        status = OrderStatus.APPROVED;
    }

    // призначення механіка
    public void assignMechanic(Mechanic m) {
        checkNotFinal();
        if (m == null) throw new BusinessException("Mechanic required");
        if (m.isBusy()) throw new BusinessException("Mechanic is already busy");
        if (status != OrderStatus.APPROVED) throw new BusinessException("Mechanic can be assigned only to APPROVED orders");
        this.mechanic = m;
        m.setBusy(true);
    }

    // початок виконання: тільки APPROVED і має механіка
    public void start() {
        checkNotFinal();
        if (status != OrderStatus.APPROVED) throw new BusinessException("Can start only APPROVED orders");
        if (mechanic == null) throw new BusinessException("Cannot start without assigned mechanic");
        status = OrderStatus.IN_PROGRESS;
    }

    // позначити роботу як виконану за індексом
    public void completeWork(int index) {
        checkNotFinal();
        if (status != OrderStatus.IN_PROGRESS) throw new BusinessException("Can complete works only when IN_PROGRESS");
        if (index < 0 || index >= works.size()) throw new BusinessException("Invalid work index");
        works.get(index).markDone();
    }

    // завершення замовлення: тільки IN_PROGRESS і всі роботи виконані
    public void completeOrder() {
        checkNotFinal();
        if (status != OrderStatus.IN_PROGRESS) throw new BusinessException("Can complete only IN_PROGRESS orders");
        for (Work w : works) {
            if (!w.isDone()) throw new BusinessException("All works must be done before completing order");
        }
        status = OrderStatus.COMPLETED;
        if (mechanic != null) mechanic.setBusy(false);
    }

    // скасування: тільки з CREATED, DIAGNOSED, APPROVED
    public void cancel() {
        checkNotFinal();
        if (!(status == OrderStatus.CREATED || status == OrderStatus.DIAGNOSED || status == OrderStatus.APPROVED))
            throw new BusinessException("Can cancel only from CREATED, DIAGNOSED or APPROVED status");
        status = OrderStatus.CANCELLED;
        if (mechanic != null) mechanic.setBusy(false);
    }

    // підсумкова вартість: сума parts + labor (без надбавок)
    public BigDecimal totalCost() {
        BigDecimal total = BigDecimal.ZERO;
        Client owner = car.getOwner();

        for (Work w : works) {
            BigDecimal parts = w.getPartsCost();
            BigDecimal labor = w.getLaborCost();

            if (owner instanceof CorporateClient corp) {
                parts = parts.subtract(parts.multiply(corp.getPartsDiscount()).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP));
                labor = labor.subtract(labor.multiply(corp.getLaborDiscount()).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP));
            }

            total = total.add(parts).add(labor);
        }
        return total;
    }

    private void checkNotFinal() {
        if (status == OrderStatus.COMPLETED || status == OrderStatus.CANCELLED)
            throw new BusinessException("Order is in final state and cannot be modified");
    }

    public String toString() {
        return "Order " + id + " [" + status + "] Car: " + car.getVin() +
                " Mechanic: " + (mechanic == null ? "None" : mechanic.getName());
    }
}
