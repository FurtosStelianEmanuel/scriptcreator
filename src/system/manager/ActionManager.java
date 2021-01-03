/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.simple.JSONArray;
import system.resource.ActionResource;
import tools.Action;
import tools.PersonEntity;

/**
 *
 * @author Manel
 */
public class ActionManager implements ActionManagerInterface {

    private final ActionResource resource;

    public ActionManager(ActionResource resource) {
        this.resource = resource;
    }

    @Override
    public void serializeScript(List<Action> actions, Path path, String fileName) throws IOException {
        resource.serializeScript(actions, path, fileName);
    }

    @Override
    public List<Action> getScript(Path path, String fileName) throws IOException, ClassNotFoundException {
        List<Action> script = resource.getScript(path, fileName);
        return script;
    }

    @Override
    public void addToIndex(PersonEntity person, Path path, String fileName) throws ClassNotFoundException, IOException {
        resource.addToIndex(person, path, fileName);
    }

    @Override
    public List<PersonEntity> getIndex(Path path, String fileName) throws IOException, ClassNotFoundException {
        return resource.getIndex(path, fileName);
    }

    public List<PersonEntity> getIndex(Path path) throws IOException, ClassNotFoundException {
        return resource.getIndex(path);
    }

    @Override
    public void setIndex(List<PersonEntity> index, Path path, String fileName) throws IOException, ClassNotFoundException {
        resource.setIndex(index, path, fileName);
    }

    @Override
    public void exportJsonArray(JSONArray array, Path path, String fileName) throws IOException, ClassNotFoundException {
        resource.exportJsonArray(array, path, fileName);
    }

    @Override
    public void minimizeImages(File[] selectedDirectories) {
        List<File> images = new ArrayList<>();
        for (File imageDirectory : selectedDirectories) {
            images.addAll(Arrays.asList(imageDirectory.listFiles()));           
        }
        resource.minimizeImages(images, 0.7, 0.5);
    }

}
