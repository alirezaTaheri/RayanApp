package rayan.rayanapp.Menu;

public class DrawerItem {
    String name;
    int imgResID;

    public DrawerItem(String name, int imgResID) {
        this.name = name;
        this.imgResID = imgResID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgResID() {
        return imgResID;
    }

    public void setImgResID(int imgResID) {
        this.imgResID = imgResID;
    }
}
