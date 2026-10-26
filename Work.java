package carservice;

import java.math.BigDecimal;

public class Work {
    private final String description;
    private final BigDecimal partsCost;
    private final BigDecimal laborCost;
    private boolean done = false;

    public Work(String description, BigDecimal partsCost, BigDecimal laborCost) {
        if (description == null || description.isBlank()) throw new BusinessException("Work description required");
        if (partsCost == null || partsCost.signum() < 0) throw new BusinessException("Parts cost cannot be negative");
        if (laborCost == null || laborCost.signum() < 0) throw new BusinessException("Labor cost cannot be negative");
        this.description = description;
        this.partsCost = partsCost;
        this.laborCost = laborCost;
    }

    public String getDescription() { return description; }
    public BigDecimal getPartsCost() { return partsCost; }
    public BigDecimal getLaborCost() { return laborCost; }
    public boolean isDone() { return done; }

    // позначити роботу як виконану
    public void markDone() {
        if (done) throw new BusinessException("Work already marked as done");
        done = true;
    }

    public String toString() {
        return "Work{" + description + " parts=" + partsCost + " labor=" + laborCost + " done=" + done + "}";
    }
}
