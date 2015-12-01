package action;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by user on 30/11/2015.
 */
@WebServlet("/listSeries")
public class listSeriesFromCategory extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String category = req.getParameter("category");
        req.setAttribute("category", category);
        System.out.println(category);

        RequestDispatcher rd = req.getRequestDispatcher("listSeries.jsp");
        rd.forward(req, resp);
    }
}
