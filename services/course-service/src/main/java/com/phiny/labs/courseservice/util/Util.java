package com.phiny.labs.courseservice.util;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class Util {
    private Util(){};

    public static List<Sort.Order> generateSortOrders(String[] orders){
        // convert to streams
        String desc = new String("desc");
        List<Sort.Order> f_orders = new ArrayList<Sort.Order>();
        if (orders[0].contains(",")){
            for (String str: orders){
                String[] tmp = str.split(",");
                f_orders.add(new Sort.Order(tmp[1].equals(desc) ? Sort.Direction.DESC : Sort.Direction.ASC, tmp[0]));
            }
        } else {
            f_orders.add(new Sort.Order(orders[1].equals(desc) ? Sort.Direction.DESC : Sort.Direction.ASC, orders[0]));
        }
        return f_orders;
    }

    public static String randomCodeGenerator(){
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

}
