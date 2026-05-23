/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package model;

public enum Specialty {

    GENERAL_MEDICINE,
    CARDIOLOGY,
    PEDIATRICS,
    NEUROLOGY,
    TRAUMATOLOGY_ORTHOPEDICS,
    GYNECOLOGY_OBSTETRICS,
    DERMATOLOGY,
    PSYCHIATRY,
    ONCOLOGY,
    OPHTHALMOLOGY,
    INTERNAL_MEDICINE;

    public static Specialty fromText(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Specialty is required");
        }
        String normalized = value.trim()
                .replace("&", "")
                .replace("-", "_")
                .replace(" ", "_")
                .toUpperCase();
        if ("ORTHOPEDICS".equals(normalized)) {
            return TRAUMATOLOGY_ORTHOPEDICS;
        }
        if ("GYNECOLOGY".equals(normalized)) {
            return GYNECOLOGY_OBSTETRICS;
        }
        return Specialty.valueOf(normalized);
    }

    public String displayName() {
        String text = name().replace("_", " ").toLowerCase();
        StringBuilder result = new StringBuilder();
        boolean nextUpper = true;
        for (char character : text.toCharArray()) {
            if (nextUpper && Character.isLetter(character)) {
                result.append(Character.toUpperCase(character));
                nextUpper = false;
            } else {
                result.append(character);
            }
            if (character == ' ') {
                nextUpper = true;
            }
        }
        return result.toString();
    }
}

