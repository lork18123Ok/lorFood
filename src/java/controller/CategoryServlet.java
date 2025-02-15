/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import dao.CategoryDAO;
import dao.ProductDAO;
import model.Category;
import jakarta.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;
import model.Product;

/**
 *
 * @author Truong Van Khang - CE181852
 */
public class CategoryServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet CategoryServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CategoryServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            switch (action) {
                case "delete":
                    deleteCategory(request, response);
                    break;
                case "update":
                    updateCategory(request, response);
                    break;
                case "add":
                    addNewCategory(request, response);
                    break;
                case "find":
                    findCategory(request, response);
                    break;
                case "getCate":
                    getMenuCate(request, response);
                    break;
                default:
                    showListCategory(request, response);
            }
        } catch (ServletException | IOException | SQLException e) {
            throw new ServletException(e);
        }

    }

    private void getMenuCate(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int cateid = Integer.parseInt(request.getParameter("cateid"));

        ProductDAO menuDAO = new ProductDAO();
        List<Category> listC = CategoryDAO.getAll();
        request.setAttribute("listCate", listC);

        List<Product> list = menuDAO.getProductByCateID(cateid);
        request.setAttribute("listProducts", list);
        request.getRequestDispatcher("menu.jsp").forward(request, response);
    }

    private void showListCategory(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        try {
            List<Category> list = CategoryDAO.getAll();
            for (Category category : list) {
                System.out.println(category.getCreate_at());
            }
            request.setAttribute("dataCategory", list);
            request.getRequestDispatcher("managementCategory.jsp").forward(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    private void addNewCategory(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        try {
            String CategoryName = request.getParameter("categoryName");
            Timestamp create_at = new Timestamp(System.currentTimeMillis());
            boolean Complete = false;
            if (check_Exist_Name(CategoryName) || !check_Name(CategoryName)) {
                HttpSession session = request.getSession(true);
                session.setAttribute("CateEx", "Add category Failed !! Category name already exists or invalid character.");
                showListCategory(request, response);
                return;
            }
            Complete = CategoryDAO.InsertCate(CategoryName, create_at);
            if (Complete) {
                showListCategory(request, response);
            } else {
                System.out.println("Loi");
            }

        } catch (ServletException | IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteCategory(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        try {
            boolean Complete = false;
            String Id = request.getParameter("id");
            System.out.println(Id);
            Complete = CategoryDAO.DelectById(Id);
            if (Complete) {
                System.out.println("da xoa");
                showListCategory(request, response);
            } else {
                System.out.println("Loi Delete");
            }
        } catch (ServletException | IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateCategory(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        try {
            boolean Complete = false;
            String Id = request.getParameter("categoryId");
            String CategoryName = request.getParameter("categoryName");
            if (check_Exist_Name(CategoryName) || !check_Name(CategoryName)) {
                HttpSession session = request.getSession(true);
                session.setAttribute("CateEx", "Update category Failed !! Category name already exists or invalid character.");
                System.out.println("Session ID: " + session.getId());
                System.out.println("Khang bi trung");
                showListCategory(request, response);
                return;
            }
            Timestamp create_at = new Timestamp(System.currentTimeMillis());
            Complete = CategoryDAO.UpdateCateById(Id, CategoryName, create_at);
            if (Complete) {
                showListCategory(request, response);
            } else {
                System.out.println("Loi Update");
            }
        } catch (ServletException | IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void findCategory(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        List<Category> list = null;
        try {
            String CategoryName = request.getParameter("search");
            list = CategoryDAO.FindCateByName(CategoryName);
            request.setAttribute("dataCategory", list);
            for (Category category : list) {
                System.out.println(category.getCategory_name());
            }
            request.getRequestDispatcher("managementCategory.jsp").forward(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    private boolean check_Exist_Name(String cateName) {
        List<Category> list = CategoryDAO.getAll();
        for (Category c : list) {
            if (c.getCategory_name().equalsIgnoreCase(cateName)) {
                return true;
            }
        }
        return false;
    }

    private boolean check_Name(String cateName) {
        return cateName.matches("^[a-zA-Z\\s-]{3,50}$");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
