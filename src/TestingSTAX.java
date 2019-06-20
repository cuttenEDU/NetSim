import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;

public class TestingSTAX {
    public static void main(String[] args) {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader eventReader =
                    factory.createXMLEventReader(new FileReader("test.xml"));
            while (eventReader.hasNext()){
                XMLEvent event = eventReader.nextEvent();
                switch (event.getEventType()){
                    case XMLStreamConstants.START_ELEMENT:
                        System.out.println(event.asStartElement().getName().getLocalPart() + "start");
                        Iterator<Attribute> attributes = event.asStartElement().getAttributes();
                        while (attributes.hasNext())
                            System.out.println(attributes.next());
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        System.out.println(event.asEndElement().getName().getLocalPart() + "end");
                        break;
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
}
