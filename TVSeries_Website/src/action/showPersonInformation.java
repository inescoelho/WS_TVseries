package action;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by user on 01/12/2015.
 */
@WebServlet("/actor")
public class showPersonInformation extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       /* String seriesID = req.getParameter("id");
        req.setAttribute("seriesID", seriesID);
        //System.out.println(category);

        RequestDispatcher rd = req.getRequestDispatcher("series.jsp");
        rd.forward(req, resp);*/
    }

}