package ru.javaops.masterjava;

import com.google.common.base.Splitter;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.*;

import static com.google.common.base.Strings.nullToEmpty;

public class UsersService {

    private static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getValue).thenComparing(User::getEmail);

    public static Set<User> loadUsersFromXml(String projectName, InputStream xml) throws Exception {
        StaxStreamProcessor processor = new StaxStreamProcessor(xml);
        final Set<String> groupNames = new HashSet<>();

        // Projects loop
        while (processor.startElement("Project", "Projects")) {
            if (projectName.equals(processor.getAttribute("name"))) {
                while (processor.startElement("Group", "Project")) {
                    groupNames.add(processor.getAttribute("name"));
                }
                break;
            }
        }
        if (groupNames.isEmpty()) {
            throw new IllegalArgumentException("Invalid " + projectName + " or no groups");
        }

        // Users loop
        Set<User> users = new TreeSet<>(USER_COMPARATOR);

        JaxbParser parser = new JaxbParser(User.class);
        while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
            String groupRefs = processor.getAttribute("groupRefs");
            if (!Collections.disjoint(groupNames, Splitter.on(' ').splitToList(nullToEmpty(groupRefs)))) {
                User user = parser.unmarshal(processor.getReader(), User.class);
                users.add(user);
            }
        }
        return users;
    }
}
