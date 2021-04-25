public class GUIcontext {
    private GuiAction gui;
    public GUIcontext(GuiAction newGui){
        gui=newGui;
    }
    public void showUI(){
        gui.show();
    }
}
