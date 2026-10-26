package carservice;

import java.math.BigDecimal;

public class CorporateClient extends Client {
    private final BigDecimal partsDiscount;
    private final BigDecimal laborDiscount;

    public CorporateClient(String id, String name, BigDecimal partsDiscount, BigDecimal laborDiscount) {
        super(id, name);
        this.partsDiscount = partsDiscount;
        this.laborDiscount = laborDiscount;
    }

    public BigDecimal getPartsDiscount() { return partsDiscount; }
    public BigDecimal getLaborDiscount() { return laborDiscount; }
}