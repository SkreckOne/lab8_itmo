package org.lab6.collection;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import common.console.Console;
import common.models.Organization;


import javax.xml.stream.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.util.PriorityQueue;


//Чтение данных из файла необходимо реализовать с помощью класса java.io.InputStreamReader
//Запись данных в файл необходимо реализовать с помощью класса java.io.BufferedOutputStream
public class DumpManager {

    private final String fileName;
    private final Console console;

    public DumpManager(String filename, Console console) {
        this.fileName = filename;
        this.console = console;
    }

    public void writeCollection(PriorityQueue<Organization> collection) {
        try {
            XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
            xmlOutputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
            XMLStreamWriter sw = xmlOutputFactory.createXMLStreamWriter(out);
            sw.setDefaultNamespace("");
            XmlMapper mapper = new XmlMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT); //enable beautifying

            sw.writeStartDocument();
            sw.writeCharacters("\n");
            sw.writeStartElement("root");
            sw.writeCharacters("\n");
            for (Organization element : collection) {
                mapper.writeValue(sw, element);
                sw.writeCharacters("\n");
            }
            sw.writeEndElement();
            sw.writeEndDocument();

            sw.close();
            out.close();
        } catch (XMLStreamException | IOException e) {
            console.printError(e.getMessage());
        }
    }

    public void readCollection(PriorityQueue<Organization> collection) {
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
            XMLStreamReader sr = xmlInputFactory.createXMLStreamReader(new InputStreamReader(new FileInputStream(fileName)));

            XmlMapper mapper = new XmlMapper();
            sr.next();
            while (sr.getEventType() != XMLStreamConstants.END_DOCUMENT) {
                if (sr.isStartElement() && sr.getLocalName().equals("Organization")) {
                    try {
                        Organization element = mapper.readValue(sr, Organization.class);
                        if (element.validate()) {
                            collection.add(element);
                        } else {
                            console.printError("Element failed to validate. Skip...\n");
                        }
                    } catch (com.fasterxml.jackson.core.JsonParseException e) {
                        console.printError("Error parsing JSON: " + e.getMessage());
                    }
                }
                sr.next();
            }
            sr.close();
        } catch (XMLStreamException e) {
            console.printError("Error reading XML stream: " + e.getMessage());
        } catch (IOException e) {
            console.printError("Error accessing file: " + e.getMessage());
        }
    }
}