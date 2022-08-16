package Model;

public class InvoiceLines {

    public InvoiceLines(){}
    public InvoiceLines(String InvoiceNum,String ItemName,String ItemPrice, String Count)
    {
        this.InvoiceNum =InvoiceNum;
        this.ItemName =ItemName;
        this.ItemPrice =ItemPrice;
        this.Count =Count;
    }




    private String InvoiceNum;
    private String ItemName;
    private String ItemPrice;
    private String Count;


    public String getInvoiceNum() {
        return InvoiceNum;
    }

    public String gettotal() {
        if(Count.isEmpty() || ItemPrice.isEmpty()) return String.valueOf(0);
        int num1 = Integer.parseInt(Count);
        int num2 = Integer.parseInt(ItemPrice);

        return String.valueOf(num1*num2);
    }

    public void setInvoiceNum(String invoiceNum) {
        InvoiceNum = invoiceNum;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemPrice() {
        return ItemPrice;
    }

    public void setItemPrice(String itemPrice) {
        ItemPrice = itemPrice;
    }

    public String getCount() {
        return Count;
    }

    public void setCount(String count) {
        Count = count;
    }



}
