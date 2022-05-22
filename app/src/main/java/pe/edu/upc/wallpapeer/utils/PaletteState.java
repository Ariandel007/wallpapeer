package pe.edu.upc.wallpapeer.utils;

public class PaletteState {
    private static PaletteState INSTANCE;

    private Integer selectedOption = 5;
    private int subOption = 0;
    private String textToPrint;
    private Integer color = -16776961;

    public String getTextToPrint() {
        return textToPrint;
    }

    public void setTextToPrint(String textToPrint) {
        this.textToPrint = textToPrint;
    }

    public PaletteState() {
    }

    public static PaletteState getInstance() {
        if(INSTANCE == null) {
//            INSTANCE = new MyLastPinch();
            //remover overhead, asi solo la primera vez se bloqueara
            //IMPORTANTE PARA THREAD SAFE
            synchronized (PaletteState.class)
            {
                if(INSTANCE==null)
                {
                    // Si la instancia es nula inicializar
                    INSTANCE = new PaletteState();
                }

            }
        }

        return INSTANCE;
    }

    public Integer getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(Integer selectedOption) {
        this.selectedOption = selectedOption;
    }

    public int getSubOption() {
        return subOption;
    }

    public void setSubOption(int subOption) {
        this.subOption = subOption;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }
}
