package com.chat.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "chat")
@Data
public class Chat implements Serializable {

    private static final long serialVersionUID = -3009157732242241606L;
    @Id
    private long id;

    @Column(name = "username")
    private String username;
    @Column(name = "message", length = 100000)
    private String message;

    public static void main(String[] args) {
        List<String> listStr = new ArrayList();
        listStr.add("A");
        listStr.add("b");
        listStr.add("c");
        listStr.add("d");
        listStr.add("e");
        Optional<String> ss = listStr.stream().filter(x -> isdStr(x)).findFirst();
        String ourString = null;
        if (ss.isPresent()){
            ourString = ss.get();
        }
        System.out.println(ourString);
    }

    private static boolean isdStr(String s){
        return "dd".equalsIgnoreCase(s);
    }
}
