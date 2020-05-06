package ru.javaops.masterjava;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.xml.schema.User;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Set;

public class UsersController implements IUploadController {

    public UsersController() {
        super();
    }

    @Override
    public void process(
            HttpServletRequest request, HttpServletResponse response,
            ServletContext servletContext, ITemplateEngine templateEngine)
            throws Exception {

        HttpSession session = request.getSession();
        final String projectName = (String) session.getAttribute("project");
        final Set<User> projectUsers = (Set<User>) session.getAttribute("users");

        final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        ctx.setVariable("project", projectName);
        ctx.setVariable("users", projectUsers);

        templateEngine.process("users", ctx, response.getWriter());
    }
}
