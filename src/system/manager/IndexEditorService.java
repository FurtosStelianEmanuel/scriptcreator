/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system.manager;

import forms.IndexEditorForm;
import forms.PacientEditorForm;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import tools.PersonEntity;

/**
 *
 * @author Manel
 */
public class IndexEditorService implements IndexEditorInterface {

    IndexEditorForm form;
    ActionManager manager;
    List<PersonEntity> index;

    class FolderFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File file) {
            return file.isDirectory();
        }

        @Override
        public String getDescription() {
            return "Select root directory";
        }
    }

    public IndexEditorService(ActionManager manager) {
        this.manager = manager;
        index = new ArrayList<>();
    }

    @Override
    public void loadIndex() throws IOException, ClassNotFoundException {
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(form);
        index = manager.getIndex(chooser.getSelectedFile().toPath());
        DefaultTableModel model = (DefaultTableModel) form.jTable1.getModel();
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        form.jTable1.getColumnModel().getColumn(0).setPreferredWidth(15);
        form.jTable1.getColumnModel().getColumn(2).setPreferredWidth(15);
        form.jTable1.getColumnModel().getColumn(3).setPreferredWidth(15);
        form.jTable1.getColumnModel().getColumn(5).setPreferredWidth(15);
        for (int i = 0; i < index.size(); i++) {
            PersonEntity entity = index.get(i);
            model.addRow(
                    new Object[]{
                        i,
                        entity.name,
                        entity.birthDate,
                        entity.sex,
                        entity.directory,
                        entity.images.size()
                    }
            );
        }
    }

    @Override
    public void startGui(Object monitor) {
        if (form == null) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    form = new IndexEditorForm(IndexEditorService.this);
                    form.setLocationRelativeTo(null);
                    form.setVisible(true);
                    synchronized (monitor) {
                        monitor.notifyAll();
                    }
                }
            });
        } else {
            form.setVisible(true);
        }
    }

    @Override
    public void pacientSelected(int selectedRow) {
        PacientEditorService pacientEditorService = new PacientEditorService(index.get(selectedRow), selectedRow, this);
        PacientEditorForm pacientEditorForm = new PacientEditorForm(pacientEditorService);
        pacientEditorService.fill(pacientEditorForm);
        pacientEditorForm.setVisible(true);
        pacientEditorService.setListener(new PacientActionListener() {
            @Override
            public void pacientChanged(PersonEntity pacient) {
                index.set(selectedRow, pacient);
                pacientEditorForm.dispose();
                updateTable(index);
            }

            @Override
            public void deletePacient(int pacientNr) {
                index.remove(pacientNr);
                pacientEditorForm.dispose();
                updateTable(index);
            }
        });
    }

    @Override
    public void updateTable(List<PersonEntity> index) {
        DefaultTableModel model = (DefaultTableModel) form.jTable1.getModel();
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        for (int i = 0; i < index.size(); i++) {
            PersonEntity entity = index.get(i);
            model.addRow(
                    new Object[]{
                        i,
                        entity.name,
                        entity.birthDate,
                        entity.sex,
                        entity.directory,
                        entity.images.size()
                    }
            );
        }
    }

    @Override
    public void saveIndex() throws IOException, ClassNotFoundException {
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(form);
        if (chooser.getSelectedFile() != null) {
            manager.setIndex(index, chooser.getSelectedFile().getParentFile().toPath(), chooser.getSelectedFile().getName());
        }
    }

    @Override
    public void addPacient() {
        PacientEditorService pacientEditorService = new PacientEditorService(this);
        PacientEditorForm pacientEditorForm = new PacientEditorForm(pacientEditorService);
        pacientEditorService.fill(pacientEditorForm);
        pacientEditorForm.jButton2.setEnabled(false);
        pacientEditorForm.setVisible(true);
        pacientEditorService.setListener(new PacientActionListener() {
            @Override
            public void pacientChanged(PersonEntity pacient) {
                index.add(pacient);
                pacientEditorForm.dispose();
                updateTable(index);
            }

            @Override
            public void deletePacient(int pacientNr) {
                System.out.println("neapelabil, butonul de delete nu apare cand adaugi un nou pacient");
            }
        });
    }

    @Override
    public void changePacientsRootDirectory() {
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setFileFilter(new FolderFilter());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.showOpenDialog(form);
        if (fileChooser.getSelectedFile() != null) {
            index.forEach((PersonEntity entity) -> {
                Path original = Paths.get(entity.directory);
                Path newPath = Paths.get(
                        fileChooser.getSelectedFile().getPath(),
                        original.getName(original.getNameCount() - 1).toString()
                );
                entity.directory = newPath.toString();
            });
            updateTable(index);
        }

    }

    @Override
    public void exportJson() {
        JSONArray pacients = new JSONArray();
        for (PersonEntity personEntity : index) {
            JSONObject personJson = new JSONObject();
            personJson.put("name", personEntity.name);
            personJson.put("gender", personEntity.sex);
            personJson.put("birth", personEntity.birthDate);
            personJson.put("directory", personEntity.directory);
            personJson.put("images", personEntity.images);
            personJson.put("id", UUID.randomUUID().toString());
            pacients.add(personJson);
        }
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(form);
        if (chooser.getSelectedFile() != null) {
            try {
                if (!chooser.getSelectedFile().exists()) {
                    chooser.getSelectedFile().createNewFile();
                }
                manager.exportJsonArray(
                        pacients,
                        chooser.getSelectedFile().toPath().getParent(),
                        chooser.getSelectedFile().getName()
                );

            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(IndexEditorService.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void minimizeImages() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FolderFilter());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(true);
        int choice = chooser.showOpenDialog(form);
        if (chooser.getSelectedFiles() != null && choice == JFileChooser.APPROVE_OPTION) {
            manager.minimizeImages(chooser.getSelectedFiles());
        }
    }

}
