package ru.javaops.masterjava;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import ru.javaops.masterjava.xml.schema.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

@WebServlet(name="xmlusers", urlPatterns={"/xmlusers"})
@MultipartConfig
public class XmlUsersServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            final InputStream xmlStream = request.getPart("xml").getInputStream();
            final InputStream projectStream = request.getPart("project").getInputStream();
            final String projectName = CharStreams.toString(new InputStreamReader(projectStream, Charsets.UTF_8));

            // Read users from XML file uploaded
            final Set<User> projectUsers = UsersService.loadUsersFromXml(projectName, xmlStream);

            // Save results as user's session attributes
            HttpSession session = request.getSession();
            session.setAttribute("project", projectName);
            session.setAttribute("users", projectUsers);

            // Redirect to results page.
            response.sendRedirect(getServletContext().getContextPath() + "/users");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
