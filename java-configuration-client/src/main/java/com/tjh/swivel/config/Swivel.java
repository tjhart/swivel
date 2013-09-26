package com.tjh.swivel.config;

import com.tjh.swivel.config.model.HttpMethod;
import com.tjh.swivel.config.model.When;

public class Swivel {

    public static When post(String data){
        return new When(HttpMethod.POST).withContent(data);
    }
}
