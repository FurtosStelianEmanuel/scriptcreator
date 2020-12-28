/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system.manager;

import forms.PacientEditorForm;

/**
 *
 * @author Manel
 */
public interface PacientEditorInterface {

    void fill(PacientEditorForm form);

    void applyChanges(PacientEditorForm form);

    void setListener(PacientActionListener listener);

    void deletePacient();
    
    void addPacient(PacientEditorForm form);
}
