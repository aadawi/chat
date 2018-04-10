package com.chat.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "chat")
@Data
public class Chat implements Serializable {

    private static final long serialVersionUID = -3009157732242241606L;
    @Id
    private long id;

    @Column(name = "username")
    private String username;
    @Column(name = "message")
    private String message;
}
