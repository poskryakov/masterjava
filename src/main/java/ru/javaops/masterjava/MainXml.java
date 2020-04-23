package ru.javaops.masterjava;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.JaxbParser;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainXml {
    public static void main(String[] args) throws Exception {
        String projectRequested = args[0];

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
    }
}
