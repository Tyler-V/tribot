package scripts.usa.api.fxgson.factories;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import scripts.usa.api.fxgson.adapters.extras.ColorTypeAdapter;
import scripts.usa.api.fxgson.adapters.extras.FontTypeAdapter;

/**
 * A {@link TypeAdapterFactory} for JavaFX's {@link Color} and {@link Font} classes.
 */
public class JavaFxExtraTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> clazz = type.getRawType();

        if (Color.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new ColorTypeAdapter();
        }

        if (Font.class.isAssignableFrom(clazz)) {
            return (TypeAdapter<T>) new FontTypeAdapter();
        }

        return null;
    }
}
