module com.olim.employeemanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
        requires javafx.web;
            
        requires org.controlsfx.controls;
                requires net.synedra.validatorfx;
            requires org.kordamp.ikonli.javafx;
            requires org.kordamp.bootstrapfx.core;
            requires eu.hansolo.tilesfx;

    opens com.olim.employeemanagementsystem to javafx.fxml;
    opens com.olim.employeemanagementsystem.view to javafx.fxml;
    opens com.olim.employeemanagementsystem.model to javafx.base;

    exports com.olim.employeemanagementsystem;
    exports com.olim.employeemanagementsystem.view;
    exports com.olim.employeemanagementsystem.exception;
    opens com.olim.employeemanagementsystem.exception to javafx.fxml;
}