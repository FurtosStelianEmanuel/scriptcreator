/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system.manager;

import tools.PersonEntity;

/**
 *
 * @author Manel
 */
public interface PacientActionListener {

    void pacientChanged(PersonEntity pacient);

    void deletePacient(int index);
}
