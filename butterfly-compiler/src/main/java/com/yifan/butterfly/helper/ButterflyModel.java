package com.yifan.butterfly.helper;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class ButterflyModel {

    /**
     * Elements annotated with BActivity
     */
    public TypeElement _Actvity;
    public String _ActivityId;

    /**
     * Elements annotated with BExtras
     */
    public final List<VariableElement> _ExtraElements;
    public final List<String> _ExtraIds;

    public ButterflyModel() {
        _ExtraElements = new ArrayList<>();
        _ExtraIds = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("_Activity: ")
                .append(_Actvity.toString())
                .append("\n_ActivityId: ")
                .append(_ActivityId)
                .append("\n");

        builder.append("\nExtraElements: \n");
        for (VariableElement extra: _ExtraElements) {
            builder.append(extra.getSimpleName())
                    .append(": ")
                    .append(extra.asType().toString())
                    .append("\n");
        }

        builder.append("\nExtraIds: \n");
        for (String extraId: _ExtraIds) {
            builder.append(extraId)
                    .append("\n");
        }

        return builder.toString();
    }
}
