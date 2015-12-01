package action;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by user on 01/12/2015.
 */
@WebServlet("/person")
public class showPersonInformation extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String personID = req.getParameter("id");
        req.setAttribute("personID", personID);
        //System.out.println(category);

        RequestDispatcher rd = req.getRequestDispatcher("person.jsp");
        rd.forward(req, resp);
    }

}