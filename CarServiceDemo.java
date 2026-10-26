package carservice;

import java.math.BigDecimal;

//всі сценарії
public class CarServiceDemo {
    public static void main(String[] args) {
        CarService svc = new CarService();

        // 1) реєстрація клієнта й автомобіля
        Client alice = new Client("c1", "Alice");
        svc.registerClient(alice);
        Car car1 = new Car("VIN123", "Toyota", "Corolla", 2015, alice);
        svc.registerCar(car1);
        System.out.println("Registered client and car: " + alice + " / " + car1);

        // реєструємо механіків
        Mechanic m1 = new Mechanic("m1", "Ivan", Specialization.ENGINE);
        Mechanic m2 = new Mechanic("m2", "Petro", Specialization.ELECTRICS);
        svc.registerMechanic(m1);
        svc.registerMechanic(m2);

        // 2) створення замовлення, додавання двох робіт, погодження, призначення механіка, виконання і завершення
        try {
            Order o1 = svc.createOrderFor("o1", "VIN123");
            o1.addWork(new Work("Replace brake pads", new BigDecimal("50.00"), new BigDecimal("30.00")));
            o1.addWork(new Work("Oil change", new BigDecimal("20.00"), new BigDecimal("15.00")));
            System.out.println("\nOrder created: " + o1);
            o1.diagnose();
            System.out.println("After diagnose: " + o1.getStatus());
            o1.approve();
            System.out.println("After approve: " + o1.getStatus());
            o1.assignMechanic(m1);
            System.out.println("Mechanic assigned: " + o1.getMechanic());
            o1.start();
            System.out.println("Order started: " + o1.getStatus());
            o1.completeWork(0);
            o1.completeWork(1);
            System.out.println("Works after completion:");
            o1.getWorks().forEach(w -> System.out.println("  " + w));
            o1.completeOrder();
            System.out.println("Order final: " + o1.getStatus());
            System.out.println("Total cost: " + o1.totalCost());
        } catch (BusinessException ex) {
            System.out.println("Unexpected error in scenario 2: " + ex.getMessage());
        }

        // 3) спроба погодити порожнє замовлення
        try {
            Order o2 = svc.createOrderFor("o2", "VIN123");
            System.out.println("\nAttempt to approve empty order o2");
            o2.approve();
        } catch (BusinessException ex) {
            System.out.println("Expected error: " + ex.getMessage());
        }

        // 4) спроба запустити замовлення без механіка
        try {
            Order o3 = svc.createOrderFor("o3", "VIN123");
            o3.addWork(new Work("Check lights", BigDecimal.ZERO, new BigDecimal("10")));
            o3.diagnose();
            o3.approve();
            System.out.println("\nAttempt to start o3 without mechanic");
            o3.start();
        } catch (BusinessException ex) {
            System.out.println("Expected error: " + ex.getMessage());
        }

        // 5) спроба призначити зайнятого механіка іншому активному замовленню
        try {
            Order o4 = svc.createOrderFor("o4", "VIN123");
            o4.addWork(new Work("Engine tune", BigDecimal.ZERO, new BigDecimal("100")));
            o4.diagnose();
            o4.approve();
            o4.assignMechanic(m2); // призначили m2
            o4.start();
            Order o5 = svc.createOrderFor("o5", "VIN123");
            o5.addWork(new Work("Battery check", BigDecimal.ZERO, new BigDecimal("20")));
            o5.diagnose();
            o5.approve();
            System.out.println("\nAttempt to assign busy mechanic to o5");
            o5.assignMechanic(m2);
        } catch (BusinessException ex) {
            System.out.println("Expected error: " + ex.getMessage());
        }

        // 6) спроба змінити завершене замовлення
        try {
            // створимо і завершимо замовлення
            Order o6 = svc.createOrderFor("o6", "VIN123");
            o6.addWork(new Work("Simple check", BigDecimal.ZERO, new BigDecimal("5")));
            o6.diagnose();
            o6.approve();
            o6.assignMechanic(m1);
            o6.start();
            o6.completeWork(0);
            o6.completeOrder();
            System.out.println("\nAttempt to add work to completed order o6");
            o6.addWork(new Work("Extra", BigDecimal.ZERO, new BigDecimal("1")));
        } catch (BusinessException ex) {
            System.out.println("Expected error: " + ex.getMessage());
        }

        // 7) спроба виконати заборонений перехід між статусами
        try {
            Order o7 = svc.createOrderFor("o7", "VIN123");
            System.out.println("\nAttempt forbidden transition: directly start from CREATED");
            o7.start(); // не можна стартувати з CREATED
        } catch (BusinessException ex) {
            System.out.println("Expected error: " + ex.getMessage());
        }

        // 8) демонстрація корпоративного клієнта зі знижками
        try {
            CorporateClient corpClient = new CorporateClient("c2", "Taxi Service", new BigDecimal("10"), new BigDecimal("20"));
            svc.registerClient(corpClient);
            Car corpCar = new Car("VIN777", "Skoda", "Rapid", 2021, corpClient);
            svc.registerCar(corpCar);

            Order o8 = svc.createOrderFor("o8", "VIN777");
            o8.addWork(new Work("Engine fix", new BigDecimal("100.00"), new BigDecimal("50.00")));
            o8.diagnose();
            o8.approve();
            o8.assignMechanic(m1);
            o8.start();
            o8.completeWork(0);
            o8.completeOrder();

            System.out.println("\nCorporate Order total (Parts -10%, Labor -20%): " + o8.totalCost());
        } catch (BusinessException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        System.out.println("\nDemo finished.");
    }
}