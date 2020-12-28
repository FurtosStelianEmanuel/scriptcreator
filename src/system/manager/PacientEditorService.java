/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system.manager;

import forms.PacientEditorForm;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import tools.PersonEntity;

/**
 *
 * @author Manel
 */
public class PacientEditorService implements PacientEditorInterface {

    private PersonEntity pacient;
    private PacientActionListener listener;
    public int pacientNr;
    private final IndexEditorService service;

    public PacientEditorService(PersonEntity pacient, int pacientNr, IndexEditorService service) {
        //nu am mai putut modifica PersonEntity, asa ca fac aici copie a valorilor, nu in constructoru PersonEntity
        this.pacient = new PersonEntity();
        this.pacient.birthDate = String.valueOf(pacient.birthDate);
        this.pacient.name = String.valueOf(pacient.name);
        this.pacient.directory = String.valueOf(pacient.directory);
        this.pacient.sex = String.valueOf(pacient.sex);
        this.pacient.images = new HashMap<>();
        pacient.images.keySet().forEach((key) -> {
            this.pacient.images.put(key, pacient.images.get(key));
        });
        this.pacientNr = pacientNr;
        this.service = service;
    }

    public PacientEditorService(IndexEditorService service) {
        pacient = new PersonEntity();
        this.service = service;
    }

    @Override
    public void fill(PacientEditorForm form) {
        form.name.setText(pacient.name);
        form.birthDate.setText(pacient.birthDate);
        form.sex.setText(pacient.sex);
        form.directory.setText(pacient.directory);
        form.images.setText(pacient.images.toString());
    }

    @Override
    public void applyChanges(PacientEditorForm form) {
        pacient.name = form.name.getText();
        pacient.birthDate = form.birthDate.getText();
        pacient.directory = form.directory.getText();
        pacient.sex = form.sex.getText();
        Map<String, String> images = new HashMap<>();
        StringTokenizer pairTokenizer = new StringTokenizer(
                form.images.getText()
                        .replace("{", "")
                        .replace("}", "")
                        .replace(", ", ","),
                ","
        );
        while (pairTokenizer.hasMoreTokens()) {
            String pair = pairTokenizer.nextToken();
            StringTokenizer keyValueTokenizer = new StringTokenizer(pair, "=");
            String key = keyValueTokenizer.nextToken();
            String value = keyValueTokenizer.nextToken();
            if (images.put(key, value) != null) {
                System.out.println("Overlap de keys");
            }
        }
        pacient.images = images;
        listener.pacientChanged(pacient);
    }

    @Override
    public void setListener(PacientActionListener listener) {
        this.listener = listener;
    }

    @Override
    public void deletePacient() {
        listener.deletePacient(pacientNr);
    }

    @Override
    public void addPacient(PacientEditorForm form) {
        listener.pacientChanged(new PersonEntity());
    }
}
