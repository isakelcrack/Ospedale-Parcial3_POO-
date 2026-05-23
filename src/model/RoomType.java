/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package model;

public enum RoomType {

    STANDARD,
    ICU,
    NICU,
    IMC,
    ISOLATION;

    public static RoomType fromText(String value) {
        if (value == null || value.isBlank()) {
            return STANDARD;
        }
        return RoomType.valueOf(value.trim().replace(" ", "_").toUpperCase());
    }
}

