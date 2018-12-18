package scripts.usa.api.fxgson.adapters.properties.primitives;

import com.google.gson.TypeAdapter;

import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleLongProperty;
import scripts.usa.api.fxgson.adapters.properties.NullPropertyException;

/**
 * An implementation of {@link PrimitivePropertyTypeAdapter} for JavaFX {@link LongProperty}. It serializes the long
 * value of the property instead of the property itself.
 */
public class LongPropertyTypeAdapter extends PrimitivePropertyTypeAdapter<Long, LongProperty> {

    /**
     * Creates a new LongPropertyTypeAdapter.
     *
     * @param delegate
     *         a delegate adapter to use for the inner value of the property
     * @param throwOnNullProperty
     *         if true, this adapter will throw {@link NullPropertyException} when given a null {@link Property} to
     *         serialize
     * @param crashOnNullValue
     *         if true, this adapter will throw {@link NullPrimitiveException} when reading a null value. If false, this
     *         adapter will create a new simple property using the default constructor instead.
     */
    public LongPropertyTypeAdapter(TypeAdapter<Long> delegate, boolean throwOnNullProperty, boolean crashOnNullValue) {
        super(delegate, throwOnNullProperty, crashOnNullValue);
    }

    @Override
    protected Long extractPrimitiveValue(LongProperty property) {
        return property.get();
    }

    @Override
    protected LongProperty createDefaultProperty() {
        return new SimpleLongProperty();
    }

    @Override
    protected LongProperty wrapNonNullPrimitiveValue(Long deserializedValue) {
        return new SimpleLongProperty(deserializedValue);
    }
}
