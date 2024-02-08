package com.sdd.mapoverlay;

import javafx.fxml.FXMLLoader;

public class Store {
    private static RootController rootController;

    public static RootController getRootController() {
        return rootController;
    }

    public static void setRootController(FXMLLoader loader) {
        Store.rootController = loader.getController();
    }
}
