/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import tools.Action;
import tools.PersonEntity;

/**
 *
 * @author Manel
 */
public class ActionResource implements ActionResourceInterface {

    @Override
    public void serializeScript(List<Action> actions, Path path, String fileName) throws IOException {
        //<editor-fold desc="body" defaultstate="collapsed">
        try (FileOutputStream file
                = new FileOutputStream(
                        Paths.get(path.toString(), fileName).toString()
                ); ObjectOutputStream out = new ObjectOutputStream(file)) {
            out.writeObject(actions);
        }
        //</editor-fold>
    }

    @Override
    public List<Action> getScript(Path path, String fileName) throws IOException, ClassNotFoundException {
        try (FileInputStream file
                = new FileInputStream(
                        Paths.get(path.toString(), fileName).toString()
                ); ObjectInputStream in = new ObjectInputStream(file)) {
            return (List<Action>) in.readObject();
        }
    }

    @Override
    public void addToIndex(PersonEntity entity, Path path, String fileName) throws IOException, ClassNotFoundException {
        File checkExists = Paths.get(path.toString(), fileName).toFile();
        if (!checkExists.exists()) {
            checkExists.createNewFile();
            try (FileOutputStream outFile
                    = new FileOutputStream(
                            Paths.get(path.toString(), fileName).toString()
                    ); ObjectOutputStream out = new ObjectOutputStream(outFile)) {
                out.writeObject(new ArrayList<>());
                System.out.println("Index nou");
            }
        }
        try (FileInputStream file
                = new FileInputStream(
                        Paths.get(path.toString(), fileName).toString()
                ); ObjectInputStream in = new ObjectInputStream(file)) {
            List<PersonEntity> index = (List<PersonEntity>) in.readObject();
            index.add(entity);
            try (FileOutputStream outFile
                    = new FileOutputStream(
                            Paths.get(path.toString(), fileName).toString()
                    ); ObjectOutputStream out = new ObjectOutputStream(outFile)) {
                out.writeObject(index);
            }
            System.out.println("Adaugat in index " + entity.toString());
        }
    }

    @Override
    public List<PersonEntity> getIndex(Path path, String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        try (FileInputStream file
                = new FileInputStream(
                        Paths.get(path.toString(), fileName).toString()
                ); ObjectInputStream in = new ObjectInputStream(file)) {
            return (List<PersonEntity>) in.readObject();
        }
    }

    public List<PersonEntity> getIndex(Path path) throws FileNotFoundException, IOException, ClassNotFoundException {
        try (FileInputStream file
                = new FileInputStream(
                        Paths.get(path.toString()).toString()
                ); ObjectInputStream in = new ObjectInputStream(file)) {
            return (List<PersonEntity>) in.readObject();
        }
    }

    @Override
    public void setIndex(List<PersonEntity> index, Path path, String fileName) throws IOException, ClassNotFoundException {
        try (FileOutputStream outFile
                = new FileOutputStream(
                        Paths.get(path.toString(), fileName).toString()
                ); ObjectOutputStream out = new ObjectOutputStream(outFile)) {
            out.writeObject(index);
        }
    }

    @Override
    public void exportJsonArray(JSONArray array, Path path, String fileName) throws IOException, ClassNotFoundException {
        try (FileWriter output = new FileWriter(Paths.get(path.toString(), fileName).toFile())) {
            output.write(array.toJSONString());
        }
    }
}
