package net.md_5.bungee.chat;

import com.google.gson.*;
import net.md_5.bungee.api.chat.*;

import java.lang.reflect.Type;
import java.util.HashSet;

/*
  This code has been taken from BungeeCord by md_5 (https://github.com/SpigotMC/BungeeCord)
 */

public class ComponentSerializer implements JsonDeserializer<BaseComponent>
{

    private final static JsonParser JSON_PARSER = new JsonParser();
    private final static Gson gson = new GsonBuilder().
            registerTypeAdapter( BaseComponent.class, new ComponentSerializer() ).
            registerTypeAdapter( TextComponent.class, new TextComponentSerializer() ).
            registerTypeAdapter( TranslatableComponent.class, new TranslatableComponentSerializer() ).
            registerTypeAdapter( KeybindComponent.class, new KeybindComponentSerializer() ).
            registerTypeAdapter( ScoreComponent.class, new ScoreComponentSerializer() ).
            registerTypeAdapter( SelectorComponent.class, new SelectorComponentSerializer() ).
            create();

    public final static ThreadLocal<HashSet<BaseComponent>> serializedComponents = new ThreadLocal<HashSet<BaseComponent>>();

    public static BaseComponent[] parse(String json)
    {
        JsonElement jsonElement = JSON_PARSER.parse( json );

        return parse(jsonElement);
    }

    public static BaseComponent[] parse(JsonElement jsonElement)
    {
        if ( jsonElement.isJsonArray() )
        {
            return gson.fromJson( jsonElement, BaseComponent[].class );
        } else
        {
            return new BaseComponent[]
                    {
                            gson.fromJson( jsonElement, BaseComponent.class )
                    };
        }
    }

    public static JsonElement toJsonTree(BaseComponent component)
    {
        return gson.toJsonTree( component );
    }

    public static JsonElement toJsonTree(BaseComponent... components)
    {
        if ( components.length == 1 )
        {
            return gson.toJsonTree( components[0] );
        } else
        {
            return gson.toJsonTree( new TextComponent( components ) );
        }
    }

    public static String toString(BaseComponent component)
    {
        return gson.toJson( component );
    }

    public static String toString(BaseComponent... components)
    {
        if ( components.length == 1 )
        {
            return gson.toJson( components[0] );
        } else
        {
            return gson.toJson( new TextComponent( components ) );
        }
    }

    @Override
    public BaseComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        if ( json.isJsonPrimitive() )
        {
            return new TextComponent( json.getAsString() );
        }
        JsonObject object = json.getAsJsonObject();
        if ( object.has( "translate" ) )
        {
            return context.deserialize( json, TranslatableComponent.class );
        }
        if ( object.has( "keybind" ) )
        {
            return context.deserialize( json, KeybindComponent.class );
        }
        if ( object.has( "score" ) )
        {
            return context.deserialize( json, ScoreComponent.class );
        }
        if ( object.has( "selector" ) )
        {
            return context.deserialize( json, SelectorComponent.class );
        }
        return context.deserialize( json, TextComponent.class );
    }
}
