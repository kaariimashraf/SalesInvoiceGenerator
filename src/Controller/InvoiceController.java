package Controller;
import Model.FileOperations;
import Model.InvoiceHeader;
import Model.InvoiceLines;
import View.MyFrame;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;

public class InvoiceController {
public MyFrame view;
public List<InvoiceHeader> model;

public FileOperations FileOps;

private boolean model_error=false;

    public InvoiceController() {}

    public InvoiceController(MyFrame view)
    {
    this.view = view;
    view.l= this;
    this.FileOps = new FileOperations();
    //this.model = FileOps.loadFile(view);




}

    private void LoadDetails()
    {

        try
        {
            //create  a new table model to fill it with the details data
            DefaultTableModel table = new DefaultTableModel();
            //set the column identifiers to the table
            table.setColumnIdentifiers(new String [] {
                    "No.", "Item Name", "Item Price", "Count", "Item Total"
            });
            //set the model to the details table
            view.InvDetails.setModel(table);

            // Calculate Invoice Total Field
            int InvoiceTotal = 0;
            String InvoiceId = view.InvTab.getValueAt(view.InvTab.getSelectedRow(),0).toString();


            for(int i = 0;i< model.size();i++)
            {
                    //get the selected invoice from the loop by the invoice id
                    if(model.get(i).getInvoiceNum().equals(InvoiceId))
                    {
                        //make sure the invoice lines is not empty
                        if(model.get(i).Lines.size()==0){break;}

                        //Display the invoice lines details
                        for(int j = 0;j< model.get(i).Lines.size();j++)
                        {
                            InvoiceTotal+= Integer.parseInt(model.get(i).Lines.get(j).gettotal());
                            table.addRow(new String[]{
                                            model.get(i).Lines.get(j).getInvoiceNum(),
                                            model.get(i).Lines.get(j).getItemName(),
                                            model.get(i).Lines.get(j).getItemPrice(),
                                            model.get(i).Lines.get(j).getCount(),
                                            model.get(i).Lines.get(j).gettotal()
                            });
                        }

                    }

            }

            //take the invoice no. to Display
            table.addRow(new String []{InvoiceId,"","","",""});
            view.jLabel7.setText(InvoiceId);
            view.jLabel8.setText(String.valueOf(InvoiceTotal));
            view.InvoiceDate.setText(view.InvTab.getValueAt(view.InvTab.getSelectedRow(),1).toString());
            view.customerName.setText(view.InvTab.getValueAt(view.InvTab.getSelectedRow(),2).toString());

            //Update the Invoice total on the screen
            DefaultTableModel newmodel = (DefaultTableModel)view.InvTab.getModel();
            newmodel.setValueAt(String.valueOf(InvoiceTotal),view.InvTab.getSelectedRow(),3);

        }
        catch (Exception e) {

            //throw e;
            //Erase the view data
            view.jLabel7.setText(" ");
            view.jLabel8.setText(" ");
            view.InvoiceDate.setText(" ");
            view.customerName.setText(" ");
        }

    }
    private void createInvoice()
    {
        DefaultTableModel table = (DefaultTableModel)view.InvTab.getModel();

        int NewId =1;
        if(!(table.getRowCount() == 0))
        {
            NewId = Integer.parseInt(table.getValueAt(table.getRowCount()-1,0).toString()) +1;
        }


        table.addRow(new String[]{String.valueOf(NewId),"","",""});

        if(model == null)
        {
            model = new ArrayList<InvoiceHeader>();
        }

        InvoiceHeader Invoice = new InvoiceHeader();
        Invoice.setInvoiceNum(String.valueOf(NewId));
        Invoice.Lines.add(new InvoiceLines(String.valueOf(NewId),"","",""));
        model.add(Invoice);

        view.InvTab.setRowSelectionInterval(table.getRowCount()-1,table.getRowCount()-1);
        LoadDetails();
        //DefaultTableModel Detailmodel = (DefaultTableModel)view.InvDetails.getModel();
        //Detailmodel.addRow(new String[]{String.valueOf(NewId),"","","",""});

    }
    private void deleteInvoice()
    {
        DefaultTableModel table = (DefaultTableModel)view.InvTab.getModel();

        //remove the invoice from the model
        String InvoiceId = table.getValueAt(view.InvTab.getSelectedRow(),0).toString();
        for(int i =0 ; i< model.size();i++)
        {
            if(model.get(i).getInvoiceNum().equals(InvoiceId))
            {
                model.remove(i);
            }
        }

        //remove the row from the view
        table.removeRow(view.InvTab.getSelectedRow());

        //Mark First row as selected
        view.InvTab.setRowSelectionInterval(0,0);
        LoadDetails();

    }
    private void  cancelChanges()
    {
        LoadDetails();
    }
    private void saveChanges()
    {

        if (model_error == true)
        {

        }
        if(view.customerName.getText().isEmpty() || view.InvoiceDate.getText().isEmpty())
        {
            JOptionPane.showMessageDialog(view,"Please Fill the empty Fields First ","ERROR",JOptionPane.ERROR_MESSAGE);

        }
        else {
            String regex = "^([0-9]*)$";
            if(view.InvDetails.isEditing()){ view.InvDetails.getCellEditor().stopCellEditing();}
            DefaultTableModel table = (DefaultTableModel) view.InvDetails.getModel();
            int InvoiceTotal = 0;
            for (int i = 0; i < model.size(); i++) {
                if (model.get(i).getInvoiceNum().equals(view.jLabel7.getText())) {

                    model.get(i).setInvoiceDate(view.InvoiceDate.getText());
                    model.get(i).setCustomerName(view.customerName.getText());
                    List<InvoiceLines> Lines = new ArrayList<InvoiceLines>();
                    for (int j = 0; j < table.getRowCount(); j++) {
                        if (table.getValueAt(j, 1).toString().isEmpty()) {

                            break;
                        }
                        else if(table.getValueAt(j, 2).toString().isEmpty() || table.getValueAt(j, 3).toString().isEmpty() )
                        {
                            JOptionPane.showMessageDialog(view,"Please enter the item count and/or the item price  ","ERROR",JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                        else if(!(table.getValueAt(j, 2).toString().matches(regex))
                                || !(table.getValueAt(j, 3).toString().matches(regex)))
                        {
                            JOptionPane.showMessageDialog(view,"Cell input is numbers only","Error",JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        InvoiceLines Line = new InvoiceLines();
                        Line.setInvoiceNum(table.getValueAt(j, 0).toString());
                        Line.setItemName(table.getValueAt(j, 1).toString());
                        Line.setItemPrice(table.getValueAt(j, 2).toString());
                        Line.setCount(table.getValueAt(j, 3).toString());
                        int num1 = Integer.parseInt(table.getValueAt(j, 2).toString());
                        int num2 = Integer.parseInt(table.getValueAt(j, 3).toString());
                        int total = num1 * num2;
                        InvoiceTotal += total;
                        Lines.add(Line);
                    }
                    //if(Lines.size()==0 ){Lines.add(new InvoiceLines()); Lines.get(0).setInvoiceNum(view.jLabel7.getText());}
                    model.get(i).setInvoiceTotal(String.valueOf(InvoiceTotal));
                    model.get(i).Lines = (ArrayList<InvoiceLines>) Lines;
                }
            }

            DefaultTableModel newmodel = (DefaultTableModel) view.InvTab.getModel();
            newmodel.setValueAt(String.valueOf(InvoiceTotal), view.InvTab.getSelectedRow(), 3);
            newmodel.setValueAt(view.customerName.getText(), view.InvTab.getSelectedRow(), 2);
            newmodel.setValueAt(view.InvoiceDate.getText(), view.InvTab.getSelectedRow(), 1);
            LoadDetails();
        }

    }
    private void delete_line()
    {
        DefaultTableModel Detailmodel = (DefaultTableModel)view.InvDetails.getModel();
        DefaultTableModel Headermodel = (DefaultTableModel)view.InvTab.getModel();
        if(view.InvDetails.getRowCount()==0)
        {
            JOptionPane.showMessageDialog(view,"Table is already Empty!","Empty",JOptionPane.INFORMATION_MESSAGE);
        }
        else
        {
            if(view.InvDetails.getSelectedRow()== -1)
            {
                if(view.InvDetails.getValueAt(view.InvDetails.getRowCount()-1, 1).toString().isEmpty()
                        || view.InvDetails.getValueAt(view.InvDetails.getRowCount()-1, 2).toString().isEmpty()
                        || view.InvDetails.getValueAt(view.InvDetails.getRowCount()-1, 3).toString().isEmpty())
                {
                    Detailmodel.removeRow(view.InvDetails.getRowCount()-1);
                }
                else
                {
                    String id = view.InvDetails.getValueAt(view.InvDetails.getRowCount()-1,0).toString();
                    int total_line =Integer.parseInt( view.InvDetails.getValueAt(view.InvDetails.getRowCount()-1,4).toString());
                    for(int i = 0; i< view.InvTab.getRowCount();i++)
                    {

                        if(Headermodel.getValueAt(i,0).equals(id))
                        {
                            //int new_total =Integer.parseInt(view.InvTab.getValueAt(i,3).toString()) - total_line;
                            int new_total =Integer.parseInt(view.jLabel8.getText()) - total_line;
                            //Headermodel.setValueAt(new_total,i,3);
                            view.jLabel8.setText(String.valueOf(new_total));
                        }
                    }
                    Detailmodel.removeRow(view.InvDetails.getRowCount()-1);
                }
            }
            else
            {
                if (view.InvDetails.getValueAt(view.InvDetails.getSelectedRow(), 1).toString().isEmpty()
                        || view.InvDetails.getValueAt(view.InvDetails.getSelectedRow(), 2).toString().isEmpty()
                        || view.InvDetails.getValueAt(view.InvDetails.getSelectedRow(), 3).toString().isEmpty())
                {
                    Detailmodel.removeRow(view.InvDetails.getSelectedRow());
                }
                else
                {
                    String id = view.InvDetails.getValueAt(view.InvDetails.getSelectedRow(),0).toString();
                    int total_line =Integer.parseInt( view.InvDetails.getValueAt(view.InvDetails.getSelectedRow(),4).toString());
                    for(int i = 0; i< view.InvTab.getRowCount();i++)
                    {

                        if(Headermodel.getValueAt(i,0).equals(id))
                        {
                            //int new_total =Integer.parseInt(view.InvTab.getValueAt(i,3).toString()) - total_line;
                            int new_total =Integer.parseInt(view.jLabel8.getText()) - total_line;

                            //Headermodel.setValueAt(new_total,i,3);
                            view.jLabel8.setText(String.valueOf(new_total));
                        }
                    }
                    Detailmodel.removeRow(view.InvDetails.getSelectedRow());
                }

            }
        }

    }
    private void update_header_totals()
    {
        for(int i = 0 ; i<view.InvTab.getRowCount();i++)
        {
            for(int j = 0 ; j<model.size();j++)
            {
                if(view.InvTab.getValueAt(i,0).equals(model.get(j).getInvoiceNum()))
                {
                    view.InvTab.setValueAt(model.get(j).getInvoiceTotal(),i,3);
                }
            }
        }
    }
    private void update_header_sum(int sum,String id)
    {
        for(int i = 0 ; i<view.InvTab.getRowCount();i++)
        {
                if(view.InvTab.getValueAt(i,0).equals(id))
                {
                    int val = Integer.parseInt(view.InvTab.getValueAt(i,3).toString());
                    view.InvTab.setValueAt(val +sum,i,3);
                }
        }
    }
    private void Exception() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


    public void Add_LineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Add_LineActionPerformed
        DefaultTableModel Detailmodel = (DefaultTableModel)view.InvDetails.getModel();
        Detailmodel.addRow(new String[]{view.jLabel7.getText(),"","","",""});
    }//GEN-LAST:event_Add_LineActionPerformed

    public void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
       //Disable editing in the view form
        view.InvTab.setDefaultEditor(Object.class, null);

        //Load the Invoice header File to the model
//        try
//        {
//            model = FileOps.loadFile("InvoiceHeader.csv");
//        }
//        catch (Exception e) {
//            if (e.getMessage().equals("Wrong Date Format"))
//            {
//                JOptionPane.showMessageDialog(view,"Wrong Date Format\n Please Choose another File ","ERROR",JOptionPane.ERROR_MESSAGE);
//            }
//            else if(e.getClass() == FileNotFoundException.class)
//            {
//                JOptionPane.showMessageDialog(view,"File Not Found\n Please make sure file exists in project directory ","ERROR",JOptionPane.ERROR_MESSAGE);
//
//            }
//            else {
//                JOptionPane.showMessageDialog(view,"File Format is not Supported\n Please Choose another File ","ERROR",JOptionPane.ERROR_MESSAGE);
//            }
//        }

        //update the table view
//        DefaultTableModel table = new DefaultTableModel();
//        view.InvTab.setModel(table);
//        table.setColumnIdentifiers(new String [] {
//                "No.", "Date", "Customer", "Total"
//        });
//        for(int i = 0;i < model.size();i++)
//        {table.addRow(new String[]{model.get(i).getInvoiceNum(),
//                model.get(i).getInvoiceDate(),model.get(i).getCustomerName(),model.get(i).getInvoiceTotal()});}

    }//GEN-LAST:event_formWindowOpened

    public void loadFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadFileActionPerformed
        //Load the chosen file into the model
        JFileChooser choosef = new JFileChooser();
        int choosenfile = choosef.showOpenDialog(view);

        if (choosenfile == JFileChooser.APPROVE_OPTION)
        {
            String Headerpath = choosef.getSelectedFile().getPath();

            JOptionPane.showMessageDialog(view,"Now choose the lines file ","Info",JOptionPane.INFORMATION_MESSAGE);

            choosenfile = choosef.showOpenDialog(view);
            if (choosenfile == JFileChooser.APPROVE_OPTION)
            {
                String LinePath = choosef.getSelectedFile().getPath();
                try
                {
                    model = FileOps.loadFile(Headerpath,LinePath);
                }
                catch (Exception e) {
                    if (e.getMessage().equals("Wrong Date Format"))
                    {
                        JOptionPane.showMessageDialog(view,"Wrong Date Format\n Please Choose another File ","ERROR",JOptionPane.ERROR_MESSAGE);
                    }
                    else if(e.getClass() == FileNotFoundException.class)
                    {
                        JOptionPane.showMessageDialog(view,"File Not Found\n Please make sure file exists in project directory ","ERROR",JOptionPane.ERROR_MESSAGE);

                    }
                    else {
                        JOptionPane.showMessageDialog(view,"File Format is not Supported\n Please Choose another File ","ERROR",JOptionPane.ERROR_MESSAGE);
                    }
                }

                //update the table view
                DefaultTableModel table = new DefaultTableModel();
                view.InvTab.setModel(table);
                table.setColumnIdentifiers(new String [] {
                        "No.", "Date", "Customer", "Total"
                });
                for(int i = 0;i < model.size();i++)
                {
                    table.addRow(new String[]{model.get(i).getInvoiceNum(),
                            model.get(i).getInvoiceDate(),model.get(i).getCustomerName(),model.get(i).getInvoiceTotal()});
                }

            }

        }




    }//GEN-LAST:event_loadFileActionPerformed
    public void saveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveFileActionPerformed
        //TODO add your handling code here:
        //update our model from the table view before saving
        //DefaultTableModel table1 = (DefaultTableModel)view.InvTab.getModel();


        try
        {

            FileOps.saveFile(model);
            JOptionPane.showMessageDialog(view,"File Saved Successfully ","Success",JOptionPane.INFORMATION_MESSAGE);

        }
        catch (Exception e)
        {
            if(e.getMessage().contains("The process cannot access the file because it is being used by another process") && !(e.getMessage().isEmpty()) )
            {
                JOptionPane.showMessageDialog(view,"File is used by another process \r\n Please close the file first","Error Saving while file is open",JOptionPane.ERROR_MESSAGE);

            }
            else if (e.getMessage().contains("No Path") && !(e.getMessage().isEmpty()))
            {
                JFileChooser choosef = new JFileChooser();
                int choosenfile = choosef.showSaveDialog(view);

                if (choosenfile == JFileChooser.APPROVE_OPTION)
                {
                    String Headerpath = choosef.getSelectedFile().getPath();

                    JOptionPane.showMessageDialog(view,"Now save the lines file ","Info",JOptionPane.INFORMATION_MESSAGE);

                    choosenfile = choosef.showSaveDialog(view);
                    if (choosenfile == JFileChooser.APPROVE_OPTION)
                    {
                        String LinePath = choosef.getSelectedFile().getPath();
                        try
                        {
                            FileOps.saveFile(model,Headerpath,LinePath);
                            JOptionPane.showMessageDialog(view,"File Saved Successfully ","Success",JOptionPane.INFORMATION_MESSAGE);

                        }
                        catch (Exception ex)
                        {
                            JOptionPane.showMessageDialog(view,"Something went wrong, try saving again","Error",JOptionPane.ERROR_MESSAGE);

                        }
                    }
                }
            }
            else
            {
                JOptionPane.showMessageDialog(view,"Something went wrong, try saving again","Error",JOptionPane.ERROR_MESSAGE);

            }

        }

    }//GEN-LAST:event_saveFileActionPerformed

    public void createNewInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewInvoiceActionPerformed
        createInvoice();

    }//GEN-LAST:event_createNewInvoiceActionPerformed

    public void cellselectionchanged(javax.swing.event.ListSelectionEvent evt) {
        if(!(view.InvDetails.getSelectedRow() == -1))
        {
            if (!evt.getValueIsAdjusting()) {
            if(!(view.InvDetails.getValueAt(view.InvDetails.getSelectedRow(), 1).toString().isEmpty()
                    || view.InvDetails.getValueAt(view.InvDetails.getSelectedRow(), 2).toString().isEmpty()
                    || view.InvDetails.getValueAt(view.InvDetails.getSelectedRow(), 3).toString().isEmpty()))
            {
                String regex = "^([0-9]*)$";
                if((view.InvDetails.getValueAt(view.InvDetails.getSelectedRow(), 2).toString().matches(regex))
                        && view.InvDetails.getValueAt(view.InvDetails.getSelectedRow(), 3).toString().matches(regex) )
                {
                int total = Integer.parseInt(view.InvDetails.getValueAt(view.InvDetails.getSelectedRow(), 2).toString())
                        * Integer.parseInt(view.InvDetails.getValueAt(view.InvDetails.getSelectedRow(), 3).toString());
                view.InvDetails.setValueAt(total,view.InvDetails.getSelectedRow(),4);
                }
                else
                {
                    JOptionPane.showMessageDialog(view,"Cell input is numbers only","Error",JOptionPane.ERROR_MESSAGE);

                }



                //update_header_sum(total,view.InvDetails.getValueAt(view.InvDetails.getSelectedRow(), 0).toString());

            }
        }
        }

    }
    public void deleteInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteInvoiceActionPerformed
        deleteInvoice();
    }//GEN-LAST:event_deleteInvoiceActionPerformed

    public void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
        saveChanges();
    }//GEN-LAST:event_saveBtnActionPerformed

    public void Delete_LineBtnActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_saveBtnActionPerformed
        delete_line();
    }//GEN-LAST:event_saveBtnActionPerformed

    public void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        cancelChanges();
    }//GEN-LAST:event_cancelBtnActionPerformed

    public void InvTabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InvTabMouseClicked
        update_header_totals();
        LoadDetails();
    }//GEN-LAST:event_InvTabMouseClicked

    public void InvTabPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_InvTabPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_InvTabPropertyChange

    public void InvDetailsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_InvDetailsFocusLost
//        DefaultTableModel Detailmodel = (DefaultTableModel)InvDetails.getModel();
//        Detailmodel.setValueAt(evt, ERROR, NORMAL);
    }//GEN-LAST:event_InvDetailsFocusLost

    public void InvDetailsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_InvDetailsMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_InvDetailsMouseClicked

    public void InvoiceDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InvoiceDateActionPerformed

    }//GEN-LAST:event_InvoiceDateActionPerformed

    public void InvoiceDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_InvoiceDateFocusLost
        if(view.InvoiceDate.getText().isEmpty()){}
        else{
            String regex = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-(20[0-9]{2})$";
            if(!(view.InvoiceDate.getText().matches(regex))) {
                JOptionPane.showMessageDialog(view,"Wrong Date Format\n Please Enter Date as DD-MM-YYYY ","ERROR",JOptionPane.ERROR_MESSAGE);
                view.InvoiceDate.setText("");

            }
        }

    }//GEN-LAST:event_InvoiceDateFocusLost


}
