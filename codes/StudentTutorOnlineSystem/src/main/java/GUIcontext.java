/*
* a class that helps us follow the strategy pattern by selscting the required GuiAction subclass at runtime*/
public class GUIcontext {
    private GuiAction gui;
    public GUIcontext(GuiAction newGui){
        gui=newGui;
    }
    public void showUI(){
        gui.show();
    }
}
