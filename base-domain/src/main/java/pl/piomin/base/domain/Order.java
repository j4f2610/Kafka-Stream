package pl.piomin.base.domain;

import lombok.Data;

@Data
public class Order {
    private Long id;
    private Long customerId;
    private Long productId;
    private Long promotionId;


    private Long pointId;
    private Long pointSpend;
    private int productCount;
    private int price;
    private String status;
    private String source;

    public Order() {
    }

    public Order(Long id, Long customerId, Long productId, Long promotionId, Long pointId, Long pointSpend, String status) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
        this.status = status;
        this.promotionId = promotionId;
        this.pointId = pointId;
        this.pointSpend = pointSpend;
    }

    public Order(Long id, Long customerId, Long productId, Long promotionId, int productCount, int price) {
        this.id = id;
        this.customerId = customerId;
        this.productId = productId;
        this.productCount = productCount;
        this.price = price;
        this.promotionId = promotionId;
        this.pointId = pointId;
        this.pointSpend = pointSpend;
        this.productCount = productCount;
    }

    public Long getPointId() {
        return pointId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }

    public Long getPointSpend() {
        return pointSpend;
    }

    public void setPointSpend(Long pointSpend) {
        this.pointSpend = pointSpend;
    }

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", productId=" + productId +
                ", promotionId=" + promotionId +
                ", productCount=" + productCount +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
