package com.nftgallery.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public class NFT {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private Timestamp creationDate;
    private String creator;
    private int categoryId;
    private String categoryName; // Để hiển thị tên danh mục
    private String walletAddress;
    private String status;

    // Constructor mặc định
    public NFT() {
        this.id = UUID.randomUUID().toString();
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.status = "FOR_SALE";
    }

    // Constructor đầy đủ
    public NFT(String id, String name, String description, String imageUrl, BigDecimal price,
               Timestamp creationDate, String creator, int categoryId, String walletAddress, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.creationDate = creationDate;
        this.creator = creator;
        this.categoryId = categoryId;
        this.walletAddress = walletAddress;
        this.status = status;
    }

    // Getters và Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "NFT{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", creator='" + creator + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                '}';
    }
}