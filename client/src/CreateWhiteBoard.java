public class CreateWhiteBoard {
    private final Gui.Usage usage;
    private Gui gui;

    public CreateWhiteBoard() {
        usage = Gui.Usage.MANAGER;
        init();
    }

    private void init() {
        gui = new Gui(usage);
    }

    /* Main Function */
    public static void main(String[] args) {
        CreateWhiteBoard cwb = new CreateWhiteBoard();
    }
}
