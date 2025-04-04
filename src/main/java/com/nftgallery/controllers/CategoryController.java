package com.nftgallery.controllers;

import com.nftgallery.model.Category;
import com.nftgallery.helper.CategoryDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/category/*")
public class CategoryController extends HttpServlet {
    private CategoryDao categoryDao;

    @Override
    public void init() {
        categoryDao = new CategoryDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            listCategories(request, response);
        } else if (pathInfo.equals("/create")) {
            showCreateForm(request, response);
        } else if (pathInfo.startsWith("/edit/")) {
            showEditForm(request, response);
        } else if (pathInfo.startsWith("/delete/")) {
            deleteCategory(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            listCategories(request, response);
        } else if (pathInfo.equals("/create")) {
            createCategory(request, response);
        } else if (pathInfo.startsWith("/edit/")) {
            updateCategory(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void listCategories(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Category> categories = categoryDao.getAllCategories();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/views/category/list.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/category/create.jsp").forward(request, response);
    }

    private void createCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String categoryName = request.getParameter("categoryName");

        if (categoryName == null || categoryName.trim().isEmpty()) {
            request.setAttribute("error", "Category name cannot be empty");
            request.getRequestDispatcher("/views/category/create.jsp").forward(request, response);
            return;
        }

        try {
            Category category = new Category();
            category.setCategoryName(categoryName);

            // Add category to database
            categoryDao.addCategory(category);

            response.sendRedirect(request.getContextPath() + "/category");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/views/category/create.jsp").forward(request, response);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getPathInfo().substring(6));
            Category category = categoryDao.getCategoryById(id);

            if (category != null) {
                request.setAttribute("category", category);
                request.getRequestDispatcher("/views/category/edit.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Category not found");
            }
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        }
    }

    private void updateCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getPathInfo().substring(6));
            String categoryName = request.getParameter("categoryName");

            if (categoryName == null || categoryName.trim().isEmpty()) {
                request.setAttribute("error", "Category name cannot be empty");
                showEditForm(request, response);
                return;
            }

            Category category = new Category(id, categoryName);

            categoryDao.updateCategory(category);
            response.sendRedirect(request.getContextPath() + "/category");
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        }
    }

    private void deleteCategory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getPathInfo().substring(8));

            // Delete category
            categoryDao.deleteCategory(id);

            response.sendRedirect(request.getContextPath() + "/category");
        } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/views/error.jsp").forward(request, response);
        }
    }
}