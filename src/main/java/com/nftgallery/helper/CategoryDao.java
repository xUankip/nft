package com.nftgallery.helper;

import com.nftgallery.model.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {

    // Lấy tất cả danh mục
    public List<Category> getAllCategories() throws SQLException, ClassNotFoundException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY category_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setCategoryName(rs.getString("category_name"));

                categories.add(category);
            }
        }

        return categories;
    }

    // Lấy danh mục theo ID
    public Category getCategoryById(int id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM categories WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setCategoryName(rs.getString("category_name"));

                    return category;
                }
            }
        }

        return null;
    }

    // Thêm danh mục mới
    public boolean addCategory(Category category) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO categories (category_name) VALUES (?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, category.getCategoryName());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        category.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    // Cập nhật danh mục
    public boolean updateCategory(Category category) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE categories SET category_name = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category.getCategoryName());
            pstmt.setInt(2, category.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Xóa danh mục
    public boolean deleteCategory(int id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM categories WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // Đếm số NFT trong danh mục
    public int countNFTsInCategory(int categoryId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM nfts WHERE category_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }
}