package com.example.activemqstudy;

import lombok.Data;

import java.io.Serializable;
@Data
public class Person  implements Serializable {
    String name = "name";
    int age = 1;
}
