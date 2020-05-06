package ru.javaops.masterjava;

import org.thymeleaf.ITemplateEngine;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUploadController {
    void process(HttpServletRequest request, HttpServletResponse response,
                 ServletContext servletContext, ITemplateEngine templateEngine) throws Exception;
}
