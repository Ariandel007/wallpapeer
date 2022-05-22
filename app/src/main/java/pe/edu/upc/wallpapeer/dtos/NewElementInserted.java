package pe.edu.upc.wallpapeer.dtos;


import pe.edu.upc.wallpapeer.entities.Element;

public class NewElementInserted {
    private String a1_eventCode;
    private Element element;
    private String originalSender;

    public NewElementInserted(String a1_eventCode, Element element, String originalSender) {
        this.a1_eventCode = a1_eventCode;
        this.element = element;
        this.originalSender = originalSender;
    }

    public NewElementInserted() {}

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public void setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
    }
}
