package scripts.usa.api.fxgson.adapters.properties;

import com.google.gson.TypeAdapter;
import com.sun.istack.internal.NotNull;

import javafx.beans.property.Property;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.ObservableSet;

/**
 * A basic {@link TypeAdapter} for JavaFX {@link SetProperty}. It serializes the set inside the property instead of the
 * property itself.
 */
public class SetPropertyTypeAdapter<T> extends PropertyTypeAdapter<ObservableSet<T>, SetProperty<T>> {

    /**
     * Creates a new SetPropertyTypeAdapter.
     *
     * @param delegate
     *         a delegate adapter to use for the inner set value of the property
     * @param throwOnNullProperty
     *         if true, this adapter will throw {@link NullPropertyException} when given a null {@link Property} to
     *         serialize
     */
    public SetPropertyTypeAdapter(TypeAdapter<ObservableSet<T>> delegate, boolean throwOnNullProperty) {
        super(delegate, throwOnNullProperty);
    }

    @NotNull
    @Override
    protected SetProperty<T> createProperty(ObservableSet<T> deserializedValue) {
        return new SimpleSetProperty<>(deserializedValue);
    }
}
