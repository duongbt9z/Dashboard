package com.example.dashboard.Helper;

import android.content.Context;
import android.widget.Toast;

import com.example.dashboard.Domain.PopularDomain;

import java.util.ArrayList;

public class ManagementCart {
    private Context context;
    private TinyDB tinyDB;

    public ManagementCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }

    public void insertItem(PopularDomain item){
        ArrayList<PopularDomain> listPop = getListCart(); //Lấy danh sách trong giỏ hàng
        boolean existAlready = false; //kiểm tra tồn tại
        int n = 0; //lưu vị trí của mục trong danh sách.
        for (int i = 0; i < listPop.size(); i++) {
            //Nếu tiêu đề của mục trong giỏ hàng khớp với tiêu đề của mục cần thêm
            if (listPop.get(i).getTitle().equals(item.getTitle())){
                existAlready = true;
                //lưu vị trí của mục vào n
                n = i;
                break;
            }
        }
        if (existAlready){ //nếu trùng
            listPop.get(n).setNumberInCart(item.getNumberInCart()); //cập nhật số lượng
        }else {
            listPop.add(item); //ngược lại, thêm mục vào danh sách giỏ hàng
        }
        tinyDB.putListObject("CartList", listPop); //Lưu vào csdl
        Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<PopularDomain> getListCart(){
        return tinyDB.getListObject("CartList");
    }

    public Double getTotalPrice(){
        ArrayList<PopularDomain> listItem = getListCart();
        double total = 0;
        for (int i = 0; i < listItem.size(); i++) {
            try {
                double price = Double.parseDouble(listItem.get(i).getPrice());
                total = total + (price * listItem.get(i).getNumberInCart());
            } catch (NumberFormatException e) {
                System.out.println("Error: Price is not a valid number for item at position " + i);
            }
        }
        return total;
    }


    //Xóa số lượng của một mặt hàng trong giỏ hàng
    public void minusNumberItem(ArrayList<PopularDomain> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener){
        if (listItem.get(position).getNumberInCart() == 1){
            listItem.remove(position);
        } else {
            listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() - 1);
        }
        tinyDB.putListObject("CartList", listItem); //Lưu vào csdl
        changeNumberItemsListener.change(); //thông báo số lượng mặt hàng thay đổi.
    }

    ////Thêm số lượng của một mặt hàng trong giỏ hàng
    public void plusNumberItem(ArrayList<PopularDomain> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener){
        listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() + 1);
        tinyDB.putListObject("CartList", listItem); //Lưu vào csdl
        changeNumberItemsListener.change(); //thông báo số lượng mặt hàng thay đổi.
    }
}
