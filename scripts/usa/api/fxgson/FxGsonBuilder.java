package scripts.usa.api.fxgson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import scripts.usa.api.fxgson.adapters.properties.NullPropertyException;
import scripts.usa.api.fxgson.adapters.properties.primitives.NullPrimitiveException;
import scripts.usa.api.fxgson.creators.ObservableListCreator;
import scripts.usa.api.fxgson.creators.ObservableMapCreator;
import scripts.usa.api.fxgson.creators.ObservableSetCreator;
import scripts.usa.api.fxgson.factories.JavaFxExtraTypeAdapterFactory;
import scripts.usa.api.fxgson.factories.JavaFxPropertyTypeAdapterFactory;

/**
 * A builder that allows for FX-Gson configuration. The base builder only handles {@link Property} subclasses and
 * observable collections. Using {@link #withExtras()} adds support for the {@link Color} and {@link Font} classes.
 */
public class FxGsonBuilder {

    private final GsonBuilder builder;

    private boolean strictProperties = true;

    private boolean strictPrimitives = true;

    private boolean includeExtras = false;

    /**
     * Creates a new {@code FxGsonBuilder}.
     */
    public FxGsonBuilder() {
        this(new GsonBuilder());
    }

    /**
     * Creates a new {@code FxGsonBuilder} configuring the existing provided {@link GsonBuilder}.
     *
     * @param sourceBuilder
     *         the {@link GsonBuilder} to configure for JavaFX support
     */
    public FxGsonBuilder(GsonBuilder sourceBuilder) {
        this.builder = sourceBuilder;
    }

    /**
     * Creates a {@link GsonBuilder} instance pre-configured based on the current configuration. This method is NOT free
     * of side-effects to this {@code FxGsonBuilder} instance and hence should not be called multiple times.
     *
     * @return an instance of GsonBuilder configured with the options currently set in this builder
     */
    public GsonBuilder builder() {
        // serialization of nulls is necessary to have properties with null values deserialized properly
        builder.serializeNulls()
               .registerTypeAdapter(ObservableList.class, new ObservableListCreator())
               .registerTypeAdapter(ObservableSet.class, new ObservableSetCreator())
               .registerTypeAdapter(ObservableMap.class, new ObservableMapCreator())
               .registerTypeAdapterFactory(new JavaFxPropertyTypeAdapterFactory(strictProperties, strictPrimitives));
        if (includeExtras) {
            builder.registerTypeAdapterFactory(new JavaFxExtraTypeAdapterFactory());
        }
        return builder;
    }

    /**
     * Creates a {@link Gson} instance based on the current configuration. This method is NOT free of side-effects to
     * this {@code FxGsonBuilder} instance and hence should not be called multiple times.
     *
     * @return an instance of Gson configured with the options currently set in this builder
     */
    public Gson create() {
        return builder().create();
    }

    /**
     * Configures this {@code FxGsonBuilder} to accept null values for properties during serialization. If this method
     * is not used, the default behaviour is to throw a {@link NullPropertyException} when encountering a null value
     * for a property.
     *
     * @return this {@code FxGsonBuilder}, for use with the builder pattern
     */
    public FxGsonBuilder acceptNullProperties() {
        strictProperties = false;
        return this;
    }

    /**
     * Configures this {@code FxGsonBuilder} to accept null values for primitive properties during deserialization. The
     * deserialized property contains the default value for the primitive type, such as 0 for a numeric types, or false
     * for the boolean type. If this method is not used, the default behaviour is to throw a
     * {@link NullPrimitiveException} when encountering a null value for a primitive property.
     *
     * @return this {@code FxGsonBuilder}, for use with the builder pattern
     */
    public FxGsonBuilder acceptNullPrimitives() {
        strictPrimitives = false;
        return this;
    }

    /**
     * Adds support for the {@link Color} and {@link Font} classes.
     *
     * @return this {@code FxGsonBuilder}, for use with the builder pattern
     */
    public FxGsonBuilder withExtras() {
        includeExtras = true;
        return this;
    }
}
