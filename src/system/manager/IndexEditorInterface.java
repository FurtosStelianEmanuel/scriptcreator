/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system.manager;

import java.io.IOException;
import java.util.List;
import tools.PersonEntity;

/**
 *
 * @author Manel
 */
public interface IndexEditorInterface {

    void loadIndex() throws IOException, ClassNotFoundException;

    void saveIndex() throws IOException, ClassNotFoundException;

    void startGui(Object monitor);

    void pacientSelected(int index);

    void updateTable(List<PersonEntity> index);

    void addPacient();
    
    void exportJson();
    
    void changePacientsRootDirectory();
    
    void minimizeImages();
}
