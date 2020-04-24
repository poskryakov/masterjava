package ru.javaops.masterjava;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;
import ru.javaops.masterjava.xml.util.XsltProcessor;

import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainXml {
    public static void main(String[] args) throws Exception {
        String projectRequested = args[0];

        System.out.println("\nJAXB solution");

        final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);
        Payload payload = JAXB_PARSER.unmarshal(
                Resources.getResource("payload.xml").openStream());

        Project projectFound = payload.getProject().stream()
                .filter(project -> project.getName().equalsIgnoreCase(projectRequested))
                .findAny()
                .orElse(null);
        List<User> usersFound = projectFound.getGroup().stream()
                .flatMap(group -> group.getUser().stream())
                .distinct()
                .collect(Collectors.toList());
        usersFound.sort(Comparator.comparing(User::getFullName));
        usersFound.forEach(System.out::println);

        System.out.println("\nStAX solution");
        try (StaxStreamProcessor processor =
                     new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())) {
            usersFound = new ArrayList<>();
            while(processor.doUntil(XMLEvent.START_ELEMENT, "Project")) {
                if(processor.getAttribute("name").equalsIgnoreCase(projectRequested)) {
                    while (processor.startElement("User", "Project")) {
                        User userFound = new User();
                        userFound.setFullName(processor.getAttribute("fullName"));
                        userFound.setEmail(processor.getAttribute("email"));
                        usersFound.add(userFound);
                   }
                }
            }
            usersFound = usersFound.stream()
                    .distinct()
                    .collect(Collectors.toList());
            usersFound.sort(Comparator.comparing(User::getFullName));
            usersFound.forEach(
                    user -> System.out.printf("%s/%s%n", user.getFullName(), user.getEmail())
            );
        }

        System.out.println("\nUsers as HTML table");
        String usersTable = "<table>" + usersFound.stream()
                .map(user -> "<tr><td>" + user.getFullName() + "</td>" + "<td>" + user.getEmail() + "</td></tr>")
                .collect(Collectors.joining()) +
                "</table>";
        System.out.println(usersTable);

        System.out.println("\nGroups as HTML table (XSLT Solution)");
        try (InputStream xslInputStream = Resources.getResource("groups.xsl").openStream();
             InputStream xmlInputStream = Resources.getResource("payload.xml").openStream()) {

            XsltProcessor processor = new XsltProcessor(xslInputStream);
            processor.setParameter("project_name", projectRequested);
            System.out.println(processor.transform(xmlInputStream));
        }
    }
}
