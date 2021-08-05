package com.custodix.insite.local.ehr2edc.query.executor.common.projector;

import java.util.Objects;

public class KotlinScriptTestDTO {
    private String stringTest;

    public KotlinScriptTestDTO(String stringTest) {
        this.stringTest = stringTest;
    }

    public String getStringTest() {
        return stringTest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        KotlinScriptTestDTO that = (KotlinScriptTestDTO) o;
        return Objects.equals(stringTest, that.stringTest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stringTest);
    }
}