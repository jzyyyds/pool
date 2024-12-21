package org.example.build;

import java.io.Serializable;

public interface Builder<T> extends Serializable {

    T build();
}
