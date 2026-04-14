package org.example.crimearchive.contactInformation;

public enum Clearance {
    locked,
    view,
    edit;

    public Clearance fromString(String input) {
        return switch (input.toLowerCase().trim()) {
            case "view" -> Clearance.view;
            case "edit" -> Clearance.edit;
            default -> Clearance.locked;
        };
    }
}
