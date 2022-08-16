package Model;

import java.util.ArrayList;

public class InvoiceHeader {


    private String invoiceNum;
    private String invoiceDate;
    private String customerName;
    private String invoiceTotal;
    public ArrayList<InvoiceLines> Lines = new ArrayList<InvoiceLines>();


    public String getInvoiceTotal() {
        int total = 0;
        for(int i = 0; i< Lines.size();i++){
            total+= Integer.parseInt(Lines.get(i).gettotal());
        }

        invoiceTotal = String.valueOf(total);
        return invoiceTotal;
    }

    public void setInvoiceTotal(String invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }



    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum;
    }


}
