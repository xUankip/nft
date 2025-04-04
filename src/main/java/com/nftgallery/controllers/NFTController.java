package com.nftgallery.controllers;

import com.nftgallery.model.NFT;
import com.nftgallery.model.Category;
import com.nftgallery.helper.NFTDao;
import com.nftgallery.helper.CategoryDao;
import com.nftgallery.utils.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@WebServlet("/nft/*")
public class NFTController extends HttpServlet {
    private NFTDao nftDao;
    private CategoryDao categoryDao;

    @Override
    public void init() {
        nftDao = new NFTDao();
        categoryDao = new CategoryDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            listNFTs(request, response);
        } else if (pathInfo.equals("/create")) {
            showCreateForm(request, response);
        } else if (pathInfo.startsWith("/edit/")) {
            showEditForm(request, response);
        } else if (pathInfo.startsWith("/delete/")) {
            deleteNFT(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            viewNFT(request, response);
        } else if (pathInfo.equals("/search")) {
            searchNFTs(request, response);
        } else if (pathInfo.equals("/filter")) {
            filterNFTs(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            listNFTs(request, response);
        } else if (pathInfo.equals("/create")) {
            createNFT(request, response);
        } else if (pathInfo.startsWith("/edit/")) {
            updateNFT(request, response);
        } else if (pathInfo.startsWith("/status/")) {
            updateStatus(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listNFTs(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Xử lý phân trang
            int page = 1;
            int limit = 10;

            String pageStr = request.getParameter("page");
            String limitStr = request.getParameter("limit");

            if (pageStr != null && !pageStr.isEmpty()) {
                page = Integer.parseInt(pageStr);
            }

            if (limitStr != null && !limitStr.isEmpty()) {
                limit = Integer.parseInt(limitStr);
            }

            // Lấy dữ liệu NFT
            List<NFT> nfts = nftDao.getAllForSaleNFTs(page, limit);
            int totalNFTs = nftDao.countForSaleNFTs();
            int totalPages = (int) Math.ceil((double) totalNFTs / limit);

            // Lấy danh sách danh mục để hiển thị bộ lọc
            List<Category> categories = categoryDao.getAllCategories();

            // Đặt các thuộc tính vào request
            request.setAttribute("nfts", nfts);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("categories", categories);

            // Chuyển hướng đến trang danh sách
            request.getRequestDispatcher("/WEB-INF/views/nft/list.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi khi truy xuất dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy danh sách danh mục để hiển thị trong form
            List<Category> categories = categoryDao.getAllCategories();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/WEB-INF/views/nft/create.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi khi truy xuất dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void createNFT(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy dữ liệu từ form
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String imageUrl = request.getParameter("imageUrl");
        String priceStr = request.getParameter("price");
        String creator = request.getParameter("creator");
        String categoryIdStr = request.getParameter("categoryId");
        String walletAddress = request.getParameter("walletAddress");
        String status = request.getParameter("status");

        // Validate dữ liệu
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        if (name == null || name.length() < 5) {
            isValid = false;
            errors.append("Tên NFT không được trống và phải dài hơn 5 ký tự. ");
        }

        if (creator == null || creator.isEmpty()) {
            isValid = false;
            errors.append("Người tạo không được trống. ");
        }

        if (!ValidationUtil.isValidImageUrl(imageUrl)) {
            isValid = false;
            errors.append("URL ảnh không hợp lệ. ");
        }

        BigDecimal price = null;
        try {
            price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                isValid = false;
                errors.append("Giá phải lớn hơn 0. ");
            }
        } catch (NumberFormatException e) {
            isValid = false;
            errors.append("Giá không hợp lệ. ");
        }

        int categoryId = 0;
        try {
            categoryId = Integer.parseInt(categoryIdStr);
        } catch (NumberFormatException e) {
            isValid = false;
            errors.append("Danh mục không hợp lệ. ");
        }

        if (!ValidationUtil.isValidEthAddress(walletAddress)) {
            isValid = false;
            errors.append("Địa chỉ ví không hợp lệ. ");
        }

        if (!isValid) {
            request.setAttribute("errorMessage", errors.toString());
            showCreateForm(request, response);
            return;
        }

        // Tạo đối tượng NFT mới
        NFT nft = new NFT();
        nft.setName(name);
        nft.setDescription(description);
        nft.setImageUrl(imageUrl);
        nft.setPrice(price);
        nft.setCreator(creator);
        nft.setCategoryId(categoryId);
        nft.setWalletAddress(walletAddress);
        nft.setStatus(status != null && !status.isEmpty() ? status : "FOR_SALE");
        nft.setCreationDate(new Timestamp(System.currentTimeMillis()));

        try {
            // Lưu NFT vào cơ sở dữ liệu
            boolean success = nftDao.addNFT(nft);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/nft/");
            } else {
                request.setAttribute("errorMessage", "Không thể thêm NFT. Vui lòng thử lại.");
                showCreateForm(request, response);
            }
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi khi lưu dữ liệu: " + e.getMessage());
            showCreateForm(request, response);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String nftId = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);

        try {
            // Lấy thông tin NFT cần sửa
            NFT nft = nftDao.getNFTById(nftId);
            if (nft != null) {
                // Lấy danh sách danh mục
                List<Category> categories = categoryDao.getAllCategories();
                request.setAttribute("nft", nft);
                request.setAttribute("categories", categories);
                request.getRequestDispatcher("/WEB-INF/views/nft/edit.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Không tìm thấy NFT với ID: " + nftId);
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi khi truy xuất dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void updateNFT(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String nftId = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);

        // Lấy dữ liệu từ form
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String imageUrl = request.getParameter("imageUrl");
        String priceStr = request.getParameter("price");
        String creator = request.getParameter("creator");
        String categoryIdStr = request.getParameter("categoryId");
        String walletAddress = request.getParameter("walletAddress");
        String status = request.getParameter("status");

        // Validate dữ liệu
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        if (name == null || name.length() < 5) {
            isValid = false;
            errors.append("Tên NFT không được trống và phải dài hơn 5 ký tự. ");
        }

        if (creator == null || creator.isEmpty()) {
            isValid = false;
            errors.append("Người tạo không được trống. ");
        }

        if (!ValidationUtil.isValidImageUrl(imageUrl)) {
            isValid = false;
            errors.append("URL ảnh không hợp lệ. ");
        }

        BigDecimal price = null;
        try {
            price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                isValid = false;
                errors.append("Giá phải lớn hơn 0. ");
            }
        } catch (NumberFormatException e) {
            isValid = false;
            errors.append("Giá không hợp lệ. ");
        }

        int categoryId = 0;
        try {
            categoryId = Integer.parseInt(categoryIdStr);
        } catch (NumberFormatException e) {
            isValid = false;
            errors.append("Danh mục không hợp lệ. ");
        }

        if (!ValidationUtil.isValidEthAddress(walletAddress)) {
            isValid = false;
            errors.append("Địa chỉ ví không hợp lệ. ");
        }

        if (!isValid) {
            request.setAttribute("errorMessage", errors.toString());
            showEditForm(request, response);
            return;
        }

        try {
            // Lấy thông tin NFT hiện tại
            NFT existingNFT = nftDao.getNFTById(nftId);
            if (existingNFT == null) {
                request.setAttribute("errorMessage", "Không tìm thấy NFT với ID: " + nftId);
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }

            // Cập nhật thông tin NFT
            existingNFT.setName(name);
            existingNFT.setDescription(description);
            existingNFT.setImageUrl(imageUrl);
            existingNFT.setPrice(price);
            existingNFT.setCreator(creator);
            existingNFT.setCategoryId(categoryId);
            existingNFT.setWalletAddress(walletAddress);
            existingNFT.setStatus(status);

            // Lưu thay đổi vào cơ sở dữ liệu
            boolean success = nftDao.updateNFT(existingNFT);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/nft/");
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật NFT. Vui lòng thử lại.");
                showEditForm(request, response);
            }
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi khi cập nhật dữ liệu: " + e.getMessage());
            showEditForm(request, response);
        }
    }

    private void deleteNFT(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String nftId = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);

        try {
            // Kiểm tra sự tồn tại của NFT
            NFT nft = nftDao.getNFTById(nftId);
            if (nft == null) {
                request.setAttribute("errorMessage", "Không tìm thấy NFT với ID: " + nftId);
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }

            // Xóa NFT
            boolean success = nftDao.deleteNFT(nftId);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/nft/");
            } else {
                request.setAttribute("errorMessage", "Không thể xóa NFT. Vui lòng thử lại.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi khi xóa dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void viewNFT(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String nftId = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);

        try {
            // Lấy thông tin chi tiết NFT
            NFT nft = nftDao.getNFTById(nftId);
            if (nft != null) {
                request.setAttribute("nft", nft);
                request.getRequestDispatcher("/WEB-INF/views/nft/view.jsp").forward(request, response);
            } else {
                request.setAttribute("errorMessage", "Không tìm thấy NFT với ID: " + nftId);
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi khi truy xuất dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void searchNFTs(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy tham số tìm kiếm
            String searchTerm = request.getParameter("searchTerm");

            // Xử lý phân trang
            int page = 1;
            int limit = 10;

            String pageStr = request.getParameter("page");
            String limitStr = request.getParameter("limit");

            if (pageStr != null && !pageStr.isEmpty()) {
                page = Integer.parseInt(pageStr);
            }

            if (limitStr != null && !limitStr.isEmpty()) {
                limit = Integer.parseInt(limitStr);
            }

            List<NFT> nfts;
            int totalNFTs;
            int totalPages;

            if (searchTerm != null && !searchTerm.isEmpty()) {
                // Tìm kiếm NFT theo tên hoặc tác giả
                nfts = nftDao.searchNFTs(searchTerm, page, limit);
                totalNFTs = nftDao.countSearchResults(searchTerm);
            } else {
                // Nếu không có từ khóa tìm kiếm, hiển thị tất cả
                nfts = nftDao.getAllForSaleNFTs(page, limit);
                totalNFTs = nftDao.countForSaleNFTs();
            }

            totalPages = (int) Math.ceil((double) totalNFTs / limit);

            // Lấy danh sách danh mục để hiển thị bộ lọc
            List<Category> categories = categoryDao.getAllCategories();

            // Đặt thuộc tính vào request
            request.setAttribute("nfts", nfts);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("categories", categories);

            request.getRequestDispatcher("/WEB-INF/views/nft/list.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi khi tìm kiếm dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void filterNFTs(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy tham số lọc
            String categoryIdStr = request.getParameter("categoryId");

            // Xử lý phân trang
            int page = 1;
            int limit = 10;

            String pageStr = request.getParameter("page");
            String limitStr = request.getParameter("limit");

            if (pageStr != null && !pageStr.isEmpty()) {
                page = Integer.parseInt(pageStr);
            }

            if (limitStr != null && !limitStr.isEmpty()) {
                limit = Integer.parseInt(limitStr);
            }

            List<NFT> nfts;
            int totalNFTs;
            int totalPages;

            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                // Lọc NFT theo danh mục
                int categoryId = Integer.parseInt(categoryIdStr);
                nfts = nftDao.filterByCategory(categoryId, page, limit);
                // Đếm tổng số NFT theo danh mục (giả sử có phương thức countByCategoryId)
                totalNFTs = nftDao.countForSaleNFTs(); // Tạm thời sử dụng phương thức đếm tất cả
            } else {
                // Nếu không có điều kiện lọc, hiển thị tất cả
                nfts = nftDao.getAllForSaleNFTs(page, limit);
                totalNFTs = nftDao.countForSaleNFTs();
            }

            totalPages = (int) Math.ceil((double) totalNFTs / limit);

            // Lấy danh sách danh mục để hiển thị bộ lọc
            List<Category> categories = categoryDao.getAllCategories();

            // Đặt thuộc tính vào request
            request.setAttribute("nfts", nfts);
            request.setAttribute("selectedCategoryId", categoryIdStr);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("categories", categories);

            request.getRequestDispatcher("/WEB-INF/views/nft/list.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi khi lọc dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void updateStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String nftId = pathInfo.substring(pathInfo.lastIndexOf("/") + 1);
        String newStatus = request.getParameter("status");

        try {
            // Kiểm tra sự tồn tại của NFT
            NFT nft = nftDao.getNFTById(nftId);
            if (nft == null) {
                request.setAttribute("errorMessage", "Không tìm thấy NFT với ID: " + nftId);
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }

            // Cập nhật trạng thái
            boolean success = nftDao.updateNFTStatus(nftId, newStatus);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/nft/");
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật trạng thái NFT. Vui lòng thử lại.");
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
        } catch (SQLException | ClassNotFoundException e) {
            request.setAttribute("errorMessage", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}