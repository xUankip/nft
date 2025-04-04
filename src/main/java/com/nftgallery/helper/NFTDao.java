package com.nftgallery.helper;

import com.nftgallery.model.NFT;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NFTDao {

    // Lấy tất cả NFT đang bán với phân trang
    public List<NFT> getAllForSaleNFTs(int page, int limit) throws SQLException, ClassNotFoundException {
        List<NFT> nfts = new ArrayList<>();
        int offset = (page - 1) * limit;

        String sql = "SELECT n.*, c.category_name FROM nfts n JOIN categories c ON n.category_id = c.id " +
                "WHERE n.status = 'FOR_SALE' ORDER BY n.creation_date DESC LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    NFT nft = new NFT();
                    nft.setId(rs.getString("id"));
                    nft.setName(rs.getString("name"));
                    nft.setDescription(rs.getString("description"));
                    nft.setImageUrl(rs.getString("image_url"));
                    nft.setPrice(rs.getBigDecimal("price"));
                    nft.setCreationDate(rs.getTimestamp("creation_date"));
                    nft.setCreator(rs.getString("creator"));
                    nft.setCategoryId(rs.getInt("category_id"));
                    nft.setCategoryName(rs.getString("category_name"));
                    nft.setWalletAddress(rs.getString("wallet_address"));
                    nft.setStatus(rs.getString("status"));

                    nfts.add(nft);
                }
            }
        }

        return nfts;
    }

    // Đếm tổng số NFT đang bán
    public int countForSaleNFTs() throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM nfts WHERE status = 'FOR_SALE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    // Thêm NFT mới
    public boolean addNFT(NFT nft) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO nfts (id, name, description, image_url, price, creation_date, creator, " +
                "category_id, wallet_address, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (nft.getId() == null || nft.getId().isEmpty()) {
                nft.setId(UUID.randomUUID().toString());
            }

            pstmt.setString(1, nft.getId());
            pstmt.setString(2, nft.getName());
            pstmt.setString(3, nft.getDescription());
            pstmt.setString(4, nft.getImageUrl());
            pstmt.setBigDecimal(5, nft.getPrice());
            pstmt.setTimestamp(6, nft.getCreationDate());
            pstmt.setString(7, nft.getCreator());
            pstmt.setInt(8, nft.getCategoryId());
            pstmt.setString(9, nft.getWalletAddress());
            pstmt.setString(10, nft.getStatus());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Cập nhật NFT
    public boolean updateNFT(NFT nft) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE nfts SET name = ?, description = ?, image_url = ?, price = ?, creator = ?, " +
                "category_id = ?, wallet_address = ?, status = ? WHERE nft_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nft.getName());
            pstmt.setString(2, nft.getDescription());
            pstmt.setString(3, nft.getImageUrl());
            pstmt.setBigDecimal(4, nft.getPrice());
            pstmt.setString(5, nft.getCreator());
            pstmt.setInt(6, nft.getCategoryId());
            pstmt.setString(7, nft.getWalletAddress());
            pstmt.setString(8, nft.getStatus());
            pstmt.setString(9, nft.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Xóa NFT
    public boolean deleteNFT(String nftId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM nfts WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nftId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Tìm NFT theo ID
    public NFT getNFTById(String nftId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT n.*, c.category_name FROM nfts n JOIN categories c ON n.category_id = c.id " +
                "WHERE n.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nftId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    NFT nft = new NFT();
                    nft.setId(rs.getString("id"));
                    nft.setName(rs.getString("name"));
                    nft.setDescription(rs.getString("description"));
                    nft.setImageUrl(rs.getString("image_url"));
                    nft.setPrice(rs.getBigDecimal("price"));
                    nft.setCreationDate(rs.getTimestamp("creation_date"));
                    nft.setCreator(rs.getString("creator"));
                    nft.setCategoryId(rs.getInt("category_id"));
                    nft.setCategoryName(rs.getString("category_name"));
                    nft.setWalletAddress(rs.getString("wallet_address"));
                    nft.setStatus(rs.getString("status"));

                    return nft;
                }
            }
        }

        return null;
    }

    // Cập nhật trạng thái NFT
    public boolean updateNFTStatus(String nftId, String newStatus) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE nfts SET status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setString(2, nftId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Tìm kiếm NFT theo tên hoặc tác giả
    public List<NFT> searchNFTs(String searchTerm, int page, int limit) throws SQLException, ClassNotFoundException {
        List<NFT> nfts = new ArrayList<>();
        int offset = (page - 1) * limit;

        String sql = "SELECT n.*, c.category_name FROM nfts n JOIN categories c ON n.category_id = c.id " +
                "WHERE (n.name LIKE ? OR n.creator LIKE ?) AND n.status = 'FOR_SALE' " +
                "ORDER BY n.creation_date DESC LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + searchTerm + "%");
            pstmt.setString(2, "%" + searchTerm + "%");
            pstmt.setInt(3, limit);
            pstmt.setInt(4, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    NFT nft = new NFT();
                    nft.setId(rs.getString("id"));
                    nft.setName(rs.getString("name"));
                    nft.setDescription(rs.getString("description"));
                    nft.setImageUrl(rs.getString("image_url"));
                    nft.setPrice(rs.getBigDecimal("price"));
                    nft.setCreationDate(rs.getTimestamp("creation_date"));
                    nft.setCreator(rs.getString("creator"));
                    nft.setCategoryId(rs.getInt("category_id"));
                    nft.setCategoryName(rs.getString("category_name"));
                    nft.setWalletAddress(rs.getString("wallet_address"));
                    nft.setStatus(rs.getString("status"));

                    nfts.add(nft);
                }
            }
        }

        return nfts;
    }

    // Đếm số lượng NFT tìm thấy
    public int countSearchResults(String searchTerm) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM nfts WHERE (name LIKE ? OR creator LIKE ?) AND status = 'FOR_SALE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + searchTerm + "%");
            pstmt.setString(2, "%" + searchTerm + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    // Lọc NFT theo danh mục
    public List<NFT> filterByCategory(int categoryId, int page, int limit) throws SQLException, ClassNotFoundException {
        List<NFT> nfts = new ArrayList<>();
        int offset = (page - 1) * limit;

        String sql = "SELECT n.*, c.category_name FROM nfts n JOIN categories c ON n.category_id = c.id " +
                "WHERE n.category_id = ? AND n.status = 'FOR_SALE' " +
                "ORDER BY n.creation_date DESC LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    NFT nft = new NFT();
                    nft.setId(rs.getString("id"));
                    nft.setName(rs.getString("name"));
                    nft.setDescription(rs.getString("description"));
                    nft.setImageUrl(rs.getString("image_url"));
                    nft.setPrice(rs.getBigDecimal("price"));
                    nft.setCreationDate(rs.getTimestamp("creation_date"));
                    nft.setCreator(rs.getString("creator"));
                    nft.setCategoryId(rs.getInt("category_id"));
                    nft.setCategoryName(rs.getString("category_name"));
                    nft.setWalletAddress(rs.getString("wallet_address"));
                    nft.setStatus(rs.getString("status"));

                    nfts.add(nft);
                }
            }
        }

        return nfts;
    }
}