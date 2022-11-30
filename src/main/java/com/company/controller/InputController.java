package com.company.controller;

import java.util.List;

public  class InputController {
    public static boolean isEmptyInput(List<String> values){
        for (String value : values) {
            if (value.isEmpty() || value.isBlank()) {
                return true;
            }
        }
        return false;
    }
}
