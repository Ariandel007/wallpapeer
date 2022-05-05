package pe.edu.upc.wallpapeer.dtos;


import pe.edu.upc.wallpapeer.entities.Element;

public class NewElementInserted {
    private String a1_eventCode;
    private Element element;

    public NewElementInserted(String a1_eventCode, Element element) {
        this.a1_eventCode = a1_eventCode;
        this.element = element;
    }

    public NewElementInserted() {}

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }


}
