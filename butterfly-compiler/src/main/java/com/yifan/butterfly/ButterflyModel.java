package com.yifan.butterfly;

import java.util.ArrayList;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

final class ButterflyModel {

    /**
     * Elements annotated with BActivity
     */
    public TypeElement _Activity;
    public boolean _Result;

    /**
     * Elements annotated with BExtras
     */
    public final ArrayList<VariableElement> _ExtraElement;
    public final ArrayList<String> _ExtraAlias;

    public ButterflyModel() {
        _ExtraElement = new ArrayList<>();
        _ExtraAlias = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("_Activity: ")
                .append(_Activity.toString())
                .append("\n_Result: ")
                .append(_Result)
                .append("\n");

        builder.append("\nExtraElements: \n");
        for (VariableElement extra : _ExtraElement) {
            builder.append(extra.getSimpleName())
                    .append(": ")
                    .append(extra.asType().toString())
                    .append("\n");
        }

        builder.append("\nExtraIds: \n");
        for (String extraId : _ExtraAlias) {
            builder.append(extraId)
                    .append("\n");
        }

        return builder.toString();
    }
}
