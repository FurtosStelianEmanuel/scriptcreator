/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import constants.Constants;
import forms.NewActionForm;
import forms.ScriptCreatorForm;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import ocr.OcrEngine;
import system.SystemManager;
import system.manager.ActionManager;
import system.manager.IndexEditorService;

/**
 *
 * @author Manel
 */
public class ScriptCreatorService {

    ScriptCreatorForm form;
    List<Action> script;
    private OcrEngine ocrEngine;
    Map<String, UUID> memoryLocationNames;
    ActionManager manager;
    IndexEditorService editorService;

    public ScriptCreatorService(OcrEngine ocrEngine, ActionManager manager, IndexEditorService editorService) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ScriptCreatorForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        script = new ArrayList<>();
        this.ocrEngine = ocrEngine;
        memoryLocationNames = new HashMap<>();
        this.manager = manager;
        this.editorService = editorService;
    }

    public void startGui(Object monitor) {
        if (form == null) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    form = new ScriptCreatorForm(ScriptCreatorService.this, editorService);
                    form.setLocationRelativeTo(null);
                    form.setVisible(true);
                    synchronized (monitor) {
                        monitor.notifyAll();
                    }
                }
            });
        } else {
            try {
                throw new Exception("GUI already started");
            } catch (Exception ex) {
                Logger.getLogger(ScriptCreatorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void addActionToFormList(String actionName) {
        DefaultListModel model = (DefaultListModel) form.jList1.getModel();
        model.addElement(actionName);
        form.jList1.setModel(model);
    }

    private void removeActionFromList(int index) {
        DefaultListModel model = (DefaultListModel) form.jList1.getModel();
        model.remove(index);
        form.jList1.setModel(model);
    }

    public void saveScript() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            manager.serializeScript(script, Paths.get(f.getParent()), f.getName());
            JOptionPane.showMessageDialog(null, "Script saved");
        } catch (IOException ex) {
            Logger.getLogger(ScriptCreatorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void runScript() {
        executeScript();
    }

    public void removeAction() {
        int[] toRemove = form.jList1.getSelectedIndices();
        for (int i = toRemove.length - 1; i >= 0; i--) {
            removeActionFromList(toRemove[i]);
            script.remove(toRemove[i]);
        }
    }

    public void loadScript() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            script = manager.getScript(Paths.get(f.getParent()), f.getName());

            for (Action action : script) {
                if (action.actionsMap.get(ActionKey.StoreOcrValues) instanceof UUID) {
                    addMemoryLocationName(action.getName(), (UUID) action.actionsMap.get(ActionKey.StoreOcrValues));
                }
                addActionToFormList(action.getName());
            }
            JOptionPane.showMessageDialog(null, "Script loaded");
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ScriptCreatorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void acomplishAction() {
        int[] indeces = form.jList1.getSelectedIndices();
        if (indeces.length == 0) {
            if (script.size() > 0) {
                JOptionPane.showMessageDialog(null, "No actions were selected");
            } else {
                JOptionPane.showMessageDialog(null, "You haven't added any actions to the script");
            }
        } else {
            executeScript(indeces);
        }
    }

    private void executeScript(int[] indeces) {
        ActionExecutor executor = new ActionExecutor(ocrEngine, form, manager);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            int index = 0;

            @Override
            public void run() {
                executor.acomplish(script.get(indeces[index++]));
                if (index >= indeces.length) {
                    this.cancel();
                }
            }
        }, new Date(System.currentTimeMillis()), 1000);
    }

    private void executeScript() {
        ActionExecutor executor = new ActionExecutor(ocrEngine, form, manager);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            int index = 0;

            @Override
            public void run() {
                executor.acomplish(script.get(index++));
                if (index >= script.size()) {
                    this.cancel();
                }
            }
        }, new Date(System.currentTimeMillis()), 1000);
    }

    private void addMemoryLocationName(String name, UUID id) {
        if (memoryLocationNames.put(name, id) != null) {
            throw new UnsupportedOperationException("Conflict de nume in memoryLocationName map");
        }
    }

    private void handleDoneButton(Action action, NewActionForm newActionForm) {
        Predicate actionPredicate = Predicate.valueOf((String) newActionForm.jComboBox1.getSelectedItem());
        action.setPredicate(actionPredicate);
        switch (actionPredicate) {
            case CreateFolder: {
                String[] rootFolderStrings = newActionForm.folderCreationFolderRootName.getText().split(Pattern.quote(System.getProperty("file.separator")));
                Path rootFolderPath = Paths.get("");
                for (int i = 0; i < rootFolderStrings.length; i++) {
                    if (rootFolderPath != null) {
                        rootFolderPath = Paths.get(rootFolderPath.toString(), rootFolderStrings[i]);
                    } else {
                        rootFolderPath = Paths.get(rootFolderStrings[i], "");
                    }
                }
                action.addToMap(ActionKey.FolderPath, rootFolderPath.toString());
                if (newActionForm.folderCreationFolderNameChoice.getSelectedIndex() == 0) {
                    action.addToMap(
                            ActionKey.FolderName,
                            memoryLocationNames.get((String) newActionForm.folderCreationMemoryLocation.getSelectedItem())
                    );
                } else if (newActionForm.folderCreationFolderNameChoice.getSelectedIndex() == 1) {
                    action.addToMap(ActionKey.FolderName, newActionForm.folderCreationRawText.getText());
                }
                break;
            }
            case MouseClick: {
                if (newActionForm.mouseClickLeft.isSelected()) {
                    action.addToMap(ActionKey.MouseClickType, SystemManager.LEFT_CLICK);
                } else if (newActionForm.mouseClickMiddle.isSelected()) {
                    action.addToMap(ActionKey.MouseClickType, SystemManager.MIDDLE_CLICK);
                } else {
                    action.addToMap(ActionKey.MouseClickType, SystemManager.RIGHT_CLICK);
                }
                break;
            }
            case MouseMove: {
                action.addToMap(ActionKey.XCoordinate, Integer.parseInt(newActionForm.mouseMoveX.getText()));
                action.addToMap(ActionKey.YCoordinate, Integer.parseInt(newActionForm.mouseMoveY.getText()));
                break;
            }
            case MouseWheel: {
                if (newActionForm.mouseWheelDirection.getSelectedIndex() == 0) {
                    action.addToMap(ActionKey.MouseWheelDirection, SystemManager.SCROLL_UP);
                } else if (newActionForm.mouseWheelDirection.getSelectedIndex() == 1) {
                    action.addToMap(ActionKey.MouseWheelDirection, SystemManager.SCROLL_DOWN);
                }
                action.addToMap(ActionKey.MouseWheelAmount, (int) newActionForm.mouseWheelAmount.getValue());
                break;
            }
            case TakeOcrScreenshot: {
                Rectangle rect = new Rectangle(
                        new Point(
                                Integer.valueOf(newActionForm.takeOcrScreenshotX.getText()),
                                Integer.valueOf(newActionForm.takeOcrScreenshotY.getText())
                        ),
                        new Dimension(
                                Integer.valueOf(newActionForm.takeOcrScreenshotWidth.getText()),
                                Integer.valueOf(newActionForm.takeOcrScreenshotHeight.getText())
                        )
                );
                action.addToMap(ActionKey.OcrScreenshotRectangle, rect);
                UUID id = UUID.randomUUID();
                addMemoryLocationName(action.getName(), id);
                action.addToMap(ActionKey.StoreOcrValues, id);
                action.addToMap(ActionKey.ConfirmValueRequired, true);
                break;
            }
            case TypeSentence: {
                if (newActionForm.typeSentenceChoice.getSelectedIndex() == 0) {
                    action.addToMap(
                            ActionKey.SentenceToType,
                            memoryLocationNames.get((String) newActionForm.typeSentenceExecutorMemory.getSelectedItem())
                    );
                } else if (newActionForm.typeSentenceChoice.getSelectedIndex() == 1) {
                    action.addToMap(ActionKey.SentenceToType, newActionForm.typeSentenceRawSentence.getText());
                }
                break;
            }
            case CustomOradentClickPictogrames: {
                action.addToMap(ActionKey.CustomOradentFirstImageRectangle, newActionForm.customOradentStartImage);
                action.addToMap(ActionKey.CustomOradentFirstImageDateRectangle, newActionForm.customOradentDate);
                action.addToMap(ActionKey.CustomOradentLastImageRectangle, newActionForm.customOradentLastImage);
                action.addToMap(ActionKey.CustomOradentHorizontalSeparationRectangle, newActionForm.customOradentSeparationHorizontal);
                action.addToMap(ActionKey.CustomOradentVerticalSeparationRectangle, newActionForm.customOradentSeparationVertical);
                break;
            }
            case CustomOradentAddPacientToIndexer: {
                action.addToMap(
                        ActionKey.CustomOradentPacientName,
                        memoryLocationNames.get((String) newActionForm.customOradentNameMemory.getSelectedItem())
                );
                action.addToMap(
                        ActionKey.CustomOradentPacientBirthDate,
                        memoryLocationNames.get((String) newActionForm.customOradentBirthDateMemory.getSelectedItem())
                );
                action.addToMap(
                        ActionKey.CustomOradentPacientSex,
                        memoryLocationNames.get((String) newActionForm.customOradentSexMemory.getSelectedItem())
                );
                break;
            }
        }
        if (true/*trece de validari*/) {
            script.add(action);
            addActionToFormList(action.getName());
            newActionForm.dispose();
        }
    }

    public void addNewAction() {
        String[] predicates = Constants.Action.PREDICATES;
        NewActionForm newActionForm = new NewActionForm();
        newActionForm.setLocationRelativeTo(null);
        final Action action = new Action();
        for (String predicate : predicates) {
            newActionForm.jComboBox1.addItem(predicate);
        }
        newActionForm.showTabForPredicate(Predicate.MouseMove);
        newActionForm.populateMemoryLocations(memoryLocationNames);

        String actionName = JOptionPane.showInputDialog(null, "Name this action");
        if (actionName == null) {
            return;
        }
        if (actionName.equals("")) {
            actionName = action.getName();
        }
        action.setName(actionName);

        newActionForm.doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                handleDoneButton(action, newActionForm);
            }
        }
        );
        newActionForm.setVisible(true);
    }

    public void startIndexEditor() {
        Object monitor = new Object();
        synchronized (monitor) {
            editorService.startGui(monitor);
        }

    }
}
