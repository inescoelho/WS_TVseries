package action;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by user on 12/12/2015.
 */
@WebServlet("/search")
public class performSearch extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String searchQuery = req.getParameter("searchQuery");
        req.setAttribute("searchQuery", searchQuery);
        //System.out.println(category);

        RequestDispatcher rd = req.getRequestDispatcher("searchResult.jsp");
        rd.forward(req, resp);
    }
 }
