package Model;

import View.MyFrame;
import com.sun.corba.se.spi.activation._LocatorImplBase;

import javax.sound.sampled.Line;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileOperations {

    private String HeaderFilePath;
    private String LinesFilePath;


    public List<InvoiceHeader> loadFile(String HeaderPath,String LinePath) throws Exception{

        HeaderFilePath = HeaderPath;
        LinesFilePath= LinePath;

        List<InvoiceHeader> InvoiceHeaderList = new ArrayList<InvoiceHeader>();

        try {
            //Load the invoice header
            BufferedReader csvReader = new BufferedReader(new FileReader(HeaderPath));
            String row;
            while ((row = csvReader.readLine()) != null) {

                InvoiceHeader Invoice = new InvoiceHeader();

                String[] LineData = row.split(",");
                String regex;
                regex = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-(20[0-9]{2})$";
                if(!(LineData[1].matches(regex))) {
                    throw new Exception("Wrong Date Format");

                }
                Invoice.setInvoiceNum(LineData[0]);
                Invoice.setInvoiceDate(LineData[1]);
                Invoice.setCustomerName(LineData[2]);
                //Invoice.setInvoiceTotal(LineData[3]);
                InvoiceHeaderList.add(Invoice);


                // do something with the LineData
            }
            csvReader.close();

            //load the invoice line details
            for(int i =0; i<InvoiceHeaderList.size();i++)
            {
                csvReader = new BufferedReader(new FileReader(LinePath));
                while ((row = csvReader.readLine()) != null) {

                    InvoiceLines Line = new InvoiceLines();

                    String[] LineData = row.split(",");
                    if(InvoiceHeaderList.get(i).getInvoiceNum().equals(LineData[0]))
                    {
                        Line.setInvoiceNum(LineData[0]);
                        Line.setItemName(LineData[1]);
                        Line.setItemPrice(LineData[2]);
                        Line.setCount(LineData[3]);
                        InvoiceHeaderList.get(i).Lines.add(Line);
                    }

                    // do something with the LineData
                }
                csvReader.close();
            }


        }
        catch (Exception e) {
            throw e;
        }

        Test(InvoiceHeaderList);
        return InvoiceHeaderList;

    }
    public void saveFile(List<InvoiceHeader> model) throws Exception{

        //this means no file was loaded and will save a new file from scratch
          if(HeaderFilePath == null)
          {
                throw new Exception("No Path");
          }
            try
            {
                BufferedWriter csvwriter = new BufferedWriter(new FileWriter(HeaderFilePath));

                //writing the invoice header file
                for (int i = 0; i < model.size(); i++)
                {
                        if(model.get(i).getCustomerName() == null || model.get(i).getInvoiceDate() == null)
                        {
                            model.remove(i);
                            continue;
                        }
                        csvwriter.write(model.get(i).getInvoiceNum());
                        csvwriter.write(',');
                        csvwriter.write(model.get(i).getInvoiceDate());
                        csvwriter.write(',');
                        csvwriter.write(model.get(i).getCustomerName());
                        //csvwriter.write(',');
                       // csvwriter.write(model.get(i).getInvoiceTotal());
                        csvwriter.newLine();
                }
                csvwriter.close();

                //writing the invoice Lines file
                csvwriter = new BufferedWriter(new FileWriter(LinesFilePath));
                for (int i = 0; i < model.size(); i++)
                {
                    for (int j = 0; j < model.get(i).Lines.size(); j++)
                    {

                        csvwriter.write(model.get(i).Lines.get(j).getInvoiceNum());
                        csvwriter.write(',');
                        csvwriter.write(model.get(i).Lines.get(j).getItemName());
                        csvwriter.write(',');
                        csvwriter.write(model.get(i).Lines.get(j).getItemPrice());
                        csvwriter.write(',');
                        csvwriter.write(model.get(i).Lines.get(j).getCount());
                        csvwriter.newLine();
                    }
                }
                csvwriter.close();
            }
            catch (IOException e)
            {

                throw e;
            }


    }

    public void saveFile(List<InvoiceHeader> model,String _HeaderFilePath,String _LinesFilePath) throws Exception{


        try
        {
            BufferedWriter csvwriter = new BufferedWriter(new FileWriter(_HeaderFilePath));

            //writing the invoice header file
            for (int i = 0; i < model.size(); i++)
            {
                if(model.get(i).getCustomerName() == null || model.get(i).getInvoiceDate() == null)
                {
                    model.remove(i);
                    continue;
                }
                csvwriter.write(model.get(i).getInvoiceNum());
                csvwriter.write(',');
                csvwriter.write(model.get(i).getInvoiceDate());
                csvwriter.write(',');
                csvwriter.write(model.get(i).getCustomerName());
                //csvwriter.write(',');
                // csvwriter.write(model.get(i).getInvoiceTotal());
                csvwriter.newLine();
            }
            csvwriter.close();

            //writing the invoice Lines file
            csvwriter = new BufferedWriter(new FileWriter(_LinesFilePath));
            for (int i = 0; i < model.size(); i++)
            {
                for (int j = 0; j < model.get(i).Lines.size(); j++)
                {

                    csvwriter.write(model.get(i).Lines.get(j).getInvoiceNum());
                    csvwriter.write(',');
                    csvwriter.write(model.get(i).Lines.get(j).getItemName());
                    csvwriter.write(',');
                    csvwriter.write(model.get(i).Lines.get(j).getItemPrice());
                    csvwriter.write(',');
                    csvwriter.write(model.get(i).Lines.get(j).getCount());
                    csvwriter.newLine();
                }
            }
            csvwriter.close();
        }
        catch (IOException e)
        {

            throw e;
        }


    }

    public void Test(List<InvoiceHeader> model)
    {
        try
        {
            //List<InvoiceHeader> model = loadFile(HeaderFilePath,LinesFilePath);
            for (int i=0; i < model.size();i++)
            {
                System.out.println(model.get(i).getInvoiceNum());
                System.out.println("{");
                System.out.println(model.get(i).getInvoiceDate()+","+model.get(i).getCustomerName());
                for (int j=0; j < model.get(i).Lines.size();j++)
                {
                    System.out.println(model.get(i).Lines.get(j).getItemName()+","
                    +model.get(i).Lines.get(j).getItemPrice()+","
                    +model.get(i).Lines.get(j).getCount());
                }
                System.out.println("}");

            }
        }
        catch (Exception e){}

    }


}
