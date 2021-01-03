/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.json.simple.JSONArray;
import tools.Action;
import tools.PersonEntity;

/**
 *
 * @author Manel
 */
public interface ActionManagerInterface {

    public void serializeScript(List<Action> actions, Path path, String fileName) throws IOException;

    List<Action> getScript(Path path, String fileName) throws IOException, ClassNotFoundException;

    void addToIndex(PersonEntity entity, Path path, String fileName) throws IOException, ClassNotFoundException;

    void setIndex(List<PersonEntity> index, Path path, String fileName) throws IOException, ClassNotFoundException;

    List<PersonEntity> getIndex(Path path, String fileName) throws IOException, ClassNotFoundException, FileNotFoundException;

    void exportJsonArray(JSONArray array, Path path, String fileName) throws IOException, ClassNotFoundException;

    void minimizeImages(File[] selectedDirectories);
}
